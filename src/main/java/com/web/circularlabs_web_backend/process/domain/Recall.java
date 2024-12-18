package com.web.circularlabs_web_backend.process.domain;

import com.web.circularlabs_web_backend.share.TimeStamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Recall extends TimeStamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long recallId; // 인덱스

    @Column(nullable = false)
    private String classificationCode; // 공급사 구분 코드

    @Column(nullable = false)
    private String productCode; // 제품 분류 코드

    @Column(nullable = false)
    private String productName; // 제품명

    @Column(nullable = false)
    private int recallMount; // 회수(반품) 요청량

    @Column
    private LocalDateTime possibleRecallAt; // 회수(반품) 가능 일자
}
