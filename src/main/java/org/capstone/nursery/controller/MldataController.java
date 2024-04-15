package org.capstone.nursery.controller;

import lombok.RequiredArgsConstructor;
import org.capstone.nursery.repository.entity.mldata.MldataView;
import org.capstone.nursery.repository.entity.pdo.PredictedDo;
import org.capstone.nursery.repository.entity.waterdata.Waterdata;
import org.capstone.nursery.service.WaterServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MldataController {

    private final WaterServiceImpl service;
    @GetMapping("/mldata")
    public ResponseEntity<List<MldataView>> getMldata(@RequestParam String tankid){
        List<MldataView> mldataViewList = service.getMldata(tankid);
        return new ResponseEntity<>(mldataViewList, HttpStatus.OK);
    }

    @GetMapping("/water")
    public ResponseEntity<List<Waterdata>> getWaterdata(@RequestParam String tankid){
        List<Waterdata> waterdataList = service.getWaterdata(tankid);
        return new ResponseEntity<>(waterdataList, HttpStatus.OK);
    }

    @GetMapping("/pdo")
    public ResponseEntity<List<PredictedDo>> getPdo(@RequestParam String tankid){
        List<PredictedDo> predictedDoList = service.getPdo(tankid);
        return new ResponseEntity<>(predictedDoList, HttpStatus.OK);
    }
}
