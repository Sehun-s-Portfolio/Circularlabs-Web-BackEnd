package com.web.circularlabs_web_backend.inquiry.request;

import lombok.Getter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
public class InquiryClientRequestDto {

    private String motherCode; // 공급사 코드
    private String classificationCode; // 고객사 코드
    private String title; // 문의 제목
    private String content; // 문의 내용


}
