package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DashboardMonthDataResponseDto {
    private Integer newSupplierCount; // 현월 신규 공급사 수
    private Integer newSupplierOrderCount; // 현월 신규 발주량
}
