package com.web.circularlabs_web_backend.member.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberRegistResponseDto {
    private int grade; // 등급 (0-관리사 / 1-공급사 / 2-고객사)
    private String motherCode; // 상위 코드 - (1) 공급사 가입의 경우 관리사 코드가 들어감 / (2) 고객사 가입의 경우 공급사 코드가 들어감
    private String classificationCode; // 본인 코드 - (1) 공급사 가입의 경우 공급사 본인의 코드가 들어감 / (2) 고객사 가입의 경우 고객사 본인의 코드가 들어감
    private String loginId; // 아이디
    private String clientCompany; // 상호명 - (1) 공급사의 경우 공급사 본인의 상호명 / (2) 고객사의 경우 고객사 본인의 상호명
    private String address; // 주소
    private String represent; // 대표자
    private String businessNumber; // 사업자 번호
    private String stateOfBusiness; // 업태
    private String categoryBusiness; // 업종
    private String bankName; // 은행
    private String bankAccountNumber; // 계좌 번호
    private String bankAccountName; // 계좌 명
    private String manager; // 담당자
    private String phone; // 연락처
    private String position; // 직급
    private String email; // 이메일
    private String custType; // 고객사 타입
}
