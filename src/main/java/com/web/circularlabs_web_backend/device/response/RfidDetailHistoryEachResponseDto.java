package com.web.circularlabs_web_backend.device.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class RfidDetailHistoryEachResponseDto {

    private String rfidChipCode; // RFID 칩 코드
    private String productSerialCode; // 각 제품 고유 코드
    private String productCode; // 제품 분류 코드
    private String supplierCode; // 공급사 코드
    private String clientCode; // 고객사 코드
    private String status; // 상태
    private int cycle; // 사이클
    private LocalDateTime latestReadingAt; // 마지막 리딩 시간
}

