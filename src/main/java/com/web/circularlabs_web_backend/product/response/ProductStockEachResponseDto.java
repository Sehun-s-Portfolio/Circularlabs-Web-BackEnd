package com.web.circularlabs_web_backend.product.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductStockEachResponseDto {
    private String productCode; // 제품 분류 코드
    private int sum; // 제품 재고합
}
