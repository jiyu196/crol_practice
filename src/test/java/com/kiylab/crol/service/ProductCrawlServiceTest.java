package com.kiylab.crol.service;

import com.kiylab.crol.entity.Attach;
import com.kiylab.crol.entity.Product;
import com.kiylab.crol.repository.AttachRepository;
import com.kiylab.crol.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ProductCrawlServiceTest {

    @Autowired
    private ProductCrawlService productCrawlService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AttachRepository attachRepository;

    @Test
    public void testCrawlOneProduct() throws Exception {
        // ✅ 크롤링할 제품 링크
        String url = "https://www.lge.co.kr/air-conditioners/fq25fn9be2";

        // 실행 전 딜레이
        Thread.sleep(2000);

        // 서비스 실행 → DB + S3 저장
        productCrawlService.crawlOneProduct(url);

        // 콘솔 확인
        System.out.println("=== 크롤링 완료 ===");
        System.out.println("URL: " + url);

        // 최신 Product 가져오기
        List<Product> products = productRepository.findAll();
        Product latest = products.get(products.size() - 1);
        System.out.println("===== DB 저장된 Product =====");
        System.out.println("ID: " + latest.getProductId());
        System.out.println("Name: " + latest.getName());
        System.out.println("Description: " + (latest.getDescription().length() > 50
                ? latest.getDescription().substring(0, 50) + "..."
                : latest.getDescription()));
        System.out.println("Category: " + latest.getProductCategory());

        // 관련 Attach 출력
        List<Attach> attachList = attachRepository.findAll();
        System.out.println("===== DB 저장된 Attach (이미지) =====");
        attachList.stream()
                .filter(a -> a.getProductId().equals(latest.getProductId()))
                .forEach(a -> {
                    System.out.println("UUID: " + a.getUuid());
                    System.out.println("Path(S3): " + a.getPath());
                    System.out.println("isThumbnail: " + a.getIsThumbnail());
                });
    }
}
