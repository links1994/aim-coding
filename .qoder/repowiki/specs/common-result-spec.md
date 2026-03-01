---
spec_type: 响应格式规范
description: 定义项目统一响应对象 CommonResult 的结构、使用场景和最佳实践
version: v1.0
scope: 所有服务模块（mall-admin/mall-app/mall-agent/mall-user等）
status: active
created_at: 2026-03-02
updated_at: 2026-03-02
author: AI Agent
reviewer: -
---

# CommonResult 响应格式规范

## 概述

### 目的

统一项目所有 API 接口的响应格式，确保前后端交互的一致性，便于错误处理和日志追踪。

### 背景

在微服务架构中，各服务模块需要统一的响应格式来：
- 简化前端错误处理逻辑
- 规范分页数据返回格式
- 提供标准化的成功/失败响应

### 适用场景

- 所有 Controller 层接口返回
- Feign 远程调用响应
- 全局异常处理响应
- 分页查询结果返回

### 适用范围

- **适用对象**: 所有服务模块（mall-admin, mall-app, mall-agent, mall-user 等）
- **生效时间**: 2026-03-02
- **替代规范**: 无

---

## 规范内容

### 1. CommonResult 结构定义

#### 1.1 类定义

**规则描述**:

`CommonResult<T>` 是泛型类，包含以下字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| code | long | 响应状态码，参考 ResultCode 枚举 |
| message | String | 响应消息 |
| data | T | 响应数据，泛型类型 |

**正确示例**:

```java
package com.aim.mall.common.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

import java.util.List;

/**
 * 通用返回对象
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonResult<T> {
    private long code;
    private String message;
    private T data;

    protected CommonResult() {
    }

    protected CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    // ... 工厂方法
}
```

#### 1.2 PageData 分页数据结构

**规则描述**:

分页查询使用 `CommonResult.PageData<T>` 作为 data 类型：

| 字段 | 类型 | 说明 |
|------|------|------|
| totalCount | Long | 总记录数 |
| items | List<T> | 分页数据列表 |

**正确示例**:

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public static class PageData<T> {
    /**
     * 总记录数
     */
    private Long totalCount;
    /**
     * 分页数据
     */
    private List<T> items;
}
```

### 2. 工厂方法使用规范

#### 2.1 成功响应

**规则描述**:

| 方法 | 使用场景 | 返回类型 |
|------|----------|----------|
| `success(T data)` | 返回成功数据 | `CommonResult<T>` |
| `success()` | 返回成功无数据 | `CommonResult<T>` |
| `success(T data, String message)` | 返回成功数据和自定义消息 | `CommonResult<T>` |
| `pageSuccess(List<T> data, Long totalCount)` | 分页成功响应 | `CommonResult<PageData<T>>` |

**正确示例**:

```java
// 返回单个对象
@GetMapping("/{id}")
public CommonResult<AgentResponse> getAgentById(@PathVariable("id") Long id) {
    AgentResponse agent = agentService.getById(id);
    return CommonResult.success(agent);
}

// 返回成功无数据
@PostMapping("/delete/{id}")
public CommonResult<Void> deleteAgent(@PathVariable("id") Long id) {
    agentService.deleteById(id);
    return CommonResult.success();
}

// 返回成功数据和自定义消息
@PostMapping("/create")
public CommonResult<Long> createAgent(@RequestBody AgentCreateRequest request) {
    Long agentId = agentService.createAgent(request);
    return CommonResult.success(agentId, "智能员工创建成功");
}

