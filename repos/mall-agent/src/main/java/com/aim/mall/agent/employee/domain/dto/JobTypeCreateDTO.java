package com.aim.mall.agent.employee.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 岗位类型创建DTO
 *
 * @author AI Agent
 */
@Data
public class JobTypeCreateDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 岗位类型名称
     */
    private String name;

    /**
     * 岗位类型编码（由应用层生成）
     */
    private String code;

    /**
     * 岗位描述
     */
    private String description;

    /**
     * 排序号（由应用层自动生成）
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建人ID
     */
    private Long creatorId;
}
