package com.web.circularlabs_web_backend.process.controller;

import com.web.circularlabs_web_backend.process.request.ClientOrderRequestDto;
import com.web.circularlabs_web_backend.process.request.SupllierOrderRequestDto;
import com.web.circularlabs_web_backend.process.request.SupplierRecallRequestDto;
import com.web.circularlabs_web_backend.process.response.SupplierDashboardv2BarResponseDto;
import com.web.circularlabs_web_backend.process.service.ProcessService;
import com.web.circularlabs_web_backend.share.ResponseBody;
import com.web.circularlabs_web_backend.share.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cl/process")
@RestController
public class ProcessController {

    private final ProcessService processService;

    // 재고관리 api
    @GetMapping("/stockList")
    public ResponseEntity<ResponseBody> supplierStock(@RequestParam String classificationCode, @RequestParam int page){
        log.info("재고관리 리스트 api - controller");
        log.info("공급사 코드 : {}", classificationCode);

        return processService.supplierStockList(classificationCode, page);
    }

    // 공급사 페기이력  리스트 조회 api
    @GetMapping("/discardhistory")
    public ResponseEntity<ResponseBody> getDiscardHistoryList(@RequestParam String classificationCode,@RequestParam int page){
        log.info("공급사 페기이력  리스트 조회 api - controller");

        return processService.getDiscardHistoryList(classificationCode, page);
    }
    // 공급사 페기이력  리스트 조회 api
    @GetMapping("/discardhistory/xlsx")
    public ResponseEntity<ResponseBody> getDiscardHistoryxlsxList(@RequestParam String classificationCode){
        log.info("공급사 페기이력  리스트 조회 api - controller");

        return processService.getDiscardHistoryxlsxList(classificationCode);
    }
    // 공급사 에서 고객사 페기이력  리스트 조회 api
    @GetMapping("/discardclienthistory")
    public ResponseEntity<ResponseBody> getDiscardClientHistoryList(@RequestParam String clientcode,@RequestParam int page){
        log.info("공급사 에서 고객사 페기이력  리스트 조회 api - controller");

        return processService.getDiscardClientHistoryList(clientcode, page);
    }
    // 공급사 에서 고객사 페기이력  리스트 조회 api
    @GetMapping("/discardclienthistor/xlsx")
    public ResponseEntity<ResponseBody> getDiscardClientHistoryXlsxList(@RequestParam String clientcode){
        log.info("공급사 에서 고객사 페기이력  리스트 조회 api - controller");

        return processService.getDiscardClientHistoryXlsxList(clientcode);
    }
    // 공급사 페기이력 사유 수정 api
    @PutMapping("/discardhistory/update")
    public ResponseEntity<ResponseBody> getDiscardHistoryUpdate(@RequestParam String discardHistoryId,@RequestParam String reason){
        log.info("공급사 페기이력 사유 수정 api - controller");

        return processService.putDiscardHistoryUpdate(discardHistoryId, reason);
    }
    // 공급사 주문 요청 (관리사에게 제품 발주 요청) api
    @PostMapping("/order/sc")
    public ResponseEntity<ResponseBody> supplierOrder(@RequestBody SupllierOrderRequestDto supllierOrderRequestDto){
        log.info("공급사 주문 요청 (관리사에게 제품 발주 요청) api - controller");

        return processService.supplierOrder(supllierOrderRequestDto);
    }
    // 공급사 주문 리스트 api
    @GetMapping("/order/list/sc")
    public ResponseEntity<ResponseBody> getsupplierOrderList(@RequestParam String classificationCode,@RequestParam int page, @RequestParam(required = false) String dateTime,  @RequestParam(required = false) String productcode){
        log.info("공급사 주문 리스트 api - controller");

        return processService.newGetsupplierOrderList(classificationCode, page, dateTime, productcode);
    }
    // 공급사 주문 리스트 api
    @GetMapping("/order/list/xlsx")
    public ResponseEntity<ResponseBody> getsupplierOrderxlsxList(@RequestParam String classificationCode, @RequestParam(required = false) String dateTime,  @RequestParam(required = false) String productcode){
        log.info("공급사 주문 리스트 api - controller");

        return processService.getsupplierOrderxlsxList(classificationCode, dateTime, productcode);
    }
    // 공급사 주문 납품가능일 수정 api
    @PutMapping("/orderdeliveryAt/update")
    public ResponseEntity<ResponseBody> putOrerdeliveryAtUpdate(@RequestParam String orderId,@RequestParam String deliveryAt){
        log.info("공급사 주문 납품가능일 수정 api - controller");

        return processService.putOrerdeliveryAtUpdate(orderId, deliveryAt);
    }
    // 공급사 측에서 제품 반품 요청 api
    @PostMapping("/recall/sc")
    public ResponseEntity<ResponseBody> supplierRecallProduct(@RequestBody SupplierRecallRequestDto supplierRecallRequestDto){
        log.info("공급사 측에서 제품 반품 요청 api - controller");

        return processService.supplierRecallProduct(supplierRecallRequestDto);
    }


