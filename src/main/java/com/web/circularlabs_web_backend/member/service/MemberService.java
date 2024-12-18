package com.web.circularlabs_web_backend.member.service;

import com.web.circularlabs_web_backend.member.domain.Member;
import com.web.circularlabs_web_backend.member.repository.MemberRepository;
import com.web.circularlabs_web_backend.member.request.MemberDetailRequestDto;
import com.web.circularlabs_web_backend.member.request.MemberInfoRequestDto;
import com.web.circularlabs_web_backend.member.request.MemberRegistRequestDto;
import com.web.circularlabs_web_backend.member.request.MemberUpdateClientDetailInfoRequestDto;
import com.web.circularlabs_web_backend.member.response.MemberClientResponseDto;
import com.web.circularlabs_web_backend.member.response.MemberRegistResponseDto;
import com.web.circularlabs_web_backend.member.response.MemberSupplierResponseDto;
import com.web.circularlabs_web_backend.product.domain.ClientProduct;
import com.web.circularlabs_web_backend.product.domain.Product;
import com.web.circularlabs_web_backend.product.domain.SupplyProduct;
import com.web.circularlabs_web_backend.product.repository.ClientProductRepository;
import com.web.circularlabs_web_backend.product.repository.SupplyProductRepository;
import com.web.circularlabs_web_backend.query.member.MemberQueryData;
import com.web.circularlabs_web_backend.query.product.ProductQueryData;
import com.web.circularlabs_web_backend.share.ResponseBody;
import com.web.circularlabs_web_backend.share.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Store;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;


