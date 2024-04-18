package org.capstone.water.apireader;

import lombok.RequiredArgsConstructor;
import org.capstone.water.repository.entity.waterdata.Waterdata;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class WaterReader {
    public List<Waterdata> waterRead() {
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

            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusMinutes(1);
            String localDateTimeString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            log.info(localDateTimeString);

            hashMap.put("startDate", localDateTimeString);
            hashMap.put("endDate", localDateTimeString);

            JSONObject jsonob = new JSONObject(hashMap);

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
            JSONObject jsonIw1 = (JSONObject) jsonList.get(0);
            JSONObject jsonRt1 = (JSONObject) jsonList.get(1);
            JSONObject jsonRt2 = (JSONObject) jsonList.get(2);

            Float ph = Float.parseFloat((String) jsonIw1.get("ph"));
            Float sa = Float.parseFloat((String) jsonIw1.get("sa"));

            Waterdata waterdata1 = getJsonToWaterdata(jsonIw1, ph, sa);

            Waterdata waterdata2 = getJsonToWaterdata(jsonRt1, ph, sa);

            Waterdata waterdata3 = getJsonToWaterdata(jsonRt2, ph, sa);

            List<Waterdata> waterdataList = new ArrayList<>();

            waterdataList.add(waterdata1);
            waterdataList.add(waterdata2);
            waterdataList.add(waterdata3);

            return waterdataList;

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private Waterdata getJsonToWaterdata(JSONObject json, Float ph, Float sa) {
        String date = (String) json.get("collectTime");
        String time = (String) json.get("collectDe");
        String tankid = json.get("tankId").toString().substring(6);
        Float wt = Float.parseFloat((String) json.get("wt"));
        Float wdo = Float.parseFloat((String) json.get("do1"));
        String dtime = time + " " + date;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dtime, formatter);

        return Waterdata.builder().num(null).tankid(tankid).time(dateTime).wt(wt).wdo(wdo).ph(ph).sa(sa).build();
    }
}
