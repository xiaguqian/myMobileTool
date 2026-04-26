package com.datamodel.generator;

import com.alibaba.fastjson.JSONObject;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class AddressGenerator implements DataGenerator {

    private final Faker faker = new Faker(Locale.CHINA);

    @Override
    public String getRuleType() {
        return "ADDRESS";
    }

    @Override
    public Object generate(JSONObject config, int index) {
        return faker.address().fullAddress();
    }
}
