package com.aim.mall.agent.api.dto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 岗位类型分页查询请求（API模块）
 *
 * @author AI Agent
 */
@Data
public class JobTypePageApiRequest implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 关键词（匹配名称/编码）
     */
    private String keyword;

    /**
     * 页码，默认1
     */
    private Integer pageNum = 1;

    /**
     * 每页大小，默认10
     */
    private Integer pageSize = 10;
}
