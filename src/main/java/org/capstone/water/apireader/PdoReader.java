package org.capstone.water.apireader;

import lombok.RequiredArgsConstructor;
import org.capstone.water.repository.entity.mldata.MldataViewRepository;
import org.capstone.water.repository.entity.pdo.PredictDo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// 최근 30개 데이터로 30분 뒤 용존산소를 수조별로 예측
@Component
@RequiredArgsConstructor
public class PdoReader {
    private static final String MODEL = "predictdo";
    private static final int INPUT_ROWS = 30;

    private final MldataViewRepository mldataViewRepository;
    private final TritonClient tritonClient;

    public List<PredictDo> pdoRead(String timeString) {
        LocalDateTime target = LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                .plusMinutes(30);

        return FeatureMapper.TANK_IDS.stream()
                .map(tank -> {
                    float[][] input = FeatureMapper.toInput(
                            mldataViewRepository.findMldataViewsByTankidOrderByTimeDesc(tank), INPUT_ROWS);
                    float pdo = tritonClient.infer(MODEL, input);
                    return PredictDo.builder().time(target).tankid(tank.toUpperCase()).pdo(pdo).build();
                })
                .toList();
    }
}
