package com.web.circularlabs_web_backend.share;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCode {

    // success response
    OK("정상 수행", "C-200"),

    // bad response
    NOT_RIGHT_REGISTER_INFO("입력한 회원가입 정보가 옳바르지 않습니다.", "C-401"),
    NOT_CORRECT_ORDER_MOUNT("주문 요청하고자 하는 제품의 수량이 존재하지 않습니다.", "C-402"),
    NOT_EXIST_DIFFERENCE("기입한 수정 렌탈 비용과 수정 최대 주문 수량에 변경점이 없어 수정할 수 없습니다.", "C-403"),
    CANT_RECALL("반품 처리를 진행할 수 없습니다. \n" +
            "(1) 반품 제품들의 요청 반품 수량이 전부 0일 경우 \n" +
            "(2) 요청 반품 수량이 현재 설정된 최대 주문량을 초과하거나 현재 재고량을 초과할 경우 \n" +
            "(3) 반품 하고자 하는 제품들이 존재하지 않을 경우", "C-404");

    private final String message;
    private final String code;
}
