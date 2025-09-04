package com.kiylab.crol.crol;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CrawlTest {

  @Test
  public void testCrawlWithCleanedHtml() throws Exception {
    // ✅ 크롬드라이버 자동 다운로드 + 경로 세팅
    WebDriverManager.chromedriver().setup();
    WebDriver driver = new ChromeDriver();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    driver.manage().window().maximize();

    try {
      driver.get("https://www.lge.co.kr/kimchi-refrigerators/z330meef21");

      // ✅ 지연 로딩 (공격 방지용)
      Thread.sleep(2000);

      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
      wait.until(ExpectedConditions.presenceOfElementLocated(By.className("iw_placeholder")));

      ((JavascriptExecutor) driver).executeScript(
              "document.querySelectorAll('img').forEach(img => {" +
                      "  if(img.getAttribute('data-current-image')) {" +
                      "    img.setAttribute('src', img.getAttribute('data-current-image'));" +
                      "  } else if(img.getAttribute('data-pc-src')) {" +
                      "    img.setAttribute('src', img.getAttribute('data-pc-src'));" +
                      "  } else if(img.getAttribute('data-m-src')) {" +
                      "    img.setAttribute('src', img.getAttribute('data-m-src'));" +
                      "  }" +
                      "});" + // ✅ 여기서 이미지 forEach 닫아줌

                      // 버튼, ul, span 제거
                      "document.querySelectorAll('button, ul, span').forEach(el => el.remove());" +

                      // 특정 텍스트 포함된 요소 삭제
                      "document.querySelectorAll('.iw_placeholder *').forEach(el => {" +
                      "  if (el.innerText && el.innerText.includes('쉽고 편한 LG ThinQ')) {" +
                      "    el.remove();" +
                      "  }" +
                      "});" +

                      "document.querySelectorAll('.iw_placeholder *').forEach(el => {" +
                      "  if (el.innerText && el.innerText.includes('똑똑한 LG UP 가전')) {" +
                      "    el.remove();" +
                      "  }" +
                      "});" +

                      // 텍스트 노드에서 LG, lg -> SAYREN 변경
                      "document.querySelectorAll('.iw_placeholder *').forEach(el => {" +
                      "  el.childNodes.forEach(node => {" +
                      "    if (node.nodeType === Node.TEXT_NODE) {" +
                      "      node.textContent = node.textContent.replace(/LG/gi, 'SAYREN');" +
                      "    }" +
                      "  });" +
                      "});"
      );
    } catch (Exception e) {
    }

//      // ✅ 결과 HTML 콘솔 출력
    String cleanedHtml = driver.findElement(By.className("iw_placeholder")).getAttribute("innerHTML");
    System.out.println("===== 정리된 iw_placeholder HTML =====");
    System.out.println(cleanedHtml);

  }}


//
//      // ✅ 추가 메서드 실행
//      String name = getProductName(driver);
//      String thumbnail = getThumbnail(driver);
//      int monthlyPrice = getMonthlyPrice(driver);
//
//      System.out.println("===== 추가 정보 =====");
//      System.out.println("제품명: " + name);
//      System.out.println("대표 이미지: " + thumbnail);
//      System.out.println("월 요금: " + monthlyPrice + "원");
//
//    } finally {
//      driver.quit();
//    }
//  }
//
//  // ✅ 제품명 가져오기
//  private String getProductName(WebDriver driver) {
//    try {
//      return driver.findElement(By.cssSelector(".pdp-wrap h1")).getText();
//    } catch (Exception e) {
//      return "제품명 없음";
//    }
//  }
//
//  // ✅ 대표 썸네일 가져오기
//  private String getThumbnail(WebDriver driver) {
//    try {
//      return driver.findElement(By.cssSelector(".pdp-wrap img")).getAttribute("src");
//    } catch (Exception e) {
//      return "대표 이미지 없음";
//    }
//  }
//
//  // ✅ 가격 가져오기 (정상가 기준 월 요금)
//  private int getMonthlyPrice(WebDriver driver) {
//    try {
//      String priceText = driver.findElement(By.cssSelector(".pdp-wrap strong")).getText();
//      priceText = priceText.replaceAll("[^0-9]", ""); // 숫자만 추출
//      int price = Integer.parseInt(priceText);
//      return price / 12; // 1년차 기준 월 요금
//    } catch (Exception e) {
//      return -1; // 가격 못 찾았을 때
//    }
//  }
//}
