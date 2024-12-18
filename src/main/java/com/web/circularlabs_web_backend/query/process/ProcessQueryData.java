package com.web.circularlabs_web_backend.query.process;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.web.circularlabs_web_backend.process.domain.ClientOrder;
import com.web.circularlabs_web_backend.process.domain.DiscardHistory;
import com.web.circularlabs_web_backend.process.domain.Recall;
import com.web.circularlabs_web_backend.process.domain.SupplierOrder;
import com.web.circularlabs_web_backend.process.repository.ClientOrderRepository;
import com.web.circularlabs_web_backend.process.repository.RecallRepository;
import com.web.circularlabs_web_backend.process.repository.SupplierOrderRepository;
import com.web.circularlabs_web_backend.process.request.*;
import com.web.circularlabs_web_backend.process.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.web.circularlabs_web_backend.member.domain.QMember.member;
import static com.web.circularlabs_web_backend.process.domain.QSupplierOrder.supplierOrder;
import static com.web.circularlabs_web_backend.product.domain.QSupplyProduct.supplyProduct;
import static com.web.circularlabs_web_backend.process.domain.QRecall.recall;
import static com.web.circularlabs_web_backend.process.domain.QClientOrder.clientOrder;
import static com.web.circularlabs_web_backend.process.domain.QDiscardHistory.discardHistory;
import static com.web.circularlabs_web_backend.device.domain.QRfidScanHistory.rfidScanHistory;
import static com.web.circularlabs_web_backend.product.domain.QProduct.product;
import static com.web.circularlabs_web_backend.product.domain.QProductDetail.productDetail;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessQueryData {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;
    private final SupplierOrderRepository supplierOrderRepository;
    private final RecallRepository recallRepository;
    private final ClientOrderRepository clientOrderRepository;

    // 공급사에 재고 관리 리스트
    public List<SupplierOrder> getSupplierStockList(String classificationCode, int page) {

        List<SupplierOrder> result = jpaQueryFactory
                .selectFrom(supplierOrder)
                .where(supplierOrder.classificationCode.eq(classificationCode))
                .groupBy(supplierOrder.productCode)
                .fetch();

        return result;
    }


    // 공급사 주문 요청 등록 (관리사에게 제품 발주 요청)
    public SupplierOrderResponseDto supplierOrder(SupllierOrderRequestDto supllierOrderRequestDto) {

        // 주문 요청받은 제품들의 정보 리스트
        List<SupplierProductListRequestDto> orderSupplyProducts = supllierOrderRequestDto.getOrderSupplyProducts();
        // 확인용 반환 리스트 객체 생성
        List<SupplierOrderProductListResponseDto> orderResponseSupplyProducts = new ArrayList<>();
        // 주문 수량이 존재 여부 체크를 위한 boolean 값
        boolean existOrderContent = true;

        // 주문 요청 제품들이 존재할 경우
        if (!orderSupplyProducts.isEmpty()) {

            // 주문 요청 제품들을 하나씩 조회
            for (SupplierProductListRequestDto eachOrderSupplyProduct : orderSupplyProducts) {
                // 주문 수량이 0 이상인 제품들만 SupplierOrder 저장
                if (eachOrderSupplyProduct.getOrderMount() != 0) {
                    SupplierOrder supplierOrder = SupplierOrder.builder()
                            .classificationCode(supllierOrderRequestDto.getSupplierClassificationCode())
                            .productCode(eachOrderSupplyProduct.getProductCode())
                            .orderMount(eachOrderSupplyProduct.getOrderMount())
                            .statementnumber(statementNumbersupplier(supllierOrderRequestDto.getSupplierClassificationCode()))
                            .build();

                    supplierOrderRepository.save(supplierOrder);

                    // 반환 객체에 저장
                    orderResponseSupplyProducts.add(
                            SupplierOrderProductListResponseDto.builder()
                                    .productCode(supplierOrder.getProductCode())
                                    .orderMount(supplierOrder.getOrderMount())
                                    .statementnumber(supplierOrder.getStatementnumber())
                                    .build()
                    );

                    // 주문 수량이 존재하기 때문에 판별 값 변경
                    existOrderContent = false;
                }
            }
        } else { // 주문 요청 제품들이 존재하지 않을 경우 예외 처리
            return null;
        }

        // 판별 값이 true인 상태일 경우 예외 처리
        if (existOrderContent) {
            return null;
        }

        // 정상적으로 주문 요청이 완료되었으면 반환 객체에 담아 반환
        return SupplierOrderResponseDto.builder()
                .supplierClassificationCode(supllierOrderRequestDto.getSupplierClassificationCode())
                .orderSupplyProducts(orderResponseSupplyProducts)
                .build();
    }

    // 공급사 페기이력 사유 수정
    @Transactional
    public Long putDiscardHistoryUpdate(String discardHistoryId, String reason) {
        long result = jpaQueryFactory
                .update(discardHistory)
                .set(discardHistory.reason, reason)
                .where(discardHistory.discardHistoryId.eq(Long.valueOf(discardHistoryId)))
                .execute();

        return result;
    }

    // 공급사 측에서 제품 반품 요청
    public SupplierRecallResponseDto supplierRecallProduct(SupplierRecallRequestDto supplierRecallRequestDto) throws NullPointerException {

        // 요청받은 반품 제품 리스트 추출 저장
        List<RecallProductRequestDto> recallRequestProducts = supplierRecallRequestDto.getRecallProducts();

        // 확인용 리스트 객체 생성
        List<RecallProductResponseDto> responseRecallProducts = new ArrayList<>();
        // 반품 여부 확인 boolean 변수 생성
        boolean existRecallContent = true;

        // 요청받은 반품 제품이 존재할 경우
        if (!recallRequestProducts.isEmpty()) {
            // 요청 반품 제품들을 하나씩 조회
            for (RecallProductRequestDto eachRecallProduct : recallRequestProducts) {
                // 반품 요청 수량이 0이상일 경우에만 반품 처리 진행
                if (eachRecallProduct.getRecallMount() != 0) {
                    // 반품 수량 가능 확인을 위한 공급사 제품 최대 주문량과 제품명 추출
                    Tuple supplyProductInfo = jpaQueryFactory
                            .select(supplyProduct.maximumOrderMount.intValue().as("maximumOrderMount"),
                                    supplyProduct.product.productName.as("productName"))
                            .from(supplyProduct)
                            .where(supplyProduct.product.productCode.eq(eachRecallProduct.getProductCode())
                                    .and(supplyProduct.classificationCode.eq(supplierRecallRequestDto.getSupplierClassificationCode())))
                            .fetchOne();

                    Integer totalOrderMount = 0;
                    Integer totalRecallMount = 0;

                    // 반품 수량 가능 확인을 위한 공급사 제품 주문 요청의 재고 수량 추출
                    if (jpaQueryFactory
                            .select(supplierOrder.orderMount.sum())
                            .from(supplierOrder)
                            .where(supplierOrder.classificationCode.eq(supplierRecallRequestDto.getSupplierClassificationCode())
                                    .and(supplierOrder.productCode.eq(eachRecallProduct.getProductCode())))
                            .fetchOne() != null) {
                        totalOrderMount = jpaQueryFactory
                                .select(supplierOrder.orderMount.sum())
                                .from(supplierOrder)
                                .where(supplierOrder.classificationCode.eq(supplierRecallRequestDto.getSupplierClassificationCode())
                                        .and(supplierOrder.productCode.eq(eachRecallProduct.getProductCode())))
                                .fetchOne();
                    }

                    // 지금까지 요청한 반품 요청 수량의 합계가 총 재고 수량보다 많으면 요청 불가
                    if (jpaQueryFactory
                            .select(recall.recallMount.sum())
                            .from(recall)
                            .where(recall.classificationCode.eq(supplierRecallRequestDto.getSupplierClassificationCode())
                                    .and(recall.productCode.eq(eachRecallProduct.getProductCode())))
                            .fetchOne() != null) {
                        totalRecallMount = jpaQueryFactory
                                .select(recall.recallMount.sum())
                                .from(recall)
                                .where(recall.classificationCode.eq(supplierRecallRequestDto.getSupplierClassificationCode())
                                        .and(recall.productCode.eq(eachRecallProduct.getProductCode())))
                                .fetchOne();
                    }

                    log.info("");
                    log.info("반품 제품 : {}", supplyProductInfo.get(supplyProduct.product.productName.as("productName")));
                    log.info("설정된 최대 주문 수량 : {}", supplyProductInfo.get(supplyProduct.maximumOrderMount.intValue().as("maximumOrderMount")));
                    log.info("총 재고 수량 : {}", totalOrderMount);
                    log.info("이전까지 요청한 반품 총 수량 : {}", totalRecallMount);
                    log.info("반품 요청 수량 : {}", eachRecallProduct.getRecallMount());
                    log.info("");


                    // 최대 주문량과 현재 재고 수량보다 반품 요청 수량이 적을 경우에만 반품 요청 처리
                    assert totalRecallMount != null;
                    if (eachRecallProduct.getRecallMount() + totalRecallMount <= supplyProductInfo.get(supplyProduct.maximumOrderMount.intValue().as("maximumOrderMount")) &&
                            eachRecallProduct.getRecallMount() + totalRecallMount <= totalOrderMount) {
                        // 반품 데이터 저장
                        Recall recall = Recall.builder()
                                .classificationCode(supplierRecallRequestDto.getSupplierClassificationCode())
                                .productCode(eachRecallProduct.getProductCode())
                                .productName(eachRecallProduct.getProductName())
                                .recallMount(eachRecallProduct.getRecallMount())
                                .build();

                        recallRepository.save(recall);

                        // 반환용 리스트 객체에 데이터 저장
                        responseRecallProducts.add(
                                RecallProductResponseDto.builder()
                                        .productName(supplyProductInfo.get(supplyProduct.product.productName.as("productName")))
                                        .productCode(recall.getProductCode())
                                        .recallMount(recall.getRecallMount())
                                        .build()
                        );

                        // 반환 여부 변수 값 변경
                        existRecallContent = false;
                    }
                }
            }
        }

        // 반환 여부 변수 값이 true일 경우 반품 처리를 진행할 수 없으므로 null 반환
        if (existRecallContent) {
            return null;
        } else { // 반환 여부 변수 값이 false일 경우 반품 처리를 진행하였으므로 반품 처리 데이터 반환
            return SupplierRecallResponseDto.builder()
                    .supplierClassificationCode(supplierRecallRequestDto.getSupplierClassificationCode())
                    .recallProducts(responseRecallProducts)
                    .build();
        }
    }


    // 공급사가 가지고 있는 반품 리스트 조회
    public TotalRecallHistoryResponseDto getSupplierRecallList(String classificationCode, String dateTime, int page, String productcode) {

        // 반환용 리스트 객체 생성
        List<RecallHistoryResponseDto> responseRecallHistoryList = new ArrayList<>();

        // 조회한 반품 이력의 총 카운트 수 추출
        Long recallTotalCount = jpaQueryFactory
                .select(recall.count())
                .from(recall)
                .where(recall.classificationCode.eq(classificationCode)
                        .and(likeRecallProductName(productcode))
                        .and(getRecallRequestDate(dateTime)))
                .fetchOne();

        // 반품 이력 정보들 추출
        List<Recall> recallHistory = jpaQueryFactory
                .selectFrom(recall)
                .where(recall.classificationCode.eq(classificationCode)
                        .and(likeRecallProductName(productcode))
                        .and(getRecallRequestDate(dateTime)))
                .offset((page * 10L) - 10)
                .limit(10)
                .orderBy(recall.createdAt.desc())
                .fetch();

        // 추출한 반품 이력들을 하나씩 조회
        for (Recall eachRecallHistory : recallHistory) {
            // 만약 조회한 반품 이력의 수령 후 반품 일자가 존재하지 않을 경우
            if (eachRecallHistory.getPossibleRecallAt() == null) {
                // 반환 리스트 객체에 반품 정보들 저장 및 수령 반품 일자는 "확인 중" 텍스트로 변경
                responseRecallHistoryList.add(
                        RecallHistoryResponseDto.builder()
                                .recallId(eachRecallHistory.getRecallId())
                                .productCode(eachRecallHistory.getProductCode())
                                .productName(eachRecallHistory.getProductName())
                                .recallMount(eachRecallHistory.getRecallMount())
                                .recallRequestAt(eachRecallHistory.getCreatedAt().toString())
                                .possibleRecallReceiveAt("확인중")
                                .build()
                );
            } else { // 만약 조회한 반품 이력의 수령 후 반품 일자가 존재할 경우
                // 반환 리스트 객체에 반품 정보들 저장 및 수령 반품 일자에 그대로 일자 정보 저장
                responseRecallHistoryList.add(
                        RecallHistoryResponseDto.builder()
                                .recallId(eachRecallHistory.getRecallId())
                                .productCode(eachRecallHistory.getProductCode())
                                .productName(eachRecallHistory.getProductName())
                                .recallMount(eachRecallHistory.getRecallMount())
                                .recallRequestAt(eachRecallHistory.getCreatedAt().toString())
                                .possibleRecallReceiveAt(eachRecallHistory.getPossibleRecallAt().toString())
                                .build()
                );
            }
        }

        // 최종 반환 객체에 추출한 데이터 정보 저장 후 반환
        return TotalRecallHistoryResponseDto.builder()
                .totalCount(recallTotalCount)
                .recallHistory(responseRecallHistoryList)
                .build();
    }

    // 공급사가 가지고 있는 반품 리스트 조회
    public TotalRecallHistoryResponseDto getSupplierRecallxlsxList(String classificationCode, String dateTime, String productcode) {

        // 반환용 리스트 객체 생성
        List<RecallHistoryResponseDto> responseRecallHistoryList = new ArrayList<>();

        // 조회한 반품 이력의 총 카운트 수 추출
        Long recallTotalCount = jpaQueryFactory
                .select(recall.count())
                .from(recall)
                .where(recall.classificationCode.eq(classificationCode))
                .fetchOne();

        // 반품 이력 정보들 추출
        List<Recall> recallHistory = jpaQueryFactory
                .selectFrom(recall)
                .where(recall.classificationCode.eq(classificationCode))
                .orderBy(recall.createdAt.desc())
                .fetch();

        // 추출한 반품 이력들을 하나씩 조회
        for (Recall eachRecallHistory : recallHistory) {
            // 만약 조회한 반품 이력의 수령 후 반품 일자가 존재하지 않을 경우
            if (eachRecallHistory.getPossibleRecallAt() == null) {
                // 반환 리스트 객체에 반품 정보들 저장 및 수령 반품 일자는 "확인 중" 텍스트로 변경
                responseRecallHistoryList.add(
                        RecallHistoryResponseDto.builder()
                                .recallId(eachRecallHistory.getRecallId())
                                .productCode(eachRecallHistory.getProductCode())
                                .productName(eachRecallHistory.getProductName())
                                .recallMount(eachRecallHistory.getRecallMount())
                                .recallRequestAt(eachRecallHistory.getCreatedAt().toString())
                                .possibleRecallReceiveAt("확인중")
                                .build()
                );
            } else { // 만약 조회한 반품 이력의 수령 후 반품 일자가 존재할 경우
                // 반환 리스트 객체에 반품 정보들 저장 및 수령 반품 일자에 그대로 일자 정보 저장
                responseRecallHistoryList.add(
                        RecallHistoryResponseDto.builder()
                                .recallId(eachRecallHistory.getRecallId())
                                .productCode(eachRecallHistory.getProductCode())
                                .productName(eachRecallHistory.getProductName())
                                .recallMount(eachRecallHistory.getRecallMount())
                                .recallRequestAt(eachRecallHistory.getCreatedAt().toString())
                                .possibleRecallReceiveAt(eachRecallHistory.getPossibleRecallAt().toString())
                                .build()
                );
            }
        }

        // 최종 반환 객체에 추출한 데이터 정보 저장 후 반환
        return TotalRecallHistoryResponseDto.builder()
                .totalCount(recallTotalCount)
                .recallHistory(responseRecallHistoryList)
                .build();
    }

    // 공급사 반품 수거 일정 수정
    @Transactional
    public Long putRecallpossibleRecallAtUpdate(String recallId, String possibleRecallAt) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse(possibleRecallAt, formatter);

        long result = jpaQueryFactory
                .update(recall)
                .set(recall.possibleRecallAt, date)
                .where(recall.recallId.eq(Long.valueOf(recallId)))
                .execute();

        return result;
    }

    // 고객사 주문 리스트 조회
    public ProcessClentOrderListResponseDto getlientOrderList(String classificationCode, int page) {

        List<ClientOrder> result = new ArrayList<>();
        List<ProcessClentOrderEachResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(clientOrder.count())
                .from(clientOrder)
                .where(clientOrder.classificationCode.eq(classificationCode))
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(clientOrder)
                .where(clientOrder.classificationCode.eq(classificationCode))
                .orderBy(clientOrder.createdAt.desc())
                .offset(page)
                .limit(10)
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    ProcessClentOrderEachResponseDto.builder()
                            .clientOrderId(result.get(i).getClientOrderId())
                            .motherCode(result.get(i).getMotherCode())
                            .classificationCode(result.get(i).getClassificationCode())
                            .productCode(result.get(i).getProductCode())
                            .orderMount(result.get(i).getOrderMount())
                            .status(result.get(i).getStatus())
                            .hopeDeliveryAt(result.get(i).getHopeDeliveryAt())
                            .createdAt(result.get(i).getCreatedAt().toString())
                            .build()
            );

        }

        return ProcessClentOrderListResponseDto.builder()
                .clientorderList(eachResponseDtos)
                .paging(pageing)
                .build();
    }

    // 공급사 페기이력  리스트 조회
    public ProcessDiscardHistoryListResponseDto getDiscardHistoryList(String classificationCode, int page) {

        List<DiscardHistory> result = new ArrayList<>();
        List<ProcessDiscardHistoryEachResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(discardHistory.count())
                .from(discardHistory)
                .where(discardHistory.supplierCode.eq(classificationCode))
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(discardHistory)
                .where(discardHistory.supplierCode.eq(classificationCode))
                .offset(page)
                .limit(10)
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    ProcessDiscardHistoryEachResponseDto.builder()
                            .discardHistoryId(result.get(i).getDiscardHistoryId())
                            .supplierCode(result.get(i).getSupplierCode())
                            .clientCode(result.get(i).getClientCode())
                            .productCode(result.get(i).getProductCode())
                            .productSerialCode(result.get(i).getProductSerialCode())
                            .rfidChipCode(result.get(i).getRfidChipCode())
                            .discardAt(result.get(i).getDiscardAt())
                            .reason(result.get(i).getReason())
                            .createdAt(result.get(i).getCreatedAt().toString())
                            .build()
            );

        }

        return ProcessDiscardHistoryListResponseDto.builder()
                .discardhistoryList(eachResponseDtos)
                .paging(pageing)
                .build();
    }

    // 공급사 페기이력  리스트 조회
    public ProcessDiscardHistoryListResponseDto getDiscardHistoryxlsxList(String classificationCode) {

        List<DiscardHistory> result = new ArrayList<>();
        List<ProcessDiscardHistoryEachResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(discardHistory.count())
                .from(discardHistory)
                .where(discardHistory.supplierCode.eq(classificationCode))
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(discardHistory)
                .where(discardHistory.supplierCode.eq(classificationCode))
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    ProcessDiscardHistoryEachResponseDto.builder()
                            .discardHistoryId(result.get(i).getDiscardHistoryId())
                            .supplierCode(result.get(i).getSupplierCode())
                            .clientCode(result.get(i).getClientCode())
                            .productCode(result.get(i).getProductCode())
                            .productSerialCode(result.get(i).getProductSerialCode())
                            .rfidChipCode(result.get(i).getRfidChipCode())
                            .discardAt(result.get(i).getDiscardAt())
                            .reason(result.get(i).getReason())
                            .createdAt(result.get(i).getCreatedAt().toString())
                            .build()
            );

        }

        return ProcessDiscardHistoryListResponseDto.builder()
                .discardhistoryList(eachResponseDtos)
                .paging(pageing)
                .build();
    }

    // 공급사 에서 고객사 페기이력   리스트 조회
    public ProcessDiscardHistoryListResponseDto getDiscardClientHistoryList(String clientcode, int page) {

        List<DiscardHistory> result = new ArrayList<>();
        List<ProcessDiscardHistoryEachResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(discardHistory.count())
                .from(discardHistory)
                .where(discardHistory.clientCode.eq(clientcode))
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(discardHistory)
                .where(discardHistory.clientCode.eq(clientcode))
                .offset(page)
                .limit(10)
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    ProcessDiscardHistoryEachResponseDto.builder()
                            .discardHistoryId(result.get(i).getDiscardHistoryId())
                            .supplierCode(result.get(i).getSupplierCode())
                            .clientCode(result.get(i).getClientCode())
                            .productCode(result.get(i).getProductCode())
                            .productSerialCode(result.get(i).getProductSerialCode())
                            .rfidChipCode(result.get(i).getRfidChipCode())
                            .discardAt(result.get(i).getDiscardAt())
                            .reason(result.get(i).getReason())
                            .createdAt(result.get(i).getCreatedAt().toString())
                            .build()
            );

        }

        return ProcessDiscardHistoryListResponseDto.builder()
                .discardhistoryList(eachResponseDtos)
                .paging(pageing)
                .build();
    }


    // 공급사 에서 고객사 페기이력   리스트 조회
    public ProcessDiscardHistoryListResponseDto getDiscardClientHistoryXlsxList(String clientcode) {

        List<DiscardHistory> result = new ArrayList<>();
        List<ProcessDiscardHistoryEachResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(discardHistory.count())
                .from(discardHistory)
                .where(discardHistory.clientCode.eq(clientcode))
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(discardHistory)
                .where(discardHistory.clientCode.eq(clientcode))
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    ProcessDiscardHistoryEachResponseDto.builder()
                            .discardHistoryId(result.get(i).getDiscardHistoryId())
                            .supplierCode(result.get(i).getSupplierCode())
                            .clientCode(result.get(i).getClientCode())
                            .productCode(result.get(i).getProductCode())
                            .productSerialCode(result.get(i).getProductSerialCode())
                            .rfidChipCode(result.get(i).getRfidChipCode())
                            .discardAt(result.get(i).getDiscardAt())
                            .reason(result.get(i).getReason())
                            .createdAt(result.get(i).getCreatedAt().toString())
                            .build()
            );

        }

        return ProcessDiscardHistoryListResponseDto.builder()
                .discardhistoryList(eachResponseDtos)
                .paging(pageing)
                .build();
    }

    // 공급사 주문 납품가능일 수정 수정
    @Transactional
    public Long putOrerdeliveryAtUpdate(String orderId, String deliveryAt) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse(deliveryAt, formatter);

        long result = jpaQueryFactory
                .update(supplierOrder)
                .set(supplierOrder.deliveryAt, date)
                .where(supplierOrder.orderId.eq(Long.valueOf(orderId)))
                .execute();

        return result;
    }

    // 고객사에서 공급사 측으로 주문 요청 api
    public ClientOrderReseponsetDto clientOrder(ClientOrderRequestDto clientOrderRequestDto) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse(clientOrderRequestDto.getHopeDeliveryAt(), formatter);

        // 주문 요청받은 제품들의 정보 리스트
        List<ClientProductListRequestDto> orderSupplyProducts = clientOrderRequestDto.getOrderClientProducts();
        // 확인용 반환 리스트 객체 생성
        List<ClientOrderProductListResponseDto> orderResponseClientProducts = new ArrayList<>();

        // 주문 요청 제품들을 하나씩 조회
        for (ClientProductListRequestDto eachOrderSupplyProduct : orderSupplyProducts) {
            // 주문 수량이 0 이상인 제품들만 SupplierOrder 저장
            if (eachOrderSupplyProduct.getOrderMount() != 0) {
                ClientOrder clientOrder = ClientOrder.builder()
                        .classificationCode(clientOrderRequestDto.getClientClassificationCode())
                        .motherCode(clientOrderRequestDto.getMotherCode())
                        .orderMount(eachOrderSupplyProduct.getOrderMount())
                        .productCode(eachOrderSupplyProduct.getProductCode())
                        .statementnumber(statementNumber(clientOrderRequestDto.getClientClassificationCode()))
                        .hopeDeliveryAt(date)
                        .modifiedYN("N")
                        .status("주문대기")
                        .build();

                clientOrderRepository.save(clientOrder);

                // 반환 객체에 저장
                orderResponseClientProducts.add(
                        ClientOrderProductListResponseDto.builder()
                                .productCode(clientOrder.getProductCode())
                                .orderMount(clientOrder.getOrderMount())
                                .build()
                );
            }
        }

        return ClientOrderReseponsetDto.builder()
                .clientClassificationCode(clientOrderRequestDto.getClientClassificationCode())
                .motherCode(clientOrderRequestDto.getMotherCode())
                .orderClientProducts(orderResponseClientProducts)
                .build();
    }

    /**
     * // 공급사 주문 리스트 조회
     * public SupplierOrderListResponseDto getsupplierOrderList(String classificationCode, int page, String dateTime, String productcode) {
     * <p>
     * List<SupplierOrder> result = new ArrayList<>();
     * List<SupplierOrderListEachResponseDto> eachResponseDtos = new ArrayList<>();
     * Long pageing = 0L;
     * <p>
     * pageing = jpaQueryFactory
     * .select(supplierOrder.count())
     * .from(supplierOrder)
     * .where(supplierOrder.classificationCode.eq(classificationCode)
     * .and(getorderDate2(dateTime))
     * .and(orderProductkeyword(productcode)))
     * .fetchOne();
     * <p>
     * result = jpaQueryFactory
     * .selectFrom(supplierOrder)
     * .where(supplierOrder.classificationCode.eq(classificationCode)
     * .and(getorderDate2(dateTime))
     * .and(orderProductkeyword(productcode)))
     * .offset(page)
     * .limit(10)
     * .fetch();
     * <p>
     * for (int i = 0; i < result.size(); i++) {
     * eachResponseDtos.add(
     * SupplierOrderListEachResponseDto.builder()
     * .orderId(result.get(i).getOrderId())
     * .classificationCode(result.get(i).getClassificationCode())
     * .productCode(result.get(i).getProductCode())
     * .orderMount(result.get(i).getOrderMount())
     * .deliveryAt(result.get(i).getDeliveryAt())
     * .statementnumber(result.get(i).getStatementnumber())
     * .createdAt(result.get(i).getCreatedAt().toString())
     * .build()
     * );
     * <p>
     * }
     * <p>
     * return SupplierOrderListResponseDto.builder()
     * .supplierOrderList(eachResponseDtos)
     * .paging(pageing)
     * .build();
     * }
     **/

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 공급사 주문 리스트 조회
    public SupplierOrderListResponseDto newGetsupplierOrderList(String classificationCode, int page, String dateTime, String productcode) {

        List<SupplierOrderListEachResponseDto> supplierOrderResultList = new ArrayList<>();

        List<SupplierOrder> supplierOrderList = jpaQueryFactory
                .selectFrom(supplierOrder)
                .where(supplierOrder.classificationCode.eq(classificationCode)
                        .and(getorderDate2(dateTime))
                        .and(orderProductkeyword(productcode)))
                .orderBy(supplierOrder.createdAt.desc())
                .fetch();

        long orderCount = supplierOrderList.size();

        if (!supplierOrderList.isEmpty()) {
            if (supplierOrderList.size() >= 10) {
                if ((page * 10L) <= orderCount) {
                    supplierOrderList = supplierOrderList.subList((page * 10) - 10, page * 10);
                } else {
                    supplierOrderList = supplierOrderList.subList((page * 10) - 10, supplierOrderList.size());
                }
            } else {
                supplierOrderList = supplierOrderList.subList((page * 10) - 10, supplierOrderList.size());
            }

            supplierOrderResultList = supplierOrderList.stream()
                    .map(eachSupplierOrder ->
                            SupplierOrderListEachResponseDto.builder()
                                    .orderId(eachSupplierOrder.getOrderId())
                                    .classificationCode(eachSupplierOrder.getClassificationCode())
                                    .productCode(eachSupplierOrder.getProductCode())
                                    .orderMount(eachSupplierOrder.getOrderMount())
                                    .deliveryAt(eachSupplierOrder.getDeliveryAt())
                                    .statementnumber(eachSupplierOrder.getStatementnumber())
                                    .createdAt(eachSupplierOrder.getCreatedAt().toString())
                                    .build()
                    )
                    .toList();
        }


        return SupplierOrderListResponseDto.builder()
                .supplierOrderList(supplierOrderResultList)
                .paging(orderCount)
                .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    // 공급사 주문 리스트 조회
    public SupplierOrderListResponseDto getsupplierOrderxlsxList(String classificationCode, String dateTime, String productcode) {

        List<SupplierOrder> result = new ArrayList<>();
        List<SupplierOrderListEachResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(supplierOrder.count())
                .from(supplierOrder)
                .where(supplierOrder.classificationCode.eq(classificationCode).and(getorderDate2(dateTime)).and(orderProductkeyword(productcode)))
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(supplierOrder)
                .where(supplierOrder.classificationCode.eq(classificationCode).and(getorderDate2(dateTime)).and(orderProductkeyword(productcode)))
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    SupplierOrderListEachResponseDto.builder()
                            .orderId(result.get(i).getOrderId())
                            .classificationCode(result.get(i).getClassificationCode())
                            .productCode(result.get(i).getProductCode())
                            .orderMount(result.get(i).getOrderMount())
                            .deliveryAt(result.get(i).getDeliveryAt())
                            .statementnumber(result.get(i).getStatementnumber())
                            .createdAt(result.get(i).getCreatedAt().toString())
                            .build()
            );

        }

        return SupplierOrderListResponseDto.builder()
                .supplierOrderList(eachResponseDtos)
                .paging(pageing)
                .build();
    }

    // 고객사에서 공급사 측으로 주문 수정 요청 api
    @Transactional
    public long getlientOrderUpdate(String clientOrderId, int orderMount, String hopeDeliveryAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse(hopeDeliveryAt, formatter);

        Date today = new Date();
        long result = jpaQueryFactory
                .update(clientOrder)
                .set(clientOrder.orderMount, orderMount)
                .set(clientOrder.modifiedYN, "Y")
                .set(clientOrder.hopeDeliveryAt, date)
                .where(clientOrder.clientOrderId.eq(Long.valueOf(clientOrderId)))
                .execute();

        return result;
    }

    /**
     * // 공급사에서 고객사 주문 리스트 조회
     * public ProcessClentOrderListResponseDto getSupplierOfClientOrderList(String classificationCode, String clientCode, int page, String dateTime, String ordernumber) {
     * <p>
     * List<ClientOrder> result = new ArrayList<>();
     * List<ProcessClentOrderEachResponseDto> eachResponseDtos = new ArrayList<>();
     * Long pageing = 0L;
     * <p>
     * pageing = jpaQueryFactory
     * .select(clientOrder.count())
     * .from(clientOrder)
     * .where(clientOrder.motherCode.eq(classificationCode).and(clinetcheck(clientCode)).and(getorderDate(dateTime)).and(statementkeyword(ordernumber)))
     * .fetchOne();
     * <p>
     * result = jpaQueryFactory
     * .selectFrom(clientOrder)
     * .where(clientOrder.motherCode.eq(classificationCode).and(clinetcheck(clientCode)).and(getorderDate(dateTime)).and(statementkeyword(ordernumber)))
     * .orderBy(clientOrder.createdAt.desc())
     * .offset(page)
     * .limit(10)
     * .fetch();
     * <p>
     * for (int i = 0; i < result.size(); i++) {
     * eachResponseDtos.add(
     * ProcessClentOrderEachResponseDto.builder()
     * .clientOrderId(result.get(i).getClientOrderId())
     * .motherCode(result.get(i).getMotherCode())
     * .classificationCode(result.get(i).getClassificationCode())
     * .productCode(result.get(i).getProductCode())
     * .orderMount(result.get(i).getOrderMount())
     * .status(result.get(i).getStatus())
     * .modifiedYN(result.get(i).getModifiedYN())
     * .statementnumber(result.get(i).getStatementnumber())
     * .hopeDeliveryAt(result.get(i).getHopeDeliveryAt())
     * .createdAt(result.get(i).getCreatedAt().toString())
     * .build()
     * );
     * <p>
     * }
     * <p>
     * return ProcessClentOrderListResponseDto.builder()
     * .clientorderList(eachResponseDtos)
     * .paging(pageing)
     * .build();
     * }
     **/


    // 공급사에서 고객사 주문 리스트 조회
    public ProcessClentOrderListResponseDto newGetSupplierOfClientOrderList(String supplierClassificationCode, String clientCode, int page, String dateTime, String ordernumber) {

        List<ProcessClentOrderEachResponseDto> clientOrderResultList = new ArrayList<>();

        List<ClientOrder> clientOrderList = jpaQueryFactory
                .selectFrom(clientOrder)
                .where(clientOrder.motherCode.eq(supplierClassificationCode)
                        .and(clinetcheck(clientCode))
                        .and(getorderDate(dateTime))
                        .and(statementkeyword(ordernumber)))
                .orderBy(clientOrder.createdAt.desc())
                .fetch();

        long orderCount = clientOrderList.size();

        if (!clientOrderList.isEmpty()) {
            if (clientOrderList.size() >= 10) {
                if ((page * 10L) <= orderCount) {
                    clientOrderList = clientOrderList.subList((page * 10) - 10, page * 10);
                } else {
                    clientOrderList = clientOrderList.subList((page * 10) - 10, clientOrderList.size());
                }
            } else {
                clientOrderList = clientOrderList.subList((page * 10) - 10, clientOrderList.size());
            }

            clientOrderResultList = clientOrderList.stream()
                    .map(eachClientOrder ->
                            ProcessClentOrderEachResponseDto.builder()
                                    .clientOrderId(eachClientOrder.getClientOrderId())
                                    .motherCode(eachClientOrder.getMotherCode())
                                    .classificationCode(eachClientOrder.getClassificationCode())
                                    .productCode(eachClientOrder.getProductCode())
                                    .orderMount(eachClientOrder.getOrderMount())
                                    .status(eachClientOrder.getStatus())
                                    .modifiedYN(eachClientOrder.getModifiedYN())
                                    .statementnumber(eachClientOrder.getStatementnumber())
                                    .hopeDeliveryAt(eachClientOrder.getHopeDeliveryAt())
                                    .createdAt(eachClientOrder.getCreatedAt().toString())
                                    .build()
                    )
                    .collect(Collectors.toList());

        }

        return ProcessClentOrderListResponseDto.builder()
                .clientorderList(clientOrderResultList)
                .paging(orderCount)
                .build();
    }

    // 공급사에서 고객사 주문 리스트 조회
    public ProcessClentOrderListResponseDto getSupplierOfClientOrderxlsxList(String classificationCode, String clientCode, String dateTime, String ordernumber) {

        List<ClientOrder> result = new ArrayList<>();
        List<ProcessClentOrderEachResponseDto> eachResponseDtos = new ArrayList<>();
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(clientOrder.count())
                .from(clientOrder)
                .where(clientOrder.motherCode.eq(classificationCode).and(clinetcheck(clientCode)).and(getorderDate(dateTime)).and(statementkeyword(ordernumber)))
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(clientOrder)
                .where(clientOrder.motherCode.eq(classificationCode).and(clinetcheck(clientCode)).and(getorderDate(dateTime)).and(statementkeyword(ordernumber)))
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    ProcessClentOrderEachResponseDto.builder()
                            .clientOrderId(result.get(i).getClientOrderId())
                            .motherCode(result.get(i).getMotherCode())
                            .classificationCode(result.get(i).getClassificationCode())
                            .productCode(result.get(i).getProductCode())
                            .orderMount(result.get(i).getOrderMount())
                            .hopeDeliveryAt(result.get(i).getHopeDeliveryAt())
                            .status(result.get(i).getStatus())
                            .modifiedYN(result.get(i).getModifiedYN())
                            .statementnumber(result.get(i).getStatementnumber())
                            .createdAt(result.get(i).getCreatedAt().toString())
                            .build()
            );

        }

        return ProcessClentOrderListResponseDto.builder()
                .clientorderList(eachResponseDtos)
                .paging(pageing)
                .build();
    }

    // 검색 키워드 동적 조건 적용 함수
    private BooleanExpression likeRecallProductName(String productcode) {
        // 검색 키워드가 존재할 경우 진입
        if (productcode != null) {
            // 검색 요청 키워드에서 텍스트 서칭이 가능하도록 키워드에 % 기호 적용
            return recall.productCode.eq(productcode);
        }

        return null;
    }

    // 조회하고자 하는 반품 요청 일자 동적 조건
    private BooleanExpression getRecallRequestDate(String dateTime) {

        // 처음 요청 조회 날짜 형식 : 2024-01-01
        if (dateTime != null) {

            // - 기호 기준으로 조회 반품 요청 일자 분리
            String[] dateTimeConvert = dateTime.split("-");
            // 년도 정보 추출
            Integer year = Integer.parseInt(dateTimeConvert[0]);
            // 달 정보 추출
            Integer month = Integer.parseInt(dateTimeConvert[1]);
            // 일 정보 추출
            Integer day = Integer.parseInt(dateTimeConvert[2]);

            // 동적 조건 적용
            return recall.createdAt.year().eq(year)
                    .and(recall.createdAt.month().eq(month))
                    .and(recall.createdAt.dayOfMonth().eq(day));
        }

        return null;
    }

    private BooleanExpression clinetcheck(String clientCode) {

        if (!clientCode.isEmpty()) {
            return clientOrder.classificationCode.eq(clientCode);
        }
        return null;
    }

    private BooleanExpression statementkeyword(String ordernumber) {
        if (ordernumber != null) {
            // 검색 요청 키워드에서 텍스트 서칭이 가능하도록 키워드에 % 기호 적용
            return clientOrder.statementnumber.like("%" + ordernumber.replace(" ", "%") + "%");
        }
        return null;
    }

    // 조회하고자 하는 반품 요청 일자 기간 동적 조건
    private BooleanExpression getorderDate(String dateTime) {

        // 처음 요청 조회 날짜 형식 : 2024-01-01/2024-05-05
        if (dateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            String[] dateTimeConvert1 = dateTime.split("/");
            String startdate = dateTimeConvert1[0];
            String enddate = dateTimeConvert1[1];
            // - 기호 기준으로 조회 반품 요청 일자 분리
            LocalDateTime dateTimeStart = LocalDateTime.parse(startdate, formatter);
            LocalDateTime dateTimeEnd = LocalDateTime.parse(enddate, formatter);

            if (dateTimeStart.isAfter(dateTimeEnd)) {
                return clientOrder.createdAt.after(dateTimeStart);
            } else {
                return clientOrder.createdAt.between(dateTimeStart, dateTimeEnd);
            }
        }

        return null;
    }

    // 검색 키워드 동적 조건 적용 함수
    private String statementNumber(String classificationCode) {
        String result = classificationCode;
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(clientOrder.count())
                .from(clientOrder)
                .where(clientOrder.classificationCode.eq(classificationCode))
                .fetchOne();

        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        pageing = pageing + 1L;
        var num = String.valueOf(pageing);
        String numstring = "";

        if (pageing < 10) {
            numstring = "000" + num;
        } else if (pageing < 100) {
            numstring = "00" + num;
        } else if (pageing < 1000) {
            numstring = "0" + num;
        } else {
            numstring = num;
        }

        result = result + "-" + formatDate + "-" + numstring;
        return result;
    }

    // 검색 키워드 동적 조건 적용 함수
    private String statementNumbersupplier(String classificationCode) {
        String result = classificationCode;
        Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(supplierOrder.count())
                .from(supplierOrder)
                .where(supplierOrder.classificationCode.eq(classificationCode))
                .fetchOne();

        String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        pageing = pageing + 1L;
        var num = String.valueOf(pageing);
        String numstring = "";

        if (pageing < 10) {
            numstring = "000" + num;
        } else if (pageing < 100) {
            numstring = "00" + num;
        } else if (pageing < 1000) {
            numstring = "0" + num;
        } else {
            numstring = num;
        }

        result = result + "-" + formatDate + "-" + numstring;
        return result;
    }

    // 조회하고자 하는 반품 요청 일자 기간 동적 조건
    private BooleanExpression orderDate(String dateTime) {

        // 처음 요청 조회 날짜 형식 : 2024-01-01
        if (dateTime != null) {

            // - 기호 기준으로 조회 반품 요청 일자 분리
            String[] dateTimeConvert = dateTime.split("-");
            // 년도 정보 추출
            Integer year = Integer.parseInt(dateTimeConvert[0]);
            // 달 정보 추출
            Integer month = Integer.parseInt(dateTimeConvert[1]);
            // 일 정보 추출
            Integer day = Integer.parseInt(dateTimeConvert[2]);

            // 동적 조건 적용
            return supplierOrder.createdAt.year().eq(year)
                    .and(supplierOrder.createdAt.month().eq(month))
                    .and(supplierOrder.createdAt.dayOfMonth().eq(day));
        }

        return null;
    }

    private BooleanExpression orderProductkeyword(String search) {
        if (search != null) {
            // 검색 요청 키워드에서 텍스트 서칭이 가능하도록 키워드에 % 기호 적용
            return supplierOrder.productCode.eq(search);
        }
        return null;
    }

    // 조회하고자 하는 반품 요청 일자 기간 동적 조건
    private BooleanExpression getorderDate2(String dateTime) {

        // 처음 요청 조회 날짜 형식 : 2024-01-01/2024-05-05
        if (dateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            String[] dateTimeConvert1 = dateTime.split("/");
            String startdate = dateTimeConvert1[0];
            String enddate = dateTimeConvert1[1];

            // - 기호 기준으로 조회 반품 요청 일자 분리
            LocalDateTime dateTimeStart = LocalDateTime.parse(startdate, formatter);
            LocalDateTime dateTimeEnd = LocalDateTime.parse(enddate, formatter);

            if (dateTimeStart.isAfter(dateTimeEnd)) {
                return supplierOrder.createdAt.after(dateTimeStart);
            } else {
                return supplierOrder.createdAt.between(dateTimeStart, dateTimeEnd);
            }
        }

        return null;
    }


    // 실시간 데이터 현황
    // (1) 써큘러랩스와 함께 하는 세척장(공급사)
    // (2) 운용 중인 다회용기 수량
    // (3) 폐기된 다회용기 수량
    public DashboardLiveDataResponseDto getDashboardLiveData() {

        // (1) 써큘러랩스와 함께 하는 세척장(공급사)
        Long coOpSupplierCount = jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(member.grade.eq(1)
                        .and(member.withDrawal.ne("Y")))
                .limit(1)
                .fetchOne();

        // (2) 운용 중인 다회용기 수량
        Integer useTotalProductCount = jpaQueryFactory
                .select(supplierOrder.orderMount.sum())
                .from(supplierOrder)
                .where(supplierOrder.deliveryAt.loe(LocalDateTime.now()))
                .limit(1)
                .fetchOne();

        // (3) 폐기된 다회용기 수량
        Long discardTotalProductCount = jpaQueryFactory
                .select(discardHistory.count())
                .from(discardHistory)
                .limit(1)
                .fetchOne();

        assert coOpSupplierCount != null;
        assert discardTotalProductCount != null;

        return DashboardLiveDataResponseDto.builder()
                .coOpSupplierCount(coOpSupplierCount.intValue())
                .useTotalProductCount(useTotalProductCount)
                .discardTotalProductCount(discardTotalProductCount.intValue())
                .build();
    }


    // 현월 데이터 현황
    // (1) 현월 신규 공급사 카운트
    // (2) 현월 신규 발주량
    public DashboardMonthDataResponseDto getDashboardMonthData() {
        LocalDateTime dateTime = LocalDateTime.now();
        int year = dateTime.getYear(); // 년
        int month = dateTime.getMonthValue(); // 월

        log.info("현월 데이터 추출을 위한 년 월 정보 추출 확인 - 년 : {} / 월 : {}", year, month);

        // (1) 현월 신규 공급사 카운트
        Long newSupplierCount = jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(member.grade.eq(1)
                        .and(member.withDrawal.ne("Y"))
                        .and(member.createdAt.year().eq(year)
                                .and(member.createdAt.month().eq(month))))
                .limit(1)
                .fetchOne();

        // (2) 현월 신규 발주량
        Integer newSupplierOrderCount = jpaQueryFactory
                .select(supplierOrder.orderMount.sum())
                .from(supplierOrder)
                .where(supplierOrder.createdAt.year().eq(year)
                        .and(supplierOrder.createdAt.month().eq(month)))
                .limit(1)
                .fetchOne();

        assert newSupplierCount != null;

        return DashboardMonthDataResponseDto.builder()
                .newSupplierCount(newSupplierCount.intValue())
                .newSupplierOrderCount(newSupplierOrderCount)
                .build();
    }


    // 지구를 지키는 지표
    // (1) 일회용기 감소량 (누적)
    // (2) 탄소 저감량
    public DashboardSaveEarthDataResponseDto getDashboardSaveEarthData() {

        // (1) 일회용기 감소량 (누적)
        Integer reduceDisposableProductCount = jpaQueryFactory
                .select(clientOrder.orderMount.sum())
                .from(clientOrder)
                .where(clientOrder.status.ne("주문대기"))
                .limit(1)
                .fetchOne();

        // 현재 논의된 내용이 없어 0으로 고정
        // (2) 탄소 저감량
        /**
         jpaQueryFactory
         .select()
         .from()
         .where()
         **/

        return DashboardSaveEarthDataResponseDto.builder()
                .reduceDisposableProductCount(reduceDisposableProductCount)
                .reduceCarbonCount(0)
                .build();
    }


    // 일일 구입 요청량
    public List<DashboardDailyPurchaseResponseDto> getDashboardDailyPurchaseData() {
        LocalDateTime dateTime = LocalDateTime.now();
        int year = dateTime.getYear(); // 년
        int month = dateTime.getMonthValue(); // 월
        int day = dateTime.getDayOfMonth(); // 일

        log.info("일일 구입 요청 확인 데이터 추출을 위한 년 / 월 / 일 정보 추출 확인 - 년 : {} / 월 : {} / 일 : {}", year, month, day);

        List<DashboardDailyPurchaseResponseDto> dashboardDailyPurchaseDataList = new ArrayList<>();

        // 일일 구입 요청량 -- 현재 ppt에 나오는 내용대로 2개의 내용만 추출
        List<Tuple> dashboardDailyPurchaseData = jpaQueryFactory
                .select(supplierOrder.classificationCode, supplierOrder.productCode, supplierOrder.orderMount)
                .from(supplierOrder)
                .where(supplierOrder.createdAt.year().eq(year)
                        .and(supplierOrder.createdAt.month().eq(month))
                        .and(supplierOrder.createdAt.dayOfMonth().eq(day)))
                .orderBy(supplierOrder.createdAt.desc())
                .limit(5)
                .fetch();

        // 추출한 내용을 토대로 반환 리스트 객체에 저장
        for (Tuple eachTupleData : dashboardDailyPurchaseData) {
            // 공급사 명 추출
            String supplierName = jpaQueryFactory
                    .select(member.clientCompany)
                    .from(member)
                    .where(member.classificationCode.eq(eachTupleData.get(supplierOrder.classificationCode))
                            .and(member.grade.eq(1)))
                    .limit(1)
                    .fetchOne();

            // 객체에 데이터 저장
            dashboardDailyPurchaseDataList.add(
                    DashboardDailyPurchaseResponseDto.builder()
                            .supplierName(supplierName)
                            .productCode(eachTupleData.get(supplierOrder.productCode))
                            .productOrderMount(eachTupleData.get(supplierOrder.orderMount))
                            .build()
            );
        }

        return dashboardDailyPurchaseDataList;
    }


    // 실시간 데이터 현황
    public SupplierDashboardLiveDataResponseDto getSupplierDashboardLiveData(String classificationCode) {

        // 공급사와 함께 하는 고객사 수
        Long coOpClientCount = jpaQueryFactory
                .select(member.count())
                .from(member)
                .where(member.motherCode.eq(classificationCode)
                        .and(member.grade.eq(2))
                        .and(member.withDrawal.ne("Y")))
                .limit(1)
                .fetchOne();

        // 운용 중인 전체 다회 용기 수량 -- 현재 2번 조건으로 추출
        Integer useTotalProductCount = jpaQueryFactory
                .select(supplierOrder.orderMount.sum())
                .from(supplierOrder)
                .where(supplierOrder.classificationCode.eq(classificationCode)
                        .and(supplierOrder.deliveryAt.loe(LocalDateTime.now())))
                .limit(1)
                .fetchOne();

        // 미회수 다회 용기 용기 수량
        List<String> productCodes = jpaQueryFactory
                .select(rfidScanHistory.productCode)
                .from(rfidScanHistory)
                .where(rfidScanHistory.supplierCode.eq(classificationCode))
                .groupBy(rfidScanHistory.productCode)
                .fetch();

        Integer totalNoReturnQuantity = 0;

        for (String eachProductCode : productCodes) {
            Integer eachProductNoReturnQuantity = jpaQueryFactory
                    .select(rfidScanHistory.noReturnQuantity)
                    .from(rfidScanHistory)
                    .where(rfidScanHistory.supplierCode.eq(classificationCode)
                            .and(rfidScanHistory.productCode.eq(eachProductCode)))
                    .orderBy(rfidScanHistory.createdAt.desc())
                    .limit(1)
                    .fetchOne();

            totalNoReturnQuantity += eachProductNoReturnQuantity;
        }


        assert coOpClientCount != null;

        return SupplierDashboardLiveDataResponseDto.builder()
                .coOpClientCount(coOpClientCount.intValue())
                .useTotalProductCount(useTotalProductCount)
                .noTurnBackTotalProductCount(totalNoReturnQuantity)
                .build();
    }


    // 실시간 재고 현황
    public List<SupplierDashboardLiveRemainResponseDto> getSupplierDashboardLiveRemainData(String classificationCode) {

        // 공급사가 운영 중인 제품들 코드 리스트
        List<String> productCodes = jpaQueryFactory
                .select(rfidScanHistory.productCode)
                .from(rfidScanHistory)
                .where(rfidScanHistory.supplierCode.eq(classificationCode))
                .groupBy(rfidScanHistory.productCode)
                .fetch();

        List<SupplierDashboardLiveRemainResponseDto> supplierDashboardLiveRemainDatas = new ArrayList<>();

        // 운영 중인 제품들을 하나씩 조회하여 정보 추출
        for (String eachProductCode : productCodes) {
            // 해당 제품 명
            String productName = jpaQueryFactory
                    .select(product.productName)
                    .from(product)
                    .where(product.productCode.eq(eachProductCode))
                    .limit(1)
                    .fetchOne();

            // 해당 제품의 유동 재고 수량
            Integer productRemainCount = jpaQueryFactory
                    .select(rfidScanHistory.flowRemainQuantity)
                    .from(rfidScanHistory)
                    .where(rfidScanHistory.supplierCode.eq(classificationCode)
                            .and(rfidScanHistory.productCode.eq(eachProductCode)))
                    .orderBy(rfidScanHistory.createdAt.desc())
                    .limit(1)
                    .fetchOne();

            supplierDashboardLiveRemainDatas.add(
                    SupplierDashboardLiveRemainResponseDto.builder()
                            .productName(productName)
                            .productRemainCount(productRemainCount)
                            .build()
            );
        }

        return supplierDashboardLiveRemainDatas;
    }

    // 공급사 대시보드 v2 전체기간
    public SupplierDashboardV2OrderResponseDto getSupplierDashboardV2TotalData(String classificationCode) {


        Integer totalOrderCount = jpaQueryFactory
                .select(clientOrder.orderMount.sum())
                .from(clientOrder)
                .where(clientOrder.motherCode.eq(classificationCode))
                .limit(1)
                .fetchOne();

        // 미회수 다회 용기 용기 수량
        Integer totalReturnCount = jpaQueryFactory
                .select(rfidScanHistory.statusCount.sum())
                .from(rfidScanHistory)
                .where(rfidScanHistory.supplierCode.eq(classificationCode).and(rfidScanHistory.status.eq("회수")))
                .limit(1)
                .fetchOne();


        return SupplierDashboardV2OrderResponseDto.builder()
                .totalOrderCount(totalOrderCount.intValue())
                .totalReturnCount(totalReturnCount.intValue())
                .build();
    }
