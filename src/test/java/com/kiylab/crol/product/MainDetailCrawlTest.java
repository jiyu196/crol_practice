package com.kiylab.crol.product;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class MainDetailCrawlTest {

  @Test
  public void testDetailImageSrc() throws Exception {
    WebDriverManager.chromedriver().setup();
    WebDriver driver = new ChromeDriver();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    driver.manage().window().maximize();

    try {
      driver.get("https://www.lge.co.kr/air-conditioners/fq25fn9be2");

      // ✅ 딜레이
      Thread.sleep(2000);

      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
      wait.until(ExpectedConditions.presenceOfElementLocated(By.className("iw_placeholder")));

      // ✅ iw_placeholder 안의 이미지 태그
      List<WebElement> images = driver.findElements(By.cssSelector(".iw_placeholder img"));

      System.out.println("===== 상세페이지 이미지 SRC 목록 =====");
      for (WebElement img : images) {
        String src = getImageUrl(img);
        System.out.println(src);
      }

    } finally {
      driver.quit();
    }
  }

  // ✅ 이미지 src 추출 + 절대경로 변환
  private String getImageUrl(WebElement img) {
    String[] attrs = {"src", "data-current-image", "data-pc-src", "data-m-src"};
    String baseUrl = "https://www.lge.co.kr";

    for (String attr : attrs) {
      String value = img.getAttribute(attr);
      if (value != null && !value.isBlank()) {
        // 상대경로 처리
        if (value.startsWith("/")) {
          return baseUrl + value;
        }
        return value;
      }
    }

    return "❌ 이미지 경로 없음";
  }
}

//    // ✅ 크롬드라이버 자동 다운로드 + 경로 세팅
//    WebDriverManager.chromedriver().setup();
//    WebDriver driver = new ChromeDriver();
//    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
//    driver.manage().window().maximize();
//
//    try {
//      driver.get("https://www.lge.co.kr/air-conditioners/fq25fn9be2");
//
//      // ✅ 지연 로딩 (공격 방지용)
//      Thread.sleep(2000);
//
//      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//      wait.until(ExpectedConditions.presenceOfElementLocated(By.className("iw_placeholder")));
//
//      ((JavascriptExecutor) driver).executeScript(
//              "document.querySelectorAll('img').forEach(img => {" +
//                      "  if(img.getAttribute('data-current-image')) {" +
//                      "    img.setAttribute('src', img.getAttribute('data-current-image'));" +
//                      "  } else if(img.getAttribute('data-pc-src')) {" +
//                      "    img.setAttribute('src', img.getAttribute('data-pc-src'));" +
//                      "  } else if(img.getAttribute('data-m-src')) {" +
//                      "    img.setAttribute('src', img.getAttribute('data-m-src'));" +
//                      "  }" +
//                      "});" + // ✅ 여기서 이미지 forEach 닫아줌
//
//                      // 버튼, ul, span 제거
//                      "document.querySelectorAll('button, ul, span').forEach(el => el.remove());" +
//
//                      // 특정 텍스트 포함된 요소 삭제
//                      "document.querySelectorAll('.iw_placeholder *').forEach(el => {" +
//                      "  if (el.innerText && el.innerText.includes('쉽고 편한 LG ThinQ')) {" +
//                      "    el.remove();" +
//                      "  }" +
//                      "});" +
//
//                      "document.querySelectorAll('.iw_placeholder *').forEach(el => {" +
//                      "  if (el.innerText && el.innerText.includes('똑똑한 LG UP 가전')) {" +
//                      "    el.remove();" +
//                      "  }" +
//                      "});" +
//
//                      // 텍스트 노드에서 LG, lg -> SAYREN 변경
//                      "document.querySelectorAll('.iw_placeholder *').forEach(el => {" +
//                      "  el.childNodes.forEach(node => {" +
//                      "    if (node.nodeType === Node.TEXT_NODE) {" +
//                      "      node.textContent = node.textContent.replace(/LG/gi, 'SAYREN');" +
//                      "    }" +
//                      "  });" +
//                      "});"
//      );
//    } catch (Exception e) {
//    }
//
////      // ✅ 결과 HTML 콘솔 출력
//    String cleanedHtml = driver.findElement(By.className("iw_placeholder")).getAttribute("innerHTML");
//    System.out.println("===== 정리된 iw_placeholder HTML =====");
//    System.out.println(cleanedHtml);
//
//  }}

