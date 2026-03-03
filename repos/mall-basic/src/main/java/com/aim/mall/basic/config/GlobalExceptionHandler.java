package com.aim.mall.basic.config;

import com.aim.mall.basic.idgen.domain.enums.BasicErrorCodeEnum;
import com.aim.mall.basic.idgen.domain.exception.BusinessException;
import com.aim.mall.common.api.CommonResult;
import com.aim.mall.common.api.IErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 全局异常处理器
 *
 * @author AI Agent
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     *
     * @param e 业务异常
     * @return 统一返回结果
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonResult handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage(), e);
        IErrorCode errorCode = e.getErrorCode() != null ? e.getErrorCode() : BasicErrorCodeEnum.OTHER_ERROR;
        return CommonResult.failed(errorCode);
    }

    /**
     * 捕获系统未知异常
     *
     * @param e 系统异常
     * @return 统一返回结果
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonResult handleException(Exception e) {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String method = request.getRequestURI();
        log.error("系统未知异常, method: {}, message: {}", method, e.getMessage(), e);
        return CommonResult.failed(BasicErrorCodeEnum.SYSTEM_ERROR, "系统内部错误");
    }
}
