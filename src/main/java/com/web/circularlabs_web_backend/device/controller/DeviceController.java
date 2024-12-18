package com.web.circularlabs_web_backend.device.controller;

import com.web.circularlabs_web_backend.device.request.DeviceCreateRequestDto;
import com.web.circularlabs_web_backend.device.request.DeviceUpdateInfoRequestDto;
import com.web.circularlabs_web_backend.device.service.DeviceService;
import com.web.circularlabs_web_backend.share.ResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cl/device")
@RestController
public class DeviceController {

    private final DeviceService deviceService;
    //기기 코드 둥복 확인
    @GetMapping("/devicecheck")
    public ResponseEntity<ResponseBody> checkDevice(@RequestParam String deviceCode){
        log.info("기기 코드 검증 api - controller");

        return deviceService.checkDevice(deviceCode);
    }

    // 기기 등록 api
    @PostMapping("/create")
    public ResponseEntity<ResponseBody> createDevice(@RequestBody DeviceCreateRequestDto deviceCreateRequestDto){
        log.info("기기 등록 api - controller");
        log.info("등록하고자 하는 기기의 유형 : {} / 기기 코드 : {}", deviceCreateRequestDto.getDeviceType(), deviceCreateRequestDto.getDeviceCode());

        return deviceService.createDevice(deviceCreateRequestDto);
    }

    // 기기 리스트 api (관리사)
    @GetMapping("/list")
    public ResponseEntity<ResponseBody> selectDevice(@RequestParam int page, @RequestParam String supplycode){
        log.info("기기 조회 api - controller");
        log.info(" 페이지 : {}",  page);

        return deviceService.newSelectDevice(supplycode, page);
    }
    // 기기 정보 수정 api (관리사)
    @PutMapping("/update")
    public ResponseEntity<ResponseBody> updateDevice(@RequestBody DeviceCreateRequestDto deviceCreateRequestDto){
        log.info("기기 정보 수정 api - controller");
        log.info("수정하고자 하는 기기의 유형 : {} / 기기 코드 : {}", deviceCreateRequestDto.getDeviceType(), deviceCreateRequestDto.getDeviceCode());

        return deviceService.updateDevice(deviceCreateRequestDto);
    }

    // 기기 리스트 조회 api (공급사)
    @GetMapping("/list/sc")
    public ResponseEntity<ResponseBody> getSupplierDeviceList(@RequestParam String supplycode, @RequestParam int page){
        log.info("공급사 측 기기 리스트 조회 api - controller");

        return deviceService.getSupplierDeviceList(supplycode, page);
    }

    // 기기 정보 수정 api (공급사)
    @PutMapping("/update/sc")
    public ResponseEntity<ResponseBody> updateSupplierDeviceInfo(@RequestBody DeviceUpdateInfoRequestDto updateInfoRequestDto){
        log.info("공급사 측 기기 정보 수정 api - controller");

        return deviceService.updateSupplierDeviceInfo(updateInfoRequestDto);
    }
    // RFID 히스토리 리스트 api (공급사)
    @GetMapping("/rfidlist/sc")
    public ResponseEntity<ResponseBody> getRfidHistoryList(@RequestParam String clinetcode, @RequestParam int page, @RequestParam(required = false)String status, @RequestParam String suppliercode){
        log.info("RFID 히스토리 리스트 api - controller");

        return deviceService.getRfidHistoryList(clinetcode, page, status, suppliercode);
    }
    // RFID 히스토리 리스트 api (공급사)
    @GetMapping("/rfidlist/xlsx")
    public ResponseEntity<ResponseBody> getRfidHistoryxlsxList(@RequestParam String clinetcode, @RequestParam(required = false)String status, @RequestParam String suppliercode){
        log.info("RFID 히스토리 리스트 api - controller");

        return deviceService.getRfidHistoryxlsxList(clinetcode, status, suppliercode);
    }
    // RFID 히스토리 디테일 리스트 api (공급사)
    @GetMapping("/rfiddetaillist/sc")
    public ResponseEntity<ResponseBody> getRfidHistoryDetailList(@RequestParam String rfidScanhistoryId, @RequestParam int page){
        log.info("RFID 히스토리 리스트 api - controller");

        return deviceService.getRfidHistoryDetailList(rfidScanhistoryId, page);
    }

}
