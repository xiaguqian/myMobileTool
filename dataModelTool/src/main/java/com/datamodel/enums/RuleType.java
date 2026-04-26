package com.datamodel.enums;

public enum RuleType {

    NAME("姓名", "生成随机姓名"),
    ADDRESS("地址", "生成随机地址"),
    PHONE("手机号", "生成随机手机号"),
    EMAIL("邮箱", "生成随机邮箱"),
    RANDOM_NUMBER("随机数字", "生成指定范围内的随机数字"),
    RANDOM_STRING("随机字符串", "生成随机字母数字串"),
    SEQUENCE("顺序序号", "生成自增序号"),
    DATE("日期", "生成随机日期"),
    DATETIME("日期时间", "生成随机日期时间"),
    UUID("UUID", "生成UUID"),
    FIXED_VALUE("固定值", "使用固定值"),
    ENUM_VALUES("枚举值", "从给定列表中随机选择");

    private final String name;
    private final String description;

    RuleType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