    // 공급사가 가지고 있는 반품 리스트 조회 api
    @GetMapping("/recall/list/sc")
    public ResponseEntity<ResponseBody> getSupplierRecallList(
            @RequestParam String classificationCode,
            @RequestParam(required = false) String dateTime, @RequestParam int page,@RequestParam(required = false) String productcode){
        log.info("공급사가 가지고 있는 반품 리스트 조회 api - controller");

        return processService.getSupplierRecallList(classificationCode, dateTime, page, productcode);
    }
    // 공급사가 가지고 있는 반품 리스트 조회 api
    @GetMapping("/recall/list/xlsx")
    public ResponseEntity<ResponseBody> getSupplierRecallxlsxList(
            @RequestParam String classificationCode,
            @RequestParam(required = false) String dateTime, @RequestParam(required = false) String productcode){
        log.info("공급사가 가지고 있는 반품 리스트 조회 api - controller");

        return processService.getSupplierRecallxlsxList(classificationCode, dateTime, productcode);
    }
    // 공급사 반품 수거 일정 수정 api
    @PutMapping("/recallpossibleRecallAt/update")
    public ResponseEntity<ResponseBody> putRecallpossibleRecallAtUpdate(@RequestParam String recallId,@RequestParam String possibleRecallAt){
        log.info("공급사 반품 수거 일정 수정 api - controller");

        return processService.putRecallpossibleRecallAtUpdate(recallId, possibleRecallAt);
    }
    // 고객사 주문 리스트 조회 api
    @GetMapping("/orderlist/cl")
    public ResponseEntity<ResponseBody> getClientOrderList(@RequestParam String classificationCode,@RequestParam int page){
        log.info("고객사에 주문 리스트 조회 api - controller");

        return processService.getClientOrderList(classificationCode, page);
    }
    //고객사 주문 요청 (공급사로 발주 요청) api
    @PostMapping("/order/cl")
    public ResponseEntity<ResponseBody> getClientOrder(@RequestBody ClientOrderRequestDto clientOrderRequestDto){
        log.info("고객사에서 공급사측으로 주문 요청 api - controller");

        return processService.ClientOrder(clientOrderRequestDto);
    }
    //고객사 주문 수정 (공급사로 발주 요청) api
    @PostMapping("/orderupdate/cl")
    public ResponseEntity<ResponseBody> getClientOrderUpdate(@RequestParam String clientOrderId, @RequestParam int orderMount,@RequestParam String hopeDeliveryAt){
        log.info("고객사에서 공급사측으로 주문 수정 요청 api - controller");

        return processService.ClientOrderUpdate(clientOrderId, orderMount, hopeDeliveryAt);
    }
    // 공급사에서 고객사 주문 리스트 조회 api
    @GetMapping("/orderlist/sc")
    public ResponseEntity<ResponseBody> getSupplierOfClientOrderList(@RequestParam String classificationCode, @RequestParam(required = false, defaultValue = "") String clientCode, @RequestParam int page, @RequestParam(required = false) String dateTime, @RequestParam(required = false) String ordernumber){
        log.info("공급사에서 고객사 주문 리스트 조회 api - controller");

        return processService.newGetSupplierOfClientOrderList(classificationCode, clientCode, page, dateTime, ordernumber);
    }
    // 공급사에서 고객사 주문 리스트 조회 api
    @GetMapping("/orderlist/sc/xlsx")
    public ResponseEntity<ResponseBody> getSupplierOfClientOrderxlsxList(@RequestParam String classificationCode, @RequestParam(required = false) String clientCode, @RequestParam(required = false) String dateTime, @RequestParam(required = false) String ordernumber){
        log.info("공급사에서 고객사 주문 리스트 조회 api - controller");

        return processService.getSupplierOfClientOrderxlsxList(classificationCode, clientCode, dateTime, ordernumber);
    }


    // 관리사 대시보드 api
    @GetMapping("/dashboard/admin")
    public ResponseEntity<ResponseBody> getAdminDashboard(){
        log.info("관리사 대시보드 조회 api - controller");

        return processService.getAdminDashboard();
    }
    // 관리사 대시보드 api
    @GetMapping("/dashboard/admin/v2")
    public ResponseEntity<ResponseBody> getAdminDashboardv2(){
        log.info("관리사 대시보드 조회 api - controller");

        return processService.getAdminDashboardv2();
    }

