package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SupplierDashboardv2BarResponseDto {
    private int orderCount; // 주문수량
    private int timeOrderCount; // 기간 주문수량
    private int returnCount; // 회수수량
}
