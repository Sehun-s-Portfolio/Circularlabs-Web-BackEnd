package com.web.circularlabs_web_backend.member.response;

import com.web.circularlabs_web_backend.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MemberSupplierListResponseDto {
    private List<MemberSupplierEachResponseDto> supplierList;
    private Long paging;
}
