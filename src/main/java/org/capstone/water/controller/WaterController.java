package org.capstone.water.controller;

import lombok.RequiredArgsConstructor;
import org.capstone.water.repository.entity.mldata.MldataMapping;
import org.capstone.water.repository.entity.mldata.MldataView;
import org.capstone.water.repository.entity.pdo.PdoMapping;
import org.capstone.water.repository.entity.pdo.PredictDo;
import org.capstone.water.repository.entity.pdoweek.PdoWeekMapping;
import org.capstone.water.repository.entity.waterdata.WaterMapping;
import org.capstone.water.repository.entity.waterdata.Waterdata;
import org.capstone.water.repository.entity.weather.Weather;
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
    public ResponseEntity<Weather> getTest(){
        Weather weather = service.getTest();
        return new ResponseEntity<>(weather, HttpStatus.OK);
    }

    @GetMapping("/pdo")
    public ResponseEntity<List<PdoMapping>> getPdo(@RequestParam String tankid){
        List<PdoMapping> predictDoList = service.getPdo(tankid);
        return new ResponseEntity<>(predictDoList, HttpStatus.OK);
    }

    @GetMapping("/water")
    public ResponseEntity<List<Waterdata>> getWater(@RequestParam String tankid){
        List<Waterdata> waterdataList = service.getWaterdata(tankid);
        return new ResponseEntity<>(waterdataList, HttpStatus.OK);
    }

    @GetMapping("/waterone")
    public ResponseEntity<Waterdata> getWaterOne(@RequestParam String tankid){
        Waterdata waterdata = service.getWaterdataOne(tankid);
        return new ResponseEntity<>(waterdata, HttpStatus.OK);
    }

    @GetMapping("/pdoweek")
    public ResponseEntity<List<PdoWeekMapping>> getPdoWeek(@RequestParam String tankid){
        List<PdoWeekMapping> predictDoWeekList = service.getPdoWeek(tankid);
        return new ResponseEntity<>(predictDoWeekList, HttpStatus.OK);
    }

    @GetMapping("/waterwithos")
    public ResponseEntity<List<Waterdata>> getWaterwithOs(@RequestParam String tankid){
        List<Waterdata> waterdataListwithOs = service.getWaterdataWithOs(tankid);
        return new ResponseEntity<>(waterdataListwithOs, HttpStatus.OK);
    }

    @GetMapping("/wateronewithos")
    public ResponseEntity<Waterdata> getWaterOneWithOs(@RequestParam String tankid){
        Waterdata waterdata = service.getWaterdataOnewithOs(tankid);
        return new ResponseEntity<>(waterdata, HttpStatus.OK);
    }
}
