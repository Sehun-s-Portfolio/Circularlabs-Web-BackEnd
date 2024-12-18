package com.web.circularlabs_web_backend.member.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberClientsOfSupplierResponseDto {
    private Long clientIndexId; // 고객사 인덱스 아이디
    private String clientClassificationCode; // 고객사 구분 코드
    private String motherCode; // 상위 소속 코드
    private String loginId; // 고객사 로그인 아이디
    private String clientCompany; // 고객사 상호명
    private String manager; // 고객사 담당자
    private String status; // 고객사 상태
    private String createdAt; // 고객사 계약 일자
    private String withdrawAt; // 고객사 탈퇴 일자
    private String etc; // 비고
}
