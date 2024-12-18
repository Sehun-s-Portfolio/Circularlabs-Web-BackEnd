package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecallProductResponseDto {
    private String productName; // 반품 제품명
    private String productCode; // 반품 제품 코드
    private int recallMount; // 반품 수량
}
