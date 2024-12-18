package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SupplierDashboardLiveDataResponseDto {
    private Integer coOpClientCount; // 공급사와 함께 하는 고객사 수
    private Integer useTotalProductCount; // 운용 중인 전체 다회 용기 수량
    private Integer noTurnBackTotalProductCount; // 미회수 다회 용기 용기 수량
}
