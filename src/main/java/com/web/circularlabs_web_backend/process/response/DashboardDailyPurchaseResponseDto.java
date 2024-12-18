package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DashboardDailyPurchaseResponseDto {
    private String supplierName; // 일일 구입 요청 공급사 명
    private String productCode; // 일일 구입 요청 제품 코드
    private Integer productOrderMount; // 일일 구입 요청 제품 수량
}
