package com.web.circularlabs_web_backend.product.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class TotalProductScanDetailHistoryResponseDto {
    private Long totalCount; // 심화 상세 이력 리스트 총 개수
    private String productCode; // 제품 분류 코드
    private String productName; // 제품명
    private String productSerialCode; // 각 제품 시리얼 코드
    private int cycle; // 사이클
    private List<ProductScanDetailHistoryResponseDto> productScanDetailHistory; // 심화 상세 이력 리스트
}
