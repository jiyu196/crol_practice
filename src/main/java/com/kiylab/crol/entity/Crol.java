package com.kiylab.crol.entity;

import jakarta.persistence.*;

// DB 테이블과 매핑되는 클래스
@Entity                  // 이 클래스가 엔티티임을 명시 (JPA가 관리)
@Table(name = "crol")    // DB 테이블 이름 지정
public class Crol {

  @Id  // PK(Primary Key) 설정
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  // DB에서 AUTO_INCREMENT로 자동 증가
  private Long id;

  @Column(columnDefinition = "TEXT")
  // DB 컬럼 content를 TEXT 타입으로 매핑
  private String content;

  // Getter & Setter
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}