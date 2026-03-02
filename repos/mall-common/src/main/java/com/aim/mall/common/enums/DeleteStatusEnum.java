package com.aim.mall.common.enums;

import lombok.Getter;

/**
 * @Author: hulingtao
 * @Date: 2025/9/25 09:55
 */
@Getter
public enum DeleteStatusEnum {
    DELETE(1, "已删除 "),
    UNDELETE(0, "未删除"),;

    private final Integer code;
    private final String desc;

    DeleteStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
