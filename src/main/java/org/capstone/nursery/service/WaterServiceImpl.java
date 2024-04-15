package org.capstone.nursery.service;


import lombok.RequiredArgsConstructor;
import org.capstone.nursery.repository.entity.pdo.PredictedDo;
import org.capstone.nursery.repository.entity.pdo.PredictedDoRepository;
import org.capstone.nursery.repository.entity.mldata.*;
import org.capstone.nursery.repository.entity.waterdata.Waterdata;
import org.capstone.nursery.repository.entity.waterdata.WaterdataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaterServiceImpl {
    private  final PredictedDoRepository predictedDorepository;
    private final MldataViewRepository mldataViewRepository;
    private final WaterdataRepository waterdataRepository;

    public List<PredictedDo> getPdo(String fcid){
        return predictedDorepository.findPredictedDosByWatertank_Fcid(fcid);
    }

    public List<MldataView> getMldata(String fcid){
        return mldataViewRepository.findMldataViewsByFcid(fcid);
    }

    public List<Waterdata> getWaterdata(String fcid){
        return waterdataRepository.findWaterdatasByWatertank_Fcid(fcid);
    }


}
