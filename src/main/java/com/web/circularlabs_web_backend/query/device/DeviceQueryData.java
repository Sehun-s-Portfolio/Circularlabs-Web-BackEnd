package com.web.circularlabs_web_backend.query.device;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.web.circularlabs_web_backend.device.domain.Device;
import com.web.circularlabs_web_backend.device.domain.RfidScanHistory;
import com.web.circularlabs_web_backend.device.request.DeviceCreateRequestDto;
import com.web.circularlabs_web_backend.device.request.DeviceUpdateInfoRequestDto;
import com.web.circularlabs_web_backend.device.response.*;
import com.web.circularlabs_web_backend.product.domain.ProductDetail;
import com.web.circularlabs_web_backend.product.domain.QProductDetailHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.web.circularlabs_web_backend.device.domain.QDevice.device;
import static com.web.circularlabs_web_backend.device.domain.QRfidScanHistory.rfidScanHistory;
import static com.web.circularlabs_web_backend.product.domain.QProductDetailHistory.productDetailHistory;

@RequiredArgsConstructor
@Component
public class DeviceQueryData {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    public Long checkDevice(String deviceCode) {
        Long result = 0L;

        result = jpaQueryFactory
                .select(device.count())
                .from(device)
                .where(device.deviceCode.eq(deviceCode))
                .fetchOne();

        return result;
    }

