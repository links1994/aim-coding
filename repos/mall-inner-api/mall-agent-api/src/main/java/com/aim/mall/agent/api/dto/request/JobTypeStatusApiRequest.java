package com.aim.mall.agent.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 岗位类型状态更新请求（API模块）
 *
 * @author AI Agent
 */
@Data
public class JobTypeStatusApiRequest implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 岗位类型ID
     */
    @NotNull(message = "岗位类型ID不能为空")
    private Long id;

    /**
     * 状态：0-禁用 1-启用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 操作人ID
     */
    private Long operatorId;
}
