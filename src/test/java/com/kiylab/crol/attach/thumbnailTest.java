package com.kiylab.crol.attach;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class thumbnailTest {
// 이미지 src (html 로 되어있는)
//  @Test
//  public void testDetailPageCrawl() throws Exception {
//    WebDriverManager.chromedriver().setup();
//    WebDriver driver = new ChromeDriver();
//    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
//    driver.manage().window().maximize();
//
//    try {
//      driver.get("https://www.lge.co.kr/air-conditioners/fq25fn9be2");
//
//      // ✅ 딜레이 (봇 탐지 방지)
//      Thread.sleep(2000);
//
//      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//      wait.until(ExpectedConditions.presenceOfElementLocated(By.className("pdp-visual-wrap")));
//
//      // ✅ div.pdp-visual-wrap 안의 모든 <img> 태그 HTML 가져오기
//      List<WebElement> images = driver.findElements(By.cssSelector(".pdp-visual-wrap img"));
//
//      System.out.println("===== pdp-visual-wrap 내부 IMG 태그 HTML =====");
//      for (WebElement img : images) {
//        String imgHtml = img.getAttribute("outerHTML"); // 태그 전체
//        System.out.println(imgHtml);
//      }
//
//    } finally {
//      driver.quit();
//    }
//  }
//}
// 모든 div 내용 htmml롷)
//  @Test
//  public void testDetailPageCrawl() throws Exception {
//    WebDriverManager.chromedriver().setup();
//    WebDriver driver = new ChromeDriver();
//    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
//    driver.manage().window().maximize();
//
//    try {
//      driver.get("https://www.lge.co.kr/air-conditioners/fq25fn9be2");
//
//      // ✅ 딜레이 (봇 탐지 방지)
//      Thread.sleep(2000);
//
//      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//      wait.until(ExpectedConditions.presenceOfElementLocated(By.className("pdp-visual-wrap")));
//
//      // ✅ div.pdp-visual-wrap 전체 HTML 가져오기
//      WebElement visualWrap = driver.findElement(By.className("pdp-visual-wrap"));
//      String html = visualWrap.getAttribute("innerHTML");
//
//      System.out.println("===== pdp-visual-wrap 전체 HTML =====");
//      System.out.println(html);
//
//    } finally {
//      driver.quit();
//    }
//  }
//}

// 이미지 링크 src 리스트만 (html 아님) //


    @Test
    public void testThumbnailImageCrawl() throws Exception {
      WebDriverManager.chromedriver().setup();
      WebDriver driver = new ChromeDriver();
      driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
      driver.manage().window().maximize();

      try {
        driver.get("https://www.lge.co.kr/air-conditioners/fq25fn9be2");

        // ✅ 딜레이
        Thread.sleep(2000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("pdp-visual-wrap")));

        // ✅ div.pdp-visual-wrap 안의 이미지 태그들
        List<WebElement> images = driver.findElements(By.cssSelector(".pdp-visual-wrap img"));

        System.out.println("===== pdp-visual-wrap 내부 썸네일 이미지 SRC =====");
        for (WebElement img : images) {
          String src = getImageUrl(img);
          System.out.println(src);
        }

      } finally {
        driver.quit();
      }
    }

    // ✅ 이미지 경로 추출 유틸 (src → data-* → 절대경로 변환)
    private String getImageUrl(WebElement img) {
      String baseUrl = "https://www.lge.co.kr";

      // 1. src 확인
      String src = img.getAttribute("src");
      if (src != null && !src.isBlank()) {
        return src.startsWith("/") ? baseUrl + src : src;
      }

      // 2. lazy-loading 속성들 확인
      String[] attrs = {"data-current-image", "data-pc-src", "data-m-src"};
      for (String attr : attrs) {
        String val = img.getAttribute(attr);
        if (val != null && !val.isBlank()) {
          return val.startsWith("/") ? baseUrl + val : val;
        }
      }

      return "❌ 이미지 경로 없음";
    }
  }