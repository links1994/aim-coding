package com.aim.mall.agent.controller.inner;

import com.aim.mall.agent.api.dto.request.JobTypeCreateApiRequest;
import com.aim.mall.agent.api.dto.request.JobTypePageApiRequest;
import com.aim.mall.agent.api.dto.request.JobTypeStatusApiRequest;
import com.aim.mall.agent.api.dto.request.JobTypeUpdateApiRequest;
import com.aim.mall.agent.api.dto.response.JobTypeApiResponse;
import com.aim.mall.agent.employee.domain.dto.JobTypeCreateDTO;
import com.aim.mall.agent.employee.domain.dto.JobTypePageQuery;
import com.aim.mall.agent.employee.domain.dto.JobTypeStatusDTO;
import com.aim.mall.agent.employee.domain.dto.JobTypeUpdateDTO;
import com.aim.mall.agent.employee.domain.enums.ErrorCodeEnum;
import com.aim.mall.agent.employee.domain.exception.BusinessException;
import com.aim.mall.agent.employee.service.JobTypeApplicationService;
import com.aim.mall.common.api.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位类型内部接口（供Feign调用）
 *
 * @author AI Agent
 */
@Slf4j
@RestController
@RequestMapping("/inner/api/v1/job-types")
@RequiredArgsConstructor
@Tag(name = "岗位类型内部接口", description = "岗位类型管理内部接口（供Feign调用）")
public class JobTypeInnerController {

    private final JobTypeApplicationService jobTypeApplicationService;

    /**
     * 岗位类型分页列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    @PostMapping("/list")
    @Operation(summary = "岗位类型分页列表")
    public CommonResult<CommonResult.PageData<JobTypeApiResponse>> list(
            @RequestBody JobTypePageApiRequest request) {
        log.debug("岗位类型分页列表, request: {}", request);

        // 手动校验参数
        validatePageRequest(request);

        // Request 转换为 Query
        JobTypePageQuery query = new JobTypePageQuery();
        query.setKeyword(request.getKeyword());
        query.setPageNum(request.getPageNum());
        query.setPageSize(request.getPageSize());

        CommonResult<CommonResult.PageData<JobTypeApplicationService.JobTypeResponse>> result =
                jobTypeApplicationService.pageJobType(query);

        // 转换为 API Response
        List<JobTypeApiResponse> items = result.getData().getItems().stream()
                .map(this::convertToApiResponse)
                .collect(Collectors.toList());

        return CommonResult.pageSuccess(items, result.getData().getTotalCount());
    }

    /**
     * 创建岗位类型
     *
     * @param request 创建请求
     * @return 新创建记录ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建岗位类型")
    public CommonResult<Long> create(@RequestBody JobTypeCreateApiRequest request) {
        log.debug("创建岗位类型, request: {}", request);

        // 手动校验参数
        validateCreateRequest(request);

        // Request 转换为 DTO
        JobTypeCreateDTO dto = new JobTypeCreateDTO();
        dto.setName(request.getName());
        dto.setDescription(request.getDescription());
        dto.setStatus(request.getStatus());
        dto.setCreatorId(request.getOperatorId());

        Long jobTypeId = jobTypeApplicationService.createJobType(dto);
        log.info("创建岗位类型成功, jobTypeId: {}", jobTypeId);

        return CommonResult.success(jobTypeId);
    }

    /**
     * 更新岗位类型
     *
     * @param request 更新请求
     * @return 是否成功
     */
    @PutMapping("/update")
    @Operation(summary = "更新岗位类型")
    public CommonResult<Boolean> update(@RequestBody JobTypeUpdateApiRequest request) {
        log.debug("更新岗位类型, request: {}", request);

        // 手动校验参数
        validateUpdateRequest(request);

        // Request 转换为 DTO
        JobTypeUpdateDTO dto = new JobTypeUpdateDTO();
        dto.setId(request.getId());
        dto.setName(request.getName());
        dto.setDescription(request.getDescription());
        dto.setStatus(request.getStatus());
        dto.setUpdaterId(request.getOperatorId());

        boolean result = jobTypeApplicationService.updateJobType(dto);
        log.info("更新岗位类型成功, jobTypeId: {}", request.getId());

        return CommonResult.success(result);
    }

