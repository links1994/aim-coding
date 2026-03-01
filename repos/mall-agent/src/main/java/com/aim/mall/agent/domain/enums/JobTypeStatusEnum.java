package com.aim.mall.agent.domain.enums;

import lombok.Getter;

/**
 * 岗位类型状态枚举
 *
 * @author AI Agent
 */
@Getter
public enum JobTypeStatusEnum {

    /**
     * 禁用
     */
    DISABLED(0, "禁用"),

    /**
     * 启用
     */
    ENABLED(1, "启用");

    private final Integer code;
    private final String desc;

    JobTypeStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 状态码
     * @return 枚举值，不存在返回null
     */
    public static JobTypeStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (JobTypeStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断code是否有效
     *
     * @param code 状态码
     * @return 是否有效
     */
    public static boolean isValid(Integer code) {
        return getByCode(code) != null;
    }
}
