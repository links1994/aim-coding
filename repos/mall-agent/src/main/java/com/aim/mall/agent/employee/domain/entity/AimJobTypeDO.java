package com.aim.mall.agent.employee.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 岗位类型实体类
 *
 * @author AI Agent
 */
@Data
@TableName("aim_agent_job_type")
public class AimJobTypeDO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 岗位类型名称（如：销售顾问、客服专员）
     */
    private String name;

    /**
     * 岗位类型编码（唯一，如：SALES、CS）
     */
    private String code;

    /**
     * 岗位描述
     */
    private String description;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记：0-未删除 1-已删除
     */
    private Integer isDeleted;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 更新人ID
     */
    private Long updaterId;
}
