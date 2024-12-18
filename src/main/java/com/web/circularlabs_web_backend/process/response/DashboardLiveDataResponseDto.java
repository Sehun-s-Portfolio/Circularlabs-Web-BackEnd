package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DashboardLiveDataResponseDto {
    private Integer coOpSupplierCount; // 써큘러랩스와 함께 하는 세척장 (공급사) 수
    private Integer useTotalProductCount; // 운용 중인 전체 다회 용기 수량
    private Integer discardTotalProductCount; // 폐기된 전체 다회 용기 수량
}
