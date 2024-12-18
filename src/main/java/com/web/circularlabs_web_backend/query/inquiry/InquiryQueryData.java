package com.web.circularlabs_web_backend.query.inquiry;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.web.circularlabs_web_backend.device.response.DeviceResponsePageDto;
import com.web.circularlabs_web_backend.inquiry.domain.ClientInquiry;
import com.web.circularlabs_web_backend.inquiry.domain.Faq;
import com.web.circularlabs_web_backend.inquiry.domain.OrderInquiry;
import com.web.circularlabs_web_backend.inquiry.request.InquiryClientListRequestDto;
import com.web.circularlabs_web_backend.inquiry.request.InquiryFaqRequestDto;
import com.web.circularlabs_web_backend.inquiry.response.*;
import com.web.circularlabs_web_backend.member.domain.Member;
import com.web.circularlabs_web_backend.member.request.MemberInfoRequestDto;
import com.web.circularlabs_web_backend.member.response.MemberSupplierEachResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


import static com.web.circularlabs_web_backend.inquiry.domain.QFaq.faq;
import static com.web.circularlabs_web_backend.inquiry.domain.QClientInquiry.clientInquiry;
import static com.web.circularlabs_web_backend.inquiry.domain.QOrderInquiry.orderInquiry;
import static com.web.circularlabs_web_backend.member.domain.QMember.member;

@RequiredArgsConstructor
@Component
public class InquiryQueryData {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

//고객사 FAQ 조회 api
    public InquiryFaqResponseDto getfaqList(String motherCode){
        List<Faq> result = new ArrayList<>();
        List<InquiryFaqListResponseDto> eachResponseDtos = new ArrayList<>();

        result = jpaQueryFactory
                .selectFrom(faq)
                .where(faq.classificationCode.eq(motherCode))
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    InquiryFaqListResponseDto.builder()
                            .faqId(result.get(i).getFaqId())
                            .answer(result.get(i).getAnswer())
                            .question(result.get(i).getQuestion())
                            .classificationCode(result.get(i).getClassificationCode())
                            .build()
            );
        }
        return InquiryFaqResponseDto.builder()
                .faqlist(eachResponseDtos)
                .build();
    }
    @Transactional
    public long putfaqUpdate(InquiryFaqRequestDto inquiryFaqRequestDto) {

        long result = jpaQueryFactory
                .update(faq)
                .set(faq.question, inquiryFaqRequestDto.getQuestion())
                .set(faq.answer, inquiryFaqRequestDto.getAnswer())
                .where(faq.faqId.eq(inquiryFaqRequestDto.getFaqId()))
                .execute();

        return result;
    }
    //고객사 문의 내역 조회 api
    public InquiryClientListResponseDto getclientInquiryList(String classificationCode, int page){

        List<ClientInquiry> result = new ArrayList<>();
        List<InquiryClientEachResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(clientInquiry.count())
                .from(clientInquiry)
                .where(clientInquiry.classificationCode.eq(classificationCode))
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(clientInquiry)
                .where(clientInquiry.classificationCode.eq(classificationCode))
                .offset(page)
                .limit(10)
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    InquiryClientEachResponseDto.builder()
                            .motherCode(result.get(i).getMotherCode())
                            .classificationCode(result.get(i).getClassificationCode())
                            .title(result.get(i).getTitle())
                            .content(result.get(i).getContent())
                            .answerAt(result.get(i).getAnswerAt())
                            .answerContent(result.get(i).getAnswerContent())
                            .answerStatus(result.get(i).getAnswerStatus())
                            .createdAt(result.get(i).getCreatedAt().toString())
                            .build()
            );
        }
        return InquiryClientListResponseDto.builder()
                .clientInauiryList(eachResponseDtos)
                .paging(pageing)
                .build();
    }

    //고객사 문의 내역 조회 api
    public InquiryClientListResponseDto getSupplierInquiryList(String classificationCode, int page){

        List<ClientInquiry> result = new ArrayList<>();
        List<InquiryClientEachResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(clientInquiry.count())
                .from(clientInquiry)
                .where(clientInquiry.motherCode.eq(classificationCode))
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(clientInquiry)
                .where(clientInquiry.motherCode.eq(classificationCode))
                .offset(page)
                .limit(10)
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    InquiryClientEachResponseDto.builder()
                            .clientInquiryId(result.get(i).getClientInquiryId())
                            .motherCode(result.get(i).getMotherCode())
                            .classificationCode(result.get(i).getClassificationCode())
                            .title(result.get(i).getTitle())
                            .content(result.get(i).getContent())
                            .answerAt(result.get(i).getAnswerAt())
                            .answerContent(result.get(i).getAnswerContent())
                            .answerStatus(result.get(i).getAnswerStatus())
                            .createdAt(result.get(i).getCreatedAt().toString())
                            .build()
            );
        }
        return InquiryClientListResponseDto.builder()
                .clientInauiryList(eachResponseDtos)
                .paging(pageing)
                .build();
    }
    @Transactional
    public long putsupplierInquiryUpdate(String clientInquiryId, String answerContent) {

        long result = jpaQueryFactory
                .update(clientInquiry)
                .set(clientInquiry.answerContent, answerContent)
                .set(clientInquiry.answerStatus, "Y")
                .where(clientInquiry.clientInquiryId.eq(Long.valueOf(clientInquiryId)))
                .execute();

        return result;
    }
    //고객사 문의 내역 조회 api
    public InquirySupplierListResponseDto adminInquiryList(int page){

        List<OrderInquiry> result = new ArrayList<>();
        List<InquirySupplierEachResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(orderInquiry.count())
                .from(orderInquiry)
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(orderInquiry)
                .offset(page)
                .limit(10)
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    InquirySupplierEachResponseDto.builder()
                            .classificationCode(result.get(i).getClassificationCode())
                            .title(result.get(i).getTitle())
                            .content(result.get(i).getContent())
                            .createdAt(result.get(i).getCreatedAt().toString())
                            .build()
            );
        }
        return InquirySupplierListResponseDto.builder()
                .supplierInauiryList(eachResponseDtos)
                .paging(pageing)
                .build();
    }
}
