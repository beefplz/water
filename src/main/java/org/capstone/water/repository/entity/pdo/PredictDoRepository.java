package org.capstone.water.repository.entity.pdo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictDoRepository extends JpaRepository<PredictDo, String> {
    List<PredictDo> findPredictDosByTankid(String tankid);

}