// 分页响应
@GetMapping("/page")
public CommonResult<CommonResult.PageData<AgentResponse>> pageAgent(
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "10") Integer pageSize) {
    Page<AgentResponse> page = agentService.pageAgent(pageNum, pageSize);
    return CommonResult.pageSuccess(page.getRecords(), page.getTotal());
}
```

#### 2.2 失败响应

**规则描述**:

| 方法 | 使用场景 | 返回类型 |
|------|----------|----------|
| `failed(IErrorCode errorCode)` | 使用错误码枚举返回失败 | `CommonResult<T>` |
| `failed(IErrorCode errorCode, String message)` | 错误码 + 自定义消息 | `CommonResult<T>` |
| `failed(String message)` | 仅使用错误消息 | `CommonResult<T>` |
| `failed()` | 默认失败 | `CommonResult<T>` |
| `validateFailed()` | 参数验证失败 | `CommonResult<T>` |
| `validateFailed(String message)` | 参数验证失败 + 自定义消息 | `CommonResult<T>` |
| `unauthorized(T data)` | 未登录 | `CommonResult<T>` |
| `forbidden(T data)` | 未授权 | `CommonResult<T>` |
| `inputIllegal()` | 输入非法 | `CommonResult<T>` |

**正确示例**:

```java
// 全局异常处理器中使用
@ExceptionHandler(BusinessException.class)
public CommonResult<Void> handleBusinessException(BusinessException e) {
    return CommonResult.failed(e.getErrorCode(), e.getMessage());
}

@ExceptionHandler(MethodArgumentNotValidException.class)
public CommonResult<Void> handleValidationException(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
    return CommonResult.validateFailed(message);
}
```

### 3. 使用场景规范

#### 3.1 Controller 层返回

**规则描述**:

- **所有 Controller 方法必须返回 `CommonResult<T>`**
- 数据转换（DO → Response）在 Service 层完成
- Controller 层职责：参数转换 + 调用 Service + 直接返回

**正确示例**:

```java
@RestController
@RequestMapping("/admin/api/v1/agent")
@Tag(name = "智能员工接口")
public class AgentController {

    @GetMapping("/page")
    @Operation(summary = "分页查询智能员工")
    public CommonResult<CommonResult.PageData<AgentResponse>> pageAgent(
            @RequestBody @Valid AgentListRequest request) {
        // Controller 层只做参数转换
        AgentPageQuery query = convertToQuery(request);
        
        // Service 层完成 DO 到 Response 的转换
        return agentService.pageAgent(query);
    }
}
```

**错误示例**:

```java
// 错误：直接返回原始对象
@GetMapping("/{id}")
public AgentResponse getAgentById(@PathVariable("id") Long id) {
    return agentService.getById(id);
}

// 错误：Controller 层做数据转换
@GetMapping("/page")
public CommonResult<CommonResult.PageData<AgentResponse>> pageAgent(
        @RequestBody @Valid AgentListRequest request) {
    Page<AimAgentDO> page = agentService.page(request);
    List<AgentResponse> responses = page.getRecords().stream()
            .map(this::convertToResponse)  // 错误：应在 Service 层转换
            .collect(Collectors.toList());
    return CommonResult.pageSuccess(responses, page.getTotal());
}
```

#### 3.2 Feign 远程调用

**规则描述**:

- Feign 接口返回 `CommonResult<T>`
- API 模块定义 RemoteService，使用 `ApiRequest` / `ApiResponse` 后缀

**正确示例**:

```java
// API 模块定义
@FeignClient(name = "mall-agent", path = "/inner/api/v1/job-type")
public interface JobTypeRemoteService {

    @GetMapping("/detail")
    CommonResult<JobTypeApiResponse> getById(@RequestParam("id") Long id);

    @PostMapping("/page")
    CommonResult<CommonResult.PageData<JobTypeApiResponse>> pageJobType(
            @RequestBody @Valid JobTypePageApiRequest request);
}
```

### 4. 错误码规范

**规则描述**:

错误码遵循 `SSMMTNNN` 格式：

| 段 | 含义 | 说明 |
|----|------|------|
| SS | 系统 | 20=Common, 30=Admin, 40=Agent |
| MM | 模块 | 01=User, 02=Order, 03=Employee |
| T | 类型 | 0=Success, 1=Client Error, 2=Server Error, 3=Business Error |
| NNN | 序号 | 顺序号 (001-999) |

**示例**:

| Code | 含义 |
|------|------|
| 2000000 | Success |
| 2001001 | User not found |
| 4003001 | Employee already exists |

---

## 示例说明

### 完整示例

#### 场景: 智能员工管理接口

**需求描述**:
实现智能员工的增删改查接口，统一使用 CommonResult 返回。

**实现代码**:

```java
@RestController
@RequestMapping("/admin/api/v1/agent")
@Tag(name = "智能员工接口", description = "智能员工管理相关接口")
@Slf4j
public class AgentController {

