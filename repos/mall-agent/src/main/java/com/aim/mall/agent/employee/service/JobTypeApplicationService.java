package com.aim.mall.agent.service;

import com.aim.mall.agent.employee.domain.dto.JobTypeCreateDTO;
import com.aim.mall.agent.employee.domain.dto.JobTypePageQuery;
import com.aim.mall.agent.employee.domain.dto.JobTypeStatusDTO;
import com.aim.mall.agent.employee.domain.dto.JobTypeUpdateDTO;
import com.aim.mall.agent.employee.domain.entity.AimJobTypeDO;
import com.aim.mall.basic.api.dto.request.IdGenApiRequest;
import com.aim.mall.basic.api.dto.response.IdGenApiResponse;
import com.aim.mall.basic.api.feign.IdGenRemoteService;
import com.aim.mall.common.api.CommonResult;
import com.aim.mall.common.enums.DeleteStatusEnum;
import com.aim.mall.common.exception.BusinessException;
import com.aim.mall.common.exception.ErrorCodeEnum;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位类型应用服务
 * <p>
 * 业务编排，协调查询和管理服务
 *
 * @author AI Agent
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobTypeApplicationService {

    private final JobTypeQueryService jobTypeQueryService;
    private final JobTypeManageService jobTypeManageService;
    private final IdGenRemoteService idGenRemoteService;

    /**
     * 分页查询岗位类型列表
     *
     * @param query 查询参数
     * @return 分页结果（包含Response转换）
     */
    public CommonResult<CommonResult.PageData<JobTypeResponse>> pageJobType(JobTypePageQuery query) {
        log.debug("业务域：分页查询岗位类型列表");

        Page<AimJobTypeDO> page = jobTypeQueryService.pageJobType(query);

        // DO 转换为 Response
        List<JobTypeResponse> items = page.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return CommonResult.pageSuccess(items, page.getTotal());
    }

    /**
     * 创建岗位类型
     *
     * @param dto 创建DTO
     * @return 新创建记录ID
     */
    public Long createJobType(JobTypeCreateDTO dto) {
        log.debug("业务域：创建岗位类型");

        // 生成岗位类型编码：J + 年 + 6位序号（如 J2026000001）
        String code = generateJobTypeCode();
        dto.setCode(code);

        return jobTypeManageService.createJobType(dto);
    }

    /**
     * 生成岗位类型编码
     * <p>
     * 格式：J + 年(yyyy) + 6位序号（如 J2026000001）
     *
     * @return 生成的编码
     */
    private String generateJobTypeCode() {
        IdGenApiRequest request = new IdGenApiRequest();
        request.setPrefix("J");
        request.setDatePattern("yyyy");

        CommonResult<IdGenApiResponse> result = idGenRemoteService.generate(request);
        if (result == null || result.getData() == null) {
            log.error("生成岗位类型编码失败，远程调用返回空");
            throw new BusinessException(ErrorCodeEnum.ID_GENERATE_ERROR, "生成岗位类型编码失败");
        }

        String code = result.getData().getCode();
        log.debug("生成岗位类型编码成功, code: {}", code);
        return code;
    }

    /**
     * 更新岗位类型
     *
     * @param dto 更新DTO
     * @return 是否成功
     */
    public boolean updateJobType(JobTypeUpdateDTO dto) {
        log.debug("业务域：更新岗位类型");
        return jobTypeManageService.updateJobType(dto);
    }

    /**
     * 更新岗位类型状态
     *
     * @param dto 状态更新DTO
     * @return 是否成功
     */
    public boolean updateStatus(JobTypeStatusDTO dto) {
        log.debug("业务域：更新岗位类型状态");
        return jobTypeManageService.updateStatus(dto);
    }

    /**
     * 删除岗位类型
     *
     * @param id 岗位类型ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    public boolean deleteJobType(Long id, Long operatorId) {
        log.debug("业务域：删除岗位类型");
        return jobTypeManageService.deleteJobType(id, operatorId);
    }

    /**
     * 根据ID查询岗位类型
     *
     * @param id 岗位类型ID
     * @return 岗位类型响应
     */
    public JobTypeResponse getById(Long id) {
        log.debug("业务域：根据ID查询岗位类型, id: {}", id);
        AimJobTypeDO entity = jobTypeQueryService.getById(id);
        if (entity == null || DeleteStatusEnum.DELETE.getCode().equals(entity.getIsDeleted())) {
            return null;
        }
        return convertToResponse(entity);
    }

    /**
     * DO 转换为 Response
     *
     * @param entity 岗位类型实体
     * @return 岗位类型响应
     */
    private JobTypeResponse convertToResponse(AimJobTypeDO entity) {
        JobTypeResponse response = new JobTypeResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setCode(entity.getCode());
        response.setDescription(entity.getDescription());
        response.setStatus(entity.getStatus());
        response.setSortOrder(entity.getSortOrder());
        response.setCreateTime(entity.getCreateTime());
        response.setUpdateTime(entity.getUpdateTime());

        // TODO: REQ-038 待智能员工需求完成后实现
        // 调用 EmployeeService 查询岗位类型关联的员工数量
        // Integer employeeCount = employeeService.countByJobTypeId(entity.getId());
        // response.setEmployeeCount(employeeCount);
        response.setEmployeeCount(0);

        return response;
    }

    /**
     * 岗位类型响应内部类
     */
    @lombok.Data
    public static class JobTypeResponse {
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
