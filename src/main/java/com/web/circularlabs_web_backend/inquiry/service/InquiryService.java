package com.web.circularlabs_web_backend.inquiry.service;

import com.web.circularlabs_web_backend.device.domain.Device;
import com.web.circularlabs_web_backend.device.repository.DeviceRepository;
import com.web.circularlabs_web_backend.device.request.DeviceCreateRequestDto;
import com.web.circularlabs_web_backend.device.request.DeviceUpdateInfoRequestDto;
import com.web.circularlabs_web_backend.device.response.DeviceCreateResponseDto;
import com.web.circularlabs_web_backend.inquiry.domain.ClientInquiry;
import com.web.circularlabs_web_backend.inquiry.domain.Faq;
import com.web.circularlabs_web_backend.inquiry.domain.OrderInquiry;
import com.web.circularlabs_web_backend.inquiry.repository.InquiryFaqRepository;
import com.web.circularlabs_web_backend.inquiry.repository.InquiryRepository;
import com.web.circularlabs_web_backend.inquiry.repository.OrderInquiryRepository;
import com.web.circularlabs_web_backend.inquiry.request.InquiryClientRequestDto;
import com.web.circularlabs_web_backend.inquiry.request.InquiryFaqRequestDto;
import com.web.circularlabs_web_backend.inquiry.request.InquirySupplierRequestDto;
import com.web.circularlabs_web_backend.inquiry.response.InquiryClientResponseDto;
import com.web.circularlabs_web_backend.inquiry.response.InquiryFaqListResponseDto;
import com.web.circularlabs_web_backend.inquiry.response.InquirySupplierEachResponseDto;
import com.web.circularlabs_web_backend.query.device.DeviceQueryData;
import com.web.circularlabs_web_backend.query.inquiry.InquiryQueryData;
import com.web.circularlabs_web_backend.share.ResponseBody;
import com.web.circularlabs_web_backend.share.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class InquiryService {


    private final InquiryQueryData inquiryQueryData;
    private final InquiryRepository inquiryRepository;
    private final InquiryFaqRepository inquiryFaqRepository;
    private final OrderInquiryRepository orderInquiryRepository;
    // 고객사 FAQ service
    public ResponseEntity<ResponseBody> getfaqList(String motherCode){
        log.info("고객사 FAQ 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, inquiryQueryData.getfaqList(motherCode)), HttpStatus.OK);
    }

    // 공급사 FAQ 등록 service
    public ResponseEntity<ResponseBody> faqInsert(InquiryFaqRequestDto inquiryFaqRequestDto){
        log.info("고객사 FAQ 등록 api - service");

        Faq createInquiry = Faq.builder()
                .answer(inquiryFaqRequestDto.getAnswer())
                .question(inquiryFaqRequestDto.getQuestion())
                .classificationCode(inquiryFaqRequestDto.getClassificationCode())
                .build();

        inquiryFaqRepository.save(createInquiry);

        InquiryFaqListResponseDto responseDto = InquiryFaqListResponseDto.builder()
                .faqId(createInquiry.getFaqId())
                .answer(createInquiry.getAnswer())
                .question(createInquiry.getQuestion())
                .classificationCode(createInquiry.getClassificationCode())
                .build();

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, responseDto), HttpStatus.OK);
    }

    // 고객사 FAQ service
    public ResponseEntity<ResponseBody> putfaqUpdate(InquiryFaqRequestDto inquiryFaqRequestDto){
        log.info("고객사 FAQ 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, inquiryQueryData.putfaqUpdate(inquiryFaqRequestDto)), HttpStatus.OK);
    }
    // 고객사 문의 내역 리스트 service (공급사)
    public ResponseEntity<ResponseBody> getclientInquiryList(String classificationCode, int page){
        log.info("고객사 문의 내역 조회 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, inquiryQueryData.getclientInquiryList(classificationCode, page)), HttpStatus.OK);
    }
    // 고객사 문의 등록 service
    public ResponseEntity<ResponseBody> clientInquiry(InquiryClientRequestDto inquiryClientRequestDto){
        log.info("고객사 문의 등록 api - service");
        ClientInquiry createInquiry = ClientInquiry.builder()
                .motherCode(inquiryClientRequestDto.getMotherCode())
                .classificationCode(inquiryClientRequestDto.getClassificationCode())
                .title(inquiryClientRequestDto.getTitle())
                .content(inquiryClientRequestDto.getContent())
                .answerStatus("N")
                .build();

        inquiryRepository.save(createInquiry);

        InquiryClientResponseDto responseDto = InquiryClientResponseDto.builder()
                .motherCode(createInquiry.getMotherCode())
                .classificationCode(createInquiry.getClassificationCode())
                .title(createInquiry.getTitle())
                .content(createInquiry.getContent())
                .build();

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, responseDto), HttpStatus.OK);
    }
    // 공급사 주문 문의 내역 리스트 service
    public ResponseEntity<ResponseBody> getSupplierInquiryList(String classificationCode, int page){
        log.info("공급사 주문 문의 내역 조회 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, inquiryQueryData.getSupplierInquiryList(classificationCode, page)), HttpStatus.OK);
    }

    // 공급사 문의 답변 수정트 service
    public ResponseEntity<ResponseBody> putsupplierInquiryUpdate(String clientInquiryId, String answerContent){
        log.info("공급사 문의 답변 수정 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, inquiryQueryData.putsupplierInquiryUpdate(clientInquiryId, answerContent)), HttpStatus.OK);
    }
    // 고객사 문의 등록 service
    public ResponseEntity<ResponseBody> supplierInquiry(InquirySupplierRequestDto inquirySupplierRequestDto){
        log.info("고객사 문의 등록 api - service");
        OrderInquiry orderInquiry = OrderInquiry.builder()
                .classificationCode(inquirySupplierRequestDto.getClassificationCode())
                .title(inquirySupplierRequestDto.getTitle())
                .content(inquirySupplierRequestDto.getContent())
                .build();


        orderInquiryRepository.save(orderInquiry);

        InquirySupplierEachResponseDto responseDto = InquirySupplierEachResponseDto.builder()
                .classificationCode(orderInquiry.getClassificationCode())
                .title(orderInquiry.getTitle())
                .content(orderInquiry.getContent())
                .build();

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, responseDto), HttpStatus.OK);
    }
    //
    public ResponseEntity<ResponseBody> adminInquiryList(int page){
        log.info("공급사 주문 문의 내역 조회 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, inquiryQueryData.adminInquiryList(page)), HttpStatus.OK);
    }

}
