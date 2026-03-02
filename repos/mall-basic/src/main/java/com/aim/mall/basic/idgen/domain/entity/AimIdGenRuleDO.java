package com.aim.mall.basic.idgen.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ID生成规则实体
 *
 * @author AI Agent
 */
@Data
@TableName("aim_idgen_rule")
public class AimIdGenRuleDO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务前缀
     */
    private String prefix;

    /**
     * 日期格式
     */
    private String datePattern;

    /**
     * 当前最大序号
     */
    private Long currentMaxSeq;

    /**
     * 号段步长
     */
    private Integer stepSize;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
