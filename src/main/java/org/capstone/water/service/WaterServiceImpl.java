package org.capstone.water.service;


import lombok.RequiredArgsConstructor;
import org.capstone.water.repository.entity.pdo.PdoMapping;
import org.capstone.water.repository.entity.pdo.PredictDo;
import org.capstone.water.repository.entity.pdo.PredictDoRepository;
import org.capstone.water.repository.entity.mldata.*;
import org.capstone.water.repository.entity.waterdata.WaterMapping;
import org.capstone.water.repository.entity.waterdata.WaterdataRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaterServiceImpl {
    private  final PredictDoRepository predictDorepository;
    private final MldataViewRepository mldataViewRepository;
    private final WaterdataRepository waterdataRepository;

    public List<PdoMapping> getPdo(String tankid){
        return predictDorepository.findPredictDosByTankid(tankid);
    }

    public List<MldataView> getMldata(String tankid){
        return mldataViewRepository.findMldataViewsByTankid(tankid);
    }

    /*public List<WaterMapping> getWaterdata(String tankid){
        return waterdataRepository.findWaterdatasByTankidOrderByTimeDesc(tankid);
    }*/

    public List<WaterMapping> getWaterdata(String tankid){
        return waterdataRepository.findWaterdataByTankid(tankid);
    }


}
