package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ProcessDiscardHistoryListResponseDto {

    private List<ProcessDiscardHistoryEachResponseDto> discardhistoryList;
    private Long paging;
}
