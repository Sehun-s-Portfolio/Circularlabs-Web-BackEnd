package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DashboardSaveEarthDataResponseDto {
    private Integer reduceDisposableProductCount; // 일회용기 감소량 (누적)
    private Integer reduceCarbonCount; // 탄소 저감량
}