@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {


    private final MemberRepository memberRepository;
    private final SupplyProductRepository supplyProductRepository;
    private final ClientProductRepository clientProductRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductQueryData productQueryData;
    private final MemberQueryData memberQueryData;
    private final EntityManager entityManager;

    //로그인 service
    public ResponseEntity<ResponseBody> memberRogin(String id, String password){
        Member roginResult = memberQueryData.getMemberRogin(id);
        
        if(passwordEncoder.matches(password, roginResult.getPassword())){
            return new ResponseEntity<>(new ResponseBody(StatusCode.OK, roginResult), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseBody(StatusCode.OK, "비밀번호를 확인 하세요"), HttpStatus.OK);
        }
        
    }
    // ID중복검사 service
    public ResponseEntity<ResponseBody> memberRegistcheckId(String RegistId){
        log.info("내정보 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, memberQueryData.memberRegistcheckId(RegistId)), HttpStatus.OK);
    }
    // 내정보 조회 service
    public ResponseEntity<ResponseBody> memberInfo(String classificationCode){
        log.info("내정보 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, memberQueryData.getmemberInfo(classificationCode)), HttpStatus.OK);
    }
    //회원탈퇴 service
    public ResponseEntity<ResponseBody> memberwithDrawal(String classificationCode){
        log.info("회원탈퇴 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, memberQueryData.memberwithDrawal(classificationCode)), HttpStatus.OK);
    }
    // 내정보 수정 service
    public ResponseEntity<ResponseBody> memberInfoUpdate(MemberInfoRequestDto MemberInfoRequestDto){
        log.info("내정보 수정 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, memberQueryData.getmemberInfoUpdate(MemberInfoRequestDto)), HttpStatus.OK);
    }
    //비밀번호 재설정 service
    public ResponseEntity<ResponseBody> passwordreset(String classificationCode, String newpassword){
        log.info("비밀번호 재설정 api - service");
        newpassword = passwordEncoder.encode(newpassword);
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, memberQueryData.passwordreset(classificationCode, newpassword)), HttpStatus.OK);
    }
    // 회원 가입 service
    public ResponseEntity<ResponseBody> memberRegist(MemberRegistRequestDto memberRegistRequestDto){
        log.info("회원 가입 api - service");

        // 고객사 / 공급사 고유 코드 발급
        String classificationCode = memberQueryData.newGetClassificationCode(memberRegistRequestDto.getGrade());

        // 사업사 등급에 따른 마더 코드 부여
        String motherCode = "";
        if(memberRegistRequestDto.getGrade() == 1){
            motherCode = "admin";
            for(int i = 0; i < memberRegistRequestDto.getProduct().size(); i++){
                SupplyProduct newsupplyproduct = SupplyProduct.builder()
                        .classificationCode(classificationCode)
                        .product(productQueryData.testGetproduct(Long.valueOf(memberRegistRequestDto.getProduct().get(i))))
                        .build();
                supplyProductRepository.save(newsupplyproduct);
            }
        }else if(memberRegistRequestDto.getGrade() == 2){
            motherCode = memberRegistRequestDto.getMotherCode();
            for(int i = 0; i < memberRegistRequestDto.getProduct().size(); i++) {
                ClientProduct newclientproduct = ClientProduct.builder()
                        .clientClassficationCode(classificationCode)
                        .supplyProduct(productQueryData.testGetSupplyproduct(Long.valueOf(memberRegistRequestDto.getProduct().get(i))))
                        .build();
                clientProductRepository.save(newclientproduct);
            }
        }

        // 회원 가입 정보들 기입 및 등록
        Member registMember = Member.builder()
                .grade(memberRegistRequestDto.getGrade())
                .motherCode(motherCode)
                .classificationCode(classificationCode)
                .loginId(memberRegistRequestDto.getLoginId())
                .password(passwordEncoder.encode(memberRegistRequestDto.getPassword()))
                .clientCompany(memberRegistRequestDto.getClientCompany())
                .address(memberRegistRequestDto.getAddress())
                .represent(memberRegistRequestDto.getRepresent())
                .businessNumber(memberRegistRequestDto.getBusinessNumber())
                .stateOfBusiness(memberRegistRequestDto.getStateOfBusiness())
                .categoryBusiness(memberRegistRequestDto.getCategoryBusiness())
                .bankName(memberRegistRequestDto.getBankName())
                .bankAccountNumber(memberRegistRequestDto.getBankAccountNumber())
                .bankAccountName(memberRegistRequestDto.getBankAccountName())
                .manager(memberRegistRequestDto.getManager())
                .phone(memberRegistRequestDto.getPhone())
                .position(memberRegistRequestDto.getPosition())
                .email(memberRegistRequestDto.getEmail())
                .etc(memberRegistRequestDto.getEtc())
                .withDrawal("N")
                .custType(memberRegistRequestDto.getCustType())
                .build();

        memberRepository.save(registMember);

        // 반환 객체 저장
        MemberRegistResponseDto responseDto = MemberRegistResponseDto.builder()
                .grade(registMember.getGrade())
                .motherCode(registMember.getMotherCode())
                .classificationCode(registMember.getClassificationCode())
                .loginId(registMember.getLoginId())
                .clientCompany(registMember.getClientCompany())
                .address(registMember.getAddress())
                .represent(registMember.getRepresent())
                .businessNumber(registMember.getBusinessNumber())
                .stateOfBusiness(registMember.getStateOfBusiness())
                .categoryBusiness(registMember.getCategoryBusiness())
                .bankName(registMember.getBankName())
                .bankAccountNumber(registMember.getBankAccountNumber())
                .bankAccountName(registMember.getBankAccountName())
                .manager(registMember.getManager())
                .phone(registMember.getPhone())
                .position(registMember.getPosition())
                .email(registMember.getEmail())
                .custType(registMember.getCustType())
                .build();

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, responseDto), HttpStatus.OK);
    }

    // 공급사 리스트 service
    public ResponseEntity<ResponseBody> memberSupplierList(String searchType, String searchWord, int page){
        log.info("공급사 리스트 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, memberQueryData.getSupplierList(searchType, searchWord, page)), HttpStatus.OK);

    }
    // 공급사 전체리스트 service
    public ResponseEntity<ResponseBody> memberSupplierAllList(){
        log.info("공급사 전체 리스트 api - service");
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, memberQueryData.getSupplierAllList()), HttpStatus.OK);

    }
    // 고객사 리스트 api
    public ResponseEntity<ResponseBody> memberClientAllList(){
        log.info("공급사 전체 리스트 api - service");
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, memberQueryData.getClientAllList()), HttpStatus.OK);

    }
    // 공급사 상세정보 service
    public ResponseEntity<ResponseBody>  memberSupplierDetail(String classificationCode){
        log.info("공급사 상세정보 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, memberQueryData.getSupplierDetail(classificationCode)), HttpStatus.OK);

    }

    // 공급사 정보 수정 service
    @Transactional
    public ResponseEntity<ResponseBody>  memberSupplierUpdate(MemberDetailRequestDto memberDetailRequestDto){
        log.info("공급사 수정 api - service");

        // 상품정보를 뺸 나머지 공급사 정보 업데이트
        long result = memberQueryData.getSupplierUpdate(memberDetailRequestDto);
        log.info("쿼리 성공 유무 : {}", result);

        // 상품정보 업데이트
        //기존에 가지고 있는 상품정보 리스트
        MemberSupplierResponseDto supplierProductResult = memberQueryData.getSupplierDetail(memberDetailRequestDto.getClassificationCode());
        List<SupplyProduct> supplierProductList =  supplierProductResult.getSupplierproduct();

        //기존 상품 리스트 productID
        List<Product> exsupplierProductList = new ArrayList<>();
        for (var j = 0; j < supplierProductList.size(); j++) {
            Long productId = Long.parseLong(String.valueOf(supplierProductList.get(j).getProduct().getProductId()));
            exsupplierProductList.add(productQueryData.testGetproduct(productId));
        }

        // [세훈쓰] 새롭게 요청받을 Product들을 담을 list
        List<Product> newsupplierProductList = new ArrayList<>();

        //새로 들어온 상품 리스트 productID
        for (var i = 0; i < memberDetailRequestDto.getSupplierproduct().size(); i++) {


            Long productId = Long.parseLong(memberDetailRequestDto.getSupplierproduct().get(i));
            newsupplierProductList.add(productQueryData.testGetproduct(productId));
        }
        List<Product> exsupplierProducttaget = new ArrayList<Product>(exsupplierProductList);
        List<Product> newsupplierProducttaget = new ArrayList<Product>(newsupplierProductList);

        exsupplierProducttaget.removeAll(newsupplierProductList);
        newsupplierProducttaget.removeAll(exsupplierProductList);


//        /// [세훈쓰] SupplyProduct 생성 로직

        for(int i = 0 ; i < newsupplierProducttaget.size() ; i++){
            SupplyProduct newsupplyproduct = SupplyProduct.builder()
                    .classificationCode(memberDetailRequestDto.getClassificationCode())
                    .product(newsupplierProducttaget.get(i))
                    .build();
            supplyProductRepository.save(newsupplyproduct);
        }

        for (var l = 0; l < exsupplierProducttaget.size(); l++) {
            memberQueryData.getSupplyProductDelte(memberDetailRequestDto.getClassificationCode(),exsupplierProducttaget.get(l).getProductId());
        }

        entityManager.flush();
        entityManager.clear();

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, "success"), HttpStatus.OK);

    }

    // 고객사 리스트 service
    public ResponseEntity<ResponseBody> memberClientList(String searchType, String searchWord, int page){
        log.info("고객사 리스트 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, memberQueryData.getClientList(searchType, searchWord, page)), HttpStatus.OK);

    }
    // 고객사 상세정보 service
    public ResponseEntity<ResponseBody>  memberClientDetail(String classificationCode, String motherCode){
        log.info("고객사 상세정보 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, memberQueryData.getClientDetail(classificationCode, motherCode)), HttpStatus.OK);

    }
    // 고객사 정보 수정 service
    @Transactional
    public ResponseEntity<ResponseBody>  memberClientUpdate(MemberDetailRequestDto memberDetailRequestDto){
        log.info("고객사 수정 api - service");

        // 상품정보를 뺸 나머지 고객사 정보 업데이트
        long result = memberQueryData.getSupplierUpdate(memberDetailRequestDto);
        log.info("쿼리 성공 유무 : {}", result);

        // 상품정보 업데이트
        //기존에 가지고 있는 상품정보 리스트
        MemberClientResponseDto clientProductResult = memberQueryData.getClientDetail(memberDetailRequestDto.getClassificationCode(), memberDetailRequestDto.getMotherCode());
        List<ClientProduct> clientProductList =  clientProductResult.getClientproduct();

        //기존 상품 리스트 productID
        List<SupplyProduct> exsupplierProductList = new ArrayList<>();
        for (var j = 0; j < clientProductList.size(); j++) {
            Long supplyproductId = Long.parseLong(String.valueOf(clientProductList.get(j).getSupplyProduct().getSupplyProductId()));
            exsupplierProductList.add(productQueryData.testGetSupplyproduct(supplyproductId));
        }

        // [세훈쓰] 새롭게 요청받을 Product들을 담을 list
        List<SupplyProduct> newsupplierProductList = new ArrayList<>();

        //새로 들어온 상품 리스트 productID
        for (var i = 0; i < memberDetailRequestDto.getSupplierproduct().size(); i++) {

            Long supplyproductId = Long.parseLong(memberDetailRequestDto.getSupplierproduct().get(i));
            newsupplierProductList.add(productQueryData.testGetSupplyproduct(supplyproductId));
        }
        List<SupplyProduct> exsupplierProducttaget = new ArrayList<SupplyProduct>(exsupplierProductList);
        List<SupplyProduct> newsupplierProducttaget = new ArrayList<SupplyProduct>(newsupplierProductList);

        exsupplierProducttaget.removeAll(newsupplierProductList);
        newsupplierProducttaget.removeAll(exsupplierProductList);


//        /// [세훈쓰] SupplyProduct 생성 로직

        for(int i = 0 ; i < newsupplierProducttaget.size() ; i++){
            ClientProduct newclientproduct = ClientProduct.builder()
                    .clientClassficationCode(memberDetailRequestDto.getClassificationCode())
                    .supplyProduct(newsupplierProducttaget.get(i))
                    .build();
            clientProductRepository.save(newclientproduct);
        }
        for (var l = 0; l < exsupplierProducttaget.size(); l++) {
            memberQueryData.getClientProductDelte(memberDetailRequestDto.getClassificationCode(),exsupplierProducttaget.get(l).getSupplyProductId());
        }

        entityManager.flush();
        entityManager.clear();

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, "success"), HttpStatus.OK);

    }

    // 공급사에 속한 고객사 리스트 조회 service
    public ResponseEntity<ResponseBody> memberClientScAllList(String classificationCode){
        log.info("공급사에 속한 고객사 리스트 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, memberQueryData.memberClientScAllList(classificationCode)), HttpStatus.OK);
    }


    // 공급사에 속한 고객사 리스트 조회 service
    public ResponseEntity<ResponseBody> getClientsOfSupplier(String supplierCode, int page, String searchType, String searchWord){
        log.info("공급사에 속한 고객사 리스트 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, memberQueryData.getClientsOfSupplier(supplierCode, page, searchType, searchWord)), HttpStatus.OK);
    }


    // 공급사에 속한 특정 고객사 상세 정보 조회 service
    public ResponseEntity<ResponseBody> getClientDetailInfo(String supplierCode, String clientCode){
        log.info("공급사에 속한 특정 고객사 상세 정보 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, memberQueryData.getClientDetailInfo(supplierCode, clientCode)), HttpStatus.OK);
    }


    // 공급사 측에서 특정 고객사 정보 수정 service
    @Transactional
    public ResponseEntity<ResponseBody> updateClientDetailInfo(
            String supplierCode,
            String clientCode,
            MemberUpdateClientDetailInfoRequestDto updateClientDetailInfoRequestDto){
        log.info("공급사 측에서 특정 고객사 정보 수정 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, memberQueryData.updateClientDetailInfo(supplierCode, clientCode, updateClientDetailInfoRequestDto)), HttpStatus.OK);
    }

}
