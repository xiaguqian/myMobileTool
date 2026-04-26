package com.datamodel.generator;

import com.alibaba.fastjson.JSONObject;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class EmailGenerator implements DataGenerator {

    private final Faker faker = new Faker(Locale.CHINA);

    @Override
    public String getRuleType() {
        return "EMAIL";
    }

    @Override
    public Object generate(JSONObject config, int index) {
        return faker.internet().emailAddress();
    }
}
