package org.capstone.water.service;


import lombok.RequiredArgsConstructor;
import org.capstone.water.repository.entity.pdo.PdoMapping;
import org.capstone.water.repository.entity.pdo.PredictDoRepository;
import org.capstone.water.repository.entity.mldata.*;
import org.capstone.water.repository.entity.pdoweek.PdoWeekMapping;
import org.capstone.water.repository.entity.pdoweek.PredictDoWeekRepository;
import org.capstone.water.repository.entity.waterdata.Waterdata;
import org.capstone.water.repository.entity.waterdata.WaterdataRepository;

import org.capstone.water.repository.entity.weather.Weather;
import org.capstone.water.repository.entity.weather.WeatherRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class WaterServiceImpl {
    private final PredictDoRepository predictDoRepository;
    private final MldataViewRepository mldataViewRepository;
    private final WaterdataRepository waterdataRepository;
    private final WeatherRepository weatherRepository;
    private final PredictDoWeekRepository predictDoWeekRepository;

    GetOxygensaturation getOxygensaturation = new GetOxygensaturation();

    public List<PdoMapping> getPdo(String tankid){
        return predictDoRepository.findPredictDosByTankid(tankid);
    }

    public List<MldataView> getMldata(String tankid){
        return mldataViewRepository.findMldataViewsByTankid(tankid);
    }

    public Weather getTest(){
        return weatherRepository.findFirstByOrderByTimeDesc();
    }

    public List<Waterdata> getWaterdata(String tankid){
        return waterdataRepository.findWaterdataByTankid(tankid);
    }

    public Waterdata getWaterdataOne(String tankid){ return waterdataRepository.findFirstByTankidOrderByTimeDesc(tankid); }

    public List<PdoWeekMapping> getPdoWeek(String tankid){ return predictDoWeekRepository.findPredictDoWeeksByTankid(tankid); }

    public Waterdata getWaterdataOnewithOs(String tankid){
        Waterdata waterdata = waterdataRepository.findFirstByTankidOrderByTimeDesc(tankid);

        // 서버에서 계산한 값을 엔티티에 설정
        float resultOs = getOxygensaturation.getOxygensaturation(waterdata.getWt(),waterdata.getWdo());
        waterdata.setOs(resultOs);

        return waterdata;
    }

    @Cacheable(value = "waterDataCache", key = "#tankid")
    public List<Waterdata> getWaterdataWithOs(String tankid){
        List<Waterdata> waterdataList = waterdataRepository.findWaterdataByTankid(tankid);
        return waterdataList.stream()
                .peek(entity -> {
                    float resultOs = getOxygensaturation.getOxygensaturation(entity.getWt(),entity.getWdo());
                    entity.setOs(resultOs);
                })
                .collect(Collectors.toList());
    }
}
