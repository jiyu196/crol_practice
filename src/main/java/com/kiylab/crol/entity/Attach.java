package com.kiylab.crol.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_attach")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachId;

    private String uuid; // 파일 고유 식별자
    private String path; // 파일 저장 경로 (S3 키)
    private Boolean isThumbnail; // 썸네일 여부

    // 상품에 연결
    private Long productId;
}
