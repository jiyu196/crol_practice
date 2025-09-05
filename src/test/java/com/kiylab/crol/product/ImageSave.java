package com.kiylab.crol.product;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.coobird.thumbnailator.Thumbnails;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class ImageSave {
  private final String BASE_URL = "https://www.lge.co.kr";
  private final String PRODUCT_URL = "https://www.lge.co.kr/air-conditioners/fq25fn9be2";
  private final String SAVE_DIR = "imageLG"; // ✅ 저장 폴더

  @Test
  public void testDownloadImages() throws Exception {
    WebDriverManager.chromedriver().setup();
    WebDriver driver = new ChromeDriver();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    driver.manage().window().maximize();

    try {
      driver.get(PRODUCT_URL);
      Thread.sleep(2000);

      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

      // ✅ 썸네일 이미지 (pdp-visual-wrap)
      wait.until(ExpectedConditions.presenceOfElementLocated(By.className("pdp-visual-wrap")));
      List<String> thumbUrls = driver.findElements(By.cssSelector(".pdp-visual-wrap img"))
              .stream().map(this::getImageUrl).filter(Objects::nonNull).distinct().collect(Collectors.toList());
      saveImages(thumbUrls, SAVE_DIR + "/thumbnail");

      // ✅ 상세페이지 이미지 (iw_placeholder)
      wait.until(ExpectedConditions.presenceOfElementLocated(By.className("iw_placeholder")));
      List<String> detailUrls = driver.findElements(By.cssSelector(".iw_placeholder img"))
              .stream().map(this::getImageUrl).filter(Objects::nonNull).distinct().collect(Collectors.toList());
      saveImages(detailUrls, SAVE_DIR + "/detail");

    } finally {
      driver.quit();
    }
  }

  // ✅ 이미지 URL 추출 (src + data-* → 절대경로 변환)
  private String getImageUrl(WebElement img) {
    String[] attrs = {"src", "data-current-image", "data-pc-src", "data-m-src"};

    for (String attr : attrs) {
      String value = img.getAttribute(attr);
      if (value != null && !value.isBlank()) {
        return value.startsWith("/") ? BASE_URL + value : value;
      }
    }
    return null; // 깨진 건 스킵
  }

  // ✅ 이미지 저장 (원본 + webp 변환)
  private void saveImages(List<String> urls, String folder) {
    Path jpgDir = Paths.get(folder, "jpg");
    Path webpDir = Paths.get(folder, "webp");

    try {
      Files.createDirectories(jpgDir);
      Files.createDirectories(webpDir);
    } catch (Exception e) {
      e.printStackTrace();
    }

    for (String url : urls) {
      try {
        // 파일명 추출 (URL 마지막 부분)
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        if (fileName.isBlank()) continue;

        // 원본 저장 경로
        Path jpgPath = jpgDir.resolve(fileName);

        // User-Agent 헤더 붙여서 요청
        URL urlObj = new URL(url);
        URLConnection conn = urlObj.openConnection();
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                        + "KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36");

        // ✅ 원본 저장
        try (InputStream in = conn.getInputStream()) {
          Files.copy(in, jpgPath, StandardCopyOption.REPLACE_EXISTING);
          System.out.println("✔ 원본 저장됨: " + jpgPath);
        }

        // ✅ webp 변환 저장
        Path webpPath = webpDir.resolve(fileName.replaceAll("\\.[a-zA-Z0-9]+$", ".webp"));
        Thumbnails.of(jpgPath.toFile())
                .scale(1.0)
                .outputFormat("webp")
                .toFile(webpPath.toFile());

        System.out.println("✔ 변환 저장됨: " + webpPath);

      } catch (Exception e) {
        System.out.println("✘ 저장 실패: " + url + " (" + e.getMessage() + ")");
      }
    }
  }
}