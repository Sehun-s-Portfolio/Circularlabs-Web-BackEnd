package com.web.circularlabs_web_backend.process.request;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ClientOrderRequestDto {
    private String clientClassificationCode; // 본인 코드 - (1) 공급사 가입의 경우 공급사 본인의 코드가 들어감 / (2) 고객사 가입의 경우 고객사 본인의 코드가 들어감
    private String motherCode;
    private String hopeDeliveryAt; // 희망 수령 일자
    private List<ClientProductListRequestDto> orderClientProducts; // 구입 요청 공급사 제품들 정보 리스트
}
