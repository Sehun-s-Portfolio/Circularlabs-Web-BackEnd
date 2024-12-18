package com.web.circularlabs_web_backend.device.response;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RfidDetailProductDetailResponsePageDto {
    private List<RelatedHistoryResponseDto> rfidhisList;
    private int paging;
}
