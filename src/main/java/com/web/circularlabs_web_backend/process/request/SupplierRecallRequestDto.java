package com.web.circularlabs_web_backend.process.request;

import lombok.Getter;

import java.util.List;

@Getter
public class SupplierRecallRequestDto {
    private String supplierClassificationCode; // 공급사 구분 코드
    private List<RecallProductRequestDto> recallProducts; // 반품 제품들 리스트
}
