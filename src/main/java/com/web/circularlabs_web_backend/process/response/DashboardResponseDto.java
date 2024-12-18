package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class DashboardResponseDto {
    private DashboardLiveDataResponseDto dashboardLiveData; // 실시간 데이터 현황
    private DashboardMonthDataResponseDto dashboardMonthData; // 현월 데이터 현황
    private DashboardSaveEarthDataResponseDto dashboardSaveEarthData; // 지구를 지키는 지표
    private List<DashboardDailyPurchaseResponseDto> dashboardDailyPurchaseData; // 일일 구입 요청량 정보
}
