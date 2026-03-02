package com.aim.mall.agent.employee.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 岗位类型状态更新DTO
 *
 * @author AI Agent
 */
@Data
public class JobTypeStatusDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 岗位类型ID
     */
    private Long id;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 更新人ID
     */
    private Long updaterId;
}
