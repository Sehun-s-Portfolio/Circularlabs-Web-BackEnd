package com.web.circularlabs_web_backend.member.controller;

import com.web.circularlabs_web_backend.member.request.MemberDetailRequestDto;
import com.web.circularlabs_web_backend.member.request.MemberInfoRequestDto;
import com.web.circularlabs_web_backend.member.request.MemberRegistRequestDto;
import com.web.circularlabs_web_backend.member.request.MemberUpdateClientDetailInfoRequestDto;
import com.web.circularlabs_web_backend.member.service.MemberService;
import com.web.circularlabs_web_backend.share.ResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cl/member")
@RestController
public class MemberController {

    private final MemberService memberService;

    //회원 로그인
    @PostMapping("/rogin")
    public ResponseEntity<ResponseBody> memberRogin(@RequestParam String loginId, @RequestParam String password){
        log.info("로그인 api - controller");
        log.info("입력 ID : {} 입력 PASSWORD : {}", loginId,password);

        return memberService.memberRogin(loginId, password);
    }

    //ID중복검사
    @GetMapping("/register/checkId")
    public ResponseEntity<ResponseBody> memberRegistcheckId(@RequestParam String RegistId){
        log.info("ID중복검사 api - controller");

        return memberService.memberRegistcheckId(RegistId);
    }

    // 회원 가입 api
    @PostMapping("/register")
    public ResponseEntity<ResponseBody> memberRegist(@RequestBody MemberRegistRequestDto memberRegistRequestDto){
        log.info("회원 가입 api - controller");
        log.info("본인 코드 : {}", memberRegistRequestDto.getClassificationCode());

        return memberService.memberRegist(memberRegistRequestDto);
    }
    // 내정보 수정 api
    @PutMapping("/memberinfoupdate")
    public ResponseEntity<ResponseBody> memberInfoUpdate(@RequestBody MemberInfoRequestDto MemberInfoRequestDto){
        log.info("내정보 조회 api - controller");
        log.info("본인 상호 : {}", MemberInfoRequestDto.getClientCompany());

        return memberService.memberInfoUpdate(MemberInfoRequestDto);
    }
    // 회원탈퇴 api
    @PutMapping("/withDrawal")
    public ResponseEntity<ResponseBody> memberwithDrawal(@RequestParam String classificationCode){
        log.info("회원탈퇴 api - controller");
        log.info("탈퇴 멤버 코드 : {}", classificationCode);

        return memberService.memberwithDrawal(classificationCode);
    }
    // 비밀번호 재설정
    @PutMapping("/passwordreset")
    public ResponseEntity<ResponseBody> passwordreset(@RequestParam String classificationCode, @RequestParam String newpassword){
        log.info("비밀번호 재설정 api - controller{}", classificationCode);

        return memberService.passwordreset(classificationCode, newpassword);
    }
    // 내정보 조회 api
    @GetMapping("/memberinfo")
    public ResponseEntity<ResponseBody> memberInfo(@RequestParam String classificationCode){
        log.info("내정보 수정 api - controller");

        return memberService.memberInfo(classificationCode);
    }
    // 공급사 리스트 api
    @GetMapping("/supplierList")
    public ResponseEntity<ResponseBody> memberSupplierList(@RequestParam String searchType, @RequestParam String searchWord, @RequestParam int page){
        log.info("공급사 리스트 api - controller");
        log.info("검색타입 - {} 검색어 - {}", searchType, searchWord);

        return memberService.memberSupplierList(searchType, searchWord, page);
    }
    // 공급사 리스트 api
    @GetMapping("/supplierAllList")
    public ResponseEntity<ResponseBody> memberSupplierAllList(){
        log.info("공급사 전체 리스트 api - controller");

        return memberService.memberSupplierAllList();
    }
    // 고객사 리스트 api
    @GetMapping("/clientAllList")
    public ResponseEntity<ResponseBody> memberClientAllList(){
        log.info("공급사 전체 리스트 api - controller");

        return memberService.memberClientAllList();
    }
    // 공급사 상세정보 api
    @GetMapping("/supplierDetail")
    public ResponseEntity<ResponseBody> memberSupplierDetail(@RequestParam String classificationCode){
        log.info("공급사 상세정보 api - controller");
        log.info("공급사 코드 : {}", classificationCode);

        return memberService.memberSupplierDetail(classificationCode);
    }
    // 공급사 정보 수정 api
    @PutMapping("/supplierUpdate")
    public ResponseEntity<ResponseBody> memberSupplierUpdate(@RequestBody MemberDetailRequestDto memberDetailRequestDto){
        log.info("공급사 수정 api - controller");
        log.info("수정데이터 정보 : {}", memberDetailRequestDto);

        return memberService.memberSupplierUpdate(memberDetailRequestDto);
    }
    // 고객사 리스트 api
    @GetMapping("/clientList")
    public ResponseEntity<ResponseBody> memberClientList(@RequestParam String searchType, @RequestParam String searchWord, @RequestParam int page){
        log.info("고객사 리스트 api - controller");
        log.info("검색타입 - {} 검색어 - {}", searchType, searchWord);

        return memberService.memberClientList(searchType, searchWord, page);
    }
    // 고객사 상세정보 api
    @GetMapping("/clientDetail")
    public ResponseEntity<ResponseBody> memberClientDetail(@RequestParam String classificationCode, @RequestParam String motherCode){
        log.info("고객사 상세정보 api - controller");
        log.info("고객사 코드 : {}", classificationCode);

        return memberService.memberClientDetail(classificationCode, motherCode);
    }
    // 고객사 정보 수정 api
    @PutMapping("/clientUpdate")
    public ResponseEntity<ResponseBody> memberClientUpdate(@RequestBody MemberDetailRequestDto memberDetailRequestDto){
        log.info("고객사 수정 api - controller");
        log.info("수정데이터 정보 : {}", memberDetailRequestDto);

        return memberService.memberClientUpdate(memberDetailRequestDto);
    }

