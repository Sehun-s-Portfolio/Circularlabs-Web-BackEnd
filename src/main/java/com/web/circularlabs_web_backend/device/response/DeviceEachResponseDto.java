package com.web.circularlabs_web_backend.device.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeviceEachResponseDto {

    private Long deviceId; // 인덱스
    private String supplierCode; // 기기를 소유할 공급사 본인 코드
    private String deviceType; // 리더기 종류
    private String deviceCode; // 리더기 코드
    private String manager; // 담당자
    private String phone; // 연락처
    private String email; // 이메일
    private String createdAt; //가입 날짜
}

