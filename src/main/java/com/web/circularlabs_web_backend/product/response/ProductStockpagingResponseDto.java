package com.web.circularlabs_web_backend.product.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ProductStockpagingResponseDto {
    private List<ProductStockEachResponseDto> productStockList;
    private Long paging;
}
