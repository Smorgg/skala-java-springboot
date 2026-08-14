package com.sk.skala.myapp.dto;

import com.sk.skala.myapp.domain.ProductStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    
    @Column(name = "product_name", nullable = false, length = 100)
    private String name;                              // 상품명 (최대 100자, NOT NULL)

    @Column(nullable = false)
    private Integer price;                            // 가격 (NOT NULL)

    @Column(name = "stock_quantity", columnDefinition = "INT DEFAULT 0")
    private Integer stockQuantity;                    // 재고 수량 (기본값 0)

    @Enumerated(EnumType.STRING)                      // DB에 "ON_SALE" 같은 문자열로 저장
    @Column(nullable = false)
    private ProductStatus status;                     // 상품 상태 (ON_SALE / SOLD_OUT / DISCONTINUED)

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;                       // 상품 상세 설명 (대용량 텍스트)

    @Transient
    private String displayLabel;
}
