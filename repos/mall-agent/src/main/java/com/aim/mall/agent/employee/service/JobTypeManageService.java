package com.aim.mall.agent.employee.service;

import com.aim.mall.agent.employee.domain.dto.JobTypeCreateDTO;
import com.aim.mall.agent.employee.domain.dto.JobTypeStatusDTO;
import com.aim.mall.agent.employee.domain.dto.JobTypeUpdateDTO;

/**
 * 岗位类型管理服务接口
 * <p>
 * 增删改操作，封装写逻辑
 *
 * @author AI Agent
 */
public interface JobTypeManageService {

    /**
     * 创建岗位类型
     *
     * @param dto 创建DTO
     * @return 新创建记录ID
     */
    Long createJobType(JobTypeCreateDTO dto);

    /**
     * 更新岗位类型
     *
     * @param dto 更新DTO
     * @return 是否成功
     */
    boolean updateJobType(JobTypeUpdateDTO dto);

    /**
     * 更新岗位类型状态
     *
     * @param dto 状态更新DTO
     * @return 是否成功
     */
    boolean updateStatus(JobTypeStatusDTO dto);

    /**
     * 删除岗位类型
     *
     * @param id 岗位类型ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean deleteJobType(Long id, Long operatorId);
}
