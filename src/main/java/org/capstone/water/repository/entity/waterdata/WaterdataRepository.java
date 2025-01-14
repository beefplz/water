package org.capstone.water.repository.entity.waterdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WaterdataRepository extends JpaRepository<Waterdata, Long> {
    //List<WaterMapping> findWaterdatasByTankidOrderByTimeDesc(String tankid);

    boolean existsByTime(LocalDateTime Time);

    @Query(value = "SELECT * FROM waterdata WHERE MOD(DATE_FORMAT(time,'%i'),30)=0 AND tankid = :id ORDER BY time desc limit 24", nativeQuery =true )
    List<Waterdata> findWaterdataByTankid(@Param("id") String tankid);

    Waterdata findFirstByTankidOrderByTimeDesc(String tankid);

    @Query(value = "SELECT * FROM waterdata WHERE MOD(DATE_FORMAT(time,'%i'),30)=0 AND tankid = :id ORDER BY time desc limit 336", nativeQuery =true )
    List<Waterdata> findWaterdataByTankidWeek(@Param("id") String tankid);

    Waterdata findFistByTankidAndTime(String tankid, LocalDateTime time);
}
