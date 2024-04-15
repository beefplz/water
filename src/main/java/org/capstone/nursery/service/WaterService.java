package org.capstone.nursery.service;

import org.capstone.nursery.repository.entity.mldata.MldataView;
import org.capstone.nursery.repository.entity.pdo.PredictedDo;
import org.capstone.nursery.repository.entity.waterdata.Waterdata;

import java.util.List;

public interface WaterService {
    List<PredictedDo> getPdo();

    List<MldataView> getMldata();

    List<Waterdata> getWaterdata();
}
