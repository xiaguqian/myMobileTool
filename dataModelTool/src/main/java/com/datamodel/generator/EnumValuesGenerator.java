package com.datamodel.generator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class EnumValuesGenerator implements DataGenerator {

    @Override
    public String getRuleType() {
        return "ENUM_VALUES";
    }

    @Override
    public Object generate(JSONObject config, int index) {
        if (config == null) {
            return null;
        }

        JSONArray values = config.getJSONArray("values");
        if (values == null || values.isEmpty()) {
            return null;
        }

        int randomIndex = ThreadLocalRandom.current().nextInt(values.size());
        return values.get(randomIndex);
    }
}
