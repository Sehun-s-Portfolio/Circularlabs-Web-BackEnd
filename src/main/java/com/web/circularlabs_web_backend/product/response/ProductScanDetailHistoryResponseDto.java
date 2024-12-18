package com.web.circularlabs_web_backend.product.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ProductScanDetailHistoryResponseDto {
    private Long productDetailHistoryId; // 제품 스캔 심화 상세 이력 인덱스 아이디
    private String clientCode; // 고객사 코드
    private String clientName; // 고객사 상호명
    private String status; // 상태
    private LocalDateTime latestReadingAt; // 최근 리딩 시간
}
