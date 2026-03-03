package com.aim.mall.basic.idgen.domain.enums;

import com.aim.mall.common.api.IErrorCode;
import lombok.Getter;

/**
 * 基础服务错误码枚举
 *
 * @author AI Agent
 */
@Getter
public enum BasicErrorCodeEnum implements IErrorCode {

    // ==================== 客户端错误 (1) ====================
    // 参数错误 40001xxx
    PARAM_ERROR(40001000L, "参数错误"),
    PREFIX_EMPTY(40001001L, "业务前缀不能为空"),
    PREFIX_FORMAT_ERROR(40001002L, "业务前缀格式错误，必须为1-3位大写字母"),
    DATE_PATTERN_EMPTY(40001003L, "日期格式不能为空"),
    DATE_PATTERN_NOT_SUPPORT(40001004L, "日期格式不支持"),

    // ==================== 服务端错误 (2) ====================
    // Redis 错误 40002xxx
    REDIS_OPERATION_ERROR(40002001L, "Redis 操作失败"),
    SEQUENCE_EXHAUSTED(40002002L, "当日序号已用完"),
    SEGMENT_ALLOCATE_ERROR(40002003L, "MySQL 号段分配失败"),

    // 系统错误 40005xxx
    SYSTEM_ERROR(40005001L, "系统内部错误"),
    OTHER_ERROR(40005999L, "其他错误");

    private final long code;
    private final String message;

    BasicErrorCodeEnum(long code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据错误码获取枚举
     *
     * @param code 错误码
     * @return 错误码枚举，找不到返回 null
     */
    public static BasicErrorCodeEnum getByCode(long code) {
        for (BasicErrorCodeEnum errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
}
