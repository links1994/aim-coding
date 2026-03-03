package com.aim.mall.agent.employee.service;

import com.aim.mall.agent.employee.domain.dto.JobTypePageQuery;
import com.aim.mall.agent.employee.domain.entity.AimJobTypeDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 岗位类型查询服务接口
 * <p>
 * 只读查询，封装查询逻辑
 *
 * @author AI Agent
 */
public interface JobTypeQueryService {

    /**
     * 分页查询岗位类型列表
     *
     * @param query 查询参数
     * @return 分页结果
     */
    Page<AimJobTypeDO> pageJobType(JobTypePageQuery query);

    /**
     * 根据ID查询岗位类型
     *
     * @param id 岗位类型ID
     * @return 岗位类型实体
     */
    AimJobTypeDO getById(Long id);

    /**
     * 根据编码查询岗位类型
     *
     * @param code 岗位类型编码
     * @return 岗位类型实体
     */
    AimJobTypeDO getByCode(String code);
}
