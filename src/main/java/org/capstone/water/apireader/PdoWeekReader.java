package org.capstone.water.apireader;

import lombok.RequiredArgsConstructor;
import org.capstone.water.config.ApiProperties;
import org.springframework.stereotype.Component;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.capstone.water.repository.entity.mldata.MldataMapping;
import org.capstone.water.repository.entity.mldata.MldataViewRepository;
import org.capstone.water.repository.entity.pdoweek.PredictDoWeek;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
@RequiredArgsConstructor
public class PdoWeekReader {
    private final MldataViewRepository mldataViewRepository;
    private final ApiProperties api;


    public List<PredictDoWeek> pdoweekRead(String timeString){
        final Logger log = LoggerFactory.getLogger(getClass());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(timeString, formatter).plusDays(7);
        log.debug("예측 목표 시각: {}", dateTime);

        List<PredictDoWeek> pdoList = new ArrayList<>();

        String result1;
        String result2;
        String result3;

        double output1, output2, output3;
        List<MldataMapping> mldataViewList;

        mldataViewList = mldataViewRepository.findMldataViewsByTankidOrderByTimeDescWeek("iw1");
        // 순서 유속 풍향 유향 수온 0 do 양식장수온 ph 염도
        float[][] inputdata = new float[336][10];
        for(int i = 335; i>=0; i--){
            float[] floats = getFloats(mldataViewList, i);
            inputdata[i]=floats;
        }

        String jsonInputString = "{"
                + "\"inputs\":["
                + "{"
                + "\"name\":\"input0\","
                + "\"shape\":[1,336, 10],"
                + "\"datatype\":\"FP32\","
                +"\"data\":"+ Arrays.deepToString(inputdata)
                + "}"
                + "],"
                + "\"outputs\":["
                + "{"
                + "\"name\":\"output0\""
                + "}"
                + "]"
                + "}";

        log.debug("Triton 요청: {}", jsonInputString);
        String url = api.tritonUrl() + "/v2/models/predictdoweek/infer";
        result1 = getStringMLPost(log, jsonInputString, url);
        JSONParser jsonParser = new JSONParser();
        output1 = (double) Math.round(getOutputData(log, result1, jsonParser) * 100) /100;
        PredictDoWeek pdo1 = PredictDoWeek.builder().num(null).time(dateTime).pdo((float) output1).tankid("IW1").build();
        log.debug("주간 예측값: {}", output1);

        mldataViewList = mldataViewRepository.findMldataViewsByTankidOrderByTimeDescWeek("rt1");
        // 순서 유속 풍향 유향 수온 0 do 양식장수온 ph 염도
        inputdata = new float[336][10];
        for(int i = 335; i>=0; i--){
            float[] floats = getFloats(mldataViewList, i);
            inputdata[i]=floats;
        }

        jsonInputString = "{"
                + "\"inputs\":["
                + "{"
                + "\"name\":\"input0\","
                + "\"shape\":[1,336, 10],"
                + "\"datatype\":\"FP32\","
                +"\"data\":"+ Arrays.deepToString(inputdata)
                + "}"
                + "],"
                + "\"outputs\":["
                + "{"
                + "\"name\":\"output0\""
                + "}"
                + "]"
                + "}";

        log.debug("Triton 요청: {}", jsonInputString);

        result2 = getStringMLPost(log, jsonInputString, url);
        output2 = (double) Math.round(getOutputData(log, result2, jsonParser) * 100) /100;
        PredictDoWeek pdo2 = PredictDoWeek.builder().num(null).time(dateTime).pdo((float) output2).tankid("RT1").build();
        log.debug("주간 예측값: {}", output2);

        mldataViewList = mldataViewRepository.findMldataViewsByTankidOrderByTimeDescWeek("rt2");
        // 순서 유속 풍향 유향 수온 0 do 양식장수온 ph 염도
        inputdata = new float[336][10];
        for(int i = 335; i>=0; i--){
            float[] floats = getFloats(mldataViewList, i);
            inputdata[i]=floats;
        }

        jsonInputString = "{"
                + "\"inputs\":["
                + "{"
                + "\"name\":\"input0\","
                + "\"shape\":[1,336, 10],"
                + "\"datatype\":\"FP32\","
                +"\"data\":"+ Arrays.deepToString(inputdata)
                + "}"
                + "],"
                + "\"outputs\":["
                + "{"
                + "\"name\":\"output0\""
                + "}"
                + "]"
                + "}";

        log.debug("Triton 요청: {}", jsonInputString);

        result3 = getStringMLPost(log, jsonInputString, url);
        output3 = (double) Math.round(getOutputData(log, result3, jsonParser) * 100) /100;
        PredictDoWeek pdo3 = PredictDoWeek.builder().num(null).time(dateTime).pdo((float) output3).tankid("RT2").build();
        log.debug("주간 예측값: {}", output3);

        /*PredictDoWeek pdo1 = PredictDo.builder().num(null).time(dateTime).pdo(8.3F).tankid("IW1").build();
        PredictDoWeek pdo2 = PredictDo.builder().num(null).time(dateTime).pdo(9.4F).tankid("RT1").build();
        PredictDoWeek pdo3 = PredictDo.builder().num(null).time(dateTime).pdo(8.8F).tankid("RT2").build();*/


        pdoList.add(pdo1);
        pdoList.add(pdo2);
        pdoList.add(pdo3);

        return pdoList;
    }

