package org.capstone.water.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

public class GetOxygensaturation {
    final Logger log = LoggerFactory.getLogger(getClass());

    public float getOxygensaturation(float temp, float wdo) {
        Map<Integer, Double> Oxygensaturation = new HashMap<>();
        Oxygensaturation.put(10, 11.3);
        Oxygensaturation.put(11, 11.1);
        Oxygensaturation.put(12, 10.9);
        Oxygensaturation.put(13, 10.7);
        Oxygensaturation.put(14, 10.5);
        Oxygensaturation.put(15, 10.1);
        Oxygensaturation.put(16, 10.0);
        Oxygensaturation.put(17, 9.8);
        Oxygensaturation.put(18, 9.6);
        Oxygensaturation.put(19, 9.4);
        Oxygensaturation.put(20, 9.1);
        Oxygensaturation.put(21, 8.9);
        Oxygensaturation.put(22, 8.7);
        Oxygensaturation.put(23, 8.6);
        Oxygensaturation.put(24, 8.4);
        Oxygensaturation.put(25, 8.3);
        Oxygensaturation.put(26, 8.1);
        Oxygensaturation.put(27, 7.9);
        Oxygensaturation.put(28, 7.8);
        Oxygensaturation.put(29, 7.7);
        Oxygensaturation.put(30, 7.6);

        Integer tempint = Math.round(temp);

        float OxygensaturationResult = Oxygensaturation.get(tempint).floatValue();


        return (float) Math.round(wdo / OxygensaturationResult * 10000) /100;
    }
}