    /**
    public DeviceResponsePageDto getDeviceList(String supplycode, int page) {

        List<DeviceEachResponseDto> eachResponseDtos = new ArrayList<>();
        List<Device> result = new ArrayList<>();
        Long pageing = 0L;

        if (supplycode.isBlank()) {
            pageing = jpaQueryFactory
                    .select(device.count())
                    .from(device)
                    .fetchOne();

            result = jpaQueryFactory
                    .selectFrom(device)
                    .limit(10)
                    .offset(page)
                    .fetch();
        } else {
            pageing = jpaQueryFactory
                    .select(device.count())
                    .from(device)
                    .where(device.supplierCode.eq(supplycode))
                    .fetchOne();

            result = jpaQueryFactory
                    .selectFrom(device)
                    .where(device.supplierCode.eq(supplycode))
                    .limit(10)
                    .offset(page)
                    .fetch();
        }

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    DeviceEachResponseDto.builder()
                            .deviceCode(result.get(i).getDeviceCode())
                            .deviceId(result.get(i).getDeviceId())
                            .phone(result.get(i).getPhone())
                            .deviceType(result.get(i).getDeviceType())
                            .email(result.get(i).getEmail())
                            .manager(result.get(i).getManager())
                            .supplierCode(result.get(i).getSupplierCode())
                            .createdAt(String.valueOf(result.get(i).getCreatedAt()))
                            .build()
            );
        }
        return DeviceResponsePageDto.builder()
                .deviceList(eachResponseDtos)
                .paging(pageing)
                .build();
    }
     **/


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 구 버전 관리자 기기 관리 리스트 호출 쿼리 함수 [개선]
    public DeviceResponsePageDto newGetDeviceList(String supplierCode, int page) {

        if (supplierCode.isBlank()) {
            List<Device> allDeviceList = jpaQueryFactory
                    .selectFrom(device)
                    .orderBy(device.createdAt.desc())
                    .fetch();

            long deviceCount = allDeviceList.size();

            if (allDeviceList.size() >= 10) {
                if ((page * 10L) <= deviceCount) {
                    allDeviceList = allDeviceList.subList((page * 10) - 10, page * 10);
                } else {
                    allDeviceList = allDeviceList.subList((page * 10) - 10, allDeviceList.size());
                }
            } else {
                allDeviceList = allDeviceList.subList((page * 10) - 10, allDeviceList.size());
            }

            List<DeviceEachResponseDto> eachDeviceResponseList = allDeviceList.stream()
                    .map(eachSupplierDevice ->
                        DeviceEachResponseDto.builder()
                                .deviceCode(eachSupplierDevice.getDeviceCode())
                                .deviceId(eachSupplierDevice.getDeviceId())
                                .phone(eachSupplierDevice.getPhone())
                                .deviceType(eachSupplierDevice.getDeviceType())
                                .email(eachSupplierDevice.getEmail())
                                .manager(eachSupplierDevice.getManager())
                                .supplierCode(eachSupplierDevice.getSupplierCode())
                                .createdAt(String.valueOf(eachSupplierDevice.getCreatedAt()))
                                .build()
                    )
                    .toList();

            return DeviceResponsePageDto.builder()
                    .deviceList(eachDeviceResponseList)
                    .paging(deviceCount)
                    .build();

        } else {

            List<Device> supplierDeviceList = jpaQueryFactory
                    .selectFrom(device)
                    .where(device.supplierCode.eq(supplierCode))
                    .orderBy(device.createdAt.desc())
                    .fetch();

            long deviceCount = supplierDeviceList.size();

            if (supplierDeviceList.size() >= 10) {
                if ((page * 10L) <= deviceCount) {
                    supplierDeviceList = supplierDeviceList.subList((page * 10) - 10, page * 10);
                } else {
                    supplierDeviceList = supplierDeviceList.subList((page * 10) - 10, supplierDeviceList.size());
                }
            } else {
                supplierDeviceList = supplierDeviceList.subList((page * 10) - 10, supplierDeviceList.size());
            }

            List<DeviceEachResponseDto> eachDeviceResponseList = supplierDeviceList.stream()
                    .map(eachSupplierDevice ->
                        DeviceEachResponseDto.builder()
                                .deviceCode(eachSupplierDevice.getDeviceCode())
                                .deviceId(eachSupplierDevice.getDeviceId())
                                .phone(eachSupplierDevice.getPhone())
                                .deviceType(eachSupplierDevice.getDeviceType())
                                .email(eachSupplierDevice.getEmail())
                                .manager(eachSupplierDevice.getManager())
                                .supplierCode(eachSupplierDevice.getSupplierCode())
                                .createdAt(String.valueOf(eachSupplierDevice.getCreatedAt()))
                                .build()
                    )
                    .toList();

            return DeviceResponsePageDto.builder()
                    .deviceList(eachDeviceResponseList)
                    .paging(deviceCount)
                    .build();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public Long getDeviceUpdate(DeviceCreateRequestDto deviceCreateRequestDto) {
        long result = jpaQueryFactory
                .update(device)
                .set(device.deviceCode, deviceCreateRequestDto.getDeviceCode())
                .set(device.deviceType, deviceCreateRequestDto.getDeviceType())
                .set(device.supplierCode, deviceCreateRequestDto.getSupplierCode())
                .set(device.manager, deviceCreateRequestDto.getManager())
                .set(device.email, deviceCreateRequestDto.getEmail())
                .set(device.phone, deviceCreateRequestDto.getPhone())
                .where(device.deviceId.eq(Long.valueOf(deviceCreateRequestDto.getDeviceId())))
                .execute();

        entityManager.flush();
        entityManager.clear();

        return result;
    }


    // 공급사 측 기기 리스트 조회
    public List<DeviceListResponseDto> getSupplierDeviceList(String supplierClassificationCode, int page) {

        // 로그인한 공급사에 해당되는 기기 리스트를 페이지에 따라 10개 조회
        List<Device> deviceList = jpaQueryFactory
                .selectFrom(device)
                .where(device.supplierCode.eq(supplierClassificationCode))
                .offset((page * 10L) - 10)
                .limit(10)
                .orderBy(device.createdAt.desc())
                .fetch();

        // 응답 리스트 객체 생성
        List<DeviceListResponseDto> responseDeviceList = new ArrayList<>();

        // 조회한 기기 리스트들을 하나씩 조회하여 응답 리스트 객체에 저장
        if (!deviceList.isEmpty()) {
            for (int i = 0; i < deviceList.size(); i++) {
                responseDeviceList.add(
                        DeviceListResponseDto.builder()
                                .deviceId(deviceList.get(i).getDeviceId())
                                .supplierCode(deviceList.get(i).getSupplierCode())
                                .deviceType(deviceList.get(i).getDeviceType())
                                .deviceCode(deviceList.get(i).getDeviceCode())
                                .manager(deviceList.get(i).getManager())
                                .phone(deviceList.get(i).getPhone())
                                .email(deviceList.get(i).getEmail())
                                .build()
                );
            }
        }

        return responseDeviceList;
    }


    // 공급사 측 기기 정보 동적 수정
    @Transactional
    public DeviceUpdateInfoResponseDto updateDeviceUpdateInfo(DeviceUpdateInfoRequestDto updateInfoRequestDto) {

        // 수정하고자 하는 기기 호출
        JPAUpdateClause clause = jpaQueryFactory
                .update(device)
                .where(device.deviceCode.eq(updateInfoRequestDto.getDeviceCode())
                        .and(device.supplierCode.eq(updateInfoRequestDto.getSupplierCode())));

        // 수정하고자 하는 매니저 명이 존재할 경우 수정
        if (updateInfoRequestDto.getManager() != null && !updateInfoRequestDto.getManager().equals("") && !updateInfoRequestDto.getManager().isEmpty()) {
            clause.set(device.manager, updateInfoRequestDto.getManager());
        }

        // 수정하고자 하는 매니저 연락처 정보가 존재할 경우 수정
        if (updateInfoRequestDto.getPhone() != null && !updateInfoRequestDto.getPhone().equals("") && !updateInfoRequestDto.getPhone().isEmpty()) {
            clause.set(device.phone, updateInfoRequestDto.getPhone());
        }

        // 수정하고자 하는 매니저 이메일 정보가 존재할 경우 수장
        if (updateInfoRequestDto.getEmail() != null && !updateInfoRequestDto.getEmail().equals("") && !updateInfoRequestDto.getEmail().isEmpty()) {
            clause.set(device.email, updateInfoRequestDto.getEmail());
        }

        clause.execute();

        entityManager.flush();
        entityManager.clear();

        // 수정한 내용이 반영된 기기 정보 호출
        Device updateDevice = jpaQueryFactory
                .selectFrom(device)
                .where(device.deviceCode.eq(updateInfoRequestDto.getDeviceCode())
                        .and(device.supplierCode.eq(updateInfoRequestDto.getSupplierCode())))
                .fetchOne();

        assert updateDevice != null;
        return DeviceUpdateInfoResponseDto.builder()
                .deviceId(updateDevice.getDeviceId())
                .supplierCode(updateDevice.getSupplierCode())
                .deviceCode(updateDevice.getDeviceCode())
                .manager(updateDevice.getManager())
                .phone(updateDevice.getPhone())
                .email(updateDevice.getEmail())
                .build();
    }

    //RFID 히스토리 리스트 api
    public RfidHistoryResponsePageDto getRfidHistoryList(String clinetcode, int page, String status, String suppliercode) {
        List<RfidScanHistory> result = new ArrayList<>();
        Long pageing = 0L;
        List<RfidHistoryEachResponseDto> eachResponseDtos = new ArrayList<>();
        // Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(rfidScanHistory.count())
                .from(rfidScanHistory)
                .where(rfidScanHistory.clientCode.eq(clinetcode)
                        .and(rfidScanHistory.supplierCode.eq(suppliercode))
                        .and(rfidstatuskeyword(status)))
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(rfidScanHistory)
                .where(rfidScanHistory.clientCode.eq(clinetcode)
                        .and(rfidScanHistory.supplierCode.eq(suppliercode))
                        .and(rfidstatuskeyword(status)))
                .orderBy(rfidScanHistory.createdAt.desc())
                .limit(10)
                .offset(page)
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    RfidHistoryEachResponseDto.builder()
                            .rfidScanhistoryId(result.get(i).getRfidScanhistoryId())
                            .deviceCode(result.get(i).getDeviceCode())
                            .rfidChipCode(result.get(i).getRfidChipCode())
                            .productCode(result.get(i).getProductCode())
                            .supplierCode(result.get(i).getSupplierCode())
                            .clientCode(result.get(i).getClientCode())
                            .status(result.get(i).getStatus())
                            .statusCount(result.get(i).getStatusCount())
                            .flowRemainQuantity(result.get(i).getFlowRemainQuantity())
                            .noReturnQuantity(result.get(i).getNoReturnQuantity())
                            .totalRemainQuantity(result.get(i).getTotalRemainQuantity())
                            //.cycle(result.get(i).getCycle())
                            .latestReadingAt(result.get(i).getLatestReadingAt())
                            .build()
            );

        }
        return RfidHistoryResponsePageDto.builder()
                .rfidhisList(eachResponseDtos)
                .paging(pageing)
                .build();

    }

