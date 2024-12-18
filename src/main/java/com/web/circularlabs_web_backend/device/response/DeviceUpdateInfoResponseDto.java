package com.web.circularlabs_web_backend.device.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeviceUpdateInfoResponseDto {
    private Long deviceId;
    private String supplierCode;
    private String deviceCode;
    private String manager;
    private String phone;
    private String email;
}