    private double getOutputData(Logger log, String result, JSONParser jsonParser) {
        JSONObject jsonObject;
        double output;
        try {
            jsonObject = (JSONObject) jsonParser.parse(result);
            JSONArray outputs = (JSONArray) jsonObject.get("outputs");
            JSONObject output0 = (JSONObject) outputs.get(0);
            JSONArray data = (JSONArray) output0.get("data");
            output = (double) data.get(0);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        log.debug("Triton 출력값: {}", output);
        return output;
    }

    private String getStringMLPost(Logger log, String jsonInputString, String url) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(HttpTimeouts.CONNECT)
                .setSocketTimeout(HttpTimeouts.INFERENCE_READ)
                .build();
        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).build()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(jsonInputString));

            // 응답 본문은 client가 닫히기 전에 읽어야 함
            HttpResponse response = client.execute(post);
            int status = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity());
            if (status != 200) {
                throw new IllegalStateException("Triton 응답 오류 " + status + ": " + result);
            }
            log.debug("Triton 응답: {}", result);
            return result;
        } catch (IOException e) {
            throw new UncheckedIOException("Triton 호출 실패: " + url, e);
        }
    }

    private static float[] getFloats(List<MldataMapping> mldataViewList, int i) {
        MldataMapping mldataView = mldataViewList.get(i);
        float time = 0F;
        Float scs =  mldataView.getScs();
        Float swd = mldataView.getScd();
        Float cd = mldataView.getWdir();
        float scd = 0F;
        if (350 <= cd || cd <= 11)
            scd = 8F;
        else if (12 <= cd && cd <= 34) {
            scd = 16F;
        } else if (35<=cd && cd <= 56) {
            scd = 9F;
        } else if (57 <= cd && cd <=79) {
            scd = 10F;
        } else if (80 <= cd && cd <= 101) {
            scd = 11F;
        } else if (102 <= cd && cd <=124) {
            scd = 12F;
        }else if (125 <=cd && cd <= 146){
            scd = 1F;
        } else if (147 <=cd && cd <=169) {
            scd = 2F;
        } else if (170 <=cd && cd <= 191) {
            scd = 3F;
        } else if (192 <= cd && cd <= 214) {
            scd = 15F;
        } else if (215 <= cd && cd <=236) {
            scd = 13F;
        } else if (237 <= cd && cd <= 260) {
            scd = 14F;
        } else if (261 <= cd && cd <= 281) {
            scd = 4F;
        } else if (282 <= cd && cd <= 304) {
            scd = 6F;
        } else if (305 <= cd && cd <= 326) {
            scd = 5F;
        } else if (327 <= cd && cd <= 349) {
            scd = 7F;
        }
        Float swt = mldataView.getSwt();
        float fc = 0F;
        Float wdo = mldataView.getWdo();
        Float wt = mldataView.getWt();
        Float ph = mldataView.getPh();
        Float sa = mldataView.getSa();
        return new float[]{scs, scd, swd, swt, fc, wdo, wt, ph, sa, time};
    }
}
