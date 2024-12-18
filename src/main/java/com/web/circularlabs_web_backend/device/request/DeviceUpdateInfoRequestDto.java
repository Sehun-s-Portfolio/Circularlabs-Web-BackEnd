package com.web.circularlabs_web_backend.device.request;

import lombok.Getter;

@Getter
public class DeviceUpdateInfoRequestDto {
    private String supplierCode;
    private String deviceCode;
    private String manager;
    private String phone;
    private String email;
}
