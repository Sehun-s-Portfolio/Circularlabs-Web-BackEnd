package com.web.circularlabs_web_backend.device.request;

import lombok.Getter;

@Getter
public class DeviceCreateRequestDto {
    private String deviceId;
    private String supplierCode;
    private String deviceType;
    private String deviceCode;
    private String manager;
    private String phone;
    private String email;
}
