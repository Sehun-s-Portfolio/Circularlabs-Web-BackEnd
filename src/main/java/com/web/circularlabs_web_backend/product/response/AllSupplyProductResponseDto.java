package com.web.circularlabs_web_backend.product.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AllSupplyProductResponseDto {
    private Long supplyProductId; // 공급사 제품 인덱스 아이디
    private String productCode; // 제품 코드
    private String productName; // 재품명
    private int purchasePrice; // 단가
    private int rentalPrice; // 렌탈 비용
    private int maximumOrderMount; // 최대 주문량
    private String status; // 상태
    private int productQtt; // 제품 구매 단위
    private String createdAt; // 등록 일시
}