    // 공급사 대시보드 api
    @GetMapping("/dashboard/supplier")
    public ResponseEntity<ResponseBody> getSupplierDashboard(@RequestParam String classificationCode){
        log.info("공급사 대시보드 조회 api - controller");

        return processService.getSupplierDashboard(classificationCode);
    }
    // 공급사 대시보드v2-1 api
    @GetMapping("/dashboard/supplier/v2")
    public ResponseEntity<ResponseBody> getSupplierDashboardV2(@RequestParam String classificationCode, @RequestParam String year){
        log.info("공급사 대시보드v2 조회 api - controller");

        return processService.getSupplierDashboardV2(classificationCode,year);
    }

    @PersistenceContext
    private EntityManager em;
    // 공급사 대시보드v2-2 api
    @GetMapping("/dashboard/supplierBar/v2")
    public ResponseEntity<ResponseBody> getSupplierDashboardV2Bar(@RequestParam String classificationCode,@RequestParam(required = false, defaultValue = "") String clientCode,@RequestParam(required = false, defaultValue = "") String productCode,@RequestParam(required = false, defaultValue = "") String date){
        log.info("공급사 대시보드v2 바그래프 조회 api - controller");

        SupplierDashboardv2BarResponseDto result = processService.getSupplierDashboardV2bar(classificationCode,clientCode, productCode, date);

        String sql = "select count(*) from rental where 1 = 1";
        int cnt = 0;
        JSONObject resultObject = new JSONObject();


        if(!clientCode.isEmpty()){
            sql += " and part_cd = '" +clientCode + "'";
        }
        if(!productCode.isEmpty() ){
            sql += " and prod_code = '" +productCode + "'";
        }
        if(!date.isEmpty()){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String[] dateTimeConvert1 = date.split("/");
            String startdate = dateTimeConvert1[0];
            String enddate = dateTimeConvert1[1];
            startdate += " 00:00:00";
            enddate += " 23:59:59";
            // - 기호 기준으로 조회 반품 요청 일자 분리
            LocalDateTime dateTimeStart = LocalDateTime.parse(startdate, formatter);
            LocalDateTime dateTimeEnd = LocalDateTime.parse(enddate, formatter);
            sql += " and (created_at between '" + dateTimeStart + "' and '" + dateTimeEnd + "')";
        }

        cnt = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());

        double recovery = (double) result.getReturnCount() / cnt * 100;
        String recoveryString = String.format("%.1f",recovery);

