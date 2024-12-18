package com.web.circularlabs_web_backend.inquiry.response;

import lombok.Builder;
import lombok.Getter;
@Builder
@Getter
public class InquiryClientResponseDto {

    private String motherCode; // 공급사 코드
    private String classificationCode; // 고객사 코드
    private String title; // 문의 제목
    private String content; // 문의 내용


}
