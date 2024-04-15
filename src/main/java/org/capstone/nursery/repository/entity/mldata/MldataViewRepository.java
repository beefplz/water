package org.capstone.nursery.repository.entity.mldata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MldataViewRepository extends JpaRepository<MldataView, String> {
    List<MldataView> findMldataViewsByFcid(String fcid);

}
