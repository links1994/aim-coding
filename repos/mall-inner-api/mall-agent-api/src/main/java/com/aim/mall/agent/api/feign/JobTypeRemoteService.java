package com.aim.mall.agent.api.feign;

import com.aim.mall.agent.api.dto.request.JobTypeCreateApiRequest;
import com.aim.mall.agent.api.dto.request.JobTypePageApiRequest;
import com.aim.mall.agent.api.dto.request.JobTypeStatusApiRequest;
import com.aim.mall.agent.api.dto.request.JobTypeUpdateApiRequest;
import com.aim.mall.agent.api.dto.response.JobTypeApiResponse;
import com.aim.mall.common.api.CommonResult;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 岗位类型远程服务（Feign客户端）
 *
 * @author AI Agent
 */
@FeignClient(name = "mall-agent", contextId = "jobTypeRemoteService")
public interface JobTypeRemoteService {

    /**
     * 岗位类型分页列表
     *
     * @param request 查询请求
     * @return 分页结果
     */
    @PostMapping("/inner/api/v1/job-types/list")
    CommonResult<CommonResult.PageData<JobTypeApiResponse>> list(
            @RequestBody @Valid JobTypePageApiRequest request);

    /**
     * 创建岗位类型
     *
     * @param request 创建请求
     * @return 新创建记录ID
     */
    @PostMapping("/inner/api/v1/job-types/create")
    CommonResult<Long> create(@RequestBody @Valid JobTypeCreateApiRequest request);

    /**
     * 更新岗位类型
     *
     * @param request 更新请求
     * @return 是否成功
     */
    @PutMapping("/inner/api/v1/job-types/update")
    CommonResult<Boolean> update(@RequestBody @Valid JobTypeUpdateApiRequest request);

    /**
     * 状态变更（启用/禁用）
     *
     * @param request 状态更新请求
     * @return 是否成功
     */
    @PutMapping("/inner/api/v1/job-types/status")
    CommonResult<Boolean> updateStatus(@RequestBody @Valid JobTypeStatusApiRequest request);

    /**
     * 删除岗位类型
     *
     * @param id 岗位类型ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    @DeleteMapping("/inner/api/v1/job-types/delete")
    CommonResult<Boolean> delete(@RequestParam("id") Long id,
                                 @RequestParam(value = "operatorId", required = false) Long operatorId);
}
