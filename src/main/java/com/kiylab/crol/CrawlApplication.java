package com.kiylab.crol;

import com.kiylab.crol.service.CrawlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// 스프링부트 애플리케이션 시작점
@SpringBootApplication
public class CrawlApplication implements CommandLineRunner {

  @Autowired
  private CrawlService crawlService;

  public static void main(String[] args) {
    SpringApplication.run(CrawlApplication.class, args);
  }

  // 프로젝트 실행 시 자동으로 크롤링 실행됨
  @Override
  public void run(String... args) throws Exception {
    crawlService.crawl();
  }
}
