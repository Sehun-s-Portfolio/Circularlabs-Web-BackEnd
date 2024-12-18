package com.web.circularlabs_web_backend.process.response;

import com.web.circularlabs_web_backend.process.request.RecallProductRequestDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SupplierRecallResponseDto {
    private String supplierClassificationCode; // 공급사 구분 코드
    private List<RecallProductResponseDto> recallProducts; // 반품 제품들 리스트
}
