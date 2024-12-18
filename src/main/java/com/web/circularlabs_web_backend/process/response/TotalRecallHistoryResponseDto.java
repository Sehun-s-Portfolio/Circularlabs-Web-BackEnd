package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class TotalRecallHistoryResponseDto {
    private Long totalCount; // 반품 이력 총 수량
    private List<RecallHistoryResponseDto> recallHistory; // 반품 이력
}
