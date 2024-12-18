package com.web.circularlabs_web_backend.process.response;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Builder
@Getter
public class ProcessDiscardHistoryEachResponseDto {

    private Long discardHistoryId; // 인덱스
    private String supplierCode; // 공급사 코드
    private String clientCode; // 고객사 코드
    private String productCode; // 제품 분류 코드
    private String productSerialCode; // 각 제품 고유 제품 코드
    private String rfidChipCode; // RFID 칩 코드
    private LocalDateTime discardAt; // 폐기 일시
    private String reason; // 폐기 사유
    private String createdAt; //등록 날짜
}
