package org.capstone.water.apireader;

import org.capstone.water.repository.entity.weather.Weather;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherReader {
    public Weather weatherRead(String timeString) {
        final Logger log = LoggerFactory.getLogger(getClass());
        String result ="";
        log.info("weather");
        try{
            //생일도 유향 유속 풍향 TW_0081
            //URL url = new URL("https://www.khoa.go.kr/api/oceangrid/tideObsRecent/search.do?ServiceKey=oldpJ/aIMLBu4ktr1g777Q==&ObsCode=DT_0027&ResultType=json");
            URL url = new URL("https://www.khoa.go.kr/api/oceangrid/buObsRecent/search.do?ServiceKey=oldpJ/aIMLBu4ktr1g777Q==&ObsCode=TW_0081&ResultType=json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            result = bf.readLine();

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
            JSONObject jresult = (JSONObject) jsonObject.get("result");
            JSONObject jdata = (JSONObject) jresult.get("data");
            log.info(jdata.toJSONString());

            //String jtime = (String) jdata.get("record_time");
            Float jwt = Float.parseFloat((String) jdata.get("water_temp"));
            Short jwd = Short.parseShort((String) jdata.get("wind_dir"));
            Float jws = Float.parseFloat((String) jdata.get("wind_speed"));
            Float jsa = Float.parseFloat((String) jdata.get("Salinity"));
            Float jat =  Float.parseFloat((String) jdata.get("air_temp"));
            Float jap =  Float.parseFloat((String) jdata.get("air_pres"));
            Float jcd =  Float.parseFloat((String) jdata.get("current_dir"));
            Float jwh =  Float.parseFloat((String) jdata.get("wave_height"));
            Float jcs =  Float.parseFloat((String) jdata.get("current_speed"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(timeString, formatter);

            return Weather.builder().time(dateTime).swh(jwh).swt(jwt).ssa(jsa).sat(jat).sap(jap).wdir(jwd).ws(jws).scd(jcd).scs(jcs).build();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
