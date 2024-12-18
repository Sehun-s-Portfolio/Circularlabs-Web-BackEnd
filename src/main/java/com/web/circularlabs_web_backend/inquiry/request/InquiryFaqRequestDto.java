package com.web.circularlabs_web_backend.inquiry.request;

import lombok.Getter;

import javax.persistence.Column;

@Getter
public class InquiryFaqRequestDto {

    private Long faqId; // 인덱스
    private String classificationCode; // 공급사 코드
    private String question; // faq 질문 내용
    private String answer; // faq 질문 답변 내용

}
