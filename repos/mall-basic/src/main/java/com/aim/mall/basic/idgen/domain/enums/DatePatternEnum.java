package com.aim.mall.basic.idgen.domain.enums;

import lombok.Getter;

import java.time.format.DateTimeFormatter;

/**
 * 日期格式枚举
 *
 * @author AI Agent
 */
@Getter
public enum DatePatternEnum {

    /**
     * 年月日
     */
    YYYY_MM_DD("yyyyMMdd", DateTimeFormatter.ofPattern("yyyyMMdd")),

    /**
     * 年月日时分秒
     */
    YYYY_MM_DD_HH_MM_SS("yyyyMMddHHmmss", DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),

    /**
     * 年月
     */
    YYYY_MM("yyyyMM", DateTimeFormatter.ofPattern("yyyyMM")),

    /**
     * 年
     */
    YYYY("yyyy", DateTimeFormatter.ofPattern("yyyy"));

    private final String pattern;
    private final DateTimeFormatter formatter;

    DatePatternEnum(String pattern, DateTimeFormatter formatter) {
        this.pattern = pattern;
        this.formatter = formatter;
    }

    /**
     * 根据 pattern 获取枚举
     *
     * @param pattern 日期格式字符串
     * @return 对应的枚举，找不到返回 null
     */
    public static DatePatternEnum fromPattern(String pattern) {
        for (DatePatternEnum value : values()) {
            if (value.pattern.equals(pattern)) {
                return value;
            }
        }
        return null;
    }
}
