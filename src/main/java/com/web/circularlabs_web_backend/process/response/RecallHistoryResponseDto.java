package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecallHistoryResponseDto {
    private Long recallId; // 인덱스
    private String productCode; // 반품 제품 코드
    private String productName; // 반품 제품명
    private int recallMount; // 반품 수량
    private String recallRequestAt; // 반품 요청 일자
    private String possibleRecallReceiveAt; // 수령 후 반품 가능 일자
}