        resultObject.put("orderCount", result.getOrderCount());
        resultObject.put("timeorderCount", result.getTimeOrderCount());
        resultObject.put("rentalCount", cnt);
        resultObject.put("returnCount", result.getReturnCount());
        resultObject.put("recovery", recoveryString);

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, resultObject), HttpStatus.OK);
    }
    // 고객사 대시보드v2-1 api
    @GetMapping("/dashboard/client/v2")
    public ResponseEntity<ResponseBody> getClientDashboardV2(@RequestParam String clientCode,@RequestParam String year){
        log.info("고객사 대시보드v2 조회 api - controller");
        JSONObject result = new JSONObject();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        YearMonth currentYearMonth = YearMonth.of(now.getYear(), now.getMonth());
        String firstDayOfMonth = String.valueOf(currentYearMonth.atDay(1));
        String lastDayOfMonth = String.valueOf(currentYearMonth.atEndOfMonth());
        firstDayOfMonth += " 00:00:00";
        lastDayOfMonth += " 23:59:59";
        LocalDateTime dateTimeStart = LocalDateTime.parse(firstDayOfMonth, formatter);
        LocalDateTime dateTimeEnd = LocalDateTime.parse(lastDayOfMonth, formatter);

        //전체 회수
        int returnCupCount = processService.getClientDashboardV2(clientCode);
        result.put("returnCupCount", returnCupCount);

        //현재 달 대여수량
        String sql1= "select sum(count) sumCount from (select count(*) count from rental where part_cd = '" + clientCode + "' and (created_at between '" + dateTimeStart + "' and '" + dateTimeEnd + "' ) GROUP BY fn_dec(user_phone), fn_dec(user_name)) A";
        int rentalMonthcnt = 0;

        if(em.createNativeQuery(sql1).getSingleResult() != null){
            rentalMonthcnt = Integer.parseInt(em.createNativeQuery(sql1).getSingleResult().toString());

        result.put("rentalMonthcnt", rentalMonthcnt);


        //전체대여 수량, 인당 평균 컵 사용수량
        String sql2 = "select sum(count) sumCount, avg(count) avgCount from (select count(*) count from rental as r1 where part_cd = '" + clientCode + "' GROUP BY fn_dec(user_phone), fn_dec(user_name)) A";
        List<Object[]> list = em.createNativeQuery(sql2).getResultList();
        list.forEach(li -> {
            result.put("rentalcnt", li[0]); //전체 대여수량
            result.put("avgCount", li[1]); //인당 평균 컵 사용수량
        });
        JSONArray ja = new JSONArray();
        for (int month = 1; month < 13 ; month++) {

            String yearMonthString = "";
            JSONObject jobj = new JSONObject();
            if(10 > month){
                yearMonthString = "0" + String.valueOf(month);
            }else {
                yearMonthString = String.valueOf(month);
            }
            String yearandMonth = year + "/" + yearMonthString;

            String sql3 = "select count(*) from rental where part_cd = '" + clientCode + "' and DATE_FORMAT(created_at,'%Y') = '" + year + "' and DATE_FORMAT(created_at,'%m') = '"+ yearMonthString +"'";
            String monthlist = em.createNativeQuery(sql3).getSingleResult().toString();

            jobj.put("count", monthlist);
            jobj.put("yearmonth", yearandMonth);
            ja.add(jobj);
        }
        String sql4 = "select created_at from rental where part_cd = '" + clientCode + "' order by created_at ASC LIMIT 1";
        String firstDate = "";
        if(em.createNativeQuery(sql4).getSingleResult() != null){
            firstDate = em.createNativeQuery(sql4).getSingleResult().toString();
        }

        result.put("monthList",ja);
        result.put("firstDate",firstDate);
        } else {
            result.put("rentalMonthcnt", rentalMonthcnt);
            result.put("rentalcnt", 0); //전체 대여수량
            result.put("avgCount", 0); //인당 평균 컵 사용수량
            result.put("monthList","");
            result.put("firstDate","");
        }
        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, result),HttpStatus.OK);
    }
    @GetMapping("/dashboard/clientBar/v2")
    public ResponseEntity<ResponseBody> getClientDashboardV2Bar(@RequestParam String clientCode,@RequestParam(required = false, defaultValue = "") String productCode,@RequestParam(required = false, defaultValue = "") String date){
        log.info("공급사 대시보드v2 바그래프 조회 api - controller");

        JSONObject result = processService.getClientDashboardV2bar(clientCode, productCode, date);


        String sql = "select count(*) from rental where 1 = 1";
        int cnt = 0;
        JSONObject resultObject = new JSONObject();


        if(!clientCode.isEmpty()){
            sql += " and part_cd = '" +clientCode + "'";
        }
        if(!productCode.isEmpty() ){
            sql += " and prod_code = '" +productCode + "'";
        }
        if(!date.isEmpty()){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String[] dateTimeConvert1 = date.split("/");
            String startdate = dateTimeConvert1[0];
            String enddate = dateTimeConvert1[1];
            startdate += " 00:00:00";
            enddate += " 23:59:59";

            LocalDateTime dateTimeStart = LocalDateTime.parse(startdate, formatter);
            LocalDateTime dateTimeEnd = LocalDateTime.parse(enddate, formatter);
            sql += " and (created_at between '" + dateTimeStart + "' and '" + dateTimeEnd + "')";
        }

        cnt = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());


//        double recovery = (double) result.get("timeReturnCount") / cnt * 100;
//        String recoveryString = String.format("%.1f",recovery);

        resultObject.put("orderCount", result.get("totalOrderCount"));
        resultObject.put("timeorderCount", result.get("timeOrderCount"));
        resultObject.put("rentalCount", cnt);
        resultObject.put("returnCount", result.get("timeReturnCount"));


        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, resultObject), HttpStatus.OK);
    }
    //태블릿 팝업보드
    @GetMapping("/popupbord/client/v2")
    public ResponseEntity<ResponseBody> getClientPopupbordV2(@RequestParam String deviceCode){
        log.info("고객사 대시보드v2 조회 api - controller");
        JSONObject result = new JSONObject();

        String sql2 = "select client_code from device2 where reader_code = '" + deviceCode + "'";
        String  clientCode = em.createNativeQuery(sql2).getSingleResult().toString();

        String sql = "select sum(count) sumCount from (select count(*) count from rental as r1 where part_cd = '" + clientCode + "' GROUP BY fn_dec(user_phone), fn_dec(user_name)) A";
        int  totalrentalcount = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());

        result.put("totalrentalCount", totalrentalcount);

        return new ResponseEntity<>(new ResponseBody(StatusCode.OK, result),HttpStatus.OK);
    }
    // 공급사에서 고객사 주문 주문 완료처리
    @PutMapping("/orderStatusUpdate")
    public ResponseEntity<ResponseBody> putorderStatusUpdate(@RequestParam String clientOrderId, @RequestParam String orderStatus){
        log.info("공급사에서 고객사 주문 주문 완료처리 api - controller");

        return processService.putorderStatusUpdate(clientOrderId, orderStatus);
    }
    // 관리사에서 공급사 주문 삭제
    @DeleteMapping("/orderdelete")
    public ResponseEntity<ResponseBody> supplierOrderDelete(@RequestParam String orderId){
        log.info("관리사에서 공급사 주문 삭제 api - controller");

        return processService.supplierOrderDelete(orderId);
    }
}
