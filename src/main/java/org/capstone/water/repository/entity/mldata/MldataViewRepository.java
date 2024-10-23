package org.capstone.water.repository.entity.mldata;

import org.capstone.water.repository.entity.waterdata.WaterMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MldataViewRepository extends JpaRepository<MldataView, String> {
    List<MldataView> findMldataViewsByTankid(String tankid);
    List<MldataView> findTop10ByOrderByTimeDesc();


    @Query(value = "SELECT * FROM nursery.mldata WHERE tankid = :id ORDER BY time desc limit 30", nativeQuery =true )
    List<MldataMapping> findMldataViewsByTankidOrderByTimeDesc(@Param("id")String tankid);

    @Query(value = "SELECT * FROM nursery.mldata WHERE MOD(DATE_FORMAT(time,'%i'),30)=0 AND tankid = :id ORDER BY time desc limit 336", nativeQuery =true )
    List<MldataMapping> findMldataViewsByTankidOrderByTimeDescWeek(@Param("id")String tankid);


}
