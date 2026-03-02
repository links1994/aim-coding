package com.aim.mall.basic.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

/**
 * ID生成请求
 *
 * @author AI Agent
 */
@Data
public class IdGenApiRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务前缀，1-2位大写字母
     */
    @NotBlank(message = "业务前缀不能为空")
    @Pattern(regexp = "^[A-Z]{1,2}$", message = "业务前缀必须为1-2位大写字母")
    private String prefix;

    /**
     * 日期格式
     * 可选值：yyyyMMdd, yyyyMMddHHmmss, yyyyMM, yyyy
     */
    @NotBlank(message = "日期格式不能为空")
    private String datePattern;
}
