package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SupplierDashboardV2CO2ResponseDto {
    private Integer totalOrderCount; // 전체 주문수량
    private Integer nowMonthReturnCount; // 당월 주문수량
    private double totalCo2Count; // 전체 주문수량
    private double nowMonthCo2Count; // 당월 주문수량

}
