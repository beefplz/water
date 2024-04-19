package org.capstone.water.controller;

import lombok.RequiredArgsConstructor;
import org.capstone.water.repository.entity.mldata.MldataView;
import org.capstone.water.repository.entity.pdo.PredictDo;
import org.capstone.water.repository.entity.waterdata.WaterMapping;
import org.capstone.water.service.WaterServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin
public class WaterController {

    private final WaterServiceImpl service;
    @GetMapping("/mldata")
    public ResponseEntity<List<MldataView>> getMldata(@RequestParam String tankid){
        List<MldataView> mldataViewList = service.getMldata(tankid);
        return new ResponseEntity<>(mldataViewList, HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<List<WaterMapping>> getWaterdata(@RequestParam String tankid){
        List<WaterMapping> waterdataList = service.getWaterdata(tankid);
        return new ResponseEntity<>(waterdataList, HttpStatus.OK);
    }

    @GetMapping("/pdo")
    public ResponseEntity<List<PredictDo>> getPdo(@RequestParam String tankid){
        List<PredictDo> predictDoList = service.getPdo(tankid);
        return new ResponseEntity<>(predictDoList, HttpStatus.OK);
    }

    @GetMapping("/water")
    public ResponseEntity<List<WaterMapping>> getTest(@RequestParam String tankid){
        List<WaterMapping> waterdataList = service.getWaterdataTest(tankid);
        return new ResponseEntity<>(waterdataList, HttpStatus.OK);
    }
}
