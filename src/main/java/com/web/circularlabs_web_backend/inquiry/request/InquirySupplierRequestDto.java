package com.web.circularlabs_web_backend.inquiry.request;

import lombok.Getter;

@Getter
public class InquirySupplierRequestDto {

    private String classificationCode; // 공급사 코드
    private String title; // 문의 제목
    private String content; // 문의 내용


}
