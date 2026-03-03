package com.aim.mall.basic.api.feign;

import com.aim.mall.basic.api.dto.request.IdGenApiRequest;
import com.aim.mall.basic.api.dto.response.IdGenApiResponse;
import com.aim.mall.common.api.CommonResult;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * ID生成远程服务（Feign客户端）
 *
 * @author AI Agent
 */
@FeignClient(name = "mall-basic", contextId = "idGenRemoteService")
public interface IdGenRemoteService {

    /**
     * 生成分布式ID编码
     *
     * @param request ID生成请求
     * @return 生成的编码
     */
    @PostMapping("/inner/api/v1/idgen/generate")
    CommonResult<IdGenApiResponse> generate(@RequestBody @Valid IdGenApiRequest request);
}
