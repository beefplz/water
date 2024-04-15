package org.capstone.nursery.repository.entity.pdo;

import org.capstone.nursery.repository.entity.watertank.Watertank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictedDoRepository extends JpaRepository<PredictedDo, Watertank> {
    List<PredictedDo> findPredictedDosByWatertank_Fcid(String watertank_fcid);
}
