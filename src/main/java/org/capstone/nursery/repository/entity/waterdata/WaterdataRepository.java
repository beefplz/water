package org.capstone.nursery.repository.entity.waterdata;

import org.capstone.nursery.repository.entity.watertank.Watertank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaterdataRepository extends JpaRepository<Waterdata, Watertank> {
    List<Waterdata> findWaterdatasByWatertank_Fcid(String watertank_fcid);
}
