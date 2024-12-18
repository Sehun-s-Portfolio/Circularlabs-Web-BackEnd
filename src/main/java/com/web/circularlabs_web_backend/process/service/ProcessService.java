package com.web.circularlabs_web_backend.process.service;

import com.web.circularlabs_web_backend.process.request.ClientOrderRequestDto;
import com.web.circularlabs_web_backend.process.request.SupllierOrderRequestDto;
import com.web.circularlabs_web_backend.process.request.SupplierRecallRequestDto;
import com.web.circularlabs_web_backend.process.response.*;
import com.web.circularlabs_web_backend.query.process.ProcessQueryData;
import com.web.circularlabs_web_backend.share.ResponseBody;
import com.web.circularlabs_web_backend.share.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcessService {


    private final ProcessQueryData processQueryData;

    //공급사 재고관리 service
    public ResponseEntity<ResponseBody> supplierStockList(String classificationCode, int page){
        log.info("공급사 재고 리스트 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.getSupplierStockList(classificationCode, page)), HttpStatus.OK);

    }

    //공급사 페기이력  리스트 조회 service
    public ResponseEntity<ResponseBody> getDiscardHistoryList(String classificationCode, int page){
        log.info("공급사 페기이력  리스트 조회 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.getDiscardHistoryList(classificationCode, page)), HttpStatus.OK);

    }
    //공급사 페기이력  리스트 조회 service
    public ResponseEntity<ResponseBody> getDiscardHistoryxlsxList(String classificationCode){
        log.info("공급사 페기이력  리스트 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.getDiscardHistoryxlsxList(classificationCode)), HttpStatus.OK);

    }
    //공급사 에서 고객사 페기이력   리스트 조회 service
    public ResponseEntity<ResponseBody> getDiscardClientHistoryList(String clientcode, int page){
        log.info("공급사 에서 고객사 페기이력  리스트 조회 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.getDiscardClientHistoryList(clientcode, page)), HttpStatus.OK);

    }

    //공급사 에서 고객사 페기이력   리스트 조회 service
    public ResponseEntity<ResponseBody> getDiscardClientHistoryXlsxList(String clientcode){
        log.info("공급사 에서 고객사 페기이력  리스트 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.getDiscardClientHistoryXlsxList(clientcode)), HttpStatus.OK);

    }
    //공급사 페기이력 사유 수정  service
    public ResponseEntity<ResponseBody> putDiscardHistoryUpdate(String discardHistoryId, String reason){
        log.info("공급사 페기이력 사유 수정 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.putDiscardHistoryUpdate(discardHistoryId, reason)), HttpStatus.OK);

    }
    // 공급사 주문 요청 (관리사에게 제품 발주 요청) service
    public ResponseEntity<ResponseBody> supplierOrder(SupllierOrderRequestDto supllierOrderRequestDto){
        log.info("공급사 주문 요청 (관리사에게 제품 발주 요청) api - service");

        SupplierOrderResponseDto responseSupplierOrder = processQueryData.supplierOrder(supllierOrderRequestDto);

        // 만약 구입 요청 제품들의 수량이 전부 0일 경우 예외 처리
        if(responseSupplierOrder == null){
            return new ResponseEntity<>(new ResponseBody(StatusCode.NOT_CORRECT_ORDER_MOUNT, null), HttpStatus.BAD_REQUEST);
        }else{ // 정상적으로 구입 요청 수량이 존재할 경우 데이터 반환
            return new ResponseEntity<>(new ResponseBody(StatusCode.OK, responseSupplierOrder), HttpStatus.OK);
        }

    }

    /**
    //공급사 주문 리스트 service
    public ResponseEntity<ResponseBody> getsupplierOrderList(String classificationCode, int page, String dateTime, String productcode){
        log.info("공급사 주문 리스트 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.getsupplierOrderList(classificationCode, page, dateTime, productcode)), HttpStatus.OK);

    }
     **/

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 관리자 공급사 발주 및 정산 관리 주문 리스트 조회 service [개선]
    public ResponseEntity<ResponseBody> newGetsupplierOrderList(String classificationCode, int page, String dateTime, String productcode){
        log.info("공급사 주문 리스트 api - service");
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.newGetsupplierOrderList(classificationCode, page, dateTime, productcode)), HttpStatus.OK);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //공급사 주문 리스트 service
    public ResponseEntity<ResponseBody> getsupplierOrderxlsxList(String classificationCode, String dateTime, String productcode){
        log.info("공급사 주문 리스트 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.getsupplierOrderxlsxList(classificationCode, dateTime, productcode)), HttpStatus.OK);

    }
    //공급사 주문 납품가능일 수정  service
    public ResponseEntity<ResponseBody> putOrerdeliveryAtUpdate(String orderId, String deliveryAt){
        log.info("공급사 주문 납품가능일 수정 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.putOrerdeliveryAtUpdate(orderId, deliveryAt)), HttpStatus.OK);

    }
    // 공급사 측에서 제품 반품 요청 service
    public ResponseEntity<ResponseBody> supplierRecallProduct(SupplierRecallRequestDto supplierRecallRequestDto) {
        log.info("공급사 측에서 제품 반품 요청 api - service");

        // 반품 요청을 통한 반품 확인 데이터
        SupplierRecallResponseDto responseRecallInfo = processQueryData.supplierRecallProduct(supplierRecallRequestDto);

        // 반품 확인 데이터가 존재하지 않을 경우 예외 처리
        if(responseRecallInfo == null){
            return new ResponseEntity<>(new ResponseBody(StatusCode.CANT_RECALL, null), HttpStatus.BAD_REQUEST);
        }else{ // 반품 확인 데이터가 존재할 경우 확인 데이터 반환 처리
            return new ResponseEntity<>(new ResponseBody(StatusCode.OK, responseRecallInfo), HttpStatus.OK);
        }

    }


    // 공급사가 가지고 있는 반품 리스트 조회 service
    public ResponseEntity<ResponseBody> getSupplierRecallList(String classificationCode, String dateTime, int page, String productcode){
        log.info("공급사가 가지고 있는 반품 리스트 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.getSupplierRecallList(classificationCode, dateTime, page, productcode)), HttpStatus.OK);
    }

    // 공급사가 가지고 있는 반품 리스트 조회 service
    public ResponseEntity<ResponseBody> getSupplierRecallxlsxList(String classificationCode, String dateTime, String productcode){
        log.info("공급사가 가지고 있는 반품 리스트 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.getSupplierRecallxlsxList(classificationCode, dateTime, productcode)), HttpStatus.OK);
    }
    //공급사 반품 수거 일정 수정   service
    public ResponseEntity<ResponseBody> putRecallpossibleRecallAtUpdate(String recallId, String possibleRecallAt){
        log.info("공급사 주문 납품가능일 수정 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.putRecallpossibleRecallAtUpdate(recallId, possibleRecallAt)), HttpStatus.OK);

    }
    // 고객사 주문 리스트 조회 service
    public ResponseEntity<ResponseBody> getClientOrderList(String classificationCode, int page){
        log.info("고객사 주문 리스트 조회 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.getlientOrderList(classificationCode, page)), HttpStatus.OK);
    }
    // 공급사 주문 요청 (관리사에게 제품 발주 요청) service
    public ResponseEntity<ResponseBody> ClientOrder(ClientOrderRequestDto clientOrderRequestDto){
        log.info("공급사 주문 요청 (관리사에게 제품 발주 요청) api - service");

        ClientOrderReseponsetDto responseSupplierOrder = processQueryData.clientOrder(clientOrderRequestDto);

            return new ResponseEntity<>(new ResponseBody(StatusCode.OK, responseSupplierOrder), HttpStatus.OK);
    }
    // 고객사 주문 수정 요청 service
    public ResponseEntity<ResponseBody> ClientOrderUpdate(String clientOrderId, int orderMount, String hopeDeliveryAt){
        log.info("고객사 주문 수정 요청 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.getlientOrderUpdate(clientOrderId, orderMount, hopeDeliveryAt)), HttpStatus.OK);
    }

    /**
    // 공급사에서 고객사 주문 리스트 조회 api  service
    public ResponseEntity<ResponseBody> getSupplierOfClientOrderList(String classificationCode,String clientCode, int page, String dateTime, String ordernumber){
        log.info("공급사에서 고객사 주문 리스트 조회 api - service");
        page =  (page - 1) * 10;
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.getSupplierOfClientOrderList(classificationCode, clientCode, page, dateTime, ordernumber)), HttpStatus.OK);
    }
     **/

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 공급사의 고객사 주문 리스트 조회 service [개선]
    public ResponseEntity<ResponseBody> newGetSupplierOfClientOrderList(String classificationCode,String clientCode, int page, String dateTime, String ordernumber){
        log.info("공급사에서 고객사 주문 리스트 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.newGetSupplierOfClientOrderList(classificationCode, clientCode, page, dateTime, ordernumber)), HttpStatus.OK);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 공급사에서 고객사 주문 리스트 조회 api  service
    public ResponseEntity<ResponseBody> getSupplierOfClientOrderxlsxList(String classificationCode,String clientCode,  String dateTime, String ordernumber){
        log.info("공급사에서 고객사 주문 리스트 조회 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.getSupplierOfClientOrderxlsxList(classificationCode, clientCode, dateTime, ordernumber)), HttpStatus.OK);
    }


    // 관리사 대시보드 service
    public ResponseEntity<ResponseBody> getAdminDashboard(){
        log.info("관리사 대시보드 조회 api - service");

        // 실시간 데이터 현황
        DashboardLiveDataResponseDto dashboardLiveData = processQueryData.getDashboardLiveData();
        // 현월 데이터 현황
        DashboardMonthDataResponseDto dashboardMonthData = processQueryData.getDashboardMonthData();
        // 지구를 지키는 지표
        DashboardSaveEarthDataResponseDto dashboardSaveEarthData = processQueryData.getDashboardSaveEarthData();
        // 일일 구입 요청량
        List<DashboardDailyPurchaseResponseDto> dashboardDailyPurchaseData = processQueryData.getDashboardDailyPurchaseData();

        // 전체 관리사 대시보드 내용
        DashboardResponseDto totalDashboardData = DashboardResponseDto.builder()
                .dashboardLiveData(dashboardLiveData)
                .dashboardMonthData(dashboardMonthData)
                .dashboardSaveEarthData(dashboardSaveEarthData)
                .dashboardDailyPurchaseData(dashboardDailyPurchaseData)
                .build();

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, totalDashboardData), HttpStatus.OK);
    }
    // 관리사 대시보드 service
    public ResponseEntity<ResponseBody> getAdminDashboardv2(){
        log.info("관리사 대시보드 조회 api - service");

        // 실시간 데이터 현황
        DashboardLiveDataResponseDto dashboardLiveData = processQueryData.getDashboardLiveData();
        // 현월 데이터 현황
        DashboardMonthDataResponseDto dashboardMonthData = processQueryData.getDashboardMonthData();
        // 지구를 지키는 지표
        DashboardSaveEarthDataResponseDto dashboardSaveEarthData = processQueryData.getDashboardSaveEarthData();
        // 일일 구입 요청량
        List<DashboardDailyPurchaseResponseDto> dashboardDailyPurchaseData = processQueryData.getDashboardDailyPurchaseData();

        // 전체 관리사 대시보드 내용
        DashboardResponseDto totalDashboardData = DashboardResponseDto.builder()
                .dashboardLiveData(dashboardLiveData)
                .dashboardMonthData(dashboardMonthData)
                .dashboardSaveEarthData(dashboardSaveEarthData)
                .dashboardDailyPurchaseData(dashboardDailyPurchaseData)
                .build();

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, totalDashboardData), HttpStatus.OK);
    }


    // 공급사 대시보드 service
    public ResponseEntity<ResponseBody> getSupplierDashboard(String classificationCode){
        log.info("공급사 대시보드 조회 api - service");

        // 실시간 데이터 현황
        SupplierDashboardLiveDataResponseDto supplierDashboardLiveData = processQueryData.getSupplierDashboardLiveData(classificationCode);
        // 실시간 재고 현황
        List<SupplierDashboardLiveRemainResponseDto>  SupplierDashboardLiveRemainData = processQueryData.getSupplierDashboardLiveRemainData(classificationCode);


        // 전체 공급사 대시보드 내용
        SupplierDashboardResponseDto supplierDashboardData = SupplierDashboardResponseDto.builder()
                .supplierDashboardLiveData(supplierDashboardLiveData)
                .supplierDashboardLiveRemainDatas(SupplierDashboardLiveRemainData)
                .build();

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, supplierDashboardData), HttpStatus.OK);
    }

    // 공급사 대시보드 v2 service
    public ResponseEntity<ResponseBody> getSupplierDashboardV2(String classificationCode, String year){
        log.info("공급사 대시보드v2 조회 api - service");

        // 전체기간
        SupplierDashboardV2OrderResponseDto supplierTotal = processQueryData.getSupplierDashboardV2TotalData(classificationCode);

        // 최대 사용 다회용기
        int maxCycle = processQueryData.getSupplierDashboardV2MaxCycle(classificationCode);

        //탄소배출점감량
        SupplierDashboardV2CO2ResponseDto supplierCO2Total = processQueryData.getSupplierDashboardV2CO2(classificationCode);

        //월별 탄소배출 저감량
        List<SupplierDashboardV2CO2MonthResponseDto> supplierCO2Month = processQueryData.getSupplierDashboardV2CO2Month(classificationCode, year);

        //첫주문 날짜
        String firstDate = processQueryData.firstOrderDate(classificationCode);

        // 전체 공급사 대시보드 내용
        SupplierDashboardv2ResponseDto supplierDashboardData = SupplierDashboardv2ResponseDto.builder()
                .supplierTotal(supplierTotal)
                .maxCycle(maxCycle)
                .supplierCO2Total(supplierCO2Total)
                .supplierCO2Month(supplierCO2Month)
                .firstOrder(firstDate)
                .build();

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, supplierDashboardData), HttpStatus.OK);
    }

    // 공급사 대시보드 v2 bar service
    public SupplierDashboardv2BarResponseDto getSupplierDashboardV2bar(String classificationCode, String clientCode, String productCode, String date){
        log.info("공급사 대시보드v2 bar조회 api - service");

        // 전체기간
        SupplierDashboardv2BarResponseDto dashbordBar = processQueryData.getSupplierDashboardV2bar(classificationCode,clientCode,productCode,date);


        return dashbordBar;
    }

    // 공급사 대시보드 v2 service
    public int getClientDashboardV2(String clientCode){
        log.info("고객사 대시보드v2 조회 api - service");

        // 회수수량
        int clientOrderTotal = processQueryData.getClientDashboardV2TotalData(clientCode);

        return clientOrderTotal;
    }
    // 공급사 대시보드 v2 service
    public JSONObject getClientDashboardV2bar(String clientCode, String productCode, String date){
        log.info("고객사 대시보드v2 조회 api - service");

        // 주문수량
        JSONObject clientOrderTotal = processQueryData.getClientDashboardV2BarTotalData(clientCode, productCode, date);

        return clientOrderTotal;
    }
    //공급사에서 고객사 주문 주문 완료처리  service
    public ResponseEntity<ResponseBody> putorderStatusUpdate(String clientOrderId, String orderStatus){
        log.info("공급사에서 고객사 주문 주문 완료처리 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.putorderStatusUpdate(clientOrderId, orderStatus)), HttpStatus.OK);

    }

    // 관리사에서 공급사 주문 삭제
    public ResponseEntity<ResponseBody> supplierOrderDelete(String orderId){
        log.info("관리사에서 공급사 주문 삭제 api - service");

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, processQueryData.supplierOrderDelete(orderId)), HttpStatus.OK);

    }
}
