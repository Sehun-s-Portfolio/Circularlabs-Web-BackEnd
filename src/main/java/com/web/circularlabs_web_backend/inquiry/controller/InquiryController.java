package com.web.circularlabs_web_backend.inquiry.controller;

import com.web.circularlabs_web_backend.device.request.DeviceCreateRequestDto;
import com.web.circularlabs_web_backend.device.request.DeviceUpdateInfoRequestDto;
import com.web.circularlabs_web_backend.device.service.DeviceService;
import com.web.circularlabs_web_backend.inquiry.request.InquiryClientRequestDto;
import com.web.circularlabs_web_backend.inquiry.request.InquiryFaqRequestDto;
import com.web.circularlabs_web_backend.inquiry.request.InquirySupplierRequestDto;
import com.web.circularlabs_web_backend.inquiry.service.InquiryService;
import com.web.circularlabs_web_backend.share.ResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cl/inquiry")
@RestController
public class InquiryController {

    private final InquiryService inquiryService;

    // FAQ 리스트 조회 api
    @GetMapping("/faqlist")
    public ResponseEntity<ResponseBody> getfaqList(@RequestParam String motherCode){
        log.info("faq 리스트 조회  api - controller");
        log.info("조회 하고자 하는 공급사 코드 : {}", motherCode);

        return inquiryService.getfaqList(motherCode);
    }
    // FAQ 리스트 조회 api
    @PostMapping("/faqinsert")
    public ResponseEntity<ResponseBody> faqInsert(@RequestBody InquiryFaqRequestDto inquiryFaqRequestDto){
        log.info("faq 리스트 조회  api - controller");
        log.info("조회 하고자 하는 공급사 코드 : {}", inquiryFaqRequestDto.getClassificationCode());

        return inquiryService.faqInsert(inquiryFaqRequestDto);
    }
    // FAQ 수정 api
    @PutMapping("/faqupdate")
    public ResponseEntity<ResponseBody> putfaqUpdate(@RequestBody InquiryFaqRequestDto inquiryFaqRequestDto){
        log.info("faq 리스트 조회  api - controller");
        log.info("조회 하고자 하는 공급사 코드 : {}", inquiryFaqRequestDto.getClassificationCode());

        return inquiryService.putfaqUpdate(inquiryFaqRequestDto);
    }
    // 고객사 문의 리스트 요철 api
    @GetMapping("/client/inquirylist")
    public ResponseEntity<ResponseBody> clientInquiryList(@RequestParam String classificationCode, @RequestParam int page){
        log.info("고객사 문의 리스트 조회  api - controller");
        log.info("문의 내역을 조회 하고자 하는 고객사 코드 : {}", classificationCode);

        return inquiryService.getclientInquiryList(classificationCode, page);
    }

    // 고객사 문의 요청 api
    @PostMapping("/client/inquiry")
    public ResponseEntity<ResponseBody> clientInquiry(@RequestBody InquiryClientRequestDto inquiryClientRequestDto){
        log.info("고객사 문의 요청  api - controller");
        log.info("문의 하고자 하는 공급사 코드 : {} - 고객사 코드 : {}", inquiryClientRequestDto.getMotherCode(), inquiryClientRequestDto.getClassificationCode());

        return inquiryService.clientInquiry(inquiryClientRequestDto);
    }
    // 공급사 문의 리스트 요청 api
    @GetMapping("/supplier/inquirylist")
    public ResponseEntity<ResponseBody> supplierInquiryList(@RequestParam String classificationCode, @RequestParam int page){
        log.info("공급사 문의 리스트 조회  api - controller");
        log.info("문의 내역을 조회 하고자 하는 공급사 코드 : {}", classificationCode);

        return inquiryService.getSupplierInquiryList(classificationCode, page);
    }
    // 공급사 문의 답변 수정 api
    @PutMapping("/supplier/inquiryupdate")
    public ResponseEntity<ResponseBody> putsupplierInquiryUpdate(@RequestParam String clientInquiryId, @RequestParam String answerContent){
        log.info("공급사 문의 답변 수정  api - controller");

        return inquiryService.putsupplierInquiryUpdate(clientInquiryId, answerContent);
    }
    // 관리사 문의 요청 api
    @PostMapping("/admin/inquiry")
    public ResponseEntity<ResponseBody> adminInquiry(@RequestBody InquirySupplierRequestDto inquirySupplierRequestDto){
        log.info("고객사 문의 요청  api - controller");
        log.info("문의 하고자 하는 공급사 코드 : {}", inquirySupplierRequestDto.getClassificationCode());

        return inquiryService.supplierInquiry(inquirySupplierRequestDto);
    }
    // 관리사 문의 요청 api
    @GetMapping("/admin/inquirylist")
    public ResponseEntity<ResponseBody> adminInquiryList(@RequestParam int page){
        log.info("고객사 문의 요청  api - controller");

        return inquiryService.adminInquiryList(page);
    }
}
