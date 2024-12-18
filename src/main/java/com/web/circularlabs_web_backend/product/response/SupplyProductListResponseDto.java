package com.web.circularlabs_web_backend.product.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SupplyProductListResponseDto {
    private Long supplyProductId;
    private String productCode;
    private String productName;
    private int rentalPrice; // 렌탈 가격
}
