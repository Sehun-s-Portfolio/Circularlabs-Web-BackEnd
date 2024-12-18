package com.web.circularlabs_web_backend.device.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeviceCreateResponseDto {
    private String supplierCode;
    private String deviceType;
    private String deviceCode;
    private String manager;
    private String phone;
    private String email;
}
