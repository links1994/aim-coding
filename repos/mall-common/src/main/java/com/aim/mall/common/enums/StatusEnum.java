package com.aim.mall.common.enums;

import lombok.Getter;

@Getter
public enum StatusEnum {

    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    private final Integer code;
    private final String desc;

    StatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
