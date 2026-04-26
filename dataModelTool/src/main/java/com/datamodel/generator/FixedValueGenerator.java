package com.datamodel.generator;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class FixedValueGenerator implements DataGenerator {

    @Override
    public String getRuleType() {
        return "FIXED_VALUE";
    }

    @Override
    public Object generate(JSONObject config, int index) {
        if (config == null) {
            return null;
        }
        return config.get("value");
    }
}
