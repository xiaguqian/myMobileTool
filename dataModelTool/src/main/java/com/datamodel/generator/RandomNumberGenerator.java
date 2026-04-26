package com.datamodel.generator;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomNumberGenerator implements DataGenerator {

    @Override
    public String getRuleType() {
        return "RANDOM_NUMBER";
    }

    @Override
    public Object generate(JSONObject config, int index) {
        if (config == null) {
            return ThreadLocalRandom.current().nextInt(0, 100);
        }

        int min = config.getIntValue("min", 0);
        int max = config.getIntValue("max", 100);
        boolean isDecimal = config.getBooleanValue("isDecimal", false);

        if (isDecimal) {
            int scale = config.getIntValue("scale", 2);
            double value = min + ThreadLocalRandom.current().nextDouble() * (max - min);
            double scaleFactor = Math.pow(10, scale);
            return Math.round(value * scaleFactor) / scaleFactor;
        } else {
            return ThreadLocalRandom.current().nextInt(min, max + 1);
        }
    }
}
