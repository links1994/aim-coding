package com.aim.mall.basic.idgen.controller.inner;

import com.aim.mall.basic.api.dto.request.IdGenApiRequest;
import com.aim.mall.basic.api.dto.response.IdGenApiResponse;
import com.aim.mall.basic.idgen.domain.enums.BasicErrorCodeEnum;
import com.aim.mall.basic.idgen.domain.enums.DatePatternEnum;
import com.aim.mall.basic.idgen.domain.exception.BusinessException;
import com.aim.mall.basic.idgen.service.IdGenService;
import com.aim.mall.common.api.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * ID生成内部接口（供Feign调用）
 *
 * @author AI Agent
 */
@Slf4j
@RestController
@RequestMapping("/inner/api/v1/idgen")
@RequiredArgsConstructor
@Tag(name = "ID生成内部接口", description = "分布式ID生成内部接口（供Feign调用）")
public class IdGenInnerController {

    private final IdGenService idGenService;

    /**
     * 生成分布式ID编码
     *
     * @param request ID生成请求
     * @return 生成的编码
     */
    @PostMapping("/generate")
    @Operation(summary = "生成分布式ID编码")
    public CommonResult<IdGenApiResponse> generate(@RequestBody IdGenApiRequest request) {
        log.debug("生成分布式ID编码, request: {}", request);

        // 手动校验参数
        validateRequest(request);

        // 转换日期格式
        DatePatternEnum datePattern = DatePatternEnum.fromPattern(request.getDatePattern());
        if (datePattern == null) {
            throw new BusinessException(BasicErrorCodeEnum.DATE_PATTERN_NOT_SUPPORT,
                    "不支持的日期格式: " + request.getDatePattern());
        }

        // 生成编码
        String code = idGenService.generateCode(request.getPrefix(), datePattern);

        // 构建响应
        IdGenApiResponse response = new IdGenApiResponse();
        response.setCode(code);
        response.setPrefix(request.getPrefix());

        // 提取日期部分
        String dateValue = LocalDateTime.now().format(datePattern.getFormatter());
        response.setDatePart(dateValue);

        // 提取序号部分
        String seqStr = code.substring(request.getPrefix().length() + dateValue.length());
        response.setSequence(seqStr);

        log.info("生成编码成功, code: {}", code);

        return CommonResult.success(response);
    }

    /**
     * 手动校验请求参数
     *
     * @param request ID生成请求
     */
    private void validateRequest(IdGenApiRequest request) {
        if (request == null) {
            throw new BusinessException(BasicErrorCodeEnum.PARAM_ERROR, "请求参数不能为空");
        }
        if (StringUtils.isBlank(request.getPrefix())) {
            throw new BusinessException(BasicErrorCodeEnum.PARAM_ERROR, "业务前缀不能为空");
        }
        if (request.getPrefix().length() < 1 || request.getPrefix().length() > 3) {
            throw new BusinessException(BasicErrorCodeEnum.PARAM_ERROR, "业务前缀长度必须在1-3个字符之间");
        }
        if (!request.getPrefix().matches("[A-Z]+")) {
            throw new BusinessException(BasicErrorCodeEnum.PARAM_ERROR, "业务前缀必须为大写字母");
        }
        if (StringUtils.isBlank(request.getDatePattern())) {
            throw new BusinessException(BasicErrorCodeEnum.PARAM_ERROR, "日期格式不能为空");
        }
    }
}
