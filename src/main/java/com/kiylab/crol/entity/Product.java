package com.kiylab.crol.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_product") // DB 테이블명과 매핑
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long productId;

    @Column(nullable = false, length = 200)
    private String name; // 상품명

    private Integer price; // 가격 (필요 없으면 null 가능)

    @Column(columnDefinition = "TEXT")
    private String description; // 상세 설명

    private String thumbnail; // 썸네일 S3 경로

    private String productCategory; // 카테고리 (예: "에어컨")
}
