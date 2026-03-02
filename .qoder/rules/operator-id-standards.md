---
spec_type: 操作人ID传递规范
description: 定义门面服务和应用服务中获取操作人ID的标准方式
version: v1.0
scope: 所有服务模块（mall-admin/mall-app/mall-agent/mall-user等）
status: active
created_at: 2026-03-02
updated_at: 2026-03-02
author: AI Agent
reviewer: -
---

# 操作人ID传递规范

## 概述

### 目的

统一项目中操作人ID的获取和传递方式，确保用户身份信息的正确传递和解析。

### 背景

在微服务架构中，操作人ID需要在不同服务间传递：
- **门面服务**（mall-admin/mall-app）：通过 HTTP Header 接收用户信息
- **应用服务**（mall-agent/mall-user）：通过方法参数接收操作人ID

### 适用场景

- 创建操作需要记录创建人
- 更新操作需要记录更新人
- 业务操作需要获取当前登录用户ID

### 适用范围

- **门面服务**: mall-admin, mall-app, mall-chat
- **应用服务**: mall-agent, mall-user
- **生效时间**: 2026-03-02

---

## 规范内容

### 1. 门面服务获取操作人ID

#### 1.1 规范描述

门面服务通过 HTTP Header 接收用户信息，使用 `UserInfoUtil` 工具类解析获取用户ID。

#### 1.2 参数定义

| 参数 | 来源 | 类型 | 说明 |
|------|------|------|------|
| `user` | Request Header | String | 用户信息的 JSON 字符串（URL编码） |
| Header 名称 | `AuthConstant.USER_TOKEN_HEADER` | - | 常量值为 `"user"` |

#### 1.3 工具类使用

**依赖引入**:

```java
import com.aim.mall.common.utils.UserInfoUtil;
import com.aim.mall.common.constant.AuthConstant;
```

**获取用户ID**:

```java
Long userId = UserInfoUtil.getUserInfo(user).getId();
```

#### 1.4 正确示例

```java
import com.aim.mall.common.utils.UserInfoUtil;
import com.aim.mall.common.constant.AuthConstant;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/admin/api/v1/job-type")
@Tag(name = "岗位类型接口")
@Slf4j
public class JobTypeController {

    private final JobTypeRemoteService jobTypeRemoteService;

    @PostMapping("/create")
    @Operation(summary = "创建岗位类型")
    public CommonResult<Long> createJobType(
            @RequestBody @Valid JobTypeCreateRequest request,
            @RequestHeader(AuthConstant.USER_TOKEN_HEADER) String user) {
        
        // 解析获取用户ID
        Long userId = UserInfoUtil.getUserInfo(user).getId();
        
        // 转换为 API 请求对象，传入操作人ID
        JobTypeCreateApiRequest apiRequest = new JobTypeCreateApiRequest();
        apiRequest.setCode(request.getCode());
        apiRequest.setName(request.getName());
        apiRequest.setDescription(request.getDescription());
        apiRequest.setOperatorId(userId);  // 设置操作人ID
        
        return jobTypeRemoteService.createJobType(apiRequest);
    }

    @PostMapping("/update")
    @Operation(summary = "更新岗位类型")
    public CommonResult<Void> updateJobType(
            @RequestBody @Valid JobTypeUpdateRequest request,
            @RequestHeader(AuthConstant.USER_TOKEN_HEADER) String user) {
        
        Long userId = UserInfoUtil.getUserInfo(user).getId();
        
        JobTypeUpdateApiRequest apiRequest = new JobTypeUpdateApiRequest();
        apiRequest.setId(request.getId());
        apiRequest.setName(request.getName());
        apiRequest.setDescription(request.getDescription());
        apiRequest.setOperatorId(userId);  // 设置操作人ID
        
        return jobTypeRemoteService.updateJobType(apiRequest);
    }
}
```

#### 1.5 错误示例

