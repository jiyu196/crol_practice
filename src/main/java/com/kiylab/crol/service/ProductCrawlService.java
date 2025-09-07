package com.kiylab.crol.service;

import com.kiylab.crol.entity.Attach;
import com.kiylab.crol.entity.Product;
import com.kiylab.crol.repository.AttachRepository;
import com.kiylab.crol.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductCrawlService {

    private final ProductRepository productRepository;
    private final AttachRepository attachRepository;
    private final S3Uploader s3Uploader;

    private final String BUCKET_NAME = "kiylab-bucket"; // 네 S3 버킷 이름

    public void crawlOneProduct(String url) throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            driver.get(url);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Thread.sleep(2000); // 안정적인 실행을 위한 딜레이

            // ==============================
            // 1. 상품명 가져오기
            // ==============================
            String name = "";
            try {
                List<WebElement> h1s = driver.findElements(By.className("tit"));
                if (!h1s.isEmpty()) {
                    name = h1s.get(0).getText();
                }
                if (name.isBlank()) {
                    List<WebElement> ogTitle = driver.findElements(By.cssSelector("meta[property='og:title']"));
                    if (!ogTitle.isEmpty()) {
                        name = ogTitle.get(0).getAttribute("content");
                    }
                }
                if (name.isBlank()) {
                    name = driver.getTitle();
                }
            } catch (Exception e) {
                name = "상품명 없음";
            }
            name = replaceBrand(name);

            // ==============================
            // 2. 상세 설명 가져오기
            // ==============================
            String description = "";
            try {
                List<WebElement> descEls = driver.findElements(By.className("iw_placeholder"));
                if (!descEls.isEmpty()) {
                    description = descEls.get(0).getText();
                } else {
                    List<WebElement> altEls = driver.findElements(By.className("product-detail"));
                    if (!altEls.isEmpty()) {
                        description = altEls.get(0).getText();
                    } else {
                        description = "상세 설명 없음";
                    }
                }
            } catch (Exception e) {
                description = "상세 설명 없음";
            }
            description = replaceBrand(description);

            // ==============================
            // 3. 가격 가져오기 (총 금액)
            // ==============================
            String priceText = "0";
            try {
                List<WebElement> priceEls = driver.findElements(By.cssSelector(".total-payment .price em"));
                if (!priceEls.isEmpty()) {
                    priceText = priceEls.get(0).getText().replaceAll("[^0-9]", "");
                }
            } catch (Exception e) {
                priceText = "0";
            }
            int price = priceText.isBlank() ? 0 : Integer.parseInt(priceText);

            // ==============================
            // 4. 카테고리 가져오기
            // ==============================
            String categoryName = "임시";
            try {
                List<WebElement> categoryEls = driver.findElements(By.cssSelector("a.category-item"));
                if (!categoryEls.isEmpty()) {
                    String text = categoryEls.get(0).getText();
                    if (text != null && !text.isBlank()) {
                        categoryName = text.trim();
                    } else {
                        String dataContents = categoryEls.get(0).getAttribute("data-contents");
                        if (dataContents != null && !dataContents.isBlank()) {
                            categoryName = dataContents.trim();
                        }
                    }
                }
            } catch (Exception e) {
                categoryName = "임시";
            }

            // ==============================
            // 5. 이미지 가져오기
            // ==============================
            List<WebElement> images = driver.findElements(By.cssSelector(".iw_placeholder img"));
            if (images.isEmpty()) {
                images = driver.findElements(By.tagName("img"));
            }

            // ==============================
            // 6. Product 저장 (첫 번째 이미지를 썸네일로 세팅)
            // ==============================
            String thumbnailPath = null;
            if (!images.isEmpty()) {
                String firstSrc = resolveImageUrl(images.get(0));
                if (firstSrc != null && !firstSrc.isBlank()) {
                    thumbnailPath = firstSrc;
                }
            }

            Product product = Product.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .productCategory(categoryName) // ✅ 카테고리 반영
                    .thumbnail(thumbnailPath)     // ✅ 첫 이미지 썸네일
                    .build();
            productRepository.save(product);

            // ==============================
            // 7. 이미지 다운로드 + S3 업로드 + Attach 저장
            // ==============================
            for (int i = 0; i < images.size(); i++) {
                String src = resolveImageUrl(images.get(i));
                if (src == null) continue;

                String uuid = UUID.randomUUID().toString();
                Path localPath = downloadImage(src, uuid + ".jpg");
                if (localPath == null) continue; // 실패 시 그냥 건너뛰기

                if (Files.exists(localPath)) {
                    String s3Key = "product/" + uuid + ".jpg";
                    try {
                        s3Uploader.uploadFile(BUCKET_NAME, s3Key, localPath);

                        String s3Url = "https://" + BUCKET_NAME + ".s3.ap-northeast-2.amazonaws.com/" + s3Key;

                        Attach attach = Attach.builder()
                                .uuid(uuid)
                                .path(s3Url)
                                .isThumbnail(i == 0)
                                .productId(product.getProductId())
                                .build();
                        attachRepository.save(attach);

                        if (i == 0) {
                            product.setThumbnail(s3Url);
                            productRepository.save(product);
                        }
                    } catch (Exception ex) {
                        System.out.println("⚠️ S3 업로드 실패: " + src + " → " + ex.getMessage());
                    }
                }
            }

        } finally {
            driver.quit();
        }
    }

    // ==============================
    // 보조 메서드: 이미지 다운로드 (JPG 그대로)
    // ==============================
    private Path downloadImage(String imageUrl, String fileName) throws Exception {
        Path targetPath = Paths.get(System.getProperty("java.io.tmpdir"), fileName);

        try (InputStream in = new URL(imageUrl).openStream()) {
            BufferedImage bufferedImage = ImageIO.read(in);

            if (bufferedImage == null) {
                // ⚠️ fallback: 그냥 바이트 그대로 저장
                System.out.println("⚠️ ImageIO로 읽기 실패 → 원본 스트림 그대로 저장: " + imageUrl);
                try (InputStream rawIn = new URL(imageUrl).openStream()) {
                    Files.copy(rawIn, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
                return targetPath;
            }

            // 정상 이미지라면 jpg로 변환 저장
            ImageIO.write(bufferedImage, "jpg", targetPath.toFile());
        } catch (Exception ex) {
            System.out.println("⚠️ 이미지 다운로드 실패 → 건너뜀: " + imageUrl + " | 이유: " + ex.getMessage());
            return null; // 실패 시 null 반환
        }

        if (!Files.exists(targetPath)) {
            System.out.println("⚠️ 최종 파일 없음 → 스킵: " + targetPath);
            return null;
        }
        return targetPath;
    }

    // ==============================
    // 보조 메서드: 이미지 URL 속성 우선순위 처리
    // ==============================
    private String resolveImageUrl(WebElement img) {
        String[] attrs = {"src", "data-src", "data-original", "data-pc-src"};
        for (String attr : attrs) {
            String value = img.getAttribute(attr);
            if (value != null && !value.isBlank()) {
                return value.startsWith("http") ? value : "https://www.lge.co.kr" + value;
            }
        }
        return null;
    }

    // ==============================
    // 보조 메서드: LG → SAYREN 변환
    // ==============================
    private String replaceBrand(String text) {
        if (text == null) return "";
        return text.replaceAll("(?i)lg", "SAYREN");
    }
}
