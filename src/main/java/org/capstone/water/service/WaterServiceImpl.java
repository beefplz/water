package org.capstone.water.service;


import lombok.RequiredArgsConstructor;
import org.capstone.water.repository.entity.pdo.PdoMapping;
import org.capstone.water.repository.entity.pdo.PredictDoRepository;
import org.capstone.water.repository.entity.mldata.*;
import org.capstone.water.repository.entity.waterdata.WaterMapping;
import org.capstone.water.repository.entity.waterdata.WaterdataRepository;

import org.capstone.water.repository.entity.weather.Weather;
import org.capstone.water.repository.entity.weather.WeatherRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WaterServiceImpl {
    private final PredictDoRepository predictDorepository;
    private final MldataViewRepository mldataViewRepository;
    private final WaterdataRepository waterdataRepository;
    private final WeatherRepository weatherRepository;

    public List<PdoMapping> getPdo(String tankid){
        return predictDorepository.findPredictDosByTankid(tankid);
    }

    public List<MldataView> getMldata(String tankid){
        return mldataViewRepository.findMldataViewsByTankid(tankid);
    }
    public List<MldataView> getMldata30(){
        return mldataViewRepository.findTop10ByOrderByTimeDesc();
    }
    public Weather getTest(){
        return weatherRepository.findFirstByOrderByTimeDesc();
    }

    public List<WaterMapping> getWaterdata(String tankid){
        return waterdataRepository.findWaterdataByTankid(tankid);
    }


}
