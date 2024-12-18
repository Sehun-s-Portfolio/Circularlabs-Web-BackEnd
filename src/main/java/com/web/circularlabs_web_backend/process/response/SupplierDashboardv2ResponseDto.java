package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SupplierDashboardv2ResponseDto {
    private SupplierDashboardV2OrderResponseDto supplierTotal; // 전체기간 데이터
    private int maxCycle; // 최대사용 다회용기
    private SupplierDashboardV2CO2ResponseDto supplierCO2Total; // 전체,당월 주문량
    private List<SupplierDashboardV2CO2MonthResponseDto> supplierCO2Month; // 월별 부문량 (현재 ~ 현재 -12)
    private String firstOrder;
}