    //RFID 히스토리 리스트 api
    public RfidHistoryResponsePageDto getRfidHistoryxlsxList(String clinetcode, String status, String suppliercode) {
        List<RfidScanHistory> result = new ArrayList<>();
        Long pageing = 0L;
        List<RfidHistoryEachResponseDto> eachResponseDtos = new ArrayList<>();
        // Long pageing = 0L;

        pageing = jpaQueryFactory
                .select(rfidScanHistory.count())
                .from(rfidScanHistory)
                .where(rfidScanHistory.clientCode.eq(clinetcode)
                        .and(rfidScanHistory.supplierCode.eq(suppliercode))
                        .and(rfidstatuskeyword(status)))
                .fetchOne();

        result = jpaQueryFactory
                .selectFrom(rfidScanHistory)
                .where(rfidScanHistory.clientCode.eq(clinetcode)
                        .and(rfidScanHistory.supplierCode.eq(suppliercode))
                        .and(rfidstatuskeyword(status)))
                .fetch();

        for (int i = 0; i < result.size(); i++) {
            eachResponseDtos.add(
                    RfidHistoryEachResponseDto.builder()
                            .rfidScanhistoryId(result.get(i).getRfidScanhistoryId())
                            .deviceCode(result.get(i).getDeviceCode())
                            .rfidChipCode(result.get(i).getRfidChipCode())
                            .productCode(result.get(i).getProductCode())
                            .supplierCode(result.get(i).getSupplierCode())
                            .clientCode(result.get(i).getClientCode())
                            .status(result.get(i).getStatus())
                            .statusCount(result.get(i).getStatusCount())
                            .flowRemainQuantity(result.get(i).getFlowRemainQuantity())
                            .noReturnQuantity(result.get(i).getNoReturnQuantity())
                            .totalRemainQuantity(result.get(i).getTotalRemainQuantity())
                            //.cycle(result.get(i).getCycle())
                            .latestReadingAt(result.get(i).getLatestReadingAt())
                            .build()
            );

        }
        return RfidHistoryResponsePageDto.builder()
                .rfidhisList(eachResponseDtos)
                .paging(pageing)
                .build();

    }

