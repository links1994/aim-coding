package com.aim.mall.agent.domain.enums;

import com.aim.mall.common.api.IErrorCode;
import lombok.Getter;

/**
 * <p>
 * 错误码枚举类
 * </p>
 *
 * @author Qoder
 * @since 2026/2/25
 */
@Getter
public enum ErrorCodeEnum implements IErrorCode {

    // 智能员工模块业务错误
    AGENT_BUSINESS_ERROR(10092001L, "智能员工业务错误"),
    AGENT_PARAM_ERROR(10091001L, "智能员工参数错误"),
    AGENT_REMOTE_CALL_ERROR(10093001L, "智能员工远程调用错误"),

    // 智能员工模块系统错误
    AGENT_SYSTEM_ERROR(10095001L, "智能员工系统内部错误"),
    AGENT_OTHER_ERROR(10095999L, "智能员工其他错误"),

    // 参数校验错误
    PARAM_BIND_ERROR(10091002L, "参数绑定错误"),
    METHOD_ARGUMENT_NOT_VALID_ERROR(10091003L, "方法参数验证错误");

    private final long code;
    private final String message;

    ErrorCodeEnum(long code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据错误码获取枚举
     *
     * @param code 错误码
     * @return 错误码枚举
     */
    public static ErrorCodeEnum getByCode(String code) {
        for (ErrorCodeEnum errorCode : values()) {
            if (String.valueOf(errorCode.getCode()).equals(code)) {
                return errorCode;
            }
        }
        return null;
    }
}