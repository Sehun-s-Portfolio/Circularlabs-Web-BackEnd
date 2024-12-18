package com.web.circularlabs_web_backend.process.request;

import lombok.Getter;

@Getter
public class ClientProductListRequestDto {

    private String productCode; // 제품 분류 코드
    private int orderMount; // 주문 수량
}
