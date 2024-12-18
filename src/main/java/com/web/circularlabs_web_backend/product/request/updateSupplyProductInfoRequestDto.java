package com.web.circularlabs_web_backend.product.request;

import lombok.Getter;

@Getter
public class updateSupplyProductInfoRequestDto {
    private String supplierClassificationCode; // 공급사 코드
    private Long supplyProductId; // 공급사 제품 인덱스 아이디
    private String productName; // 공급사 제품명
    private String productCode; // 공급사 분류 코드
    private int rentalPrice; // 수정할 렌탈 비용
    private int maximumOrderMount; // 수정할 최대 주문 수량
}
