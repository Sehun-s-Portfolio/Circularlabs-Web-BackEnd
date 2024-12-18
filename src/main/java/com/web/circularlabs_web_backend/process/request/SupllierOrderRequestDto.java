package com.web.circularlabs_web_backend.process.request;

import lombok.Getter;

import java.util.List;

@Getter
public class SupllierOrderRequestDto {
    private String supplierClassificationCode; // 본인 코드 - (1) 공급사 가입의 경우 공급사 본인의 코드가 들어감 / (2) 고객사 가입의 경우 고객사 본인의 코드가 들어감
    private List<SupplierProductListRequestDto> orderSupplyProducts; // 구입 요청 공급사 제품들 정보 리스트
}
