package com.web.circularlabs_web_backend.member.response;

import com.web.circularlabs_web_backend.product.response.ClientProductResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MemberClientOfSupplierDetailInfoListResponseDto {
    private List<MemberClientsOfSupplierResponseDto> clientList;
    private Long paging;
}
