package org.capstone.water.apireader;

import lombok.RequiredArgsConstructor;
import org.capstone.water.repository.entity.waterdata.Waterdata;
import org.capstone.water.repository.entity.waterdata.WaterdataRepository;
import org.capstone.water.repository.entity.weather.Weather;
import org.capstone.water.repository.entity.weather.WeatherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class Scheduler {
    private final WaterdataRepository waterdataRepository;
    private final WeatherRepository weatherRepository;
    final Logger log = LoggerFactory.getLogger(getClass());
    @Scheduled(fixedRate = 60000)
    public void run() {

        WeatherReader weatherReader = new WeatherReader();
        Weather weathers = weatherReader.weatherRead();
        if(!weatherRepository.existsByTime(weathers.getTime())){
            weatherRepository.save(weatherReader.weatherRead());
        }

        WaterReader waterReader = new WaterReader();
        List<Waterdata> waterdataList = waterReader.waterRead();
        if (waterdataRepository.existsByTime(waterdataList.get(0).getTime())){
            log.info("already exist");
        }
        else {
            waterdataRepository.save(waterdataList.get(0));
            waterdataRepository.save(waterdataList.get(1));
            waterdataRepository.save(waterdataList.get(2));
        }
    }
}
