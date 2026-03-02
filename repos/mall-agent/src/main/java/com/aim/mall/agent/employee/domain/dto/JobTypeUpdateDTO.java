package com.aim.mall.agent.employee.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 岗位类型更新DTO
 *
 * @author AI Agent
 */
@Data
public class JobTypeUpdateDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 岗位类型ID
     */
    private Long id;

    /**
     * 岗位类型名称
     */
    private String name;

    /**
     * 岗位描述
     */
    private String description;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 更新人ID
     */
    private Long updaterId;
}
