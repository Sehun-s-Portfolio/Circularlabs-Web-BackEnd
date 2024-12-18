package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SupplierDashboardLiveRemainResponseDto {
    private String productName; // 제품 명
    private Integer productRemainCount; // 제품 재고 수량
}
