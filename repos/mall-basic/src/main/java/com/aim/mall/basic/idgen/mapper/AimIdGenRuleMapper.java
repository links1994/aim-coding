package com.aim.mall.basic.idgen.mapper;

import com.aim.mall.basic.idgen.domain.entity.AimIdGenRuleDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * ID生成规则 Mapper
 *
 * @author AI Agent
 */
@Mapper
public interface AimIdGenRuleMapper extends BaseMapper<AimIdGenRuleDO> {

    /**
     * 根据前缀和日期格式查询
     *
     * @param prefix       业务前缀
     * @param datePattern  日期格式
     * @return ID生成规则
     */
    AimIdGenRuleDO selectByPrefixAndPattern(@Param("prefix") String prefix, @Param("datePattern") String datePattern);

    /**
     * 分配号段（原子更新）
     * 使用数据库乐观锁思想，通过 UPDATE 的原子性保证并发安全
     *
     * @param prefix       业务前缀
     * @param datePattern  日期格式
     * @param stepSize     步长
     * @return 更新行数
     */
    int allocateSegment(@Param("prefix") String prefix, @Param("datePattern") String datePattern, @Param("stepSize") int stepSize);
}
