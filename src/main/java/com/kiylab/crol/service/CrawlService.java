package com.kiylab.crol.service;

import com.kiylab.crol.entity.Crol;
import com.kiylab.crol.repository.CrolRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service  // 서비스 계층 클래스
public class CrawlService {

  @Autowired
  private CrolRepository repository;

  // 크롤링 실행 메서드
  public void crawl() {
    // 1) 크롬 드라이버 자동 설정
    WebDriverManager.chromedriver().setup();

    // 2) 크롬 브라우저 실행
    WebDriver driver = new ChromeDriver();

    try {
      // 3) 크롤링할 URL 접속
      String url = "https://www.lge.co.kr/projectors/pu615u";
      driver.get(url);

      // 4) 특정 클래스명으로 요소 찾기
      //    <div class="iw_placeholder"> ... </div>
      List<WebElement> elements = driver.findElements(By.className("iw_placeholder"));

      // 5) 요소 반복문으로 탐색
      for (WebElement el : elements) {
        // el.getAttribute("innerHTML") -> 태그 포함된 전체 내용 가져옴
        String htmlContent = el.getAttribute("innerHTML");

        // 콘솔에 찍어서 확인
        System.out.println("===== 크롤링된 내용 =====");
        System.out.println(htmlContent);

        // 6) DB 저장
//        Crol crol = new Crol();
//        crol.setContent(htmlContent); // HTML 전체 저장
//        repository.save(crol);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // 7) 크롬 브라우저 종료
      driver.quit();
    }
  }
}