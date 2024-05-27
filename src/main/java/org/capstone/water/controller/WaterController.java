package org.capstone.water.controller;

import lombok.RequiredArgsConstructor;
import org.capstone.water.repository.entity.mldata.MldataMapping;
import org.capstone.water.repository.entity.mldata.MldataView;
import org.capstone.water.repository.entity.pdo.PdoMapping;
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
    public ResponseEntity<List<MldataMapping>> getTest(@RequestParam String tankid){
        List<MldataMapping> mldataViewList = service.getTest(tankid);
        return new ResponseEntity<>(mldataViewList, HttpStatus.OK);
    }

    @GetMapping("/pdo")
    public ResponseEntity<List<PdoMapping>> getPdo(@RequestParam String tankid){
        List<PdoMapping> predictDoList = service.getPdo(tankid);
        return new ResponseEntity<>(predictDoList, HttpStatus.OK);
    }

    @GetMapping("/water")
    public ResponseEntity<List<WaterMapping>> getWater(@RequestParam String tankid){
        List<WaterMapping> waterdataList = service.getWaterdata(tankid);
        return new ResponseEntity<>(waterdataList, HttpStatus.OK);
    }
}
