package com.aim.mall.agent.domain.exception;

import com.aim.mall.common.api.IErrorCode;

/**
 * 参数校验异常
 */
public class MethodArgumentValidationException extends RuntimeException {

    private IErrorCode errorCode;
    
    /**
     * 1. 仅错误码 - 使用错误码的默认消息
     */
    public MethodArgumentValidationException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    /**
     * 2. 错误码 + 自定义消息 - 优先使用自定义消息
     */
    public MethodArgumentValidationException(IErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * 3. 错误码 + 异常原因 - 保留异常堆栈
     */
    public MethodArgumentValidationException(IErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
    
    /**
     * 4. 错误码 + 自定义消息 + 异常原因
     */
    public MethodArgumentValidationException(IErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public IErrorCode getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(IErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}