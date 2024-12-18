package com.web.circularlabs_web_backend.inquiry.response;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Builder
@Getter
public class InquiryClientEachResponseDto {

    private Long clientInquiryId; // 인덱스
    private String motherCode; // 공급사 코드
    private String classificationCode; // 고객사 코드
    private String title; // 문의 제목
    private String content; // 문의 내용
    private String answerContent; // 답변 내용
    private String answerStatus; // 답변 상태 유무
    private LocalDateTime answerAt; // 답변 일자
    private String createdAt; //가입 날짜

}