    //RFID 히스토리 디테일 리스트 api
    public RfidDetailProductDetailResponsePageDto getRfidHistoryDetailList(String rfidScanhistoryId, int page) {

        List<RfidDetailHistoryEachResponseDto> eachResponseDtos = new ArrayList<>();
        int pageing = 0;

        List<RelatedHistoryResponseDto> relatedHistories = new ArrayList<>();

        pageing = jpaQueryFactory
                .select(productDetailHistory.productCode, productDetailHistory.productSerialCode)
                .from(productDetailHistory)
                .where(productDetailHistory.rfidScanHistory.rfidScanhistoryId.eq(Long.valueOf(rfidScanhistoryId)))
                .fetch().size();

        List<Tuple> detailHistoryInfo = jpaQueryFactory
                .select(productDetailHistory.productCode, productDetailHistory.productSerialCode)
                .from(productDetailHistory)
                .where(productDetailHistory.rfidScanHistory.rfidScanhistoryId.eq(Long.valueOf(rfidScanhistoryId)))
                .limit(10)
                .offset(page)
                .fetch();

        detailHistoryInfo.forEach(eachHistory ->
                relatedHistories.add(
                        RelatedHistoryResponseDto.builder()
                                .productCode(eachHistory.get(productDetailHistory.productCode))
                                .productSerialCode(eachHistory.get(productDetailHistory.productSerialCode))
                                .build()
                )
        );

        return RfidDetailProductDetailResponsePageDto.builder()
                .rfidhisList(relatedHistories)
                .paging(pageing)
                .build();

    }

    private BooleanExpression rfidstatuskeyword(String status) {
        if (status != null) {
            // 검색 요청 키워드에서 텍스트 서칭이 가능하도록 키워드에 % 기호 적용
            return rfidScanHistory.status.eq(status);
        }
        return null;
    }
}
