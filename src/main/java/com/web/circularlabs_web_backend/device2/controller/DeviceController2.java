package com.web.circularlabs_web_backend.device2.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

//import com.web.circularlabs_web_backend.device.request.DeviceCreateRequestDto;
//import com.web.circularlabs_web_backend.share.ResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cl/device2")
@RestController
@Transactional
public class DeviceController2 {

    @PersistenceContext
    private EntityManager em;

    // 기기 등록 api
    @PostMapping("/create")
    //public ResponseEntity<ResponseBody> createDevice(@RequestBody String req){
    public JSONObject createDevice(@RequestBody Map<String, String> req){

        //log.info("기기 등록 api - controller");
        //log.info("등록하고자 하는 기기의 유형 : {} / 기기 코드 : {}", deviceCreateRequestDto.getDeviceType(), deviceCreateRequestDto.getDeviceCode());
        String clientCode = req.get("clientCode");
        String readerType = req.get("readerType");
        String readerCode = req.get("readerCode");
        System.out.println(clientCode + "," + readerType + "," + readerCode);

        //readerCode 중복 체크
        String sql = "select count(*) from device2 where reader_code = '" + readerCode + "'";
        int cnt = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());
        System.out.println(cnt);

        JSONObject res = new JSONObject();

        if(cnt == 0) {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = format.format(date);
            System.out.println(currentDateTime);

            sql = "insert into device2 (created_at, client_code, reader_type, reader_code) values ('" + currentDateTime + "','" + clientCode + "','" + readerType + "','" + readerCode + "')";
            int result = em.createNativeQuery(sql).executeUpdate();

            if (result == 0) { // 실패
                res.put("statusCode", "C-401");
                res.put("statusMessage", "등록 실패");
                res.put("data", "");
            } else { // 성공
                res.put("statusCode", "C-200");
                res.put("statusMessage", "정상 수행");
                res.put("data", "");
            }

        }else{
            res.put("statusCode", "C-402");
            res.put("statusMessage", "리더기 중복");
            res.put("data", "");
        }

