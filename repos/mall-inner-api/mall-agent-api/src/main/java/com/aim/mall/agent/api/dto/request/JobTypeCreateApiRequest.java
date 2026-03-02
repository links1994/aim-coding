package com.aim.mall.agent.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 岗位类型创建请求（API模块）
 *
 * @author AI Agent
 */
@Data
public class JobTypeCreateApiRequest implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 岗位类型名称（1-64字符）
     */
    @NotBlank(message = "岗位类型名称不能为空")
    @Size(min = 1, max = 64, message = "岗位类型名称长度必须在1-64字符之间")
    private String name;

    /**
     * 岗位描述（0-255字符）
     */
    @Size(max = 255, message = "岗位描述长度不能超过255字符")
    private String description;

    /**
     * 状态：0-禁用，1-启用，默认1
     */
    private Integer status = 1;

    /**
     * 操作人ID
     */
    private Long operatorId;
}
