package com.aim.mall.agent.config;

import com.aim.mall.agent.domain.exception.BusinessException;
import com.aim.mall.agent.domain.exception.MethodArgumentValidationException;
import com.aim.mall.agent.domain.exception.RemoteApiCallException;
import com.aim.mall.agent.domain.enums.ErrorCodeEnum;
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
 * <p>
 * 统一错误处理
 * </p>
 *
 * @author Qoder
 * @since 2026/2/25
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
        // 如果异常对象中包含错误码，则使用异常对象的错误码，否则返回该模块系统内部异常
        IErrorCode errorCode = e.getErrorCode() != null ? e.getErrorCode() : ErrorCodeEnum.AGENT_OTHER_ERROR;
        return CommonResult.failed(errorCode);
    }

    /**
     * 捕获参数校验异常
     *
     * @param e 参数校验异常
     * @return 统一返回结果
     */
    @ExceptionHandler(value = MethodArgumentValidationException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonResult handleMethodArgumentValidationException(MethodArgumentValidationException e) {
        log.error("参数校验异常: {}", e.getMessage(), e);
        // 如果异常对象中包含错误码，则使用异常对象的错误码，否则返回该模块系统内部异常
        IErrorCode errorCode = e.getErrorCode() != null ? e.getErrorCode() : ErrorCodeEnum.AGENT_OTHER_ERROR;
        return CommonResult.failed(errorCode, e.getMessage());
    }

    /**
     * 捕获远程API调用异常
     *
     * @param e 远程API调用异常
     * @return 统一返回结果
     */
    @ExceptionHandler(value = RemoteApiCallException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CommonResult handleRemoteApiCallException(RemoteApiCallException e) {
        log.error("远程API调用异常: {}", e.getMessage(), e);
        // 如果异常对象中包含错误码，则使用异常对象的错误码，否则返回该模块系统内部异常
        IErrorCode errorCode = e.getErrorCode() != null ? e.getErrorCode() : ErrorCodeEnum.AGENT_OTHER_ERROR;
        return CommonResult.failed(errorCode, e.getMessage());
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
        return CommonResult.failed(ErrorCodeEnum.AGENT_SYSTEM_ERROR, "系统内部错误");
    }

}