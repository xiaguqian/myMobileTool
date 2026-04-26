package com.datamodel.generator;

import com.alibaba.fastjson.JSONObject;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Component
public class DateGenerator implements DataGenerator {

    private final Faker faker = new Faker(Locale.CHINA);
    private static final String DEFAULT_FORMAT = "yyyy-MM-dd";

    @Override
    public String getRuleType() {
        return "DATE";
    }

    @Override
    public Object generate(JSONObject config, int index) {
        String format = DEFAULT_FORMAT;
        String startDate = null;
        String endDate = null;

        if (config != null) {
            format = config.getString("format");
            if (format == null || format.isEmpty()) {
                format = DEFAULT_FORMAT;
            }
            startDate = config.getString("startDate");
            endDate = config.getString("endDate");
        }

        Date date;
        if (startDate != null && endDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);
                date = faker.date().between(start, end);
            } catch (Exception e) {
                date = faker.date().past(365, TimeUnit.DAYS);
            }
        } else {
            date = faker.date().past(365, TimeUnit.DAYS);
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
}
