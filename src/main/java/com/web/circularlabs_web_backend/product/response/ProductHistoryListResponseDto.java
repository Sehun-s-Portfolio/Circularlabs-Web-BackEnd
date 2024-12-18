package com.web.circularlabs_web_backend.product.response;

import com.web.circularlabs_web_backend.product.domain.ProductDetailHistory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ProductHistoryListResponseDto {
    private List<ProductScanHistoryResponseDto> productHisList;
    private Long paging;
}
