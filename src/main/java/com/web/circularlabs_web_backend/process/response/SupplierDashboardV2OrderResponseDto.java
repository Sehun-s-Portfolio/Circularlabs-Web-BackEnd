package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SupplierDashboardV2OrderResponseDto {
    private Integer totalOrderCount; // 전체 주문수량
    private Integer totalReturnCount; // 전체 회수량

}
