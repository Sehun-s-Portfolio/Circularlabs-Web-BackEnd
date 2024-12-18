package com.web.circularlabs_web_backend.query.member;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.web.circularlabs_web_backend.member.domain.Member;
import com.web.circularlabs_web_backend.member.request.MemberDetailRequestDto;
import com.web.circularlabs_web_backend.member.request.MemberInfoRequestDto;
import com.web.circularlabs_web_backend.member.request.MemberUpdateClientDetailInfoRequestDto;
import com.web.circularlabs_web_backend.member.response.*;
import com.web.circularlabs_web_backend.product.domain.ClientProduct;
import com.web.circularlabs_web_backend.product.domain.Product;
import com.web.circularlabs_web_backend.product.domain.SupplyProduct;
import com.web.circularlabs_web_backend.product.repository.ClientProductRepository;
import com.web.circularlabs_web_backend.product.response.ClientProductResponseDto;
import com.web.circularlabs_web_backend.query.product.ProductQueryData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.web.circularlabs_web_backend.member.domain.QMember.member;
import static com.web.circularlabs_web_backend.product.domain.QProduct.product;
import static com.web.circularlabs_web_backend.product.domain.QSupplyProduct.supplyProduct;
import static com.web.circularlabs_web_backend.product.domain.QClientProduct.clientProduct;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberQueryData {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;
    private final ClientProductRepository clientProductRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductQueryData productQueryData;


    //로그인 api
    public Member getMemberRogin(String id) {
        return jpaQueryFactory
                .selectFrom(member)
                .where(member.loginId.eq(id))
                .fetchOne();
    }
    //ID중복검사 api
    public Long memberRegistcheckId(String RegistId) {
        return jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(member.loginId.eq(RegistId))
                .fetchOne();
    }
    //내정보 조회 api
    public Member getmemberInfo(String classificationCode) {
        return jpaQueryFactory
                .selectFrom(member)
                .where(member.classificationCode.eq(classificationCode))
                .fetchOne();
    }
    @Transactional
    public long memberwithDrawal(String classificationCode) {
        String grade = String.valueOf(jpaQueryFactory
                .select(member.grade)
                .from(member)
                .where(member.classificationCode.eq(classificationCode))
                .fetchOne());

        if (grade.equals("1")){
            jpaQueryFactory
            .update(member)
            .set(member.withDrawal, "Y")
            .set(member.withDrawalDate, LocalDateTime.now())
            .where(member.motherCode.eq(classificationCode))
            .execute();
        }
        long result = jpaQueryFactory
                .update(member)
                .set(member.withDrawal, "Y")
                .set(member.withDrawalDate, LocalDateTime.now())
                .where(member.classificationCode.eq(classificationCode))
                .execute();

        return result;
    }
    //내정보 수정 api
    @Transactional
    public long getmemberInfoUpdate(MemberInfoRequestDto MemberInfoRequestDto) {

        long result = jpaQueryFactory
                .update(member)
                .set(member.loginId, MemberInfoRequestDto.getLoginId())
                .set(member.clientCompany, MemberInfoRequestDto.getClientCompany())
                .set(member.address, MemberInfoRequestDto.getAddress())
                .set(member.represent, MemberInfoRequestDto.getRepresent())
                .set(member.stateOfBusiness, MemberInfoRequestDto.getStateOfBusiness())
                .set(member.categoryBusiness, MemberInfoRequestDto.getCategoryBusiness())
                .set(member.bankName, MemberInfoRequestDto.getBankName())
                .set(member.bankAccountNumber, MemberInfoRequestDto.getBankAccountNumber())
                .set(member.bankAccountName, MemberInfoRequestDto.getBankAccountName())
                .set(member.manager, MemberInfoRequestDto.getManager())
                .set(member.phone, MemberInfoRequestDto.getPhone())
                .set(member.position, MemberInfoRequestDto.getPosition())
                .set(member.email, MemberInfoRequestDto.getEmail())
                .set(member.lastOrders, MemberInfoRequestDto.getLastOrders())
                .set(member.custType, MemberInfoRequestDto.getCustType())
                .where(member.classificationCode.eq(MemberInfoRequestDto.getClassificationCode()))
                .execute();

        return result;
    }
    //내정보 수정 api
    @Transactional
    public long passwordreset(String classificationCode, String newpassword) {

        long result = jpaQueryFactory
                .update(member)
                .set(member.password, newpassword)
                .where(member.classificationCode.eq(classificationCode))
                .execute();

        return result;
    }
    /**
    // 회원가입 시 부여될 고유 코드
    public String getClassificationCode(int grade) {

        StringBuilder classificationCode = new StringBuilder();

        Long indexId = jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(member.grade.eq(grade))
                .limit(1)
                .fetchOne();

        if (indexId != 0L) {
            int remainCode = 4 - indexId.toString().length();

            if (grade == 1) {
                classificationCode = new StringBuilder("C");
            } else if (grade == 2) {
                classificationCode = new StringBuilder("UC");
            }

            for (int i = 0; i < remainCode; i++) {
                String zeroCode = "0";
                classificationCode.append(zeroCode);
            }

            classificationCode.append(indexId + 1L);
        } else {
            if (grade == 1) {
                classificationCode = new StringBuilder("C0001");
            } else if (grade == 2) {
                classificationCode = new StringBuilder("UC0001");
            }
        }

        return classificationCode.toString();
    }
     **/

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 회원가입 시 부여될 고유 코드 [개선]
    public String newGetClassificationCode(int grade) {

        StringBuilder classificationCode = new StringBuilder();

        String lastClassficationCode = jpaQueryFactory
                .select(member.classificationCode)
                .from(member)
                .where(member.grade.eq(grade))
                .orderBy(member.memberId.desc())
                .limit(1)
                .fetchOne();

        if(lastClassficationCode == null || lastClassficationCode.isEmpty()){
            if (grade == 1) {
                classificationCode = new StringBuilder("C0001");
            } else if (grade == 2) {
                classificationCode = new StringBuilder("UC0001");
            }
        }else{

            if (grade == 1) {
                classificationCode = new StringBuilder("C");

                String newClassficationCode = String.valueOf(Integer.parseInt(lastClassficationCode.replace("C", "")) + 1);

                int remainCode = 4 - newClassficationCode.length();

                for (int i = 0; i < remainCode; i++) {
                    classificationCode.append("0");
                }

                classificationCode.append(newClassficationCode);
            } else if (grade == 2) {
                classificationCode = new StringBuilder("UC");

                String newClassficationCode = String.valueOf(Integer.parseInt(lastClassficationCode.replace("UC", "")) + 1);

                int remainCode = 4 - newClassficationCode.length();

                for (int i = 0; i < remainCode; i++) {
                    classificationCode.append("0");
                }

                classificationCode.append(newClassficationCode);
            }

        }

        return classificationCode.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //공급사 리스트
    public MemberSupplierListResponseDto getSupplierList(String searchType, String searchWord, int page) {

        List<Member> result = new ArrayList<>();
        List<MemberSupplierEachResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(member.grade.eq(1).and(likeSearchWord(searchType, searchWord)))
                .orderBy(member.withDrawal.asc(), member.createdAt.desc())
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(member)
                .where(member.grade.eq(1).and(likeSearchWord(searchType, searchWord)))
                .orderBy(member.withDrawal.asc(), member.createdAt.desc())
                .offset(page)
                .limit(10)
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    MemberSupplierEachResponseDto.builder()
                            .grade(result.get(i).getGrade())
                            .motherCode(result.get(i).getMotherCode())
                            .classificationCode(result.get(i).getClassificationCode())
                            .loginId(result.get(i).getLoginId())
                            .clientCompany(result.get(i).getClientCompany())
                            .address(result.get(i).getAddress())
                            .represent(result.get(i).getRepresent())
                            .businessNumber(result.get(i).getBusinessNumber())
                            .stateOfBusiness(result.get(i).getStateOfBusiness())
                            .categoryBusiness(result.get(i).getCategoryBusiness())
                            .bankName(result.get(i).getBankName())
                            .bankAccountNumber(result.get(i).getBankAccountNumber())
                            .bankAccountName(result.get(i).getBankAccountName())
                            .manager(result.get(i).getManager())
                            .phone(result.get(i).getPhone())
                            .position(result.get(i).getPosition())
                            .email(result.get(i).getEmail())
                            .etc(result.get(i).getEtc())
                            .withDrawal(result.get(i).getWithDrawal())
                            .createdAt(result.get(i).getCreatedAt().toString())
                            .build()
            );

        }

        return MemberSupplierListResponseDto.builder()
                .supplierList(eachResponseDtos)
                .paging(pageing)
                .build();

    }
    // 공급사 조건 없이 전체 리스트
    public List<MemberSupplierResponseDto> getSupplierAllList() {

        List<Member> result = new ArrayList<>();
        List<MemberSupplierResponseDto> eachResponseDtos = new ArrayList<>();




        result = jpaQueryFactory
                .selectFrom(member)
                .where(member.grade.eq(1).and(member.withDrawal.eq("N")))
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    MemberSupplierResponseDto.builder()
                            .grade(result.get(i).getGrade())
                            .motherCode(result.get(i).getMotherCode())
                            .classificationCode(result.get(i).getClassificationCode())
                            .loginId(result.get(i).getLoginId())
                            .clientCompany(result.get(i).getClientCompany())
                            .address(result.get(i).getAddress())
                            .represent(result.get(i).getRepresent())
                            .businessNumber(result.get(i).getBusinessNumber())
                            .stateOfBusiness(result.get(i).getStateOfBusiness())
                            .categoryBusiness(result.get(i).getCategoryBusiness())
                            .bankName(result.get(i).getBankName())
                            .bankAccountNumber(result.get(i).getBankAccountNumber())
                            .bankAccountName(result.get(i).getBankAccountName())
                            .manager(result.get(i).getManager())
                            .phone(result.get(i).getPhone())
                            .position(result.get(i).getPosition())
                            .email(result.get(i).getEmail())
                            .etc(result.get(i).getEtc())
                            .withDrawal(result.get(i).getWithDrawal())
                            .build()
            );

        }

        return eachResponseDtos;

    }

    //고객사 조건 없이 전체 검색
    public List<MemberSupplierResponseDto> getClientAllList() {

        List<Member> result = new ArrayList<>();
        List<MemberSupplierResponseDto> eachResponseDtos = new ArrayList<>();




        result = jpaQueryFactory
                .selectFrom(member)
                .where(member.grade.eq(2))
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    MemberSupplierResponseDto.builder()
                            .grade(result.get(i).getGrade())
                            .motherCode(result.get(i).getMotherCode())
                            .classificationCode(result.get(i).getClassificationCode())
                            .loginId(result.get(i).getLoginId())
                            .clientCompany(result.get(i).getClientCompany())
                            .address(result.get(i).getAddress())
                            .represent(result.get(i).getRepresent())
                            .businessNumber(result.get(i).getBusinessNumber())
                            .stateOfBusiness(result.get(i).getStateOfBusiness())
                            .categoryBusiness(result.get(i).getCategoryBusiness())
                            .bankName(result.get(i).getBankName())
                            .bankAccountNumber(result.get(i).getBankAccountNumber())
                            .bankAccountName(result.get(i).getBankAccountName())
                            .manager(result.get(i).getManager())
                            .phone(result.get(i).getPhone())
                            .position(result.get(i).getPosition())
                            .email(result.get(i).getEmail())
                            .etc(result.get(i).getEtc())
                            .withDrawal(result.get(i).getWithDrawal())
                            .build()
            );

        }

        return eachResponseDtos;

    }
    // 공급사 조건 없이 전체 리스트
    public List<MemberSupplierResponseDto> memberClientScAllList(String classificationCode) {

        List<Member> result = new ArrayList<>();
        List<MemberSupplierResponseDto> eachResponseDtos = new ArrayList<>();

        result = jpaQueryFactory
                .selectFrom(member)
                .where(member.grade.eq(2).and(member.motherCode.eq(classificationCode)).and(member.withDrawal.eq("N")))
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    MemberSupplierResponseDto.builder()
                            .grade(result.get(i).getGrade())
                            .motherCode(result.get(i).getMotherCode())
                            .classificationCode(result.get(i).getClassificationCode())
                            .loginId(result.get(i).getLoginId())
                            .clientCompany(result.get(i).getClientCompany())
                            .address(result.get(i).getAddress())
                            .represent(result.get(i).getRepresent())
                            .businessNumber(result.get(i).getBusinessNumber())
                            .stateOfBusiness(result.get(i).getStateOfBusiness())
                            .categoryBusiness(result.get(i).getCategoryBusiness())
                            .bankName(result.get(i).getBankName())
                            .bankAccountNumber(result.get(i).getBankAccountNumber())
                            .bankAccountName(result.get(i).getBankAccountName())
                            .manager(result.get(i).getManager())
                            .phone(result.get(i).getPhone())
                            .position(result.get(i).getPosition())
                            .email(result.get(i).getEmail())
                            .etc(result.get(i).getEtc())
                            .withDrawal(result.get(i).getWithDrawal())
                            .build()
            );

        }

        return eachResponseDtos;

    }
    //공급사 상세정보
    public MemberSupplierResponseDto getSupplierDetail(String classificationCode) {

        Member memberResult = jpaQueryFactory
                .selectFrom(member)
                .where(member.classificationCode.eq(classificationCode))
                .fetchOne();

        List<SupplyProduct> supplierproductresult = jpaQueryFactory
                .selectFrom(supplyProduct)
                .where(supplyProduct.classificationCode.eq(classificationCode))
                .fetch();

        List<Product> productresult = jpaQueryFactory
                .selectFrom(product)
                .fetch();

        assert memberResult != null;
        return MemberSupplierResponseDto.builder()
                .grade(memberResult.getGrade())
                .motherCode(memberResult.getMotherCode())
                .classificationCode(memberResult.getClassificationCode())
                .loginId(memberResult.getLoginId())
                .password(memberResult.getPassword())
                .clientCompany(memberResult.getClientCompany())
                .address(memberResult.getAddress())
                .represent(memberResult.getRepresent())
                .businessNumber(memberResult.getBusinessNumber())
                .stateOfBusiness(memberResult.getStateOfBusiness())
                .categoryBusiness(memberResult.getCategoryBusiness())
                .bankName(memberResult.getBankName())
                .bankAccountNumber(memberResult.getBankAccountNumber())
                .bankAccountName(memberResult.getBankAccountName())
                .manager(memberResult.getManager())
                .phone(memberResult.getPhone())
                .position(memberResult.getPosition())
                .email(memberResult.getEmail())
                .etc(memberResult.getEtc())
                .withDrawal(memberResult.getWithDrawal())
                .with_drawal_date(String.valueOf(memberResult.getWithDrawalDate()))
                .product(productresult)
                .supplierproduct(supplierproductresult)
                .build();

    }

    //공급사 정보 수정
    @Transactional
    public long getSupplierUpdate(MemberDetailRequestDto memberDetailRequestDto) {

        long result = jpaQueryFactory
                .update(member)
                .set(member.custType, memberDetailRequestDto.getCustType())
                .set(member.loginId, memberDetailRequestDto.getLoginId())
                .set(member.clientCompany, memberDetailRequestDto.getClientCompany())
                .set(member.address, memberDetailRequestDto.getAddress())
                .set(member.represent, memberDetailRequestDto.getRepresent())
                .set(member.businessNumber, memberDetailRequestDto.getBusinessNumber())
                .set(member.stateOfBusiness, memberDetailRequestDto.getStateOfBusiness())
                .set(member.categoryBusiness, memberDetailRequestDto.getCategoryBusiness())
                .set(member.bankName, memberDetailRequestDto.getBankName())
                .set(member.bankAccountNumber, memberDetailRequestDto.getBankAccountNumber())
                .set(member.bankAccountName, memberDetailRequestDto.getBankAccountName())
                .set(member.manager, memberDetailRequestDto.getManager())
                .set(member.phone, memberDetailRequestDto.getPhone())
                .set(member.position, memberDetailRequestDto.getPosition())
                .set(member.email, memberDetailRequestDto.getEmail())
                .set(member.etc, memberDetailRequestDto.getEtc())
                .where(member.classificationCode.eq(memberDetailRequestDto.getClassificationCode()))
                .execute();

        return result;

    }

    @Transactional
    public void getSupplyProductDelte(String classificationCode, Long productId) {

        jpaQueryFactory
                .delete(supplyProduct)
                .where(supplyProduct.classificationCode.eq(classificationCode))
                .where(supplyProduct.product.productId.eq(productId))
                .execute();

    }

    //고객사 리스트
    public MemberClientListResponseDto getClientList(String searchType, String searchWord, int page) {

        List<Member> result = new ArrayList<>();
        List<MemberSupplierEachResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(member.grade.eq(2).and(likeSearchWord(searchType, searchWord)))
                .orderBy(member.withDrawal.asc(), member.createdAt.desc())
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(member)
                .where(member.grade.eq(2).and(likeSearchWord(searchType, searchWord)))
                .orderBy(member.withDrawal.asc(), member.createdAt.desc())
                .offset(page)
                .limit(10)
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    MemberSupplierEachResponseDto.builder()
                            .grade(result.get(i).getGrade())
                            .motherCode(result.get(i).getMotherCode())
                            .classificationCode(result.get(i).getClassificationCode())
                            .loginId(result.get(i).getLoginId())
                            .clientCompany(result.get(i).getClientCompany())
                            .address(result.get(i).getAddress())
                            .represent(result.get(i).getRepresent())
                            .businessNumber(result.get(i).getBusinessNumber())
                            .stateOfBusiness(result.get(i).getStateOfBusiness())
                            .categoryBusiness(result.get(i).getCategoryBusiness())
                            .bankName(result.get(i).getBankName())
                            .bankAccountNumber(result.get(i).getBankAccountNumber())
                            .bankAccountName(result.get(i).getBankAccountName())
                            .manager(result.get(i).getManager())
                            .phone(result.get(i).getPhone())
                            .position(result.get(i).getPosition())
                            .email(result.get(i).getEmail())
                            .etc(result.get(i).getEtc())
                            .withDrawal(result.get(i).getWithDrawal())
                            .createdAt(result.get(i).getCreatedAt().toString())
                            .build()
            );

        }

        return MemberClientListResponseDto.builder()
                .clientList(eachResponseDtos)
                .paging(pageing)
                .build();
    }

    //고객사 상세정보
    public MemberClientResponseDto getClientDetail(String classificationCode, String motherCode) {

        Member memberResult = jpaQueryFactory
                .selectFrom(member)
                .where(member.classificationCode.eq(classificationCode))
                .fetchOne();

        List<SupplyProduct> supplierproductresult = jpaQueryFactory
                .selectFrom(supplyProduct)
                .where(supplyProduct.classificationCode.eq(motherCode))
                .fetch();

        List<ClientProduct> productresult = jpaQueryFactory
                .selectFrom(clientProduct)
                .where(clientProduct.clientClassficationCode.eq(classificationCode))
                .fetch();

        assert memberResult != null;
        return MemberClientResponseDto.builder()
                .custType(memberResult.getCustType())
                .grade(memberResult.getGrade())
                .motherCode(memberResult.getMotherCode())
                .classificationCode(memberResult.getClassificationCode())
                .loginId(memberResult.getLoginId())
                .password(memberResult.getPassword())
                .clientCompany(memberResult.getClientCompany())
                .address(memberResult.getAddress())
                .represent(memberResult.getRepresent())
                .businessNumber(memberResult.getBusinessNumber())
                .stateOfBusiness(memberResult.getStateOfBusiness())
                .categoryBusiness(memberResult.getCategoryBusiness())
                .bankName(memberResult.getBankName())
                .bankAccountNumber(memberResult.getBankAccountNumber())
                .bankAccountName(memberResult.getBankAccountName())
                .manager(memberResult.getManager())
                .phone(memberResult.getPhone())
                .position(memberResult.getPosition())
                .email(memberResult.getEmail())
                .etc(memberResult.getEtc())
                .withDrawal(memberResult.getWithDrawal())
                .withDrawalDate(memberResult.getWithDrawalDate())
                .supplierproduct(supplierproductresult)
                .clientproduct(productresult)
                .build();

    }

    @Transactional
    public void getClientProductDelte(String classificationCode, Long productId) {

        jpaQueryFactory
                .delete(clientProduct)
                .where(clientProduct.clientClassficationCode.eq(classificationCode))
                .where(clientProduct.supplyProduct.supplyProductId.eq(productId))
                .execute();

    }


    // 공급사에 속한 고객사 리스트 조회
    public MemberClientOfSupplierDetailInfoListResponseDto getClientsOfSupplier(String supplierCode, int page, String searchType, String searchWord) {

        // 공급사에 속한 고객사 리스트 반환 리스트 객체 생성
        List<Member> result = new ArrayList<>();
        List<MemberClientsOfSupplierResponseDto> responseClients = new ArrayList<>();
        Long pageing = 0L;

        // 공급사에 속한 고객사들을 검색 키워드와 검색 타입에 따라 호출한 고객사 리스트 호출
        result = jpaQueryFactory
                .selectFrom(member)
                .where(member.grade.eq(2)
                        .and(member.motherCode.eq(supplierCode))
                        .and(likeSearchWord(searchType, searchWord)))
                .orderBy(member.withDrawal.asc(), member.createdAt.desc())
                .offset((page * 10L) - 10)
                .limit(10)
                .fetch();

        pageing = jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(member.grade.eq(2)
                        .and(member.motherCode.eq(supplierCode))
                        .and(likeSearchWord(searchType, searchWord)))
                .orderBy(member.withDrawal.asc(), member.createdAt.desc())
                .fetchOne();


        // 호출한 고객사들을 하나씩 조회하여 정보 기입
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getWithDrawal().equals("N")) { // 운영 중인 고객사 정보 기입
                responseClients.add(
                        MemberClientsOfSupplierResponseDto.builder()
                                .clientIndexId(result.get(i).getMemberId())
                                .clientClassificationCode(result.get(i).getClassificationCode())
                                .motherCode(result.get(i).getMotherCode())
                                .loginId(result.get(i).getLoginId())
                                .clientCompany(result.get(i).getClientCompany())
                                .manager(result.get(i).getManager())
                                .status(result.get(i).getWithDrawal())
                                .createdAt(result.get(i).getCreatedAt().toString())
                                .withdrawAt("")
                                .etc(result.get(i).getEtc())
                                .build()
                );
            } else {
                responseClients.add(
                        MemberClientsOfSupplierResponseDto.builder()
                                .clientIndexId(result.get(i).getMemberId())
                                .clientClassificationCode(result.get(i).getClassificationCode())
                                .motherCode(result.get(i).getMotherCode())
                                .loginId(result.get(i).getLoginId())
                                .clientCompany(result.get(i).getClientCompany())
                                .manager(result.get(i).getManager())
                                .status(result.get(i).getWithDrawal())
                                .createdAt(result.get(i).getCreatedAt().toString())
                                .withdrawAt(result.get(i).getWithDrawalDate().toString())
                                .etc(result.get(i).getEtc())
                                .build()
                );
            }
        }
        Collections.reverse(responseClients);
        return MemberClientOfSupplierDetailInfoListResponseDto.builder()
                .clientList(responseClients)
                .paging(pageing)
                .build();
    }


    // 공급사에 속한 고객사 리스트 조회 동적 조건
    private BooleanExpression likeSearchWord(String searchType, String searchWord) {
        if (searchType != null) { // 검색 타입이 존재할 경우
            if (searchType.equals("name")) { // 검색 타입이 고객사 상호명일 경우
                if (searchWord != null) { // 검색 키워드가 존재할 경우
                    // 고객사 상호명 기준 검색 키워드를 반영하여 검색 정보 반환
                    return member.clientCompany.like("%" + searchWord.replace(" ", "%") + "%");
                } else { // 검색 키워드가 존재하지 않을 경우
                    // 검색 키워드가 존재하지 않으므로 전체 고객사 리스트 반환
                    return null;
                }
            } else if (searchType.equals("loginId")) { // 검색 타입이 로그인 아이디일 경우
                if (searchWord != null) { // 검색 키워드가 존재할 경우
                    // 고객사 로그인 아이디 기준 검색 키워드를 반영하여 검색 정보 반환
                    return member.loginId.like("%" + searchWord.replace(" ", "%") + "%");
                } else { // 검색 키워드가 존재하지 않을 경우
                    // 검색 키워드가 존재하지 않으므로 전체 고객사 리스트 반환
                    return null;
                }
            }

            // 검색 타입이 아예 없을 경우 전체 리스트 정보 반환
            return null;
        } else { // 검색 타입이 존재하지 않을 경우
            if (searchWord != null) { // 검색 키워드가 존재할 경우
                // 고객사 상호명과 로그인 아이디를 두개 다 검색 키워드와 대조하여 해당되는 고객사 리스트 반환
                return member.clientCompany.like("%" + searchWord.replace(" ", "%") + "%")
                        .or(member.loginId.like("%" + searchWord.replace(" ", "%") + "%"));
            }

            // 검색 타입이 존재하지 않고 검색 키워드 또한 존재하지 않으므로 전체 고객사 리스트 반환
            return null;
        }

    }


    // 공급사에 속한 특정 고객사 상세 정보 조회
    public MemberClientOfSupplierDetailInfoResponseDto getClientDetailInfo(String supplierCode, String clientCode) {

        // 상세 정보를 조회하고자 하는 고객사 호출
        Member client = jpaQueryFactory
                .selectFrom(member)
                .where(member.grade.eq(2)
                        .and(member.motherCode.eq(supplierCode))
                        .and(member.classificationCode.eq(clientCode)))
                .fetchOne();

        // 고객사가 보유한 제품들 정보를 저장할 리스트 객체 생성
        List<ClientProductResponseDto> clientProducts = new ArrayList<>();

        // 고객사 보유 제품들 정보 리스트 호출
        List<Tuple> clientProductsInfo = jpaQueryFactory
                .select(clientProduct.clientProductId,
                        clientProduct.clientClassficationCode,
                        clientProduct.supplyProduct.supplyProductId,
                        clientProduct.supplyProduct.classificationCode,
                        clientProduct.supplyProduct.product.productName,
                        clientProduct.supplyProduct.product.productCode,
                        clientProduct.supplyProduct.product.purchasePrice,
                        clientProduct.supplyProduct.rentalPrice,
                        clientProduct.supplyProduct.maximumOrderMount,
                        clientProduct.supplyProduct.product.status)
                .from(clientProduct)
                .where(clientProduct.clientClassficationCode.eq(clientCode))
                .fetch();

        // 보유 제품들을 하나씩 조회하며 리스트 객체에 저장
        for (int i = 0; i < clientProductsInfo.size(); i++) {
            clientProducts.add(
                    ClientProductResponseDto.builder()
                            .clientProductId(clientProductsInfo.get(i).get(clientProduct.clientProductId))
                            .clientClassificationCode(clientProductsInfo.get(i).get(clientProduct.clientClassficationCode))
                            .supplyProductId(clientProductsInfo.get(i).get(clientProduct.supplyProduct.supplyProductId))
                            .supplierClassificationCode(clientProductsInfo.get(i).get(clientProduct.supplyProduct.classificationCode))
                            .productName(clientProductsInfo.get(i).get(clientProduct.supplyProduct.product.productName))
                            .productCode(clientProductsInfo.get(i).get(clientProduct.supplyProduct.product.productCode))
                            .purchasePrice(clientProductsInfo.get(i).get(clientProduct.supplyProduct.product.purchasePrice))
                            .rentalPrice(clientProductsInfo.get(i).get(clientProduct.supplyProduct.rentalPrice))
                            .maximumOrderMount(clientProductsInfo.get(i).get(clientProduct.supplyProduct.maximumOrderMount))
                            .status(clientProductsInfo.get(i).get(clientProduct.supplyProduct.product.status))
                            .build()
            );
        }

        assert client != null;

        // 탈퇴 여부가 N일 경우 반환
        if (client.getWithDrawal().equals("N")) {
            return MemberClientOfSupplierDetailInfoResponseDto.builder()
                    .clientIndexId(client.getMemberId())
                    .grade(client.getGrade())
                    .motherCode(client.getMotherCode())
                    .classificationCode(client.getClassificationCode())
                    .loginId(client.getLoginId())
                    .clientCompany(client.getClientCompany())
                    .address(client.getAddress())
                    .represent(client.getRepresent())
                    .businessNumber(client.getBusinessNumber())
                    .stateOfBusiness(client.getStateOfBusiness())
                    .categoryBusiness(client.getCategoryBusiness())
                    .bankName(client.getBankName())
                    .bankAccountNumber(client.getBankAccountNumber())
                    .bankAccountName(client.getBankAccountName())
                    .manager(client.getManager())
                    .phone(client.getPhone())
                    .position(client.getPosition())
                    .email(client.getEmail())
                    .etc(client.getEtc())
                    .withDrawal(client.getWithDrawal())
                    .withDrawalDate("")
                    .clientProducts(clientProducts)
                    .build();

        } else { // 탈퇴 여부가 Y일 경우 반환
            return MemberClientOfSupplierDetailInfoResponseDto.builder()
                    .clientIndexId(client.getMemberId())
                    .grade(client.getGrade())
                    .motherCode(client.getMotherCode())
                    .classificationCode(client.getClassificationCode())
                    .loginId(client.getLoginId())
                    .clientCompany(client.getClientCompany())
                    .address(client.getAddress())
                    .represent(client.getRepresent())
                    .businessNumber(client.getBusinessNumber())
                    .stateOfBusiness(client.getStateOfBusiness())
                    .categoryBusiness(client.getCategoryBusiness())
                    .bankName(client.getBankName())
                    .bankAccountNumber(client.getBankAccountNumber())
                    .bankAccountName(client.getBankAccountName())
                    .manager(client.getManager())
                    .phone(client.getPhone())
                    .position(client.getPosition())
                    .email(client.getEmail())
                    .etc(client.getEtc())
                    .withDrawal(client.getWithDrawal())
                    .withDrawalDate(client.getWithDrawalDate().toString())
                    .clientProducts(clientProducts)
                    .build();
        }
    }


    // 공급사 측에서 특정 고객사 정보 수정
    @Transactional
    public MemberClientOfSupplierDetailInfoResponseDto updateClientDetailInfo(
            String supplierCode,
            String clientCode,
            MemberUpdateClientDetailInfoRequestDto updateClientDetailInfoRequestDto) {

        // 수정하고 반영한 고객사 정보 호출
        Member client = updateInfo(supplierCode, clientCode, updateClientDetailInfoRequestDto);
        // 반영된 고객사가 보유한 제품 정보 리스트
        List<ClientProductResponseDto> clientProducts = productQueryData.getClientProducts(client);

        // 탈퇴 일자
        String withDrawalDate = "";

        // 탈퇴 유무가 Y일 경우 탈퇴 일자 반영
        if (client.getWithDrawal().equals("Y")) {
            withDrawalDate = client.getWithDrawalDate().toString();
        }

        return MemberClientOfSupplierDetailInfoResponseDto.builder()
                .clientIndexId(client.getMemberId())
                .grade(client.getGrade())
                .motherCode(client.getMotherCode())
                .classificationCode(client.getClassificationCode())
                .loginId(client.getLoginId())
                .clientCompany(client.getClientCompany())
                .address(client.getAddress())
                .represent(client.getRepresent())
                .businessNumber(client.getBusinessNumber())
                .stateOfBusiness(client.getStateOfBusiness())
                .categoryBusiness(client.getCategoryBusiness())
                .bankName(client.getBankName())
                .bankAccountNumber(client.getBankAccountNumber())
                .bankAccountName(client.getBankAccountName())
                .manager(client.getManager())
                .phone(client.getPhone())
                .position(client.getPosition())
                .email(client.getEmail())
                .etc(client.getEtc())
                .withDrawal(client.getWithDrawal())
                .withDrawalDate(withDrawalDate)
                .clientProducts(clientProducts)
                .build();

    }


    @Transactional
    public Member updateInfo(String supplierCode, String clientCode, MemberUpdateClientDetailInfoRequestDto updateClientDetailInfoRequestDto) {
        log.info("업데이트 함수 초기 진입");

        // 수정하고자 하는 고객사 정보 호출
        Member client = jpaQueryFactory
                .selectFrom(member)
                .where(member.grade.eq(2)
                        .and(member.motherCode.eq(supplierCode))
                        .and(member.classificationCode.eq(clientCode)))
                .fetchOne();

        assert client != null;

        log.info("업데이트 할 고객사 조회 호출 : {}", client.getClassificationCode());

        boolean existUpdateContent = false;

        JPAUpdateClause clause = jpaQueryFactory
                .update(member)
                .where(member.memberId.eq(client.getMemberId()));

        // 로그인 아이디 수정
        if (!updateClientDetailInfoRequestDto.getLoginId().equals("") && !client.getLoginId().equals(updateClientDetailInfoRequestDto.getLoginId())) {
            log.info("로그인 아이디 수정");
            existUpdateContent = true;
            clause.set(member.loginId, updateClientDetailInfoRequestDto.getLoginId());
        }

        // 비밀번호 수정
        if (!passwordEncoder.matches(updateClientDetailInfoRequestDto.getPassword(), client.getPassword()) && !updateClientDetailInfoRequestDto.getPassword().equals("")) {
            log.info("비밀번호 수정");
            existUpdateContent = true;
            clause.set(member.password, passwordEncoder.encode(updateClientDetailInfoRequestDto.getLoginId()));
        }

        // 고객사 상호명 수정
        if (!updateClientDetailInfoRequestDto.getClientCompany().equals("") && !client.getClientCompany().equals(updateClientDetailInfoRequestDto.getClientCompany())) {
            log.info("고객사 상호명 수정");
            existUpdateContent = true;
            clause.set(member.clientCompany, updateClientDetailInfoRequestDto.getClientCompany());
        }

        // 고객사 주소 수정
        if (!updateClientDetailInfoRequestDto.getAddress().equals("") && !client.getAddress().equals(updateClientDetailInfoRequestDto.getAddress())) {
            log.info("주소 수정");
            existUpdateContent = true;
            clause.set(member.address, updateClientDetailInfoRequestDto.getAddress());
        }

        // 고객사 대표자 수정
        if (!updateClientDetailInfoRequestDto.getRepresent().equals("") && !client.getRepresent().equals(updateClientDetailInfoRequestDto.getRepresent())) {
            log.info("대표자 수정");
            existUpdateContent = true;
            clause.set(member.represent, updateClientDetailInfoRequestDto.getRepresent());
        }

        // 고객사 사업자 번호 수정
        if (!updateClientDetailInfoRequestDto.getBusinessNumber().equals("") && !client.getBusinessNumber().equals(updateClientDetailInfoRequestDto.getBusinessNumber())) {
            log.info("사업자 번호 수정");
            existUpdateContent = true;
            clause.set(member.businessNumber, updateClientDetailInfoRequestDto.getBusinessNumber());
        }

        // 고객사 업태 수정
        if (!updateClientDetailInfoRequestDto.getStateOfBusiness().equals("") && !client.getStateOfBusiness().equals(updateClientDetailInfoRequestDto.getStateOfBusiness())) {
            log.info("업태 수정");
            existUpdateContent = true;
            clause.set(member.stateOfBusiness, updateClientDetailInfoRequestDto.getStateOfBusiness());
        }

        // 고객사 업종 수정
        if (!updateClientDetailInfoRequestDto.getCategoryBusiness().equals("") && !client.getCategoryBusiness().equals(updateClientDetailInfoRequestDto.getCategoryBusiness())) {
            log.info("업종 수정");
            existUpdateContent = true;
            clause.set(member.categoryBusiness, updateClientDetailInfoRequestDto.getCategoryBusiness());
        }

        // 고객사 은행명 수정
        if (!updateClientDetailInfoRequestDto.getBankName().equals("") && !client.getBankName().equals(updateClientDetailInfoRequestDto.getBankName())) {
            log.info("은행명 수정");
            existUpdateContent = true;
            clause.set(member.bankName, updateClientDetailInfoRequestDto.getBankName());
        }

        // 고객사 은행 계좌 번호 수정
        if (!updateClientDetailInfoRequestDto.getBankAccountNumber().equals("") && !client.getBankAccountNumber().equals(updateClientDetailInfoRequestDto.getBankAccountNumber())) {
            log.info("계좌 번호 수정");
            existUpdateContent = true;
            clause.set(member.bankAccountNumber, updateClientDetailInfoRequestDto.getBankAccountNumber());
        }

        // 고객사 은행 계좌명 수정
        if (!updateClientDetailInfoRequestDto.getBankAccountName().equals("") && !client.getBankAccountName().equals(updateClientDetailInfoRequestDto.getBankAccountName())) {
            log.info("계좌명 수정");
            existUpdateContent = true;
            clause.set(member.bankAccountName, updateClientDetailInfoRequestDto.getBankAccountName());
        }

        // 고객사 담당자 수정
        if (!updateClientDetailInfoRequestDto.getManager().equals("") && !client.getManager().equals(updateClientDetailInfoRequestDto.getManager())) {
            log.info("담당 매니저 수정");
            existUpdateContent = true;
            clause.set(member.manager, updateClientDetailInfoRequestDto.getManager());
        }

        // 고객사 연락처 수정
        if (!updateClientDetailInfoRequestDto.getPhone().equals("") && !client.getPhone().equals(updateClientDetailInfoRequestDto.getPhone())) {
            log.info("연락처 수정");
            existUpdateContent = true;
            clause.set(member.phone, updateClientDetailInfoRequestDto.getPhone());
        }

        // 고객사 담당자 직급 수정
        if (!updateClientDetailInfoRequestDto.getPosition().equals("") && !client.getPosition().equals(updateClientDetailInfoRequestDto.getPosition())) {
            log.info("직급 수정");
            existUpdateContent = true;
            clause.set(member.position, updateClientDetailInfoRequestDto.getPosition());
        }

        // 고객사 담당자 이메일 수정
        if (!updateClientDetailInfoRequestDto.getEmail().equals("") && !client.getEmail().equals(updateClientDetailInfoRequestDto.getEmail())) {
            log.info("이메일 수정");
            existUpdateContent = true;
            clause.set(member.email, updateClientDetailInfoRequestDto.getEmail());
        }

        // 고객사 비고 수정
        if (!updateClientDetailInfoRequestDto.getEtc().equals("") && !client.getEtc().equals(updateClientDetailInfoRequestDto.getEtc())) {
            log.info("비고 수정");
            existUpdateContent = true;
            clause.set(member.etc, updateClientDetailInfoRequestDto.getEtc());
        }

        // 고객사 탈퇴 유무 및 탈퇴 일자 수정
        if (!updateClientDetailInfoRequestDto.getWithdrawal().equals("") && !client.getWithDrawal().equals(updateClientDetailInfoRequestDto.getWithdrawal())) {
            if (updateClientDetailInfoRequestDto.getWithdrawal().equals("N")) {
                log.info("탈퇴 여부 N으로 수정");
                existUpdateContent = true;
                clause.set(member.withDrawal, updateClientDetailInfoRequestDto.getWithdrawal());
            } else {
                log.info("탈퇴 여부 Y로 수정");
                existUpdateContent = true;
                clause.set(member.withDrawal, updateClientDetailInfoRequestDto.getWithdrawal());
                clause.set(member.withDrawalDate, LocalDateTime.now());
            }
        }

        if(existUpdateContent){
            clause.execute();
        }

        // 고객사 제품 수정

        // 현재 보유 운영 중인 고객사 제품(공급사에서 주문한 제품) 리스트
        List<SupplyProduct> clientProducts = jpaQueryFactory
                .select(clientProduct.supplyProduct)
                .from(clientProduct)
                .where(clientProduct.clientClassficationCode.eq(client.getClassificationCode()))
                .fetch();

        // 수정하고자 하는 제품들의 코드 리스트가 존재할 경우 수정 및 필요시 삭제 처리 로직 수행
        if (!updateClientDetailInfoRequestDto.getSupplyProduct().isEmpty()) {
            log.info("수정 등록할 고객사 코드 리스트 존재");

            // 고객사 수정 요청 제품 코드 리스트
            List<String> supplyProductCodes = updateClientDetailInfoRequestDto.getSupplyProduct();

            // 고객사가 제품을 보유하고 있을 경우에만 비교하여 삭제가 필요할 때 삭제 처리
            if (!clientProducts.isEmpty() || clientProducts != null) {
                log.info("기존에 보유하고 있는 고객사 제품들이 존재");

                // 만약 제품 수정 코드 리스트에 기존에 등록된 고객사 제품 코드가 존재하지 않을 경우 삭제로 보고 해당 고객사 제품 삭제
                for (int j = 0; j < clientProducts.size(); j++) {
                    if (!supplyProductCodes.contains(clientProducts.get(j).getProduct().getProductCode())) {
                        jpaQueryFactory
                                .delete(clientProduct)
                                .where(clientProduct.supplyProduct.eq(clientProducts.get(j))
                                        .and(clientProduct.clientClassficationCode.eq(client.getClassificationCode())))
                                .execute();
                    }
                }
            }

            entityManager.flush();
            entityManager.clear();

            // 신규 고객사 제품 등록
            for (int i = 0; i < supplyProductCodes.size(); i++) {

                // 신규로 제품을 등록하고자 하는 공급사 제품 조회
                SupplyProduct clientRegistSupplyProduct = jpaQueryFactory
                        .selectFrom(supplyProduct)
                        .where(supplyProduct.classificationCode.eq(supplierCode)
                                .and(supplyProduct.product.productCode.eq(supplyProductCodes.get(i))))
                        .fetchOne();

                // 만약 현재 보유 중이고 있는 고객사 제품들 중 수정 요청 받은 제품들의 코드 리스트를 대조하여 기존에 존재하지 않은 제품일 경우 새로이 등록
                if (!clientProducts.contains(clientRegistSupplyProduct) || clientProducts.isEmpty()) {
                    ClientProduct newClientProduct = ClientProduct.builder()
                            .clientClassficationCode(client.getClassificationCode())
                            .supplyProduct(clientRegistSupplyProduct)
                            .build();

                    clientProductRepository.save(newClientProduct);

                    log.info("새로이 등록할 고객사 제품 조건 진입 : {}", newClientProduct.getSupplyProduct().getProduct().getProductName());
                }

            }

        } else { // 고객사 제품을 수정할 때 전부 해제하여 보유하고자 하는 제품 코드 리스트가 아무것도 없이 들어올 경우
            log.info("수정 등록할 고객사 코드 리스트 존재하지 않음");

            // 고객사가 제품을 보유하고 있을 경우에는 비교하여 전부 삭제 처리
            if (!clientProducts.isEmpty() || clientProducts != null) {
                log.info("기존에 보유하고 있는 고객사 제품이 존재할 경우");

                // 만약 제품 수정 코드 리스트에 기존에 등록된 고객사 제품 코드가 존재하지 않을 경우 삭제로 보고 해당 고객사 제품 삭제
                for (int j = 0; j < clientProducts.size(); j++) {
                    log.info("수정 등록할 고객사 코드가 아무것도 없는 상태로 들어왔으므로 기존 등록된 고객사 제품들 정보 삭제");

                    jpaQueryFactory
                            .delete(clientProduct)
                            .where(clientProduct.supplyProduct.eq(clientProducts.get(j))
                                    .and(clientProduct.clientClassficationCode.eq(client.getClassificationCode())))
                            .execute();

                }
            }

            entityManager.flush();
            entityManager.clear();
        }

        return jpaQueryFactory
                .selectFrom(member)
                .where(member.grade.eq(2)
                        .and(member.motherCode.eq(supplierCode))
                        .and(member.classificationCode.eq(clientCode)))
                .fetchOne();
    }
}
