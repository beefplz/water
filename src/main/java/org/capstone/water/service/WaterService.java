package org.capstone.water.service;

import org.capstone.water.repository.entity.mldata.MldataView;
import org.capstone.water.repository.entity.pdo.PredictDo;
import org.capstone.water.repository.entity.waterdata.Waterdata;

import java.util.List;

public interface WaterService {
    List<PredictDo> getPdo();

    List<MldataView> getMldata();

    List<Waterdata> getWaterdata();
}
