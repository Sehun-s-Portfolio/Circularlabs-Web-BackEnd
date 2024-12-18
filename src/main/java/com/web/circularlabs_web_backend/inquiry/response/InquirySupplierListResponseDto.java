package com.web.circularlabs_web_backend.inquiry.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class InquirySupplierListResponseDto {
    private List<InquirySupplierEachResponseDto> supplierInauiryList;
    private Long paging;
}