```java
// ❌ 错误：未使用 AuthConstant，硬编码 header 名称
@PostMapping("/create")
public CommonResult<Long> createJobType(
        @RequestBody @Valid JobTypeCreateRequest request,
        @RequestHeader("user") String user) {  // 错误：应使用 AuthConstant.USER_TOKEN_HEADER
    ...
}

// ❌ 错误：在门面服务中不传递操作人ID，让应用服务自己解析
@PostMapping("/create")
public CommonResult<Long> createJobType(
        @RequestBody @Valid JobTypeCreateRequest request,
        @RequestHeader(AuthConstant.USER_TOKEN_HEADER) String user) {
    // 错误：应该解析 user 并传递给应用服务
    return jobTypeRemoteService.createJobType(request);
}
```

---

### 2. 应用服务接收操作人ID

#### 2.1 规范描述

应用服务通过方法参数接收操作人ID，**不直接解析 HTTP Header**。

#### 2.2 参数传递方式

| 场景 | 传递方式 | 说明 |
|------|----------|------|
| Feign 调用 | RequestBody 对象中的 `operatorId` 字段 | 通过 ApiRequest 对象传递 |
| 内部方法调用 | 方法参数直接传递 | `Long operatorId` |

#### 2.3 API 请求对象定义

```java
@Data
public class JobTypeCreateApiRequest implements Serializable {
    private static final long serialVersionUID = -1L;
    
    private String code;
    private String name;
    private String description;
    
    // 操作人ID，由门面服务传入
    private Long operatorId;
}
```

#### 2.4 正确示例

**InnerController 接收**:

```java
@RestController
@RequestMapping("/inner/api/v1/job-type")
@Tag(name = "岗位类型内部接口")
@Slf4j
public class JobTypeInnerController {

    private final JobTypeManageService jobTypeManageService;

    @PostMapping("/create")
    @Operation(summary = "创建岗位类型")
    public CommonResult<Long> createJobType(
            @RequestBody @Valid JobTypeCreateApiRequest request) {
        
        // 从请求对象中获取操作人ID
        Long operatorId = request.getOperatorId();
        
        // 转换为 DTO，传入操作人ID
        JobTypeCreateDTO dto = new JobTypeCreateDTO();
        dto.setCode(request.getCode());
        dto.setName(request.getName());
        dto.setDescription(request.getDescription());
        dto.setOperatorId(operatorId);
        
        Long jobTypeId = jobTypeManageService.createJobType(dto);
        return CommonResult.success(jobTypeId);
    }
}
```

**ManageService 处理**:

```java
@Service
@Slf4j
public class JobTypeManageService {

    private final AimJobTypeService aimJobTypeService;

    @Transactional(rollbackFor = Exception.class)
    public Long createJobType(JobTypeCreateDTO dto) {
        log.debug("创建岗位类型, dto: {}", dto);

        // 参数校验
        validateCreateRequest(dto);

        // 转换为实体
        AimJobTypeDO entity = new AimJobTypeDO();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus(JobTypeStatusEnum.ENABLED.getCode());
        
        // 设置操作人信息
        entity.setCreateBy(dto.getOperatorId());
        entity.setUpdateBy(dto.getOperatorId());

        aimJobTypeService.save(entity);

        log.info("创建岗位类型成功, jobTypeId: {}, operatorId: {}", 
                entity.getId(), dto.getOperatorId());
        return entity.getId();
    }
}
```

#### 2.5 错误示例

```java
// ❌ 错误：应用服务直接解析 Header
@PostMapping("/create")
public CommonResult<Long> createJobType(
        @RequestBody @Valid JobTypeCreateApiRequest request,
        @RequestHeader(AuthConstant.USER_TOKEN_HEADER) String user) {  // 错误：应用服务不应直接解析 Header
    Long userId = UserInfoUtil.getUserInfo(user).getId();
    ...
}

// ❌ 错误：不传递 operatorId，导致无法记录操作人
@PostMapping("/create")
public CommonResult<Long> createJobType(
        @RequestBody @Valid JobTypeCreateApiRequest request) {
    // 错误：request 中没有 operatorId 字段
    Long jobTypeId = jobTypeManageService.createJobType(request);
    return CommonResult.success(jobTypeId);
}
```

