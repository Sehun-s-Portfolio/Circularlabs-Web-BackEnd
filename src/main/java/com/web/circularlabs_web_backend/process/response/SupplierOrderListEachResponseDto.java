package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Builder
@Getter
public class SupplierOrderListEachResponseDto {

    private Long orderId; // 인덱스
    private String classificationCode; // 공급사 구분 코드
    private String productCode; // 제품 분류 코드
    private int orderMount; // 주문 요청량
    private LocalDateTime deliveryAt; // 수령 일자
    private String statementnumber; // 전표번호
    private String createdAt; //등록 날짜
}
