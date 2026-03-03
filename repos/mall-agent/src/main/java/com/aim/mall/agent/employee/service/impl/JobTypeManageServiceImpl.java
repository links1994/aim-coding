package com.aim.mall.agent.employee.service.impl;

import com.aim.mall.agent.employee.domain.dto.JobTypeCreateDTO;
import com.aim.mall.agent.employee.domain.dto.JobTypeStatusDTO;
import com.aim.mall.agent.employee.domain.dto.JobTypeUpdateDTO;
import com.aim.mall.agent.employee.domain.entity.AimJobTypeDO;
import com.aim.mall.agent.employee.domain.enums.AgentErrorCodeEnum;
import com.aim.mall.agent.employee.domain.exception.BusinessException;
import com.aim.mall.agent.employee.service.JobTypeManageService;
import com.aim.mall.agent.employee.service.mp.AimJobTypeService;
import com.aim.mall.common.enums.DeleteStatusEnum;
import com.aim.mall.common.enums.StatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 岗位类型管理服务实现
 * <p>
 * 增删改操作，封装写逻辑
 *
 * @author AI Agent
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobTypeManageServiceImpl implements JobTypeManageService {

    private final AimJobTypeService aimJobTypeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createJobType(JobTypeCreateDTO dto) {
        log.debug("创建岗位类型开始, dto: {}", dto);

        // 创建实体（code 和 sortOrder 已由应用层生成并设置到 dto 中）
        AimJobTypeDO entity = new AimJobTypeDO();
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        entity.setSortOrder(dto.getSortOrder());
        // 状态：传入值或默认启用
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusEnum.ENABLE.getCode());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setIsDeleted(DeleteStatusEnum.UNDELETE.getCode());
        entity.setCreatorId(dto.getCreatorId());

        aimJobTypeService.save(entity);

        log.info("创建岗位类型成功, jobTypeId: {}, code: {}", entity.getId(), entity.getCode());
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateJobType(JobTypeUpdateDTO dto) {
        log.debug("更新岗位类型开始, dto: {}", dto);

        // 查询记录是否存在
        AimJobTypeDO entity = aimJobTypeService.getById(dto.getId());
        if (entity == null || DeleteStatusEnum.DELETE.getCode().equals(entity.getIsDeleted())) {
            log.warn("岗位类型不存在或已删除, id: {}", dto.getId());
            throw new BusinessException(AgentAgentErrorCodeEnum.AGENT_BUSINESS_ERROR, "岗位类型不存在");
        }

        // 更新字段（code和sortOrder不允许修改）
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdaterId(dto.getUpdaterId());

        boolean result = aimJobTypeService.updateById(entity);

        log.info("更新岗位类型成功, jobTypeId: {}", dto.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(JobTypeStatusDTO dto) {
        log.debug("更新岗位类型状态开始, id: {}, status: {}", dto.getId(), dto.getStatus());

        // 校验状态值是否有效
        if (!isValidStatus(dto.getStatus())) {
            log.warn("无效的状态值, status: {}", dto.getStatus());
            throw new BusinessException(AgentErrorCodeEnum.Agent_PARAM_ERROR, "无效的状态值");
        }

        // 查询记录是否存在
        AimJobTypeDO entity = aimJobTypeService.getById(dto.getId());
        if (entity == null || DeleteStatusEnum.DELETE.getCode().equals(entity.getIsDeleted())) {
            log.warn("岗位类型不存在或已删除, id: {}", dto.getId());
            throw new BusinessException(AgentAgentErrorCodeEnum.AGENT_BUSINESS_ERROR, "岗位类型不存在");
        }

        // 更新状态
        entity.setStatus(dto.getStatus());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdaterId(dto.getUpdaterId());

        boolean result = aimJobTypeService.updateById(entity);

        log.info("更新岗位类型状态成功, jobTypeId: {}, status: {}", dto.getId(), dto.getStatus());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteJobType(Long id, Long operatorId) {
        log.debug("删除岗位类型开始, id: {}", id);

        // 查询记录是否存在
        AimJobTypeDO entity = aimJobTypeService.getById(id);
        if (entity == null || DeleteStatusEnum.DELETE.getCode().equals(entity.getIsDeleted())) {
            log.warn("岗位类型不存在或已删除, id: {}", id);
            throw new BusinessException(AgentErrorCodeEnum.AGENT_BUSINESS_ERROR, "岗位类型不存在");
        }

        // TODO: REQ-038 待智能员工需求完成后实现
        // 调用 EmployeeService 查询岗位类型关联的员工数量
        // Integer employeeCount = employeeService.countByJobTypeId(id);
        // if (employeeCount > 0) {
        //     throw new BusinessException(AgentErrorCodeEnum.JOB_TYPE_HAS_EMPLOYEES, "岗位类型有关联员工，无法删除");
        // }
        log.warn("功能暂未实现: 岗位类型关联员工数量统计, jobTypeId: {}", id);

        // 执行逻辑删除
        entity.setIsDeleted(DeleteStatusEnum.DELETE.getCode());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdaterId(operatorId);

        boolean result = aimJobTypeService.updateById(entity);

        log.info("删除岗位类型成功, jobTypeId: {}", id);
        return result;
    }

    /**
     * 校验状态值是否有效
     *
     * @param status 状态值
     * @return 是否有效
     */
    private boolean isValidStatus(Integer status) {
        return StatusEnum.ENABLE.getCode().equals(status)
                || StatusEnum.DISABLE.getCode().equals(status);
    }
}
