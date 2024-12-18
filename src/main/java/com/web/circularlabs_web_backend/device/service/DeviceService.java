package com.web.circularlabs_web_backend.device.service;

import com.web.circularlabs_web_backend.device.domain.Device;
import com.web.circularlabs_web_backend.device.repository.DeviceRepository;
import com.web.circularlabs_web_backend.device.request.DeviceCreateRequestDto;
import com.web.circularlabs_web_backend.device.request.DeviceUpdateInfoRequestDto;
import com.web.circularlabs_web_backend.device.response.DeviceCreateResponseDto;
import com.web.circularlabs_web_backend.query.device.DeviceQueryData;
import com.web.circularlabs_web_backend.query.member.MemberQueryData;
import com.web.circularlabs_web_backend.share.ResponseBody;
import com.web.circularlabs_web_backend.share.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceQueryData deviceQueryData;

    // 기기 리스트 service (관리사)
    public ResponseEntity<ResponseBody> checkDevice(String deviceCode){
        log.info("기기 코드 검증 api - service{}", deviceCode);

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, deviceQueryData.checkDevice(deviceCode)), HttpStatus.OK);

    }

    // 기기 등록 service
    public ResponseEntity<ResponseBody> createDevice(DeviceCreateRequestDto deviceCreateRequestDto){
        log.info("기기 등록 api - service");

        // 등록할 기기 정보들 기입 및 등록
        Device createDevice = Device.builder()
                .supplierCode(deviceCreateRequestDto.getSupplierCode())
                .deviceType(deviceCreateRequestDto.getDeviceType())
                .deviceCode(deviceCreateRequestDto.getDeviceCode())
                .manager(deviceCreateRequestDto.getManager())
                .phone(deviceCreateRequestDto.getPhone())
                .email(deviceCreateRequestDto.getEmail())
                .build();

        deviceRepository.save(createDevice);

        // 확인 용 반환 객체에 정보값들 저장
        DeviceCreateResponseDto responseDto = DeviceCreateResponseDto.builder()
                .supplierCode(createDevice.getSupplierCode())
                .deviceType(createDevice.getDeviceType())
                .deviceCode(createDevice.getDeviceCode())
                .manager(createDevice.getManager())
                .phone(createDevice.getPhone())
                .email(createDevice.getEmail())
                .build();

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, responseDto), HttpStatus.OK);
    }

    /**
    // 기기 리스트 service (관리사)
    public ResponseEntity<ResponseBody> selectDevice(String supplycode, int page){
        log.info("기기 리스트 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, deviceQueryData.getDeviceList(supplycode, page)), HttpStatus.OK);

    }
     **/

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 구 버전 기기 리스트 service (관리사) [개선]
    public ResponseEntity<ResponseBody> newSelectDevice(String supplierCode, int page){
        log.info("기기 리스트 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, deviceQueryData.newGetDeviceList(supplierCode, page)), HttpStatus.OK);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 기기 정보 수정 service (관리사)
    @Transactional
    public ResponseEntity<ResponseBody> updateDevice(DeviceCreateRequestDto deviceCreateRequestDto){
        log.info("기기 수정 api - service");
        long result = deviceQueryData.getDeviceUpdate(deviceCreateRequestDto);
        log.info("쿼리 성공 유무 : {}", result);
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, "success"), HttpStatus.OK);

    }

    // 기기 리스트 조회 service (공급사)
    public ResponseEntity<ResponseBody> getSupplierDeviceList(String supplierClassificationCode, int page){
        log.info("공급사 측 기기 리스트 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, deviceQueryData.getSupplierDeviceList(supplierClassificationCode, page)), HttpStatus.OK);
    }

    // 기기 정보 수정 service (공급사)
    public ResponseEntity<ResponseBody> updateSupplierDeviceInfo(DeviceUpdateInfoRequestDto updateInfoRequestDto){
        log.info("공급사 측 기기 정보 수정 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, deviceQueryData.updateDeviceUpdateInfo(updateInfoRequestDto)), HttpStatus.OK);
    }

    // RFID 히스토리 리스트 api (공급사)
    public ResponseEntity<ResponseBody> getRfidHistoryList(String clinetcode, int page, String status, String suppliercode){
        log.info("RFID 히스토리 리스트 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, deviceQueryData.getRfidHistoryList(clinetcode, page, status, suppliercode)), HttpStatus.OK);
    }
    // RFID 히스토리 리스트 api (공급사)
    public ResponseEntity<ResponseBody> getRfidHistoryxlsxList(String clinetcode, String status, String suppliercode){
        log.info("RFID 히스토리 리스트 api - service");
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, deviceQueryData.getRfidHistoryxlsxList(clinetcode, status, suppliercode)), HttpStatus.OK);
    }
    // RFID 히스토리 디테일 리스트 api (공급사)
    public ResponseEntity<ResponseBody> getRfidHistoryDetailList(String rfidScanhistoryId, int page){
        log.info("공급사 측 기기 리스트 조회 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, deviceQueryData.getRfidHistoryDetailList(rfidScanhistoryId, page)), HttpStatus.OK);
    }
}
