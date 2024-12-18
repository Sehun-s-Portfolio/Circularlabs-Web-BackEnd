package com.web.circularlabs_web_backend.inquiry.response;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Builder
@Getter
public class InquirySupplierEachResponseDto {

    private String classificationCode; // 공급사 코드
    private String title; // 주문 문의 제목
    private String content; // 주문 문의 내용
    private String createdAt; //가입 날짜
}
