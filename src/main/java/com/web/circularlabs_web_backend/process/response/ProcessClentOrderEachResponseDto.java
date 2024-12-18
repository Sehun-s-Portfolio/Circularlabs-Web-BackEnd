package com.web.circularlabs_web_backend.process.response;

import com.web.circularlabs_web_backend.member.response.MemberSupplierEachResponseDto;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class ProcessClentOrderEachResponseDto {

    private Long clientOrderId; // 인덱스
    private String motherCode; // 상위 코드
    private String classificationCode; // 고객사 본인 코드
    private String productCode; // 제품 분류 코드
    private int orderMount; // 주문 요청량
    private String status; //주문 상태
    private String createdAt; //등록 날짜
    private String modifiedYN; // 수정 유무
    private LocalDateTime hopeDeliveryAt; // 희망 수령 일자
    private String statementnumber; // 전표번호
}
