package com.aim.mall.agent.employee.service;

import com.aim.mall.agent.employee.domain.dto.JobTypeCreateDTO;
import com.aim.mall.agent.employee.domain.dto.JobTypePageQuery;
import com.aim.mall.agent.employee.domain.dto.JobTypeStatusDTO;
import com.aim.mall.agent.employee.domain.dto.JobTypeUpdateDTO;
import com.aim.mall.common.api.CommonResult;

/**
 * 岗位类型应用服务接口
 * <p>
 * 业务编排，协调查询和管理服务
 *
 * @author AI Agent
 */
public interface JobTypeApplicationService {

    /**
     * 分页查询岗位类型列表
     *
     * @param query 查询参数
     * @return 分页结果（包含Response转换）
     */
    CommonResult<CommonResult.PageData<JobTypeResponse>> pageJobType(JobTypePageQuery query);

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

    /**
     * 根据ID查询岗位类型
     *
     * @param id 岗位类型ID
     * @return 岗位类型响应
     */
    JobTypeResponse getById(Long id);

    /**
     * 岗位类型响应内部类
     */
    @lombok.Data
    class JobTypeResponse {
        private Long id;
        private String name;
        private String code;
        private String description;
        private Integer status;
        private Integer sortOrder;
        private Integer employeeCount;
        private java.time.LocalDateTime createTime;
        private java.time.LocalDateTime updateTime;
    }
}
