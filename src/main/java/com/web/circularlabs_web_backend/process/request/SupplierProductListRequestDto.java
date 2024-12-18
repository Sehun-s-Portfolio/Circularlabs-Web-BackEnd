package com.web.circularlabs_web_backend.process.request;

import lombok.Getter;

@Getter
public class SupplierProductListRequestDto {
    private Long supplyProductId; // 공급사 제품 인덱스 아이디
    private String supplyProductName; // 제품명
    private String productCode; // 제품 분류 코드
    private int orderMount; // 주문 수량
}
