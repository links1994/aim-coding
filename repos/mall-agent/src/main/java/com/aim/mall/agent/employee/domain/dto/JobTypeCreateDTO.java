package com.aim.mall.agent.domain.dto;

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
     * 岗位类型编码
     */
    private String code;

    /**
     * 岗位描述
     */
    private String description;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 创建人ID
     */
    private Long creatorId;
}
