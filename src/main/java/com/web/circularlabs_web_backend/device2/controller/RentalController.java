package com.web.circularlabs_web_backend.device2.controller;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

//import com.web.circularlabs_web_backend.device.request.DeviceCreateRequestDto;
//import com.web.circularlabs_web_backend.share.ResponseBody;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.apache.http.conn.HttpHostConnectException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ContentType;
import org.json.simple.parser.JSONParser;
import org.apache.http.Header;
import javax.crypto.Cipher;
import java.net.URLDecoder;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cl/rental")
@RestController
@Transactional
public class RentalController {

    @PersistenceContext
    private EntityManager em;

    public static String key = "hapyhabitnmm9284";

    @PostMapping("/test")
    public String test(@RequestBody Map<String, Object> req){
        if(req == null || req.isEmpty()){
            System.out.println("No Param");
        }else{
            System.out.println("Param");
        }
        /*
        int starCnt = 0;
        String star = "";
        String userName = "가나다라마";
        String maskingValue = userName;
        if (userName.length() == 2){
            maskingValue = "*"+userName.substring(1);
        }else if (userName.length() == 3) {
            maskingValue = userName.substring(0, 1) + "*" + userName.substring(2);
        }else if (userName.length() >= 4) {
            //maskingValue = userName.substring(0,1)+"**"+userName.substring(3);
            starCnt = userName.length() - 1 - 1;
            for (int i = 0; i < starCnt; i++) {
                star += "*";
            }
            maskingValue = userName.substring(0, 1) + star + userName.substring(userName.length() - 1);
        }
        System.out.println(maskingValue);
        */
        System.out.println(maskingName("가나"));
        System.out.println(maskingName("가나다"));
        System.out.println(maskingName("가나다라"));
        System.out.println(maskingName("가나다라마"));
        System.out.println(maskingName("가나다라마바"));

        System.out.println(maskingCard("1234567890123456"));

        System.out.println(maskingPhone("0101231234"));
        System.out.println(maskingPhone("01012341234"));

        return "hello world";
    }

    public String maskingPhone(String userPhone){
        if(StringUtils.isEmpty(userPhone)){
            return userPhone;
        }
        if(!(userPhone.length() == 10 || userPhone.length() == 11)){
            return userPhone;
        }
        if(userPhone.length() == 10){
            return userPhone.substring(0, 3) + "***" + userPhone.substring(6, 10);
        }else if(userPhone.length() == 11) {
            return userPhone.substring(0, 3) + "****" + userPhone.substring(7, 11);
        }
        return userPhone;
    }
    public String maskingCard(String barCode){

        //String maskingValue = barCode.substring(0, 4) + "********" + barCode.substring(12, 16);
        String maskingValue = barCode.substring(0, 6) + "******" + barCode.substring(12, 16);

        return maskingValue;
    }

    // 이름 마스킹 룰 : 두자일 경우 앞1자 *, 세자일 경우 가운데자 *, 4자 이상일 경우 처음과 마지막을 제외하고 모두 *
    public String maskingName(String userName){

        int starCnt = 0;
        String star = "";
        //String userName = "가나다라마";
        String maskingValue = userName;
        if (userName.length() == 2){
            maskingValue = "*"+userName.substring(1);
        }else if (userName.length() == 3) {
            maskingValue = userName.substring(0, 1) + "*" + userName.substring(2);
        }else if (userName.length() >= 4) {
            //maskingValue = userName.substring(0,1)+"**"+userName.substring(3);
            starCnt = userName.length() - 1 - 1;
            for (int i = 0; i < starCnt; i++) {
                star += "*";
            }
            maskingValue = userName.substring(0, 1) + star + userName.substring(userName.length() - 1);
        }

        return maskingValue;
    }

    @GetMapping("/getProdList")
    public JSONObject getProdName(){

        JSONObject res = new JSONObject();
        JSONArray ja = new JSONArray();

        String sql = "select product_code, product_name from product";
        List<Object[]> list = em.createNativeQuery(sql).getResultList();

        list.forEach(li -> {
            JSONObject jobj = new JSONObject();
            jobj.put("prodCode", li[0]);
            jobj.put("prodName", li[1]);
            ja.add(jobj);
        });
        res.put("prodList", ja);

        return res;
    }


    @PostMapping("/rfid_api/deleteUsrInfo")
    public JSONObject deleteUsrInfo(@RequestBody Map<String, Object> req) throws Exception {
        JSONObject res = new JSONObject();

        String usrNo = (String) req.get("usrNo");
        if(usrNo.length() != 16){
            res.put("resutlCode", "0099");
            res.put("resultMessage", "실패");
            res.put("reason", "바코드 자리수 확인");
            return res;
        }

        try {
            //usrNo = decrypt(usrNo);

            String sql = "delete from rental where fn_dec(bar_code) = '" + usrNo + "'";
            System.out.println(sql);
            //cnt = em.createNativeQuery(sql).executeUpdate();
            em.createNativeQuery(sql).executeUpdate();

            res.put("resultCode", "0000");
            res.put("resultMessage", "성공");
            return res;
        }catch(Exception e){
            res.put("resutlCode", "0099");
            res.put("resultMessage", "실패");
            return res;
        }
    }

