package com.aim.mall.agent.employee.service.impl;

import com.aim.mall.agent.employee.domain.dto.JobTypeCreateDTO;
import com.aim.mall.agent.employee.domain.dto.JobTypePageQuery;
import com.aim.mall.agent.employee.domain.dto.JobTypeStatusDTO;
import com.aim.mall.agent.employee.domain.dto.JobTypeUpdateDTO;
import com.aim.mall.agent.employee.domain.entity.AimJobTypeDO;
import com.aim.mall.agent.employee.domain.enums.AgentErrorCodeEnum;
import com.aim.mall.agent.employee.domain.exception.BusinessException;
import com.aim.mall.agent.employee.mapper.AimJobTypeMapper;
import com.aim.mall.agent.employee.service.JobTypeApplicationService;
import com.aim.mall.agent.employee.service.JobTypeManageService;
import com.aim.mall.agent.employee.service.JobTypeQueryService;
import com.aim.mall.basic.api.dto.request.IdGenApiRequest;
import com.aim.mall.basic.api.dto.response.IdGenApiResponse;
import com.aim.mall.basic.api.feign.BasicRemoteService;
import com.aim.mall.common.api.CommonResult;
import com.aim.mall.common.enums.DeleteStatusEnum;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位类型应用服务实现
 * <p>
 * 业务编排，协调查询和管理服务
 *
 * @author AI Agent
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobTypeApplicationServiceImpl implements JobTypeApplicationService {

    private final JobTypeQueryService jobTypeQueryService;
    private final JobTypeManageService jobTypeManageService;
    private final BasicRemoteService basicRemoteService;
    private final AimJobTypeMapper aimJobTypeMapper;

    @Override
    public CommonResult<CommonResult.PageData<JobTypeResponse>> pageJobType(JobTypePageQuery query) {
        log.debug("业务域：分页查询岗位类型列表");

        Page<AimJobTypeDO> page = jobTypeQueryService.pageJobType(query);

        // DO 转换为 Response
        List<JobTypeResponse> items = page.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return CommonResult.pageSuccess(items, page.getTotal());
    }

    @Override
    public Long createJobType(JobTypeCreateDTO dto) {
        log.debug("业务域：创建岗位类型");

        // 生成岗位类型编码：J + 年 + 6位序号（如 J2026000001）
        String code = generateJobTypeCode();
        dto.setCode(code);

        // 生成排序号：当前最大值 + 1
        Integer sortOrder = generateSortOrder();
        dto.setSortOrder(sortOrder);

        return jobTypeManageService.createJobType(dto);
    }

    /**
     * 生成排序号
     * <p>
     * 规则：查询当前最大排序号，新记录排序号 = 最大值 + 1
     * 若表为空，则从 1 开始
     *
     * @return 生成的排序号
     */
    private Integer generateSortOrder() {
        Integer maxSortOrder = aimJobTypeMapper.selectMaxSortOrder();
        int sortOrder = (maxSortOrder != null) ? maxSortOrder + 1 : 1;
        log.debug("生成排序号成功, sortOrder: {}", sortOrder);
        return sortOrder;
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

    @Override
    public boolean updateJobType(JobTypeUpdateDTO dto) {
        log.debug("业务域：更新岗位类型");
        return jobTypeManageService.updateJobType(dto);
    }

    @Override
    public boolean updateStatus(JobTypeStatusDTO dto) {
        log.debug("业务域：更新岗位类型状态");
        return jobTypeManageService.updateStatus(dto);
    }

    @Override
    public boolean deleteJobType(Long id, Long operatorId) {
        log.debug("业务域：删除岗位类型");
        return jobTypeManageService.deleteJobType(id, operatorId);
    }

    @Override
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
}
