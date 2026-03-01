package com.aim.mall.agent.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 岗位类型更新请求（API模块）
 *
 * @author AI Agent
 */
@Data
public class JobTypeUpdateApiRequest implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 岗位类型ID
     */
    @NotNull(message = "岗位类型ID不能为空")
    private Long id;

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
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 操作人ID
     */
    private Long operatorId;
}
