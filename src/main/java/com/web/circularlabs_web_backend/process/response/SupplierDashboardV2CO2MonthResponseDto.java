package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SupplierDashboardV2CO2MonthResponseDto {
    private String yearandMonth; // 년월
    private int ordercount; // 당월 주문수량
    private double monthOnceCo2Count;

}
