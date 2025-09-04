package com.kiylab.crol.crol;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CrawlTest {

  private final String baseUrl = "https://www.lge.co.kr";

  @Test
  void testCrawlWithDelayAndImageFix() throws InterruptedException {
    // 1. 크롬 드라이버 자동 세팅
    WebDriverManager.chromedriver().setup();

    // 2. 옵션 세팅 (필요하면 headless 모드)
    ChromeOptions options = new ChromeOptions();
    // options.addArguments("--headless"); // 브라우저 안 띄우고 실행

    WebDriver driver = new ChromeDriver(options);

    try {
      // 3. 크롤링할 URL 접속
      String url = baseUrl + "/projectors/pu615u";
      driver.get(url);

      // 4. 특정 요소 로딩될 때까지 대기
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
      wait.until(ExpectedConditions.presenceOfElementLocated(By.className("iw_placeholder")));

      // 5. iw_placeholder div 가져오기
      List<WebElement> elements = driver.findElements(By.className("iw_placeholder"));

      for (WebElement el : elements) {
        String htmlContent = el.getAttribute("innerHTML");

        // 콘솔 출력
        System.out.println("===== 크롤링 데이터 =====");
        System.out.println(htmlContent);

        // 이미지 태그들 따로 가져오기
        List<WebElement> imgs = el.findElements(By.tagName("img"));
        for (WebElement img : imgs) {
          String imgSrc = fixImageUrl(img);
          System.out.println("이미지 주소: " + imgSrc);
        }

        // 서버 공격처럼 안 보이게 1초 딜레이
        Thread.sleep(2000);
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      driver.quit();
    }
  }

  // ✅ 이미지 경로 보정 메서드
  private String fixImageUrl(WebElement img) {
    String src = img.getAttribute("src");
    String pcSrc = img.getAttribute("data-pc-src");
    String moSrc = img.getAttribute("data-m-src");
    String current = img.getAttribute("data-current-image");

    String result = null;

    if (current != null && !current.isEmpty()) result = current;
    else if (pcSrc != null && !pcSrc.isEmpty()) result = pcSrc;
    else if (src != null && !src.isEmpty()) result = src;

    if (result != null && result.startsWith("/")) {
      result = baseUrl + result; // 상대 경로 → 절대 경로
    }
    return result;
  }
}
