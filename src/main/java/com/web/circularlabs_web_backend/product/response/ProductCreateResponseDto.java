package com.web.circularlabs_web_backend.product.response;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;

@Builder
@Getter
public class ProductCreateResponseDto {
    private String productCode; // 제품 고유 코드 (관리사 계정의 요청값을 입력받는 것이 아니라 백엔드 로직 상에서 제품 고유코드 값을 자동으로 부여)
    private String productName; // 제품 명
    private int purchasePrice; // 단가
    private String productImgName; // 제품 이미지 명
    private String productImgUrl; // 제품 이미지 호출 url
    private String status; // 제품 상태
}
