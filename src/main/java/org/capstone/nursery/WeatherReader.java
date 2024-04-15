package org.capstone.nursery;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@RequiredArgsConstructor
public class WeatherReader{
    @Scheduled(cron = "0 0/1 * * *")
    public void run(){
        String result ="";
        try{
            URL url = new URL("https://www.khoa.go.kr/api/oceangrid/tideObsRecent/search.do?ServiceKey=oldpJ/aIMLBu4ktr1g777Q==&ObsCode=DT_0027&ResultType=json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
            result = bf.readLine();

            JSONParser jsonParser = new JSONParser(result);
            JSONObject jsonObject = (JSONObject) jsonParser.parse();
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");


        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
}
