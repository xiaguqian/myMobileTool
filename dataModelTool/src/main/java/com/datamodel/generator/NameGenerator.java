package com.datamodel.generator;

import com.alibaba.fastjson.JSONObject;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class NameGenerator implements DataGenerator {

    private final Faker faker = new Faker(Locale.CHINA);

    @Override
    public String getRuleType() {
        return "NAME";
    }

    @Override
    public Object generate(JSONObject config, int index) {
        return faker.name().fullName();
    }
}