---

### 3. UserInfoUtil 工具类说明

#### 3.1 类位置

```
repos/mall-common/src/main/java/com/aim/mall/common/utils/UserInfoUtil.java
```

#### 3.2 主要方法

| 方法 | 返回值 | 说明 |
|------|--------|------|
| `getUserInfo(String user)` | `UserHeaderInfoDTO` | 解析用户信息字符串 |

#### 3.3 用户信息结构

```java
@Data
public static class UserHeaderInfoDTO {
    private Long id;           // 用户ID
    private String device_id;  // 设备ID
    private String user_name;  // 用户名
    private Long merchant_id;  // 商家ID
    private Long partner_id;   // 合作商ID
    private String client_id;  // 客户端ID
}
```

#### 3.4 异常处理

`UserInfoUtil.getUserInfo()` 在以下情况会抛出 `ApiException`：
- 用户信息为空
- 用户信息解析失败

门面服务无需额外捕获，由全局异常处理器统一处理。

---

### 4. 流程对比

#### 4.1 门面服务 vs 应用服务

| 维度 | 门面服务（mall-admin/mall-app） | 应用服务（mall-agent/mall-user） |
|------|-------------------------------|--------------------------------|
| **获取方式** | `@RequestHeader` + `UserInfoUtil` | 方法参数接收 |
| **Header 解析** | ✅ 需要 | ❌ 不需要 |
| **参数传递** | 解析后通过 ApiRequest 传递 | 从 ApiRequest 获取 |
| **职责** | 解析用户信息并传递 | 执行业务逻辑，记录操作人 |

#### 4.2 数据流向

```
前端请求
    ↓ [Header: user={encoded_user_info}]
门面服务 (mall-admin/mall-app)
    ↓ 解析: UserInfoUtil.getUserInfo(user).getId()
    ↓ [ApiRequest.operatorId]
Feign 调用
    ↓ [RequestBody: JobTypeCreateApiRequest]
应用服务 (mall-agent/mall-user)
    ↓ 获取: request.getOperatorId()
    ↓ [DTO.operatorId]
Service 层
    ↓ 设置: entity.setCreateBy(operatorId)
数据库
```

---

## 检查清单

### 代码审查时检查

- [ ] 门面服务使用 `@RequestHeader(AuthConstant.USER_TOKEN_HEADER)` 接收用户信息
- [ ] 门面服务使用 `UserInfoUtil.getUserInfo(user).getId()` 解析用户ID
- [ ] ApiRequest 对象包含 `operatorId` 字段
- [ ] 应用服务从 ApiRequest 获取 operatorId，不直接解析 Header
- [ ] Service 层正确设置 `createBy` / `updateBy` 字段

### 架构评审时检查

- [ ] 门面服务和应用服务职责划分清晰
- [ ] 用户信息传递链路完整
- [ ] 所有写操作都记录操作人

---

## 相关规范

### 依赖规范

- [Java 编码规范](./coding-standards.md) - 详细编码规范
- [架构规范](./architecture-standards.md) - 系统架构规范
- [CommonResult 响应格式规范](./common-result-spec.md) - 响应格式规范

### 引用代码

- [UserInfoUtil.java](../../repos/mall-common/src/main/java/com/aim/mall/common/utils/UserInfoUtil.java) - 用户信息工具类
- [AuthConstant.java](../../repos/mall-common/src/main/java/com/aim/mall/common/constant/AuthConstant.java) - 认证常量

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
| 门面服务 | 直接面向前端的服务，如 mall-admin, mall-app |
| 应用服务 | 核心业务服务，如 mall-agent, mall-user |
| UserInfoUtil | 用户信息解析工具类 |
| operatorId | 操作人ID字段名 |

### 规范维护

- **维护责任人**: 技术团队
- **审核周期**: 每季度
- **反馈渠道**: 技术讨论群
- **上次审核**: 2026-03-02
