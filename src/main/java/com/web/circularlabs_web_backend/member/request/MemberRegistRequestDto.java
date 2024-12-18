package com.web.circularlabs_web_backend.member.request;

import com.web.circularlabs_web_backend.product.domain.Product;
import lombok.Getter;

import java.util.List;

@Getter
public class   MemberRegistRequestDto {
    private int grade; // 등급 (0-관리사 / 1-공급사 / 2-고객사)
    private String motherCode; // 상위 코드 - (1) 공급사 가입의 경우 관리사 코드가 들어감 / (2) 고객사 가입의 경우 공급사 코드가 들어감
    private String classificationCode; // 본인 코드 - (1) 공급사 가입의 경우 공급사 본인의 코드가 들어감 / (2) 고객사 가입의 경우 고객사 본인의 코드가 들어감
    private String loginId; // 아이디
    private String password; // 패스워드
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
    private String etc;
    private List<String> product; // 제품 설정
    private String custType;
}
