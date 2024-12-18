package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SupplierOrderProductListResponseDto {
    private String productCode; // 제품 분류 코드
    private int orderMount; // 주문 수량
    private String statementnumber; // 전표번호
}
