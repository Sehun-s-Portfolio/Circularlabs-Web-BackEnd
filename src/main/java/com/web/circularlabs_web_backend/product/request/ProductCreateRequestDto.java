package com.web.circularlabs_web_backend.product.request;

import lombok.Getter;

@Getter
public class ProductCreateRequestDto {

    private String productCode; //제품 분류 코드
    private String productName; // 제품 명
    private int purchasePrice; // 단가
    private String status; //상태
    private int productQtt; // 제품 구매 단위
}
