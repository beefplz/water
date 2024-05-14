package org.capstone.water.apireader;

import org.capstone.water.repository.entity.pdo.PredictDo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PdoReader {
    public List<PredictDo> pdoRead(String timeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(timeString, formatter);

        List<PredictDo> pdoList = new ArrayList<>();

        PredictDo pdo1 = PredictDo.builder().num(null).time(dateTime).pdo(8.3F).tankid("IW1").build();
        PredictDo pdo2 = PredictDo.builder().num(null).time(dateTime).pdo(8.3F).tankid("RT1").build();
        PredictDo pdo3 = PredictDo.builder().num(null).time(dateTime).pdo(8.3F).tankid("RT2").build();

        pdoList.add(pdo1);
        pdoList.add(pdo2);
        pdoList.add(pdo3);

        return pdoList;
    }
}
