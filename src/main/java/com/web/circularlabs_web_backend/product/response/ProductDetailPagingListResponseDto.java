package com.web.circularlabs_web_backend.product.response;

import com.web.circularlabs_web_backend.member.response.MemberSupplierEachResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ProductDetailPagingListResponseDto {
    private List<ProductDetailEachResponseDto> productDetailList;
    private Long paging;
}
