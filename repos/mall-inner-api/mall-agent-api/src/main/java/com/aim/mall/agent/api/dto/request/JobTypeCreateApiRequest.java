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
     * 岗位类型编码（1-32字符，唯一，仅允许大写字母、数字、下划线）
     */
    @NotBlank(message = "岗位类型编码不能为空")
    @Size(min = 1, max = 32, message = "岗位类型编码长度必须在1-32字符之间")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "岗位类型编码只能包含大写字母、数字和下划线")
    private String code;

    /**
     * 岗位描述（0-255字符）
     */
    @Size(max = 255, message = "岗位描述长度不能超过255字符")
    private String description;

    /**
     * 排序号，默认0
     */
    private Integer sortOrder = 0;

    /**
     * 操作人ID
     */
    private Long operatorId;
}
