package com.web.circularlabs_web_backend.product.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ProductScanHistoryResponseDto {
    private Long productDetailId;
    private String productCode;
    private String productName;
    private String productSerialCode;
    private String status;
    private int cycle;
    private LocalDateTime latestReadingAt;
}
