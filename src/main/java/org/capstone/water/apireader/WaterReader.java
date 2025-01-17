package org.capstone.water.apireader;

import lombok.RequiredArgsConstructor;
import org.capstone.water.repository.entity.waterdata.Waterdata;
import org.capstone.water.repository.entity.waterdata.WaterdataRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class WaterReader {
    public List<Waterdata> waterRead(String timeString, WaterdataRepository waterdataRepository) {
        final Logger log = LoggerFactory.getLogger(getClass());
        String result ="";
        log.info("water");
        try {
            URL urlf = new URL("http://aqua.kware.co.kr:/openapi/v1/acesstoken?key=4b5df6236c40559098c463738fd3db9e07879a5015dd4896c2ccfd477d0a2082");
            HttpURLConnection urlConnectionf = (HttpURLConnection) urlf.openConnection();
            urlConnectionf.setRequestMethod("POST");

            BufferedReader bf = new BufferedReader(new InputStreamReader(urlf.openStream(), StandardCharsets.UTF_8));
            result = bf.readLine();

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
            String accessToken = (String) jsonObject.get("acessToken");
            log.info(accessToken);

            JSONArray jsonarr = new JSONArray();

            jsonarr.add("61AF1");
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("pageSize", 200);
            hashMap.put("pageNumber", 1);
            hashMap.put("fcltyIds", jsonarr);

            hashMap.put("startDate", timeString);
            hashMap.put("endDate", timeString);

            JSONObject jsonob = new JSONObject(hashMap);

            log.info(jsonob.toJSONString());

            String surl = "http://aqua.kware.co.kr:/openapi/v1/fac/sensorstatus?acessToken=";
            surl += accessToken;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
            headers.setContentType(mediaType);
            HttpEntity<String> httpEntity = new HttpEntity<>(jsonob.toJSONString(), headers);
            String res = restTemplate.postForObject(surl, httpEntity, String.class);
            log.info(res);
            result = res;

            jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(result);
            JSONObject jsonContent = (JSONObject) jsonObject.get("content");
            JSONArray jsonList = (JSONArray) jsonContent.get("list");
            JSONObject jsonIw1 ;
            JSONObject jsonRt1;
            JSONObject jsonRt2;
            Float ph, sa;
            Waterdata waterdata1;
            Waterdata waterdata2;
            Waterdata waterdata3;
            if(!jsonList.isEmpty()) {
                jsonIw1 = (JSONObject) jsonList.get(0);
                jsonRt1 = (JSONObject) jsonList.get(1);
                jsonRt2 = (JSONObject) jsonList.get(2);
                log.info(jsonIw1.toString());

                if (jsonIw1.get("ph")==null){
                    log.info("ph is null");
                    Waterdata waterdataIw1 =  waterdataRepository.findFirstByTankidOrderByTimeDesc("iw1");
                    ph= waterdataIw1.getPh();
                }else {
                    ph = Float.parseFloat((String) jsonIw1.get("ph"));
                }
                if (jsonIw1.get("sa")==null){
                    log.info("sa is null");
                    Waterdata waterdataIw1 =  waterdataRepository.findFirstByTankidOrderByTimeDesc("iw1");
                    sa= waterdataIw1.getSa();
                }else {
                    sa = Float.parseFloat((String) jsonIw1.get("sa"));
                }
                waterdata1 = getJsonToWaterdata(jsonIw1, ph, sa, waterdataRepository);

                waterdata2 = getJsonToWaterdata(jsonRt1, ph, sa, waterdataRepository);

                waterdata3 = getJsonToWaterdata(jsonRt2, ph, sa, waterdataRepository);
            }else{
                log.info("error no water data");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime dateTime = LocalDateTime.parse(timeString, formatter);
                LocalDateTime dateTimeMonthago = LocalDateTime.parse(timeString, formatter).minusMonths(1);

                waterdata1 = waterdataRepository.findFistByTankidAndTime("iw1" , dateTimeMonthago);
                waterdata2 = waterdataRepository.findFistByTankidAndTime("rt1" , dateTimeMonthago);
                waterdata3 = waterdataRepository.findFistByTankidAndTime("rt2" , dateTimeMonthago);

                if(waterdata1==null){
                    log.info("2 month ago");
                    dateTimeMonthago = dateTimeMonthago.minusMonths(2);
                    waterdata1 = waterdataRepository.findFistByTankidAndTime("iw1" , dateTimeMonthago);
                    waterdata2 = waterdataRepository.findFistByTankidAndTime("rt1" , dateTimeMonthago);
                    waterdata3 = waterdataRepository.findFistByTankidAndTime("rt2" , dateTimeMonthago);
                }

                /*waterdata1 =  waterdataRepository.findFirstByTankidOrderByTimeDesc("iw1");
                waterdata2 =  waterdataRepository.findFirstByTankidOrderByTimeDesc("rt1");
                waterdata3 =  waterdataRepository.findFirstByTankidOrderByTimeDesc("rt2");*/

                waterdata1.setTime(dateTime);
                waterdata2.setTime(dateTime);
                waterdata3.setTime(dateTime);
                waterdata1.setNum(null);
                waterdata2.setNum(null);
                waterdata3.setNum(null);
            }

            List<Waterdata> waterdataList = new ArrayList<>();

            waterdataList.add(waterdata1);
            waterdataList.add(waterdata2);
            waterdataList.add(waterdata3);


            return waterdataList;

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private Waterdata getJsonToWaterdata(JSONObject json, Float ph, Float sa, WaterdataRepository waterdataRepository) {
        String date = (String) json.get("collectTime");
        String time = (String) json.get("collectDe");
        String tankid = json.get("tankId").toString().substring(6);
        Float wt, wdo;
        if (json.get("wt")==null){
            Waterdata waterdata =  waterdataRepository.findFirstByTankidOrderByTimeDesc(tankid);
            wt= waterdata.getWt();
        }else {
            wt = Float.parseFloat((String) json.get("wt"));
        }
        if (json.get("do1")==null){
            Waterdata waterdata =  waterdataRepository.findFirstByTankidOrderByTimeDesc(tankid);
            wdo= waterdata.getWdo();
        }else {
            wdo = Float.parseFloat((String) json.get("do1"));
        }
        String dtime = time + " " + date;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dtime, formatter);

        return Waterdata.builder().num(null).tankid(tankid).time(dateTime).wt(wt).wdo(wdo).ph(ph).sa(sa).build();
    }
}
