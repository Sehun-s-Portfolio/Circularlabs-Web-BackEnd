package com.web.circularlabs_web_backend.member.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MemberClientListResponseDto {
    private List<MemberSupplierEachResponseDto> clientList;
    private Long paging;
}