        return res;
    }


    // 기기 리스트 api (관리사)
    @GetMapping("/list")
    //public ResponseEntity<ResponseBody> selectDevice(@RequestParam int page, @RequestParam String supplyCode, @RequestParam String clientCode){
    public JSONObject selectDevice(@RequestParam int page, @RequestParam String supplyCode, @RequestParam String clientCode){
        log.info("기기 조회 api - controller");
        log.info(" 페이지 : {}",  page);
        System.out.println(page + "," + supplyCode + "," + clientCode);
        String sql = "";
        //int result;
        int limit = 10;
        int offset = (page - 1) * limit;

        /*
        if (clientCode == ""){
            sql = "select count(*) from device2 where client_code in (SELECT classification_code from member where mother_code = '" + supplyCode + "') and del_yn = 'N'";
            result = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());
            System.out.println(result);
        }else{
           sql =  "select count(*) from device2 where client_code in (SELECT classification_code from member where mother_code = '" + supplyCode + "') and client_code = '" + clientCode + "' and del_yn = 'N'";
           result = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());
           System.out.println(result);
        }
        */

        JSONObject res = new JSONObject();
        JSONArray ja = new JSONArray();
        if (clientCode == ""){
            sql = "select count(*) from device2 where client_code in (SELECT classification_code from member where mother_code = '" + supplyCode + "') and del_yn = 'N'";
            int cnt = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());
            //System.out.println(cnt);

            sql = "select device_id, created_at, client_code, (select client_company from member where classification_code = client_code and grade = 2) client_name, reader_type, reader_code from device2 where client_code in (SELECT classification_code from member where mother_code = '" + supplyCode + "') and del_yn = 'N' limit " + limit + " offset " + offset;
            List<Object[]> list = em.createNativeQuery(sql).getResultList();
            list.forEach(li -> {
               JSONObject jobj = new JSONObject();
               jobj.put("deviceId", li[0]);
               jobj.put("createdAt", li[1]);
               jobj.put("clientCode", li[2]);
               jobj.put("clientName", li[3]);
               jobj.put("readerType",li[4]);
               jobj.put("readerCode", li[5]);
               ja.add(jobj);
            });
            res.put("data", ja);
            res.put("total", cnt);
            res.put("statusCode", "C-200");
            res.put("statusMessage", "정상 수행");
        }else{
            sql = "select count(*) from device2 where client_code in (SELECT classification_code from member where mother_code = '" + supplyCode + "') and client_code = '" + clientCode + "' and del_yn = 'N'";
            int cnt = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());

            sql =  "select device_id, created_at, client_code, (select client_company from member where classification_code = client_code and grade = 2) client_name, reader_type, reader_code from device2 where client_code in (SELECT classification_code from member where mother_code = '" + supplyCode + "') and client_code = '" + clientCode + "' and del_yn = 'N' limit " + limit + " offset " + offset;
            List<Object[]> list = em.createNativeQuery(sql).getResultList();
            list.forEach(li -> {
                JSONObject jobj = new JSONObject();
                jobj.put("deviceId", li[0]);
                jobj.put("createdAt", li[1]);
                jobj.put("clientCode", li[2]);
                jobj.put("clientName", li[3]);
                jobj.put("readerType",li[4]);
                jobj.put("readerCode", li[5]);
                ja.add(jobj);
            });
            res.put("data", ja);
            res.put("tatal", cnt);
            res.put("statusCode", "C-200");
            res.put("statusMessage", "정상 수행");
        }

        return res;
    }

    // 기기 정보 수정 api (관리사)
    @PutMapping("/update")
    //public ResponseEntity<ResponseBody> updateDevice(@RequestBody DeviceCreateRequestDto deviceCreateRequestDto){
    public JSONObject updateDevice(@RequestBody Map<String, String> req){
        log.info("기기 정보 수정 api - controller");

        String deviceId = req.get("deviceId");
        String clientCode = req.get("clientCode");
        String readerType = req.get("readerType");
        String readerCode = req.get("readerCode");
        System.out.println(clientCode + "," + readerType + "," + readerCode);

        //readerCode 중복 체크
        //String sql = "select count(*) from device2 where reader_code = '" + readerCode + "'";
        //int result = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());
        //System.out.println(result);

        //if(result == 0) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = format.format(date);
        System.out.println(currentDateTime);

        //sql = "insert into device2 (created_at, client_code, reader_type, reader_code) values ('" + currentDateTime + "','" + clientCode + "','" + readerType + "','" + readerCode + "')";
        String sql = "update device2 set modified_at = '" + currentDateTime + "', client_code = '" + clientCode + "', reader_type = '" + readerType + "', reader_code = '" + readerCode + "' where device_id = " + deviceId;
        int result = em.createNativeQuery(sql).executeUpdate();
        //};

        JSONObject res =new JSONObject();
        if(result == 0){ // 실패
            res.put("statusCode", "C-401");
            res.put("statusMessage", "수정 실패");
            res.put("data", "");
        }else{ // 성공
            res.put("statusCode", "C-200");
            res.put("statusMessage", "정상 수행");
            res.put("data", "");
        }
        return res;
    }


    @DeleteMapping("/delete")
    //public ResponseEntity<ResponseBody> updateDevice(@RequestBody DeviceCreateRequestDto deviceCreateRequestDto){
    public JSONObject deleteDevice(@RequestParam int deviceId){
        System.out.println(deviceId);

        String sql = "update device2 set del_yn = 'Y' where device_id =" + deviceId + "  and del_yn = 'N'";
        int result = em.createNativeQuery(sql).executeUpdate();
        System.out.println(result);

        JSONObject res =new JSONObject();
        if(result == 0){ // 실패
            res.put("statusCode", "C-401");
            res.put("statusMessage", "삭제 실패");
            res.put("data", "");
        }else{ // 성공
            res.put("statusCode", "C-200");
            res.put("statusMessage", "정상 수행");
            res.put("data", "");
        }
        return res;
    }


}
