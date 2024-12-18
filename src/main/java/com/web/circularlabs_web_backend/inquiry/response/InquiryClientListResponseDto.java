package com.web.circularlabs_web_backend.inquiry.response;

import com.web.circularlabs_web_backend.member.response.MemberSupplierEachResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class InquiryClientListResponseDto {
    private List<InquiryClientEachResponseDto> clientInauiryList;
    private Long paging;
}