    /**
     * 状态变更（启用/禁用）
     *
     * @param request 状态更新请求
     * @return 是否成功
     */
    @PutMapping("/status")
    @Operation(summary = "状态变更（启用/禁用）")
    public CommonResult<Boolean> updateStatus(@RequestBody JobTypeStatusApiRequest request) {
        log.debug("更新岗位类型状态, request: {}", request);

        // 手动校验参数
        validateStatusRequest(request);

        // Request 转换为 DTO
        JobTypeStatusDTO dto = new JobTypeStatusDTO();
        dto.setId(request.getId());
        dto.setStatus(request.getStatus());
        dto.setUpdaterId(request.getOperatorId());

        boolean result = jobTypeApplicationService.updateStatus(dto);
        log.info("更新岗位类型状态成功, jobTypeId: {}, status: {}", request.getId(), request.getStatus());

        return CommonResult.success(result);
    }

    /**
     * 删除岗位类型
     *
     * @param id 岗位类型ID
     * @return 是否成功
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除岗位类型")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id,
                                        @RequestParam(value = "operatorId", required = false) Long operatorId) {
        log.debug("删除岗位类型, id: {}, operatorId: {}", id, operatorId);

        boolean result = jobTypeApplicationService.deleteJobType(id, operatorId);
        log.info("删除岗位类型成功, jobTypeId: {}", id);

        return CommonResult.success(result);
    }

    /**
     * 转换为 API Response
     *
     * @param response 内部Response
     * @return API Response
     */
    private JobTypeApiResponse convertToApiResponse(JobTypeApplicationService.JobTypeResponse response) {
        JobTypeApiResponse apiResponse = new JobTypeApiResponse();
        apiResponse.setId(response.getId());
        apiResponse.setName(response.getName());
        apiResponse.setCode(response.getCode());
        apiResponse.setDescription(response.getDescription());
        apiResponse.setStatus(response.getStatus());
        apiResponse.setSortOrder(response.getSortOrder());
        apiResponse.setEmployeeCount(response.getEmployeeCount());
        apiResponse.setCreateTime(response.getCreateTime());
        apiResponse.setUpdateTime(response.getUpdateTime());
        return apiResponse;
    }

    // ==================== 手动参数校验方法 ====================

    private void validatePageRequest(JobTypePageApiRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "请求参数不能为空");
        }
        if (request.getPageNum() == null || request.getPageNum() < 1) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "页码必须大于等于1");
        }
        if (request.getPageSize() == null || request.getPageSize() < 1 || request.getPageSize() > 100) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "每页数量必须在1-100之间");
        }
    }

    private void validateCreateRequest(JobTypeCreateApiRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "请求参数不能为空");
        }
        if (StringUtils.isBlank(request.getName())) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "岗位类型名称不能为空");
        }
        if (request.getName().length() > 50) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "岗位类型名称长度不能超过50个字符");
        }
        if (StringUtils.isNotBlank(request.getDescription()) && request.getDescription().length() > 200) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "岗位类型描述长度不能超过200个字符");
        }
        if (request.getStatus() == null || (request.getStatus() != 0 && request.getStatus() != 1)) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "状态值只能是0（禁用）或1（启用）");
        }
    }

    private void validateUpdateRequest(JobTypeUpdateApiRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "请求参数不能为空");
        }
        if (request.getId() == null || request.getId() <= 0) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "岗位类型ID不能为空且必须大于0");
        }
        if (StringUtils.isBlank(request.getName())) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "岗位类型名称不能为空");
        }
        if (request.getName().length() > 50) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "岗位类型名称长度不能超过50个字符");
        }
        if (StringUtils.isNotBlank(request.getDescription()) && request.getDescription().length() > 200) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "岗位类型描述长度不能超过200个字符");
        }
        if (request.getStatus() == null || (request.getStatus() != 0 && request.getStatus() != 1)) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "状态值只能是0（禁用）或1（启用）");
        }
    }

    private void validateStatusRequest(JobTypeStatusApiRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "请求参数不能为空");
        }
        if (request.getId() == null || request.getId() <= 0) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "岗位类型ID不能为空且必须大于0");
        }
        if (request.getStatus() == null || (request.getStatus() != 0 && request.getStatus() != 1)) {
            throw new BusinessException(ErrorCodeEnum.AGENT_PARAM_ERROR, "状态值只能是0（禁用）或1（启用）");
        }
    }
}
