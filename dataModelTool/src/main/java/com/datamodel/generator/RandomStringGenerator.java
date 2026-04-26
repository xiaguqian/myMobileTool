package com.datamodel.generator;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomStringGenerator implements DataGenerator {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";

    @Override
    public String getRuleType() {
        return "RANDOM_STRING";
    }

    @Override
    public Object generate(JSONObject config, int index) {
        if (config == null) {
            return generateRandomString(8, true, true, true);
        }

        int length = config.getIntValue("length", 8);
        boolean includeUppercase = config.getBooleanValue("includeUppercase", true);
        boolean includeLowercase = config.getBooleanValue("includeLowercase", true);
        boolean includeDigits = config.getBooleanValue("includeDigits", true);

        return generateRandomString(length, includeUppercase, includeLowercase, includeDigits);
    }

    private String generateRandomString(int length, boolean uppercase, boolean lowercase, boolean digits) {
        StringBuilder chars = new StringBuilder();
        if (uppercase) chars.append(UPPERCASE);
        if (lowercase) chars.append(LOWERCASE);
        if (digits) chars.append(DIGITS);

        if (chars.length() == 0) {
            chars.append(LOWERCASE);
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
        }
        return result.toString();
    }
}