// 공급사 대시보드 v2  최대사용다회용기 사이클
    public int getSupplierDashboardV2MaxCycle(String classificationCode) {

        Integer maxCycle = jpaQueryFactory
                .select(productDetail.cycle.max())
                .from(productDetail)
                .where(productDetail.supplierCode.eq(classificationCode))
                .limit(1)
                .fetchOne();

        return maxCycle;
    }
    // 공급사 대시보드 v2 탄소배출저감량
    public SupplierDashboardV2CO2ResponseDto getSupplierDashboardV2CO2(String classificationCode) {
        LocalDateTime now = LocalDateTime.now();


        ClientOrder firstOrder = jpaQueryFactory
                .select(clientOrder)
                .from(clientOrder)
                .where(clientOrder.motherCode.eq(classificationCode))
                .orderBy(clientOrder.createdAt.desc())
                .limit(1)
                .fetchOne();

        assert firstOrder != null;

        Integer firstOrderAmount = firstOrder.getOrderMount();


        // 전체 주문 수량
        Integer totalOrderCount = jpaQueryFactory
                .select(clientOrder.orderMount.sum())
                .from(clientOrder)
                .where(clientOrder.motherCode.eq(classificationCode))
                .limit(1)
                .fetchOne();

        // 전체 co2
        double totalCo2 = firstOrderAmount * 72;
        totalCo2 += (totalOrderCount - firstOrderAmount) * 22.5;


        Integer nowMonthReturnCount = 0;
        double monthCo2 = 0;

        if(firstOrder.getCreatedAt().getYear() == now.getYear() && firstOrder.getCreatedAt().getMonthValue() == now.getMonthValue()){
            // 당월 주문 수량
            monthCo2 = firstOrderAmount * 72;

            nowMonthReturnCount = jpaQueryFactory
                    .select(clientOrder.orderMount.sum())
                    .from(clientOrder)
                    .where(clientOrder.motherCode.eq(classificationCode)
                            .and(clientOrder.createdAt.month().eq(now.getMonthValue()))
                            .and(clientOrder.createdAt.year().eq(now.getYear()))
                    )
                    .limit(1)
                    .fetchOne();

            monthCo2 += (nowMonthReturnCount - firstOrderAmount) * 22.5;

        } else {
            nowMonthReturnCount = jpaQueryFactory
                    .select(clientOrder.orderMount.sum())
                    .from(clientOrder)
                    .where(clientOrder.motherCode.eq(classificationCode)
                            .and(clientOrder.createdAt.month().eq(now.getMonthValue()))
                            .and(clientOrder.createdAt.year().eq(now.getYear()))
                    )
                    .limit(1)
                    .fetchOne();

            monthCo2 += nowMonthReturnCount * 22.5;
        }

        return SupplierDashboardV2CO2ResponseDto.builder()
                .totalOrderCount(totalOrderCount.intValue())
                .nowMonthReturnCount(nowMonthReturnCount.intValue())
                .totalCo2Count(totalCo2 / 1000)
                .nowMonthCo2Count(monthCo2 / 1000)
                .build();
    }

    // 공급사 대시보드 v2 월별 탄소배출저감량
    public List<SupplierDashboardV2CO2MonthResponseDto> getSupplierDashboardV2CO2Month(String classificationCode, String year) {
        LocalDateTime now = LocalDateTime.now();

        List<SupplierDashboardV2CO2MonthResponseDto> supplierDashboardV2CO2MonthResponseDto = new ArrayList<>();

        //첫 구매 달 구하기
        ClientOrder firstOrder = jpaQueryFactory
                .select(clientOrder)
                .from(clientOrder)
                .where(clientOrder.motherCode.eq(classificationCode))
                .orderBy(clientOrder.createdAt.asc())
                .limit(1)
                .fetchOne();

        assert firstOrder != null;


        for (int month = 1; month < 13 ; month++) {

            String yearMonthString = "";

            if(10 > month){
                yearMonthString = "0" + String.valueOf(month);
            }else {
                yearMonthString =  String.valueOf(month);
            }

            String yearandMonth = year + "/" + yearMonthString;

            LocalDateTime monthTime = LocalDate.of(Integer.parseInt(year), month, 1).atStartOfDay();

            Integer nowMonthReturnCount = 0;
            Integer totalOrder = jpaQueryFactory
                        .select(clientOrder.orderMount.sum())
                        .from(clientOrder)
                        .where(clientOrder.motherCode.eq(classificationCode)
                                .and(clientOrder.createdAt.month().eq(monthTime.getMonthValue()))
                                .and(clientOrder.createdAt.year().eq(monthTime.getYear()))
                        )
                        .limit(1)
                        .fetchOne();

            if(totalOrder != null){
                nowMonthReturnCount = totalOrder;
            }
            supplierDashboardV2CO2MonthResponseDto.add(
                    SupplierDashboardV2CO2MonthResponseDto.builder()
                            .yearandMonth(yearandMonth)
                            .ordercount(nowMonthReturnCount)
                            .monthOnceCo2Count(0)
                            .build()
            );
        }

        return supplierDashboardV2CO2MonthResponseDto;
    }
    public String firstOrderDate(String classificationCode){

        ClientOrder firstOrder = jpaQueryFactory
                .select(clientOrder)
                .from(clientOrder)
                .where(clientOrder.motherCode.eq(classificationCode))
                .orderBy(clientOrder.createdAt.asc())
                .limit(1)
                .fetchOne();

        return firstOrder.getCreatedAt().toString();

    }

    // 공급사 대시보드 v2 전체기간
    public SupplierDashboardv2BarResponseDto getSupplierDashboardV2bar(String classificationCode, String clientCode, String productCode, String date) {

        //주문 수량
        Integer totalOrderCount = jpaQueryFactory
                .select(clientOrder.orderMount.sum())
                .from(clientOrder)
                .where(clientOrder.motherCode.eq(classificationCode)
                        .and(dashbordClinetcheck(clientCode))
                        .and(dashbordProductcheck(productCode))
                )
                .limit(1)
                .fetchOne();

        Integer firstOrder = jpaQueryFactory
                .select(clientOrder.orderMount)
                .from(clientOrder)
                .where(clientOrder.motherCode.eq(classificationCode)
                        .and(dashbordClinetcheck(clientCode))
                        .and(dashbordProductcheck(productCode)))
                .orderBy(clientOrder.createdAt.asc())
                .limit(1)
                .fetchOne();

        // 기간 주문 수량
        Integer OrderCount2 = jpaQueryFactory
                .select(clientOrder.orderMount.sum())
                .from(clientOrder)
                .where(clientOrder.motherCode.eq(classificationCode)
                        .and(dashbordClinetcheck(clientCode))
                        .and(dashbordProductcheck(productCode))
                        .and(dashbordDatecheck(date))
                )
                .limit(1)
                .fetchOne();

        // 회수 다회 용기 용기 수량
        Integer totalReturnCount = jpaQueryFactory
                .select(rfidScanHistory.statusCount.sum())
                .from(rfidScanHistory)
                .where(rfidScanHistory.supplierCode.eq(classificationCode)
                        .and(rfidScanHistory.status.eq("회수"))
                        .and(dashbordClinetcheck2(clientCode))
                        .and(dashbordProductcheck2(productCode))
                        .and(dashbordDatecheck2(date))
                )
                .limit(1)
                .fetchOne();

        if(totalOrderCount == null){
            totalOrderCount = 0;
        }
        if(firstOrder == null){
            firstOrder = 0;
        }
        if(OrderCount2 == null){
            OrderCount2 = 0;
        }
        if(totalReturnCount == null){
            totalReturnCount = 0;
        }
        Integer recoveryNum = 0;



        return SupplierDashboardv2BarResponseDto.builder()
                .orderCount(firstOrder)
                .timeOrderCount(OrderCount2)
                .returnCount(totalReturnCount)
                .build();
    }
    private BooleanExpression dashbordClinetcheck(String clientCode) {

        if (!clientCode.isEmpty()) {
            return clientOrder.classificationCode.eq(clientCode);
        }
        return null;
    }
    private BooleanExpression dashbordProductcheck(String productcode) {

        if (!productcode.isEmpty()) {
            return clientOrder.productCode.eq(productcode);
        }
        return null;
    }
    private BooleanExpression dashbordDatecheck(String dateTime) {

        // 처음 요청 조회 날짜 형식 : 2024-01-01/2024-05-05
        if (!dateTime.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            String[] dateTimeConvert1 = dateTime.split("/");
            String startdate = dateTimeConvert1[0];
            String enddate = dateTimeConvert1[1];
            startdate += " 00:00:00";
            enddate += " 23:59:59";
            // - 기호 기준으로 조회 반품 요청 일자 분리
            LocalDateTime dateTimeStart = LocalDateTime.parse(startdate, formatter);
            LocalDateTime dateTimeEnd = LocalDateTime.parse(enddate, formatter);

            if (dateTimeStart.isAfter(dateTimeEnd)) {
                return clientOrder.createdAt.after(dateTimeStart);
            } else {
                return clientOrder.createdAt.between(dateTimeStart, dateTimeEnd);
            }
        }

        return null;
    }
    private BooleanExpression dashbordClinetcheck2(String clientCode) {

        if (!clientCode.isEmpty()) {
            return rfidScanHistory.clientCode.eq(clientCode);
        }
        return null;
    }
    private BooleanExpression dashbordProductcheck2(String productcode) {

        if (!productcode.isEmpty()) {
            return rfidScanHistory.productCode.eq(productcode);
        }
        return null;
    }
    private BooleanExpression dashbordDatecheck2(String dateTime) {

        // 처음 요청 조회 날짜 형식 : 2024-01-01/2024-05-05
        if (dateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            String[] dateTimeConvert1 = dateTime.split("/");
            String startdate = dateTimeConvert1[0];
            String enddate = dateTimeConvert1[1];
            startdate += " 00:00:00";
            enddate += " 23:59:59";
            // - 기호 기준으로 조회 반품 요청 일자 분리
            LocalDateTime dateTimeStart = LocalDateTime.parse(startdate, formatter);
            LocalDateTime dateTimeEnd = LocalDateTime.parse(enddate, formatter);

            if (dateTimeStart.isAfter(dateTimeEnd)) {
                return rfidScanHistory.createdAt.after(dateTimeStart);
            } else {
                return rfidScanHistory.createdAt.between(dateTimeStart, dateTimeEnd);
            }
        }

        return null;
    }
    // 공급사 대시보드 v2 전체기간
    public int getClientDashboardV2TotalData(String clientCode) {

        // 회수 다회 용기 용기 수량
        Integer totalReturnCount = jpaQueryFactory
                .select(rfidScanHistory.statusCount.sum())
                .from(rfidScanHistory)
                .where(rfidScanHistory.clientCode.eq(clientCode).and(rfidScanHistory.status.eq("회수")))
                .limit(1)
                .fetchOne();
    if(totalReturnCount == null){
        totalReturnCount = 0;
    }
        return totalReturnCount;
    }

    // 공급사 대시보드 v2 전체기간
    public JSONObject getClientDashboardV2BarTotalData(String clientCode, String productCode, String date) {
        JSONObject result  = new JSONObject();
        // 미회수 다회 용기 용기 수량
        Integer totalOrderCount = jpaQueryFactory
                .select(clientOrder.orderMount.sum())
                .from(clientOrder)
                .where(clientOrder.classificationCode.eq(clientCode)
                        .and(dashbordProductcheck(productCode)))
                .limit(1)
                .fetchOne();

        Integer firstOrder = jpaQueryFactory
                .select(clientOrder.orderMount)
                .from(clientOrder)
                .where(clientOrder.classificationCode.eq(clientCode)
                        .and(dashbordProductcheck(productCode)))
                .orderBy(clientOrder.createdAt.asc())
                .limit(1)
                .fetchOne();

        Integer timeOrderCount = jpaQueryFactory
                .select(clientOrder.orderMount.sum())
                .from(clientOrder)
                .where(clientOrder.classificationCode.eq(clientCode)
                        .and(dashbordProductcheck(productCode))
                        .and(dashbordDatecheck(date)))
                .limit(1)
                .fetchOne();

        Integer timeReturnCount = jpaQueryFactory
                .select(rfidScanHistory.statusCount.sum())
                .from(rfidScanHistory)
                .where(rfidScanHistory.clientCode.eq(clientCode).and(rfidScanHistory.status.eq("회수"))
                        .and(dashbordProductcheck2(productCode))
                        .and(dashbordDatecheck2(date)))
                .limit(1)
                .fetchOne();
        result.put("totalOrderCount", firstOrder);
        result.put("timeOrderCount", timeOrderCount);
        result.put("timeReturnCount", timeReturnCount);

        return result;
    }
    // 공급사에서 고객사 주문 주문 완료처리
    @Transactional
    public Long putorderStatusUpdate(String clientOrderId, String orderStatus) {


        long result = jpaQueryFactory
                .update(clientOrder)
                .set(clientOrder.status, orderStatus)
                .set(clientOrder.deliveryAt, LocalDateTime.now())
                .where(clientOrder.clientOrderId.eq(Long.valueOf(clientOrderId)))
                .execute();

        return result;
    }

    // 공급사에서 고객사 주문 주문 완료처리
    @Transactional
    public Long supplierOrderDelete(String orderId) {

        long result = jpaQueryFactory
                .delete(supplierOrder)
                .where(supplierOrder.orderId.eq(Long.valueOf(orderId)))
                .execute();

        return result;
    }

}
