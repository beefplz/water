package org.capstone.water.apireader;

import lombok.RequiredArgsConstructor;
import org.capstone.water.repository.entity.mldata.MldataViewRepository;
import org.capstone.water.repository.entity.pdoweek.PredictDoWeek;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// 최근 7일(30분 간격 336개) 데이터로 7일 뒤 용존산소를 수조별로 예측
@Component
@RequiredArgsConstructor
public class PdoWeekReader {
    private static final String MODEL = "predictdoweek";
    private static final int INPUT_ROWS = 336;

    private final MldataViewRepository mldataViewRepository;
    private final TritonClient tritonClient;

    public List<PredictDoWeek> pdoweekRead(String timeString) {
        LocalDateTime target = LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                .plusDays(7);

        return Tanks.IDS.stream()
                .map(tank -> {
                    float[][] input = FeatureMapper.toInput(
                            mldataViewRepository.findMldataViewsByTankidOrderByTimeDescWeek(tank), INPUT_ROWS);
                    float pdo = tritonClient.infer(MODEL, input);
                    return PredictDoWeek.builder().time(target).tankid(tank.toUpperCase()).pdo(pdo).build();
                })
                .toList();
    }
}
