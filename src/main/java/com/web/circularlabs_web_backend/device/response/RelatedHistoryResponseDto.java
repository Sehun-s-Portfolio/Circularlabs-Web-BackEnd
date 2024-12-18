package com.web.circularlabs_web_backend.device.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RelatedHistoryResponseDto {
    private String productCode;
    private String productSerialCode;
}
