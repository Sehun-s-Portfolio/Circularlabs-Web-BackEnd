package com.web.circularlabs_web_backend.product.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ClientOrderProductListResponseDto {
    private Long clientProductId; // 고객사 제품 인덱스 아이디
    private String clientClassificationCode; // 고객사 구분 코드
    private Long supplyProductId; // 공급사 제품 인덱스 아이디
    private String supplierClassificationCode; // 공급사 구분 코드
    private String productName; // 제품 명
    private String productCode; // 제품 구분 코드
    private int purchasePrice; // 제품 원래 단가
    private int rentalPrice; // 렌탈 가격
    private int maximumOrderMount; // 최대 주문 수량
    private String productImgUrl; // 제품 이미지 호출 url
    private String status; // 제품 상태
    private int productQtt; // 제품 구매 단위
}
