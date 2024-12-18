package com.web.circularlabs_web_backend.inquiry.domain;

import com.web.circularlabs_web_backend.share.TimeStamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Faq extends TimeStamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long faqId; // 인덱스

    @Column(nullable = false)
    private String classificationCode; // 공급사 코드

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String question; // faq 질문 내용

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String answer; // faq 질문 답변 내용

}
