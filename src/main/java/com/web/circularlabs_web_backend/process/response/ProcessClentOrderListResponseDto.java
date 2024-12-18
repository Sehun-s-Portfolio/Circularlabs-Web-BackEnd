package com.web.circularlabs_web_backend.process.response;

import com.web.circularlabs_web_backend.member.response.MemberSupplierEachResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ProcessClentOrderListResponseDto {

    private List<ProcessClentOrderEachResponseDto> clientorderList;
    private Long paging;
}
