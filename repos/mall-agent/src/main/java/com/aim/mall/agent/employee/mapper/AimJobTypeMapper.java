package com.aim.mall.agent.mapper;

import com.aim.mall.agent.domain.entity.AimJobTypeDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 岗位类型Mapper接口
 *
 * @author AI Agent
 */
@Mapper
public interface AimJobTypeMapper extends BaseMapper<AimJobTypeDO> {

    /**
     * 根据编码查询（排除已删除）
     *
     * @param code 岗位类型编码
     * @return 岗位类型实体
     */
    AimJobTypeDO selectByCode(@Param("code") String code);

    /**
     * 分页查询岗位类型列表
     *
     * @param keyword  关键词
     * @param offset   偏移量
     * @param limit    每页大小
     * @return 岗位类型列表
     */
    List<AimJobTypeDO> selectPageByKeyword(@Param("keyword") String keyword,
                                           @Param("offset") Integer offset,
                                           @Param("limit") Integer limit);

    /**
     * 统计总数（根据关键词）
     *
     * @param keyword 关键词
     * @return 总数
     */
    Long countByKeyword(@Param("keyword") String keyword);
}
