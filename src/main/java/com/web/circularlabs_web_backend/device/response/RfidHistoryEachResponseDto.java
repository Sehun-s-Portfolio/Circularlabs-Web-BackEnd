package com.web.circularlabs_web_backend.device.response;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Builder
@Getter
public class RfidHistoryEachResponseDto {

    private Long rfidScanhistoryId; // 인덱스
    private String deviceCode; // 기기 코드
    private String rfidChipCode; // RFID 기기 코드
    private String productCode; // 제품 분류 코드
    private String supplierCode; // 공급사 코드
    private String clientCode; // 고객사 코드
    private String status; // 상태
    private int statusCount; // 작업 수량
    private int flowRemainQuantity; // 유동 재고 수량
    private int noReturnQuantity; // 미회수 수량
    private int totalRemainQuantity; // 총 재고 수량
    private int cycle; // 사이클
    private LocalDateTime latestReadingAt; // 마지막 리딩 시간
}