    // 고객사 리스트 api
    @GetMapping("/clientAllList/sc")
    public ResponseEntity<ResponseBody> memberClientScAllList(@RequestParam String classificationCode){
        log.info("공급사 전체 리스트 api - controller");

        return memberService.memberClientScAllList(classificationCode);
    }
    // 공급사에 속한 고객사 리스트 조회 api
    @GetMapping("/client/list/sc/{supplierCode}")
    public ResponseEntity<ResponseBody> getClientsOfSupplier(
            @PathVariable String supplierCode,
            @RequestParam int page,
            @RequestParam String searchType,
            @RequestParam(required = false) String searchWord){
        log.info("공급사에 속한 고객사 리스트 조회 api - controller");

        return memberService.getClientsOfSupplier(supplierCode, page, searchType, searchWord);
    }


    // 공급사에 속한 특정 고객사 상세 정보 조회 api
    @GetMapping("/client/sc/{supplierCode}")
    public ResponseEntity<ResponseBody> getClientDetailInfo(@PathVariable String supplierCode, @RequestParam String clientCode){
        log.info("공급사에 속한 특정 고객사 상세 정보 조회 api - controller");

        return memberService.getClientDetailInfo(supplierCode, clientCode);
    }


    // 공급사 측에서 특정 고객사 정보 수정 api
    @PutMapping("/client/update/sc/{supplierCode}/uc/{clientCode}")
    public ResponseEntity<ResponseBody> updateClientDetailInfo(
            @PathVariable String supplierCode,
            @PathVariable String clientCode,
            @RequestBody MemberUpdateClientDetailInfoRequestDto updateClientDetailInfoRequestDto){
        log.info("공급사 측에서 특정 고객사 정보 수정 api - controller");

        return memberService.updateClientDetailInfo(supplierCode, clientCode, updateClientDetailInfoRequestDto);
    }

}