    private final AgentService agentService;

    @PostMapping("/create")
    @Operation(summary = "创建智能员工")
    public CommonResult<Long> createAgent(
            @RequestBody @Valid AgentCreateRequest request) {
        log.debug("创建智能员工, request: {}", request);
        Long agentId = agentService.createAgent(request);
        log.info("创建智能员工成功, agentId: {}", agentId);
        return CommonResult.success(agentId);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询智能员工")
    public CommonResult<CommonResult.PageData<AgentResponse>> pageAgent(
            @RequestBody @Valid AgentListRequest request) {
        AgentPageQuery query = convertToQuery(request);
        return agentService.pageAgent(query);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询智能员工")
    public CommonResult<AgentResponse> getAgentById(@PathVariable("id") Long id) {
        return agentService.getById(id);
    }

    @PostMapping("/update")
    @Operation(summary = "更新智能员工")
    public CommonResult<Void> updateAgent(
            @RequestBody @Valid AgentUpdateRequest request) {
        agentService.updateAgent(request);
        return CommonResult.success();
    }

    @PostMapping("/delete/{id}")
    @Operation(summary = "删除智能员工")
    public CommonResult<Void> deleteAgent(@PathVariable("id") Long id) {
        agentService.deleteById(id);
        return CommonResult.success();
    }

    private AgentPageQuery convertToQuery(AgentListRequest request) {
        AgentPageQuery query = new AgentPageQuery();
        query.setKeyword(request.getKeyword());
        query.setStatus(request.getStatus());
        query.setPageNum(request.getPageNum());
        query.setPageSize(request.getPageSize());
        return query;
    }
}
```

**关键要点**:
1. 所有方法返回 `CommonResult<T>`
2. 分页查询使用 `CommonResult.PageData<T>`
3. 无数据返回使用 `CommonResult<Void>`
4. Controller 层负责参数转换，Service 层负责数据转换

---

## 检查清单

### 代码审查时检查

- [ ] 所有 Controller 方法返回 `CommonResult<T>`
- [ ] 分页查询使用 `CommonResult.pageSuccess()`
- [ ] 成功响应使用 `CommonResult.success()`
- [ ] 失败响应使用 `CommonResult.failed()` 或具体错误方法
- [ ] Service 层完成 DO 到 Response 的转换
- [ ] Feign 接口使用 `ApiRequest` / `ApiResponse` 后缀

### 架构评审时检查

- [ ] 错误码符合 `SSMMTNNN` 格式
- [ ] 响应格式前后端一致

---

## 相关规范

### 依赖规范

- [Java 编码规范](./04-coding-standards.md) - 详细编码规范
- [架构规范](./05-architecture-standards.md) - 系统架构规范

### 引用规范

- [错误码规范](./error-code-spec.md) - 错误码定义和使用

### 冲突处理

如与本规范冲突，按以下优先级处理：
1. 项目整体架构规范
2. 本规范
3. 各模块自定义规范

---

## 版本历史

| 版本 | 日期 | 修改人 | 修改内容 | 审核人 |
|------|------|--------|----------|--------|
| v1.0 | 2026-03-02 | AI Agent | 初始版本 | - |

---

## 附录

### 术语表

| 术语 | 定义 |
|------|------|
| CommonResult | 通用响应对象，统一 API 返回格式 |
| PageData | 分页数据结构，包含总记录数和数据列表 |
| MDC | Mapped Diagnostic Context，日志上下文 |

### 参考资料

- [mall-common/CommonResult.java](../../repos/mall-common/src/main/java/com/aim/mall/common/api/CommonResult.java) - 源代码
- [Java 编码规范](./04-coding-standards.md) - 项目编码规范

### 规范维护

- **维护责任人**: 技术团队
- **审核周期**: 每季度
- **反馈渠道**: 技术讨论群
- **上次审核**: 2026-03-02