    @GetMapping("/checkDevice")
    public JSONObject checkDevice(@RequestParam String device_code){
        JSONObject res = new JSONObject();
        //System.out.println(device_code);

        String sql = "select count(*) from device2 where reader_code = '" + device_code + "'";
        System.out.println(sql);
        //cnt = em.createNativeQuery(sql).executeUpdate();
        int cnt = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());
        System.out.println(cnt);

        if(cnt > 0){
            res.put("resultCode", "0000");
            res.put("resultMessage", "성공");
        }else{
            res.put("resutlCode", "0099");
            res.put("resultMessage", "실패");
            res.put("reason", "존재하지 않는 기기코드입니다.");
        }

        return res;
    }

    @PostMapping("/rfid_api/selectUserName")
    public JSONObject selectUserName(@RequestBody Map<String, Object> req) throws Exception {
        String  serviceId = "RFID001";
        JSONObject res = new JSONObject();

        String usrNo = (String) req.get("usrNo");
        String sql;
        String sql2;
        int cnt;
        int cnt2;
        //System.out.println(usrNo);
        try {
            RequestConfig requesetConfig = RequestConfig.custom()
                    .setConnectTimeout(15 * 1000)
                    .setSocketTimeout(15 * 1000)
                    .setConnectionRequestTimeout(15 * 1000)
                    .build();

            // stg
            //HttpPost post = new HttpPost("http://stg.happyhabit.co.kr:8080/RFID_API/selectUserName");
            // prd
            HttpPost post = new HttpPost("http://www.happyhabit.co.kr/RFID_API/selectUserName");
            post.setConfig(requesetConfig);

            post.setHeader("accept", "Application/json");
            //post.setHeader("Content-Type", "application/json");
            post.setHeader("serviceId", serviceId);
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date d = new Date();
            String serviceCheck = sf.format(d) + serviceId;
            post.setHeader("serviceCheck", serviceCheck);

            JSONObject app = new JSONObject();

            //app.put("usrNo", "3618913741447305");
            app.put("usrNo", usrNo);

            post.setEntity(new StringEntity(app.toString(), ContentType.APPLICATION_JSON));

            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(post);
            String result = EntityUtils.toString(response.getEntity());
            System.out.println(result);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(result);
            String statusCode = (String) jsonObj.get("statusCode");
            String resultCode = (String) jsonObj.get("resultCode");
            String resultMessage = (String) jsonObj.get("resultMessage");

            if ("0000".equals(resultCode)) {
                JSONObject data = (JSONObject) jsonObj.get("data");
                String userName = (String) data.get("usrName");

                System.out.println(statusCode + "," + resultCode + "," + resultMessage + "," + userName);
                userName = decrypt(userName);
                System.out.println(userName);

                String maskingValue = maskingName(userName);

                sql = "select count(*) from rental where fn_dec(user_phone) = '" + usrNo + "'";
                cnt = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());
                sql2 = "select count(*) from rental where fn_dec(user_phone) = '" + usrNo + "' and rental_stat = 'C'";
                cnt2 = Integer.parseInt(em.createNativeQuery(sql2).getSingleResult().toString());
                res.put("resultCode", "0000");
                res.put("resultMessage", "성공");
                //res.put("userName", userName);
                res.put("userName", maskingValue);
                res.put("rentalCnt", cnt);
                res.put("compliteCnt", cnt2);
            } else {
                res.put("resutlCode", resultCode);
                res.put("resultMessage", resultMessage);
            }
            return res;
            //return "홍*동";
        }catch(Exception e){
            System.out.println(e);
            res.put("resutlCode", "00991");
            res.put("resultMessage", "실패");
            return res;
        }
    }


    // barcode, username은 DB 암호화처리, 암호화 방식은 mySQL에서 제공하는 AES 사용
    @PostMapping("/rfid_api/rentalCupReg")
    public JSONObject rentalCupReg(@RequestBody Map<String, Object> req) throws Exception {

        String serviceId = "RFID001";
        String companyCd = "CPN001";
        String sql = "";
        String sql2 = "";
        String sql3 = "";
        String deviceCode = (String) req.get("deviceCode");
        String barCode = (String) req.get("barCode");
        //String partCd = (String) req.get("partCd");
        String uuid = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        List<String> cupCd = (List<String>) req.get("cupCd");
        for(int i = 0; i < cupCd.size(); i++){
            cupCd.set(i, cupCd.get(i).replace(" ",""));
            LocalDateTime now = LocalDateTime.now();
            now.minusHours(12);
            sql2 = "select count(*) from rental where cup_cd = '" + cupCd.get(i) + "' and fn_dec(user_phone) = '" + barCode + "' and created_at >= DATE_SUB(NOW(), INTERVAL 6 HOUR);";
            int createdAtint =  Integer.parseInt(em.createNativeQuery(sql2).getSingleResult().toString());

            if(createdAtint > 0){
                cupCd.remove(i);
            }

        }

        if (cupCd.size() == 0) {
            JSONObject res = new JSONObject();
            res.put("resutlCode", "0099");
            res.put("resultMessage", "실패");
            return res;
        }
        if(barCode == null || barCode == ""){
            JSONObject res = new JSONObject();
            res.put("resutlCode", "0099");
            res.put("resultMessage", "실패");
            return res;
        }
        String commaStr = String.join(",", cupCd).replace("[","").replace("]","").replace(" ","");


        sql = "select client_code from device2 where reader_code = '" + deviceCode + "' and del_yn = 'N'";
        String partCd = em.createNativeQuery(sql).getSingleResult().toString();
        System.out.println(uuid + "," + deviceCode + "," + barCode + "," + cupCd + "," + partCd);

        try{
            RequestConfig requesetConfig = RequestConfig.custom()
                    .setConnectTimeout(10 * 1000)
                    .setSocketTimeout(10 * 1000)
                    .setConnectionRequestTimeout(10 * 1000)
                    .build();

            // stg
            //HttpPost post = new HttpPost("http://stg.happyhabit.co.kr:8080/RFID_API/rentalCupReg");
            // prd
            HttpPost post = new HttpPost("http://www.happyhabit.co.kr/RFID_API/rentalCupReg");
            post.setConfig(requesetConfig);

            post.setHeader("accept", "Application/json");
            //post.setHeader("Content-Type", "application/json");
            post.setHeader("serviceId", serviceId);
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date d = new Date();
            String serviceCheck = sf.format(d) + serviceId;
            System.out.println(serviceCheck);
            System.out.println(serviceCheck.substring(0, 8));
            System.out.println(serviceCheck.substring(8, 14));
            post.setHeader("serviceCheck", serviceCheck);

            JSONObject app = new JSONObject();

            String trdDate = serviceCheck.substring(0, 8);
            String trdTime = serviceCheck.substring(8, 14);
            //app.put("usrNo", "3618913741447305");
            app.put("usrNo", barCode);
            app.put("trdDate", trdDate);
            app.put("trdTime", trdTime);
            //app.put("trdNo", "12345678901234567890");
            app.put("trdNo", uuid);
            //app.put("machineId", "12345678901234567891");
            app.put("machineId", deviceCode);
            //rfidInfo.add("CCA23101AA1020000D240412");
            //rfidInfo.add("CCA23101AA1020000D240412");
            //app.put("rfidInfo", rfidInfo);
            //app.put("cupCd", prodSerial);
            app.put("cupCd", commaStr);
            //app.put("cupCount", "2");
            app.put("cupCount", Integer.toString(cupCd.size()));
            app.put("companyCd", companyCd);
            //app.put("partCd", "UC0045"); //jyp
            app.put("partCd", partCd); //jyp

            post.setEntity(new StringEntity(app.toString(), ContentType.APPLICATION_JSON));

            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(post);
            String result = EntityUtils.toString(response.getEntity());
            System.out.println(result);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(result);
            String statusCode = (String) jsonObj.get("statusCode");
            String resultCode = (String) jsonObj.get("resultCode");
            String resultMessage = (String) jsonObj.get("resultMessage");
            JSONObject data = (JSONObject) jsonObj.get("data");
            String userName = (String) data.get("usrName");
            String userPhone = (String) data.get("usrPhone");
            System.out.println(statusCode + "," + resultCode + "," + resultMessage + "," + userName + "," + userPhone);
            userName = decrypt(userName);
            userPhone = decrypt(userPhone);
            System.out.println(userName + "," + userPhone);

            JSONObject res = new JSONObject();
            //String sql = "";
            int cnt;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = format.format(d);

            System.out.println("----------------------" + resultCode);
            if ("0000".equals(resultCode)) {
                for (int i = 0; i < cupCd.size(); i++) {
                    //System.out.println(prodSerial.get(i));
                    //sql = "insert into rental (created_at, bar_code, user_name, device_code, trd_date, trd_time, trd_no, cup_cd, prod_serial, company_cd, part_cd, rental_stat) values ('" + currentDateTime + "',fn_enc('" + barCode + "'), fn_enc('" + userName + "'),'" + deviceCode + "','" + trdDate + "','" + trdTime + "','" + uuid + "','" + cupCd.get(i) + "','" + cupCd.get(i).substring(8, 24) + "','" +  companyCd + "','" + partCd + "','R')";
                    //sql = "insert into rental (created_at, bar_code, mask_code, user_name, mask_name, user_phone, mask_phone, device_code, trd_date, trd_time, trd_no, cup_cd, prod_code, prod_serial, company_cd, part_cd, rental_stat) values ('" + currentDateTime + "',fn_enc('" + barCode + "'), fn_enc('" + maskingCard(barCode) + "'), fn_enc('" + userName + "'), fn_enc('" + maskingName(userName) + "'), fn_enc('" + userPhone + "'), fn_enc('" + maskingPhone(userPhone) + "'), '" + deviceCode + "','" + trdDate + "','" + trdTime + "','" + uuid + "','" + cupCd.get(i) + "','" + cupCd.get(i).substring(8, 11) + "','" + cupCd.get(i).substring(8, 24) + "','" + companyCd + "','" + partCd + "','R')";
                    sql = "insert into rental (created_at, user_name, mask_name, user_phone, mask_phone, device_code, trd_date, trd_time, trd_no, cup_cd, prod_code, prod_serial, company_cd, part_cd, rental_stat) values ('" + currentDateTime + "', fn_enc('" + userName + "'), fn_enc('" + maskingName(userName) + "'), fn_enc('" + userPhone + "'), fn_enc('" + maskingPhone(userPhone) + "'), '" + deviceCode + "','" + trdDate + "','" + trdTime + "','" + uuid + "','" + cupCd.get(i) + "','" + cupCd.get(i).substring(8, 11) + "','" + cupCd.get(i).substring(8, 24) + "','" + companyCd + "','" + partCd + "','R')";
                    System.out.println(sql);
                    //cnt = em.createNativeQuery(sql).executeUpdate();
                    em.createNativeQuery(sql).executeUpdate();
                }

                res.put("resultCode", "0000");
                res.put("resultMessage", "성공");
            } else {
                System.out.println("22222222222");
                res.put("resutlCode", "0099");
                res.put("resultMessage", "실패");
            }
            return res;
            //return "홍*동";
        }catch(Exception e){

            try {
                RequestConfig requesetConfig = RequestConfig.custom()
                        .setConnectTimeout(10 * 1000)
                        .setSocketTimeout(10 * 1000)
                        .setConnectionRequestTimeout(10 * 1000)
                        .build();

                // stg
                //HttpPost post = new HttpPost("http://stg.happyhabit.co.kr:8080/RFID_API/rentalCupReg");
                // prd
                HttpPost post = new HttpPost("http://www.happyhabit.co.kr/RFID_API/rentalCupReg");
                post.setConfig(requesetConfig);

                post.setHeader("accept", "Application/json");
                //post.setHeader("Content-Type", "application/json");
                post.setHeader("serviceId", serviceId);
                SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                Date d = new Date();
                String serviceCheck = sf.format(d) + serviceId;
                System.out.println(serviceCheck);
                System.out.println(serviceCheck.substring(0, 8));
                System.out.println(serviceCheck.substring(8, 14));
                post.setHeader("serviceCheck", serviceCheck);

                JSONObject app = new JSONObject();

                String trdDate = serviceCheck.substring(0, 8);
                String trdTime = serviceCheck.substring(8, 14);
                //app.put("usrNo", "3618913741447305");
                app.put("usrNo", barCode);
                app.put("trdDate", trdDate);
                app.put("trdTime", trdTime);
                //app.put("trdNo", "12345678901234567890");
                app.put("trdNo", uuid);
                //app.put("machineId", "12345678901234567891");
                app.put("machineId", deviceCode);
                //rfidInfo.add("CCA23101AA1020000D240412");
                //rfidInfo.add("CCA23101AA1020000D240412");
                //app.put("rfidInfo", rfidInfo);
                //app.put("cupCd", prodSerial);
                app.put("cupCd", commaStr);
                //app.put("cupCount", "2");
                app.put("cupCount", Integer.toString(cupCd.size()));
                app.put("companyCd", companyCd);
                //app.put("partCd", "UC0045"); //jyp
                app.put("partCd", partCd); //jyp

                post.setEntity(new StringEntity(app.toString(), ContentType.APPLICATION_JSON));

                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(post);
                String result = EntityUtils.toString(response.getEntity());
                System.out.println(result);

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObj = (JSONObject) jsonParser.parse(result);
                String statusCode = (String) jsonObj.get("statusCode");
                String resultCode = (String) jsonObj.get("resultCode");
                String resultMessage = (String) jsonObj.get("resultMessage");
                JSONObject data = (JSONObject) jsonObj.get("data");
                String userName = (String) data.get("usrName");
                String userPhone = (String) data.get("usrPhone");
                System.out.println(statusCode + "," + resultCode + "," + resultMessage + "," + userName + "," + userPhone);
                userName = decrypt(userName);
                userPhone = decrypt(userPhone);
                System.out.println(userName + "," + userPhone);

                JSONObject res = new JSONObject();
                //String sql = "";
                int cnt;

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = format.format(d);

                if ("0000".equals(resultCode)) {

                    for (int i = 0; i < cupCd.size(); i++) {
                        //System.out.println(prodSerial.get(i));
                        //sql = "insert into rental (created_at, bar_code, user_name, device_code, trd_date, trd_time, trd_no, cup_cd, prod_serial, company_cd, part_cd, rental_stat) values ('" + currentDateTime + "',fn_enc('" + barCode + "'), fn_enc('" + userName + "'),'" + deviceCode + "','" + trdDate + "','" + trdTime + "','" + uuid + "','" + cupCd.get(i) + "','" + cupCd.get(i).substring(8, 24) + "','" +  companyCd + "','" + partCd + "','R')";
                        //sql = "insert into rental (created_at, bar_code, mask_code, user_name, mask_name, user_phone, mask_phone, device_code, trd_date, trd_time, trd_no, cup_cd, prod_code, prod_serial, company_cd, part_cd, rental_stat) values ('" + currentDateTime + "',fn_enc('" + barCode + "'), fn_enc('" + maskingCard(barCode) + "'), fn_enc('" + userName + "'), fn_enc('" + maskingName(userName) + "'), fn_enc('" + userPhone + "'), fn_enc('" + maskingPhone(userPhone) + "'), '" + deviceCode + "','" + trdDate + "','" + trdTime + "','" + uuid + "','" + cupCd.get(i) + "','" + cupCd.get(i).substring(8, 11) + "','" + cupCd.get(i).substring(8, 24) + "','" + companyCd + "','" + partCd + "','R')";
                        sql = "insert into rental (created_at, user_name, mask_name, user_phone, mask_phone, device_code, trd_date, trd_time, trd_no, cup_cd, prod_code, prod_serial, company_cd, part_cd, rental_stat) values ('" + currentDateTime + "', fn_enc('" + userName + "'), fn_enc('" + maskingName(userName) + "'), fn_enc('" + userPhone + "'), fn_enc('" + maskingPhone(userPhone) + "'), '" + deviceCode + "','" + trdDate + "','" + trdTime + "','" + uuid + "','" + cupCd.get(i) + "','" + cupCd.get(i).substring(8, 11) + "','" + cupCd.get(i).substring(8, 24) + "','" + companyCd + "','" + partCd + "','R')";
                        System.out.println(sql);
                        //cnt = em.createNativeQuery(sql).executeUpdate();
                        em.createNativeQuery(sql).executeUpdate();
                    }

                    res.put("resultCode", "0000");
                    res.put("resultMessage", "성공");
                } else {
                    res.put("resutlCode", "0099");
                    res.put("resultMessage", "실패");
                }
                return res;
                //return "홍*동";
            }catch(Exception e2){
                JSONObject res = new JSONObject();
                res.put("resutlCode", "0099");
                res.put("resultMessage", "실패");
                return res;

            }

        }

    }


    public static byte[] hexToByteArray(String hex) {

        if (hex == null || hex.length() == 0) {
            return null;
        }

        byte[] ba = new byte[hex.length() / 2];

        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }

        return ba;
    }


    // 암호화 모듈 ---> AES/ECB/PKCS5PADDING
    public static String decrypt(String encrypted) throws Exception {

        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] original = cipher.doFinal(hexToByteArray(encrypted));
        return new String(original);

    }


    @PostMapping("/rentalList")
    //public ResponseEntity<ResponseBody> selectDevice(@RequestParam int page, @RequestParam String supplyCode, @RequestParam String clientCode){
    public JSONObject rentalList(@RequestBody Map<String, Object> req) throws java.text.ParseException, UnsupportedEncodingException {
        int page = (int) req.get("page");
        String clientCode = (String) req.get("clientCode");
        String userName = (String) req.get("userName");

        String sql = "";
        //int result;
        int limit = 10;
        int offset = (page - 1) * limit;
        String from, to;

        //SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);

        cal.add(Calendar.DATE, 1);
        to = sdf.format(cal.getTime());

        cal.add(Calendar.DATE, -7);
        from = sdf.format(cal.getTime());

        System.out.println(from + ":" + to);


        JSONObject res = new JSONObject();
        JSONArray ja = new JSONArray();

        //sql = "select count(*) from device2 where client_code in (SELECT classification_code from member where mother_code = '" + supplyCode + "') and del_yn = 'N'";
        //sql = "select count(*) from rental where rental_stat = 'R' and part_cd = '" + clientCode + "' and created_at between '" + from + "' and '" + to + "'";
        if(userName == null || userName == ""){
            sql = "select count(*) from rental where rental_stat = 'R' and part_cd = '" + clientCode + "' and created_at < '" + from + "'";
        }else {
            sql = "select count(*) from rental where rental_stat = 'R' and part_cd = '" + clientCode + "' and created_at < '" + from + "' and fn_dec(user_name) like '%" + userName + "%'";
        }
        int cnt = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());
        //System.out.println(cnt);

        //sql = "select device_id, created_at, client_code, (select client_company from member where classification_code = client_code and grade = 2) client_name, reader_type, reader_code from device2 where client_code in (SELECT classification_code from member where mother_code = '" + supplyCode + "') and del_yn = 'N' limit " + limit + " offset " + offset;
        //sql = "select fn_dec(bar_code) bar_code, fn_dec(user_name) user_name, fn_dec(user_phone) user_phone, (select product_name from product where product_code = rental.prod_code) product_name, prod_serial, date_format(created_at, '%Y-%m-%d') created_at, rental_stat from rental where rental_stat = 'R' and part_cd = '" + clientCode + "' and created_at between '" + from + "' and '" + to + "' order by created_at desc limit " + limit + " offset " + offset;
        if(userName == null || userName == ""){
            sql = "select bar_code, user_name, user_phone, product_name, prod_serial, created_at, rental_stat from ( select fn_dec(bar_code) bar_code, fn_dec(user_name) user_name, fn_dec(user_phone) user_phone, (select product_name from product where product_code = rental.prod_code) product_name, prod_serial, date_format(created_at, '%Y-%m-%d %H:%i:%s') created_at, rental_stat from rental where rental_stat = 'R' and part_cd = '" + clientCode + "' and created_at < '" + from + "' order by created_at desc ) A limit " + limit + " offset " + offset;
        }else{
            sql = "select bar_code, user_name, user_phone, product_name, prod_serial, created_at, rental_stat from ( select fn_dec(bar_code) bar_code, fn_dec(user_name) user_name, fn_dec(user_phone) user_phone, (select product_name from product where product_code = rental.prod_code) product_name, prod_serial, date_format(created_at, '%Y-%m-%d %H:%i:%s') created_at, rental_stat from rental where rental_stat = 'R' and part_cd = '" + clientCode + "' and created_at < '" + from + "' and fn_dec(user_name) like '" + userName + "%' order by created_at desc ) A limit " + limit + " offset " + offset;
        }
        //System.out.println(sql);
        List<Object[]> list = em.createNativeQuery(sql).getResultList();
        list.forEach(li -> {
            JSONObject jobj = new JSONObject();
            jobj.put("barCode", li[0]);
            jobj.put("userName", li[1]);
            jobj.put("userPhone", li[2]);
            jobj.put("prodName", li[3]);
            jobj.put("prodSerial",li[4]);
            jobj.put("createdAt", li[5]);
            jobj.put("rentalStat", li[6]);
            ja.add(jobj);
        });
        res.put("data", ja);
        res.put("total", cnt);
        res.put("statusCode", "C-200");
        res.put("statusMessage", "정상 수행");

        return res;
    }
    @PostMapping("/client/rentalList")
    public JSONObject clientRentalList(@RequestBody Map<String, Object> req) throws java.text.ParseException, UnsupportedEncodingException {
        System.out.println(req);
        String clientCode = (String) req.get("clientCode");
        String searchType = (String) req.get("searchType");
        String searchKeyWord = (String) req.get("searchKeyWord");
        System.out.println(req.get("searchType"));
        String sql = "";

        JSONObject res = new JSONObject();
        JSONArray ja = new JSONArray();
        sql = "select fn_dec(user_name), fn_dec(user_phone), count(*),max(created_at) from rental as r1 where 1 = 1";

        if(!clientCode.isEmpty()){
            sql += " and part_cd = '" + clientCode + "'";
        }

        if(!searchType.isEmpty() && !searchKeyWord.isEmpty()){
            if(searchType.equals("name")){
                sql += " and fn_dec(user_name) like '%" + searchKeyWord + "%'";
            } else if(searchType.equals("phone")){
                sql += " and fn_dec(user_phone) like '%" + searchKeyWord + "%'";
            }
        }

        sql += " GROUP BY fn_dec(user_phone), fn_dec(user_name)";
        sql += " ORDER BY fn_dec(user_name) ASC";

        List<Object[]> list = em.createNativeQuery(sql).getResultList();
        list.forEach(li -> {
            JSONObject jobj = new JSONObject();
            jobj.put("userName", li[0]);
            jobj.put("userPhone", li[1]);
            jobj.put("count", li[2]);
            jobj.put("lastRantalDate", li[3]);
            ja.add(jobj);
        });

        res.put("data", ja);
        res.put("statusCode", "C-200");
        res.put("statusMessage", "정상 수행");

        return res;
    }

    @PostMapping("/excelRentalList")
    //public ResponseEntity<ResponseBody> selectDevice(@RequestParam int page, @RequestParam String supplyCode, @RequestParam String clientCode){
    public JSONObject excelRentalList(@RequestBody Map<String, Object> req) throws java.text.ParseException, UnsupportedEncodingException {

        String clientCode = (String) req.get("clientCode");
        String userName = (String) req.get("userName");
        String from;
        String sql;


        //SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);

        cal.add(Calendar.DATE, 1);

        cal.add(Calendar.DATE, -7);
        from = sdf.format(cal.getTime());

        JSONObject res = new JSONObject();
        JSONArray ja = new JSONArray();

        if(userName == null || userName == ""){
            sql = "select count(*) from rental where rental_stat = 'R' and part_cd = '" + clientCode + "' and created_at < '" + from + "'";
        }else {
            sql = "select count(*) from rental where rental_stat = 'R' and part_cd = '" + clientCode + "' and created_at < '" + from + "' and fn_dec(user_name) like '" + userName + "%'";
        }
        int cnt = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());

        if(userName == null || userName == ""){
            sql = "select fn_dec(bar_code) bar_code, fn_dec(user_name) user_name, fn_dec(user_phone) user_phone, (select product_name from product where product_code = rental.prod_code) product_name, prod_serial, date_format(created_at, '%Y-%m-%d %H:%i:%s') created_at, rental_stat from rental where rental_stat = 'R' and part_cd = '" + clientCode + "' and created_at < '" + from + "' order by created_at desc ";
        }else{
            sql = "select fn_dec(bar_code) bar_code, fn_dec(user_name) user_name, fn_dec(user_phone) user_phone, (select product_name from product where product_code = rental.prod_code) product_name, prod_serial, date_format(created_at, '%Y-%m-%d %H:%i:%s') created_at, rental_stat from rental where rental_stat = 'R' and part_cd = '" + clientCode + "' and created_at < '" + from + "' and fn_dec(user_name) like '" + userName + "%' order by created_at desc ";
        }
        //System.out.println(sql);
        List<Object[]> list = em.createNativeQuery(sql).getResultList();
        list.forEach(li -> {
            JSONObject jobj = new JSONObject();
            jobj.put("barCode", li[0]);
            jobj.put("userName", li[1]);
            jobj.put("userPhone", li[2]);
            jobj.put("prodName", li[3]);
            jobj.put("prodSerial",li[4]);
            jobj.put("createdAt", li[5]);
            jobj.put("rentalStat", li[6]);
            ja.add(jobj);
        });
        res.put("data", ja);
        res.put("total", cnt);
        res.put("statusCode", "C-200");
        res.put("statusMessage", "정상 수행");

        return res;
    }

    @PostMapping("/returnList")
    //public ResponseEntity<ResponseBody> selectDevice(@RequestParam int page, @RequestParam String supplyCode, @RequestParam String clientCode){
    public JSONObject returnList(@RequestBody Map<String, Object> req){
        int page = (int) req.get("page");
        String clientCode = (String) req.get("clientCode");
        String userName = (String) req.get("userName");

        String sql = "";
        //int result;
        int limit = 10;
        int offset = (page - 1) * limit;

        JSONObject res = new JSONObject();
        JSONArray ja = new JSONArray();

        //sql = "select count(*) from device2 where client_code in (SELECT classification_code from member where mother_code = '" + supplyCode + "') and del_yn = 'N'";
        if(userName == null || userName == ""){
            sql = "select count(*) from rental where part_cd = '" + clientCode + "'";
        }else{
            sql = "select count(*) from rental where part_cd = '" + clientCode + "' and fn_dec(user_name) like '" + userName + "%'";
        }
        int cnt = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());
        //System.out.println(cnt);

        //sql = "select device_id, created_at, client_code, (select client_company from member where classification_code = client_code and grade = 2) client_name, reader_type, reader_code from device2 where client_code in (SELECT classification_code from member where mother_code = '" + supplyCode + "') and del_yn = 'N' limit " + limit + " offset " + offset;
        if(userName == null || userName == ""){
            sql = "select bar_code, user_name, user_phone, product_name, prod_serial, created_at, modified_at, rental_stat from ( select fn_dec(bar_code) bar_code, fn_dec(user_name) user_name, fn_dec(user_phone) user_phone, (select product_name from product where product_code = rental.prod_code) product_name, prod_serial, date_format(created_at, '%Y-%m-%d %H:%i:%s') created_at, date_format(modified_at, '%Y-%m-%d %H:%i:%s') modified_at, rental_stat from rental where part_cd = '" + clientCode + "' order by created_at desc ) A limit " + limit + " offset " + offset;
        }else{
            sql = "select bar_code, user_name, user_phone, product_name, prod_serial, created_at, modified_at, rental_stat from ( select fn_dec(bar_code) bar_code, fn_dec(user_name) user_name, fn_dec(user_phone) user_phone, (select product_name from product where product_code = rental.prod_code) product_name, prod_serial, date_format(created_at, '%Y-%m-%d %H:%i:%s') created_at, date_format(modified_at, '%Y-%m-%d %H:%i:%s') modified_at, rental_stat from rental where part_cd = '" + clientCode + "' and fn_dec(user_name) like '" + userName + "%' order by created_at desc ) A limit " + limit + " offset " + offset;
        }
        //System.out.println(sql);
        List<Object[]> list = em.createNativeQuery(sql).getResultList();
        list.forEach(li -> {
            JSONObject jobj = new JSONObject();
            jobj.put("barCode", li[0]);
            jobj.put("userName", li[1]);
            jobj.put("userPhone", li[2]);
            jobj.put("prodName", li[3]);
            jobj.put("prodSerial",li[4]);
            jobj.put("createdAt", li[5]);
            jobj.put("modifiedAt", li[6]);
            jobj.put("rentalStat", li[7]);
            ja.add(jobj);
        });
        res.put("data", ja);
        res.put("total", cnt);
        res.put("statusCode", "C-200");
        res.put("statusMessage", "정상 수행");

        return res;
    }

    @PostMapping("/excelReturnList")
    //public ResponseEntity<ResponseBody> selectDevice(@RequestParam int page, @RequestParam String supplyCode, @RequestParam String clientCode){
    public JSONObject excelReturnList(@RequestBody Map<String, Object> req){

        String clientCode = (String) req.get("clientCode");
        String userName = (String) req.get("userName");
        String sql = "";

        JSONObject res = new JSONObject();
        JSONArray ja = new JSONArray();

        if(userName == null || userName == ""){
            sql = "select count(*) from rental where part_cd = '" + clientCode + "'";
        }else{
            sql = "select count(*) from rental where part_cd = '" + clientCode + "' and fn_dec(user_name) like '" + userName + "%'";
        }
        int cnt = Integer.parseInt(em.createNativeQuery(sql).getSingleResult().toString());
        //System.out.println(cnt);


        //sql = "select fn_dec(bar_code) bar_code, fn_dec(user_name) user_name, fn_dec(user_phone) user_phone, (select product_name from product where product_code = rental.prod_code) product_name, prod_serial, date_format(created_at, '%Y-%m-%d') created_at, rental_stat from rental where part_cd = '" + clientCode + "' order by created_at desc ";
        if(userName == null || userName == ""){
            sql = "select fn_dec(bar_code) bar_code, fn_dec(user_name) user_name, fn_dec(user_phone) user_phone, (select product_name from product where product_code = rental.prod_code) product_name, prod_serial, date_format(created_at, '%Y-%m-%d %H:%i:%s') created_at, date_format(modified_at, '%Y-%m-%d %H:%i:%s') modified_at, rental_stat from rental where part_cd = '" + clientCode + "' order by created_at desc ";
        }else{
            sql = "select fn_dec(bar_code) bar_code, fn_dec(user_name) user_name, fn_dec(user_phone) user_phone, (select product_name from product where product_code = rental.prod_code) product_name, prod_serial, date_format(created_at, '%Y-%m-%d %H:%i:%s') created_at, date_format(modified_at, '%Y-%m-%d %H:%i:%s') modified_at, rental_stat from rental where part_cd = '" + clientCode + "' and fn_dec(user_name) like '" + userName + "%' order by created_at desc ";
        }
        //System.out.println(sql);
        List<Object[]> list = em.createNativeQuery(sql).getResultList();
        list.forEach(li -> {
            JSONObject jobj = new JSONObject();
            jobj.put("barCode", li[0]);
            jobj.put("userName", li[1]);
            jobj.put("userPhone", li[2]);
            jobj.put("prodName", li[3]);
            jobj.put("prodSerial",li[4]);
            jobj.put("createdAt", li[5]);
            jobj.put("modifiedAt", li[6]);
            jobj.put("rentalStat", li[7]);
            ja.add(jobj);
        });
        res.put("data", ja);
        res.put("total", cnt);
        res.put("statusCode", "C-200");
        res.put("statusMessage", "정상 수행");

        return res;
    }


}