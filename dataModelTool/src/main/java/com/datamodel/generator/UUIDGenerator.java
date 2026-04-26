package com.datamodel.generator;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGenerator implements DataGenerator {

    @Override
    public String getRuleType() {
        return "UUID";
    }

    @Override
    public Object generate(JSONObject config, int index) {
        boolean removeDash = false;
        boolean toUpperCase = false;

        if (config != null) {
            removeDash = config.getBooleanValue("removeDash", false);
            toUpperCase = config.getBooleanValue("toUpperCase", false);
        }

        String uuid = UUID.randomUUID().toString();
        
        if (removeDash) {
            uuid = uuid.replace("-", "");
        }
        
        if (toUpperCase) {
            uuid = uuid.toUpperCase();
        }

        return uuid;
    }
}
