package com.datamodel.generator;

import com.alibaba.fastjson.JSONObject;

public interface DataGenerator {

    String getRuleType();

    Object generate(JSONObject config, int index);
}
