package com.datamodel.generator;

import com.alibaba.fastjson.JSONObject;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Component
public class DateTimeGenerator implements DataGenerator {

    private final Faker faker = new Faker(Locale.CHINA);
    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public String getRuleType() {
        return "DATETIME";
    }

    @Override
    public Object generate(JSONObject config, int index) {
        String format = DEFAULT_FORMAT;

        if (config != null) {
            format = config.getString("format");
            if (format == null || format.isEmpty()) {
                format = DEFAULT_FORMAT;
            }
        }

        Date date = faker.date().past(365, TimeUnit.DAYS);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
}
