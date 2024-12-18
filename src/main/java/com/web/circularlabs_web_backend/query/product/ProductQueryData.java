package com.web.circularlabs_web_backend.query.product;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.web.circularlabs_web_backend.member.domain.Member;
import com.web.circularlabs_web_backend.product.domain.Product;
import com.web.circularlabs_web_backend.product.domain.ProductDetail;
import com.web.circularlabs_web_backend.product.domain.ProductDetailHistory;
import com.web.circularlabs_web_backend.product.domain.SupplyProduct;
import com.web.circularlabs_web_backend.product.request.ProductUpdateRequestDto;
import com.web.circularlabs_web_backend.product.request.updateSupplyProductInfoRequestDto;
import com.web.circularlabs_web_backend.product.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.web.circularlabs_web_backend.member.domain.QMember.member;
import static com.web.circularlabs_web_backend.product.domain.QClientProduct.clientProduct;
import static com.web.circularlabs_web_backend.product.domain.QProduct.product;
import static com.web.circularlabs_web_backend.product.domain.QProductDetail.productDetail;
import static com.web.circularlabs_web_backend.product.domain.QSupplyProduct.supplyProduct;
import static com.web.circularlabs_web_backend.product.domain.QProductDetailHistory.productDetailHistory;
import static com.web.circularlabs_web_backend.process.domain.QSupplierOrder.supplierOrder;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductQueryData {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    // 제품 등록 시 발급될 제품 고유 코드 값을 부여하기 위한 제품 데이터 조회
    public String getProductCode() {

        StringBuilder productCode = new StringBuilder("PC");

        Long productCount = jpaQueryFactory
                .select(product.count())
                .from(product)
                .limit(1)
                .fetchOne();

        if (productCount != null) {
            int remainCode = 7 - productCount.toString().length();

            for (int i = 0; i < remainCode; i++) {
                String zeroCode = "0";
                productCode.append(zeroCode);
            }

            productCode.append(productCount);
        } else {
            productCode.append("0000001");
        }

        return productCode.toString();
    }

    //상품 삭제
    @Transactional
    public Long ProductDelete(String productId) {
        List<SupplyProduct> sresult = new ArrayList<>();
        Long result = 0L;

        Product deleteProduct = jpaQueryFactory
                .selectFrom(product)
                .where(product.productId.eq(Long.parseLong(productId)))
                .fetchOne();

        List<SupplyProduct> deleteSupplyProducts = jpaQueryFactory
                .selectFrom(supplyProduct)
                .where(supplyProduct.product.eq(deleteProduct))
                .fetch();

        deleteSupplyProducts.forEach(eachSupplyProduct -> {
            jpaQueryFactory
                    .delete(clientProduct)
                    .where(clientProduct.supplyProduct.eq(eachSupplyProduct))
                    .execute();

            jpaQueryFactory
                    .delete(supplyProduct)
                    .where(supplyProduct.supplyProductId.eq(eachSupplyProduct.getSupplyProductId()))
                    .execute();
        });

        Long deleteResult = jpaQueryFactory
                .delete(product)
                .where(product.productId.eq(deleteProduct.getProductId()))
                .execute();

        entityManager.flush();
        entityManager.clear();

        return deleteResult;
    }

    // 제품 객체 호출
    public Product testGetproduct(Long productId) {
        return jpaQueryFactory
                .selectFrom(product)
                .where(product.productId.eq(productId))
                .fetchOne();
    }

    // 공급사 제품 객체 호출
    public SupplyProduct testGetSupplyproduct(Long supplyproductId) {
        return jpaQueryFactory
                .selectFrom(supplyProduct)
                .where(supplyProduct.supplyProductId.eq(supplyproductId))
                .fetchOne();
    }

    //제품 전체리스트(관리사)
    public List<Product> getProductAllList() {
        return jpaQueryFactory
                .selectFrom(product)
                .fetch();
    }

    /**
    //제품 리스트(관리사)
    public List<Product> getProductList(int page) {
        return jpaQueryFactory
                .selectFrom(product)
                .limit(10)
                .offset(page)
                .fetch();
    }
     **/

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //제품 리스트(관리사)
    public List<Product> newGetProductList(int page) {
        return jpaQueryFactory
                .selectFrom(product)
                .orderBy(product.createdAt.desc())
                .offset(page)
                .limit(10)
                .fetch();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //제품 정보 수정(관리사)
    @Transactional
    public void getProductUpdate(ProductUpdateRequestDto ProductUpdateRequestDto, HashMap<String, String> imageUploadInfo, String productImgName) {

        Product updateProduct = jpaQueryFactory
                .selectFrom(product)
                .where(product.productId.eq(ProductUpdateRequestDto.getProductid()))
                .fetchOne();

        JPAUpdateClause clause = jpaQueryFactory
                .update(product)
                .where(product.productId.eq(ProductUpdateRequestDto.getProductid()));

        boolean existUpdateContent = false;

        // 제품 명 수정
        if (!ProductUpdateRequestDto.getProductName().isEmpty()) {
            existUpdateContent = true;
            clause.set(product.productName, ProductUpdateRequestDto.getProductName());
        }

        // 제품 가격 수정
        if (ProductUpdateRequestDto.getPurchasePrice() != 0) {
            existUpdateContent = true;
            clause.set(product.purchasePrice, ProductUpdateRequestDto.getPurchasePrice());
        }

        // 제품 상태 수정
        if (!ProductUpdateRequestDto.getStatus().isEmpty()) {
            existUpdateContent = true;
            clause.set(product.status, ProductUpdateRequestDto.getStatus());
        }
        // 제품 구매 단위 수정
        if (ProductUpdateRequestDto.getProductQtt() != 0) {
            existUpdateContent = true;
            clause.set(product.productQtt, ProductUpdateRequestDto.getProductQtt());
        }
        // 이미지 수정
        if (!imageUploadInfo.isEmpty()) {
            existUpdateContent = true;

            assert updateProduct != null;

            if (!updateProduct.getProductImgUrl().isEmpty()) {
                String prevImg = updateProduct.getProductImgUrl().substring(33);
                log.info("삭제될 기존 이미지 파일의 난수화 명 : {}", prevImg);

                File deleteImage = new File("/home/circularlabs/web/image/" + prevImg);
                if (deleteImage.delete()) {
                    log.info("기존에 이미 이미지가 존재할 경우 삭제");
                }
            }

            clause.set(product.productImgName, productImgName);
            clause.set(product.productImgUrl, imageUploadInfo.get("uploadUrl"));
        }

        if (existUpdateContent) {
            log.info("수정 성공");
            clause.execute();
        } else {
            log.info("수정 실패");
        }
    }

    /**
    //상세 제품 리스트(관리사)
    public ProductDetailPagingListResponseDto getDetailProductList(int page, String supplierCode) {
        List<ProductDetail> result = new ArrayList<>();
        List<ProductDetailEachResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(productDetail.count())
                .from(productDetail)
                .where(productDetail.supplierCode.eq(supplierCode))
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(productDetail)
                .where(productDetail.supplierCode.eq(supplierCode))
                .limit(10)
                .offset(page)
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    ProductDetailEachResponseDto.builder()
                            .rfidChipCode(result.get(i).getRfidChipCode())
                            .productSerialCode(result.get(i).getProductSerialCode())
                            .productCode(result.get(i).getProductCode())
                            .supplierCode(result.get(i).getSupplierCode())
                            .clientCode(result.get(i).getClientCode())
                            .cycle(result.get(i).getCycle())
                            .latestReadingAt(result.get(i).getLatestReadingAt())
                            .build()
            );

        }
        return ProductDetailPagingListResponseDto.builder()
                .productDetailList(eachResponseDtos)
                .paging(pageing)
                .build();
    }
     **/

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 관리자 상품 상세 이력 리스트 쿼리 함수 [개선]
    public ProductDetailPagingListResponseDto newGetDetailProductList(int page, String supplierCode) {
        List<ProductDetailEachResponseDto> productDetailListResult = new ArrayList<>();

//        List<ProductDetail> productDetailList = jpaQueryFactory
//                .selectFrom(productDetail)
//                .where(productDetail.productDetailId.goe(0L)
//                        .and(eqProductDetailSupplier(supplierCode)))
//                .orderBy(productDetail.createdAt.desc())
//                .fetch()
//                .stream()
//                .map(ProductDetail::getProductSerialCode)
//                .distinct()
//                .toList()
//                .stream()
//                .map(eachProductDetailCode ->
//                        jpaQueryFactory
//                                .selectFrom(productDetail)
//                                .where(productDetail.productSerialCode.eq(eachProductDetailCode))
//                                .fetchOne()
//                )
//                .collect(Collectors.toList());


        List<ProductDetail> productDetailListv2 = jpaQueryFactory
                .selectFrom(productDetail)
                .where(productDetail.productDetailId.goe(0L)
                        .and(eqProductDetailSupplier(supplierCode)))
                .orderBy(productDetail.createdAt.desc())
                .limit(10)
                .offset(page)
                .fetch();

       Long productDetailListv2count = jpaQueryFactory
                .select(productDetail.count())
               .from(productDetail)
                .where(productDetail.productDetailId.goe(0L)
                        .and(eqProductDetailSupplier(supplierCode)))
                .fetchOne();

//        long productDetailCount = productDetailList.size();
//
        if (!productDetailListv2.isEmpty()) {
//            if (productDetailList.size() >= 10) {
//                if ((page * 10L) <= productDetailCount) {
//                    productDetailList = productDetailList.subList((page * 10) - 10, page * 10);
//                } else {
//                    productDetailList = productDetailList.subList((page * 10) - 10, productDetailList.size());
//                }
//            } else {
//                productDetailList = productDetailList.subList((page * 10) - 10, productDetailList.size());
//            }
//
            productDetailListResult = productDetailListv2.stream()
                    .map(eachProductDetail ->
                            ProductDetailEachResponseDto.builder()
                                    .rfidChipCode(eachProductDetail.getRfidChipCode())
                                    .productSerialCode(eachProductDetail.getProductSerialCode())
                                    .productCode(eachProductDetail.getProductCode())
                                    .supplierCode(eachProductDetail.getSupplierCode())
                                    .clientCode(eachProductDetail.getClientCode())
                                    .cycle(eachProductDetail.getCycle())
                                    .latestReadingAt(eachProductDetail.getLatestReadingAt())
                                    .build()
                    )
                    .collect(Collectors.toList());
        }

        return ProductDetailPagingListResponseDto.builder()
                .productDetailList(productDetailListResult)
                .paging(productDetailListv2count)
                .build();
    }

    private BooleanExpression eqProductDetailSupplier(String supplierCode) {

        if (supplierCode != null || !supplierCode.isEmpty()) {
            return productDetail.supplierCode.eq(supplierCode);
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //상세 제품 리스트(관리사)
    public ProductDetailPagingListResponseDto selectDetailProductXlsx(String supplierCode) {
        List<ProductDetail> result = new ArrayList<>();
        List<ProductDetailEachResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        result = jpaQueryFactory
                .selectFrom(productDetail)
                .where(productDetail.supplierCode.eq(supplierCode))
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    ProductDetailEachResponseDto.builder()
                            .rfidChipCode(result.get(i).getRfidChipCode())
                            .productSerialCode(result.get(i).getProductSerialCode())
                            .productCode(result.get(i).getProductCode())
                            .supplierCode(result.get(i).getSupplierCode())
                            .clientCode(result.get(i).getClientCode())
                            .cycle(result.get(i).getCycle())
                            .latestReadingAt(result.get(i).getLatestReadingAt())
                            .build()
            );

        }
        return ProductDetailPagingListResponseDto.builder()
                .productDetailList(eachResponseDtos)
                .build();
    }

    //상세 상품 상세정보 조회
    public List<ProductDetail> getDetailProductInfo(String productSerialCode) {
        return jpaQueryFactory
                .selectFrom(productDetail)
                .where(productDetail.productSerialCode.eq(productSerialCode))
                .fetch();
    }

    /**
    //상세 상품 기록 조회
    public ProductDetailHisPagingListResponseDto getDetailProductHistoryList(String productSerialCode, int page) {
        List<ProductDetailHistory> result = new ArrayList<>();
        List<ProductDetailHisEachResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(productDetailHistory.count())
                .from(productDetailHistory)
                .where(productDetailHistory.productSerialCode.eq(productSerialCode))
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(productDetailHistory)
                .where(productDetailHistory.productSerialCode.eq(productSerialCode))
                .limit(10)
                .offset(page)
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    ProductDetailHisEachResponseDto.builder()
                            .rfidChipCode(result.get(i).getRfidChipCode())
                            .productSerialCode(result.get(i).getProductSerialCode())
                            .productCode(result.get(i).getProductCode())
                            .supplierCode(result.get(i).getSupplierCode())
                            .clientCode(result.get(i).getClientCode())
                            .status(result.get(i).getStatus())
                            .cycle(result.get(i).getCycle())
                            .latestReadingAt(result.get(i).getLatestReadingAt())
                            .build()
            );

        }
        return ProductDetailHisPagingListResponseDto.builder()
                .productDetailHisList(eachResponseDtos)
                .paging(pageing)
                .build();

    }
     **/

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 관리자 특정 상품 상세 이력의 내부 상세 이력 리스트 조회 쿼리 함수 [개선]
    public ProductDetailHisPagingListResponseDto newGetDetailProductHistoryList(String productSerialCode, int page) {

        List<ProductDetailHisEachResponseDto> productDetailHistoryListResult = new ArrayList<>();

        List<ProductDetailHistory> productDetailHistoryList = jpaQueryFactory
                .selectFrom(productDetailHistory)
                .where(productDetailHistory.productDetailHistoryId.goe(0L)
                        .and(eqProductDetailHistoryOfProductSerialCode(productSerialCode)))
                .orderBy(productDetailHistory.createdAt.desc())
                .fetch();

        long productDetailHistoryCount = productDetailHistoryList.size();

        if (!productDetailHistoryList.isEmpty()) {
            if (productDetailHistoryList.size() >= 10) {
                if ((page * 10L) <= productDetailHistoryCount) {
                    productDetailHistoryList = productDetailHistoryList.subList((page * 10) - 10, page * 10);
                } else {
                    productDetailHistoryList = productDetailHistoryList.subList((page * 10) - 10, productDetailHistoryList.size());
                }
            } else {
                productDetailHistoryList = productDetailHistoryList.subList((page * 10) - 10, productDetailHistoryList.size());
            }

            productDetailHistoryListResult = productDetailHistoryList.stream()
                    .map(eachProductDetailHistory ->
                            ProductDetailHisEachResponseDto.builder()
                                    .rfidChipCode(eachProductDetailHistory.getRfidChipCode())
                                    .productSerialCode(eachProductDetailHistory.getProductSerialCode())
                                    .productCode(eachProductDetailHistory.getProductCode())
                                    .supplierCode(eachProductDetailHistory.getSupplierCode())
                                    .clientCode(eachProductDetailHistory.getClientCode())
                                    .status(eachProductDetailHistory.getStatus())
                                    .cycle(eachProductDetailHistory.getCycle())
                                    .latestReadingAt(eachProductDetailHistory.getLatestReadingAt())
                                    .build()
                    )
                    .collect(Collectors.toList());
        }

        return ProductDetailHisPagingListResponseDto.builder()
                .productDetailHisList(productDetailHistoryListResult)
                .paging(productDetailHistoryCount)
                .build();

    }

    private BooleanExpression eqProductDetailHistoryOfProductSerialCode(String productSerialCode){

        if(productSerialCode != null || !productSerialCode.isEmpty()){
            return productDetailHistory.productSerialCode.eq(productSerialCode);
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 고객사가 보유한 제품들 리스트
    public List<ClientProductResponseDto> getClientProducts(Member client) {
        List<ClientProductResponseDto> clientProducts = new ArrayList<>();

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
                .where(clientProduct.clientClassficationCode.eq(client.getClassificationCode()))
                .fetch();

        if (!clientProductsInfo.isEmpty()) {
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
        }

        return clientProducts;

    }

    //공급사에 재고 리스트 조회
    public ProductStockpagingResponseDto selectStockList(String classificationCode, int page) {
        List<Tuple> result = new ArrayList<>();
        List<String> pageing = new ArrayList<>();
        List<ProductStockEachResponseDto> eachResponseDtos = new ArrayList<>();
        // Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(supplierOrder.productCode)
                .from(supplierOrder)
                .groupBy(supplierOrder.productCode)
                .where(supplierOrder.classificationCode.eq(classificationCode))
                .fetch();

        result = jpaQueryFactory
                .select(supplierOrder.productCode, supplierOrder.orderMount.sum().as("sum"))
                .from(supplierOrder)
                .groupBy(supplierOrder.productCode)
                .where(supplierOrder.classificationCode.eq(classificationCode))
                .limit(10)
                .offset(page)
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    ProductStockEachResponseDto.builder()
                            .productCode(result.get(i).get(supplierOrder.productCode))
                            .sum(result.get(i).get(supplierOrder.orderMount.sum().as("sum")))
                            .build()
            );

        }
        return ProductStockpagingResponseDto.builder()
                .productStockList(eachResponseDtos)
                .paging((long) pageing.size())
                .build();

    }


    //공급사에 재고 리스트 조회
    public ProductStockpagingResponseDto selectStockXlsxList(String classificationCode) {
        List<Tuple> result = new ArrayList<>();
        List<String> pageing = new ArrayList<>();
        List<ProductStockEachResponseDto> eachResponseDtos = new ArrayList<>();
        // Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(supplierOrder.productCode)
                .from(supplierOrder)
                .groupBy(supplierOrder.productCode)
                .where(supplierOrder.classificationCode.eq(classificationCode))
                .fetch();

        result = jpaQueryFactory
                .select(supplierOrder.productCode, supplierOrder.orderMount.sum().as("sum"))
                .from(supplierOrder)
                .groupBy(supplierOrder.productCode)
                .where(supplierOrder.classificationCode.eq(classificationCode))
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    ProductStockEachResponseDto.builder()
                            .productCode(result.get(i).get(supplierOrder.productCode))
                            .sum(result.get(i).get(supplierOrder.orderMount.sum().as("sum")))
                            .build()
            );

        }
        return ProductStockpagingResponseDto.builder()
                .productStockList(eachResponseDtos)
                .paging((long) pageing.size())
                .build();

    }

    // 현재 로그인한 공급사가 가지고 있는 제품들 리스트 조회
    public List<SupplyProductListResponseDto> getSupplyProductList(String classificationCode) {

        // 공급사가 가지고 있는 제품들 리스트 호출
        List<SupplyProduct> supplyProducts = jpaQueryFactory
                .selectFrom(supplyProduct)
                .where(supplyProduct.classificationCode.eq(classificationCode))
                .fetch();

        // 확인용 반환 리스트 객체 생성
        List<SupplyProductListResponseDto> responseSupplyProductList = new ArrayList<>();

        // 호출한 제품들 리스트를 하나씩 조회하며 반환 리스트 객체에 저장
        for (SupplyProduct eachSupplyProduct : supplyProducts) {
            responseSupplyProductList.add(
                    SupplyProductListResponseDto.builder()
                            .supplyProductId(eachSupplyProduct.getSupplyProductId())
                            .productCode(eachSupplyProduct.getProduct().getProductCode())
                            .productName(eachSupplyProduct.getProduct().getProductName())
                            .rentalPrice(eachSupplyProduct.getRentalPrice())
                            .build()
            );
        }

        return responseSupplyProductList;
    }


    // 제품 관리에서 공급사가 보유한 모든 제품 리스트 조회
    public TotalAllSupplyProductResponseDto getAllSupplierProducts(String classificationCode, int page) {

        Long supplyProductCount = jpaQueryFactory
                .select(supplyProduct.count())
                .from(supplyProduct)
                .where(supplyProduct.classificationCode.eq(classificationCode))
                .limit(1)
                .fetchOne();


        // 확인용 반환 리스트 객체 생성
        List<AllSupplyProductResponseDto> responseAllSupplyProducts = new ArrayList<>();

        // 공급사가 가지고 있는 모든 제품들 호출 조회
        List<SupplyProduct> allSupplyProducts = jpaQueryFactory
                .selectFrom(supplyProduct)
                .where(supplyProduct.classificationCode.eq(classificationCode))
                .offset((page * 10L) - 10)
                .limit(10)
                .orderBy(supplyProduct.createdAt.desc())
                .fetch();

        // 조회한 제품들을 반환 리스트 객체에 필요한 정보들만 담아 저장
        for (SupplyProduct eachProduct : allSupplyProducts) {
            responseAllSupplyProducts.add(
                    AllSupplyProductResponseDto.builder()
                            .supplyProductId(eachProduct.getSupplyProductId())
                            .productCode(eachProduct.getProduct().getProductCode())
                            .productName(eachProduct.getProduct().getProductName())
                            .purchasePrice(eachProduct.getProduct().getPurchasePrice())
                            .rentalPrice(eachProduct.getRentalPrice())
                            .maximumOrderMount(eachProduct.getMaximumOrderMount())
                            .productQtt(eachProduct.getProduct().getProductQtt())
                            .status(eachProduct.getProduct().getStatus())
                            .createdAt(eachProduct.getCreatedAt().toString())
                            .build()
            );
        }

        return TotalAllSupplyProductResponseDto.builder()
                .supplyProductCount(supplyProductCount)
                .allSupplyProducts(responseAllSupplyProducts)
                .build();
    }


    // 공급사가 가진 특정 제품 정보 수정 (렌탈 비용 / 최대 주문량)
    @Transactional
    public AllSupplyProductResponseDto updateSupplyProductInfo(updateSupplyProductInfoRequestDto updateInfo) {

        // 수정하고자 하는 공급사 제품 호출
        SupplyProduct prevSupplyProduct = jpaQueryFactory
                .selectFrom(supplyProduct)
                .where(supplyProduct.supplyProductId.eq(updateInfo.getSupplyProductId())
                        .and(supplyProduct.classificationCode.eq(updateInfo.getSupplierClassificationCode())))
                .fetchOne();

        // 공급사 제품을 JPAUpdateClause를 통해 수정 준비
        assert prevSupplyProduct != null;
        JPAUpdateClause clause = jpaQueryFactory
                .update(supplyProduct)
                .where(supplyProduct.classificationCode.eq(prevSupplyProduct.getClassificationCode())
                        .and(supplyProduct.supplyProductId.eq(prevSupplyProduct.getSupplyProductId())));

        // 수정 여지가 있는지 확인하는 boolean 판별값
        boolean existDifference = false;

        // 수정하고자 기입한 렌탈 비용과 기존의 렌탈 비용이 다르고 기입한 수정 렌탈 비용이 0원이 아닐 경우 수정하도록 설정
        if (prevSupplyProduct.getRentalPrice() != updateInfo.getRentalPrice() && updateInfo.getRentalPrice() != 0) {
            clause.set(supplyProduct.rentalPrice, updateInfo.getRentalPrice());

            // 수정 여지 true 변환
            existDifference = true;
        }

        // 수정하고자 기입한 최대 주문 수량과 기존의 최대 주문 수량이 다르고 기입한 수정 최대 주문 수량이 0원이 아닐 경우 수정하도록 설정
        if (prevSupplyProduct.getMaximumOrderMount() != updateInfo.getMaximumOrderMount() && updateInfo.getMaximumOrderMount() != 0) {
            clause.set(supplyProduct.maximumOrderMount, updateInfo.getMaximumOrderMount());

            // 수정 여지 true 변환
            existDifference = true;
        }

        // 수정 여지가 있을 경우
        if (existDifference) {
            // 수정 업데이트
            clause.execute();

            // 실 DB 반영
            entityManager.flush();
            entityManager.clear();

            // 반영된 상태의 공급사 제품 호출 조회
            SupplyProduct updateSupplyProduct = jpaQueryFactory
                    .selectFrom(supplyProduct)
                    .where(supplyProduct.supplyProductId.eq(prevSupplyProduct.getSupplyProductId())
                            .and(supplyProduct.classificationCode.eq(prevSupplyProduct.getClassificationCode())))
                    .fetchOne();

            // 반환 객체에 정보 저장
            assert updateSupplyProduct != null;
            return AllSupplyProductResponseDto.builder()
                    .supplyProductId(updateSupplyProduct.getSupplyProductId())
                    .productCode(updateSupplyProduct.getProduct().getProductCode())
                    .productName(updateSupplyProduct.getProduct().getProductName())
                    .purchasePrice(updateSupplyProduct.getRentalPrice())
                    .rentalPrice(updateSupplyProduct.getRentalPrice())
                    .maximumOrderMount(updateSupplyProduct.getMaximumOrderMount())
                    .status(updateSupplyProduct.getProduct().getStatus())
                    .createdAt(updateSupplyProduct.getCreatedAt().toString())
                    .build();

        } else { // 수정 여지가 없을 경우 null 값 반환
            return null;
        }
    }


    // 각 제품 스캔 이력 리스트 조회
    public ProductHistoryListResponseDto getProductScanHistory(String classificationCode, int page) {

        List<ProductDetail> result = new ArrayList<>();
        List<ProductHistoryResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(productDetail.count())
                .from(productDetail)
                .where(productDetail.supplierCode.eq(classificationCode))
                .fetchOne();

        // 공급사가 가진 제품 이력 리스트 조회
        result = jpaQueryFactory
                .selectFrom(productDetail)
                .where(productDetail.supplierCode.eq(classificationCode))
                .offset((page * 10L) - 10)
                .limit(10)
                .orderBy(productDetail.latestReadingAt.desc())
                .fetch();

        // 최종 반환 리스트 객체 생성
        List<ProductScanHistoryResponseDto> responseProductScanHistory = new ArrayList<>();

        // 제품 이력 리스트가 존재할 경우
        if (!result.isEmpty()) {
            // 제품 이력 리스트 하나씩 조회
            for (ProductDetail eachProductScanHistory : result) {

                // 제품 코드 추출
                String productName = jpaQueryFactory
                        .select(product.productName)
                        .from(product)
                        .where(product.productCode.eq(eachProductScanHistory.getProductCode()))
                        .fetchOne();

                // 반환 리스트 객체에 정보 저장
                responseProductScanHistory.add(
                        ProductScanHistoryResponseDto.builder()
                                .productDetailId(eachProductScanHistory.getProductDetailId())
                                .productCode(eachProductScanHistory.getProductCode())
                                .productName(productName)
                                .productSerialCode(eachProductScanHistory.getProductSerialCode())
                                .status(eachProductScanHistory.getStatus())
                                .cycle(eachProductScanHistory.getCycle())
                                .latestReadingAt(eachProductScanHistory.getLatestReadingAt())
                                .build()
                );
            }
        }

        return ProductHistoryListResponseDto.builder()
                .productHisList(responseProductScanHistory)
                .paging(pageing)
                .build();
    }


    // 각 제품 심화 스캔 상세 이력 리스트 조회
    public TotalProductScanDetailHistoryResponseDto getProductScanDetailHistory(String classificationCode, String productCode, String productSerialCode, int page) {

        // 선택한 제품 시리얼 코드를 가진 제품의 제품명, 제품 코드, 시리얼 코드, 사이클 정보 추출
        Tuple productHistory = jpaQueryFactory
                .select(product.productName.as("productName"),
                        product.productCode.as("productCode"),
                        productDetail.productSerialCode.as("productSerialCode"),
                        productDetail.cycle.as("cycle"))
                .from(product)
                .innerJoin(productDetail)
                .on(product.productCode.eq(productDetail.productCode))
                .where(productDetail.productSerialCode.eq(productSerialCode))
                .limit(1)
                .fetchOne();

        // 선택한 제품의 각 제품 스캔 심화 상세 이력 총 카운트
        Long totalCount = jpaQueryFactory
                .select(productDetailHistory.count())
                .from(productDetailHistory)
                .where(productDetailHistory.productSerialCode.eq(productSerialCode)
                        .and(productDetailHistory.productCode.eq(productCode))
                        .and(productDetailHistory.supplierCode.eq(classificationCode)))
                .fetchOne();

        // 선택한 제품의 각 제품 스캔 심화 상세 이력 리스트
        List<ProductDetailHistory> productDetailHistories = jpaQueryFactory
                .selectFrom(productDetailHistory)
                .where(productDetailHistory.productSerialCode.eq(productSerialCode)
                        .and(productDetailHistory.productCode.eq(productCode))
                        .and(productDetailHistory.supplierCode.eq(classificationCode)))
                .offset((page * 10L) - 10)
                .limit(10)
                .orderBy(productDetailHistory.latestReadingAt.desc())
                .fetch();

        // 반환용 리스트 객체 생성
        List<ProductScanDetailHistoryResponseDto> productScanDetailHistoryResponseDtos = new ArrayList<>();

        // 조회한 선택한 제품의 각 제품 스캔 심화 상세 이력 리스트를 하나씩 조회
        for (ProductDetailHistory eachDetailHistory : productDetailHistories) {

            // 이력에 해당되는 고객사의 구분 코드, 상호명 추출
            Tuple clientInfo = jpaQueryFactory
                    .select(member.classificationCode, member.clientCompany)
                    .from(member)
                    .where(member.classificationCode.eq(eachDetailHistory.getClientCode())
                            .and(member.grade.eq(2)))
                    .fetchOne();

            // 반환 리스트 객체에 정보 저장
            assert clientInfo != null;
            productScanDetailHistoryResponseDtos.add(
                    ProductScanDetailHistoryResponseDto.builder()
                            .productDetailHistoryId(eachDetailHistory.getProductDetailHistoryId())
                            .clientCode(clientInfo.get(member.classificationCode))
                            .clientName(clientInfo.get(member.clientCompany))
                            .status(eachDetailHistory.getStatus())
                            .latestReadingAt(eachDetailHistory.getLatestReadingAt())
                            .build()
            );
        }

        // 최종 반환 객체에 정보 저장 후 반환
        assert productHistory != null;
        return TotalProductScanDetailHistoryResponseDto.builder()
                .totalCount(totalCount)
                .productCode(productHistory.get(product.productCode.as("productCode")))
                .productName(productHistory.get(product.productName.as("productName")))
                .productSerialCode(productHistory.get(productDetail.productSerialCode.as("productSerialCode")))
                .cycle(productHistory.get(productDetail.cycle.as("cycle")))
                .productScanDetailHistory(productScanDetailHistoryResponseDtos)
                .build();
    }

    public List<ClientOrderProductListResponseDto> getClientProductList(String classificationCode) {
        List<ClientOrderProductListResponseDto> clientProducts = new ArrayList<>();

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
                        clientProduct.supplyProduct.product.status,
                        clientProduct.supplyProduct.product.productImgUrl,
                        clientProduct.supplyProduct.product.productQtt)
                .from(clientProduct)
                .where(clientProduct.clientClassficationCode.eq(classificationCode))
                .fetch();

        if (!clientProductsInfo.isEmpty()) {
            for (int i = 0; i < clientProductsInfo.size(); i++) {
                clientProducts.add(
                        ClientOrderProductListResponseDto.builder()
                                .clientProductId(clientProductsInfo.get(i).get(clientProduct.clientProductId))
                                .clientClassificationCode(clientProductsInfo.get(i).get(clientProduct.clientClassficationCode))
                                .supplyProductId(clientProductsInfo.get(i).get(clientProduct.supplyProduct.supplyProductId))
                                .supplierClassificationCode(clientProductsInfo.get(i).get(clientProduct.supplyProduct.classificationCode))
                                .productName(clientProductsInfo.get(i).get(clientProduct.supplyProduct.product.productName))
                                .productQtt(clientProductsInfo.get(i).get(clientProduct.supplyProduct.product.productQtt))
                                .productCode(clientProductsInfo.get(i).get(clientProduct.supplyProduct.product.productCode))
                                .purchasePrice(clientProductsInfo.get(i).get(clientProduct.supplyProduct.product.purchasePrice))
                                .rentalPrice(clientProductsInfo.get(i).get(clientProduct.supplyProduct.rentalPrice))
                                .maximumOrderMount(clientProductsInfo.get(i).get(clientProduct.supplyProduct.maximumOrderMount))
                                .status(clientProductsInfo.get(i).get(clientProduct.supplyProduct.product.status))
                                .productImgUrl(clientProductsInfo.get(i).get(clientProduct.supplyProduct.product.productImgUrl))
                                .build()
                );
            }
        }

        return clientProducts;

    }

}
