package com.datamodel.generator;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class SequenceGenerator implements DataGenerator {

    @Override
    public String getRuleType() {
        return "SEQUENCE";
    }

    @Override
    public Object generate(JSONObject config, int index) {
        if (config == null) {
            return index + 1;
        }

        int start = config.containsKey("start") ? config.getIntValue("start") : 1;
        int step = config.containsKey("step") ? config.getIntValue("step") : 1;
        String prefix = config.getString("prefix");
        String suffix = config.getString("suffix");
        int padding = config.containsKey("padding") ? config.getIntValue("padding") : 0;

        int value = start + index * step;
        String result = String.valueOf(value);

        if (padding > 0 && result.length() < padding) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < padding - result.length(); i++) {
                sb.append("0");
            }
            sb.append(result);
            result = sb.toString();
        }

        if (prefix != null) {
            result = prefix + result;
        }
        if (suffix != null) {
            result = result + suffix;
        }

        return result;
    }
}
