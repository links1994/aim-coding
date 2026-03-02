package com.aim.mall.agent.service;

import com.aim.mall.agent.employee.domain.dto.JobTypeCreateDTO;
import com.aim.mall.agent.employee.domain.dto.JobTypeStatusDTO;
import com.aim.mall.agent.employee.domain.dto.JobTypeUpdateDTO;
import com.aim.mall.agent.employee.domain.entity.AimJobTypeDO;
import com.aim.mall.agent.employee.service.mp.AimJobTypeService;
import com.aim.mall.common.enums.DeleteStatusEnum;
import com.aim.mall.common.enums.StatusEnum;
import com.aim.mall.common.exception.BusinessException;
import com.aim.mall.common.exception.ErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 岗位类型管理服务
 * <p>
 * 增删改操作，封装写逻辑
 *
 * @author AI Agent
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobTypeManageService {

    private final AimJobTypeService aimJobTypeService;

    /**
     * 创建岗位类型
     *
     * @param dto 创建DTO
     * @return 新创建记录ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createJobType(JobTypeCreateDTO dto) {
        log.debug("创建岗位类型开始, dto: {}", dto);

        // 校验编码是否已存在
        if (aimJobTypeService.isCodeExists(dto.getCode())) {
            log.warn("岗位类型编码已存在, code: {}", dto.getCode());
            throw new BusinessException(ErrorCodeEnum.JOB_TYPE_CODE_EXISTS, "岗位类型编码已存在");
        }

        // 创建实体
        AimJobTypeDO entity = new AimJobTypeDO();
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        entity.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        entity.setStatus(StatusEnum.ENABLE.getCode()); // 默认启用
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setIsDeleted(DeleteStatusEnum.UNDELETE.getCode());
        entity.setCreatorId(dto.getCreatorId());

        aimJobTypeService.save(entity);

        log.info("创建岗位类型成功, jobTypeId: {}, code: {}", entity.getId(), entity.getCode());
        return entity.getId();
    }

    /**
     * 更新岗位类型
     *
     * @param dto 更新DTO
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateJobType(JobTypeUpdateDTO dto) {
        log.debug("更新岗位类型开始, dto: {}", dto);

        // 查询记录是否存在
        AimJobTypeDO entity = aimJobTypeService.getById(dto.getId());
        if (entity == null || DeleteStatusEnum.DELETE.getCode().equals(entity.getIsDeleted())) {
            log.warn("岗位类型不存在或已删除, id: {}", dto.getId());
            throw new BusinessException(ErrorCodeEnum.JOB_TYPE_NOT_FOUND, "岗位类型不存在");
        }

        // 更新字段（code不允许修改）
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdaterId(dto.getUpdaterId());

        boolean result = aimJobTypeService.updateById(entity);

        log.info("更新岗位类型成功, jobTypeId: {}", dto.getId());
        return result;
    }

    /**
     * 更新岗位类型状态
     *
     * @param dto 状态更新DTO
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(JobTypeStatusDTO dto) {
        log.debug("更新岗位类型状态开始, id: {}, status: {}", dto.getId(), dto.getStatus());

        // 校验状态值是否有效
        if (!isValidStatus(dto.getStatus())) {
            log.warn("无效的状态值, status: {}", dto.getStatus());
            throw new BusinessException(ErrorCodeEnum.PARAM_ERROR, "无效的状态值");
        }

        // 查询记录是否存在
        AimJobTypeDO entity = aimJobTypeService.getById(dto.getId());
        if (entity == null || DeleteStatusEnum.DELETE.getCode().equals(entity.getIsDeleted())) {
            log.warn("岗位类型不存在或已删除, id: {}", dto.getId());
            throw new BusinessException(ErrorCodeEnum.JOB_TYPE_NOT_FOUND, "岗位类型不存在");
        }

        // 更新状态
        entity.setStatus(dto.getStatus());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setUpdaterId(dto.getUpdaterId());

        boolean result = aimJobTypeService.updateById(entity);

        log.info("更新岗位类型状态成功, jobTypeId: {}, status: {}", dto.getId(), dto.getStatus());
        return result;
    }

    /**
     * 删除岗位类型
     *
     * @param id 岗位类型ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteJobType(Long id, Long operatorId) {
        log.debug("删除岗位类型开始, id: {}", id);

        // 查询记录是否存在
        AimJobTypeDO entity = aimJobTypeService.getById(id);
        if (entity == null || DeleteStatusEnum.DELETE.getCode().equals(entity.getIsDeleted())) {
            log.warn("岗位类型不存在或已删除, id: {}", id);
            throw new BusinessException(ErrorCodeEnum.JOB_TYPE_NOT_FOUND, "岗位类型不存在");
        }

        // TODO: REQ-038 待智能员工需求完成后实现
        // 调用 EmployeeService 查询岗位类型关联的员工数量
        // Integer employeeCount = employeeService.countByJobTypeId(id);
        // if (employeeCount > 0) {
        //     throw new BusinessException(ErrorCodeEnum.JOB_TYPE_HAS_EMPLOYEES, "岗位类型有关联员工，无法删除");
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
