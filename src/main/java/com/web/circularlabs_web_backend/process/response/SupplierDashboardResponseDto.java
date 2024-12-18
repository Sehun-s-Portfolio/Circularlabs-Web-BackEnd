package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SupplierDashboardResponseDto {
    private SupplierDashboardLiveDataResponseDto supplierDashboardLiveData; // 실시간 데이터 현황
    private List<SupplierDashboardLiveRemainResponseDto> supplierDashboardLiveRemainDatas; // 실시간 재고 현황
}
