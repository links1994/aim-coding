# 技术规格书 - P-2026-001-REQ-018

## 1. 概述

### 1.1 目标

实现 mall-admin 管理后台的岗位类型管理接口组，作为门面层对外暴露管理后台接口，通过 Feign 调用 mall-agent 服务完成实际业务操作。

### 1.2 背景

- **来源**: PRD 第 5.2 节，线框图 A-002/A-003
- **所属需求**: REQ-018（mall-admin 门面层）
- **前置依赖**: REQ-038（mall-agent 岗位类型基础服务）- 已完成
- **被依赖**: 无

### 1.3 服务分层定位

```
前端应用（管理后台）
    ↓
┌─────────────────────────────────────────┐
│  mall-admin (门面服务) - 本需求实现层      │
│  - 参数校验、响应包装、权限控制            │
│  - 通过 Feign 调用 mall-agent           │
└─────────────────────────────────────────┘
                    ↓ Feign
┌─────────────────────────────────────────┐
│  mall-agent (应用服务) - REQ-038 已实现    │
│  - 业务逻辑、数据操作、编码生成            │
└─────────────────────────────────────────┘
```

### 1.4 技术选型理由

- **服务分层**: 门面服务层，遵循门面服务职责规范
  - 负责参数校验和响应包装
  - 不负责业务逻辑，业务逻辑下沉到 mall-agent
- **跨服务调用**: OpenFeign 调用 mall-agent 服务
- **编码规范**: 遵循 [common-coding-standards.md](../../../../.qoder/rules/common-coding-standards.md) Java 编码规范
  - 使用 Lombok 注解（`@Data`, `@Slf4j` 等）
  - DTO 转换使用手动 convert 方法，禁止使用 `BeanUtils.copyProperties`

---

## 2. API 接口定义

### 2.1 接口概览

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/admin/api/v1/job-types` | 岗位类型列表（分页） |
| POST | `/admin/api/v1/job-types` | 创建岗位类型 |
| PUT | `/admin/api/v1/job-types/{jobTypeId}` | 更新岗位类型 |
| PUT | `/admin/api/v1/job-types/{jobTypeId}/status` | 状态变更（启用/禁用） |

### 2.2 接口详情

#### 2.2.1 岗位类型列表

**接口信息**
- **路径**: `GET /admin/api/v1/job-types`
- **说明**: 查询岗位类型列表，支持分页和关键词搜索

**请求参数**

| 参数名 | 类型 | 必填 | 位置 | 说明 |
|--------|------|------|------|------|
| keyword | String | 否 | Query | 关键词（匹配名称/编码） |
| pageNum | Integer | 否 | Query | 页码，默认1 |
| pageSize | Integer | 否 | Query | 每页大小，默认10 |

**请求示例**

```
GET /admin/api/v1/job-types?keyword=销售&pageNum=1&pageSize=10
```

**响应数据**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "id": 1,
        "name": "销售顾问",
        "code": "J2026000001",
        "description": "负责商品销售推广",
        "status": 1,
        "sortOrder": 1,
        "employeeCount": 0,
        "createTime": "2026-03-01 10:00:00",
        "updateTime": "2026-03-01 10:00:00"
      }
    ],
    "totalCount": 1
  }
}
```

> **注意**: 分页数据遵循 [CommonResult 响应格式规范](../../../../.qoder/rules/project-common-result-standards.md)，使用 `items` 和 `totalCount` 字段。

#### 2.2.2 创建岗位类型

**接口信息**
- **路径**: `POST /admin/api/v1/job-types`
- **说明**: 创建新的岗位类型

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 岗位类型名称（1-64字符） |
| description | String | 否 | 岗位描述（0-255字符） |
| status | Integer | 否 | 状态（0-禁用，1-启用），默认1 |

**请求示例**

```json
{
  "name": "销售顾问",
  "description": "负责商品销售推广",
  "status": 1
}
```

**响应数据**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1
  }
}
```

> **排序号生成规则**: sortOrder 由后端自动生成，规则为当前最大排序号 + 1

#### 2.2.3 更新岗位类型

**接口信息**
- **路径**: `PUT /admin/api/v1/job-types/{jobTypeId}`
- **说明**: 更新岗位类型信息

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| jobTypeId | Long | 是 | 岗位类型ID |

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 岗位类型名称（1-64字符） |
| description | String | 否 | 岗位描述（0-255字符） |
| status | Integer | 否 | 状态（0-禁用，1-启用） |

**请求示例**

```json
{
  "name": "高级销售顾问",
  "description": "负责大客户销售",
  "status": 1
}
```

**响应数据**

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

> **注意**: 更新接口不修改排序号，sortOrder 保持不变

#### 2.2.4 状态变更

**接口信息**
- **路径**: `PUT /admin/api/v1/job-types/{jobTypeId}/status`
- **说明**: 启用或禁用岗位类型

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| jobTypeId | Long | 是 | 岗位类型ID |

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 是 | 状态（0-禁用，1-启用） |

**请求示例**

```json
{
  "status": 0
}
```

**响应数据**

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 2.3 Feign 调用映射

mall-admin 通过 Feign 调用 mall-agent 服务，接口映射关系如下：

| mall-admin 接口 | mall-agent 接口 | Feign 方法 |
|----------------|----------------|-----------|
| GET /admin/api/v1/job-types | POST /inner/api/v1/job-types/list | JobTypeRemoteService.list() |
| POST /admin/api/v1/job-types | POST /inner/api/v1/job-types/create | JobTypeRemoteService.create() |
| PUT /admin/api/v1/job-types/{id} | PUT /inner/api/v1/job-types/update | JobTypeRemoteService.update() |
| PUT /admin/api/v1/job-types/{id}/status | PUT /inner/api/v1/job-types/status | JobTypeRemoteService.updateStatus() |

**Feign 客户端定义**（位于 mall-agent-api 模块）：

```java
@FeignClient(name = "mall-agent", contextId = "jobTypeRemoteService")
public interface JobTypeRemoteService {
    
    @PostMapping("/inner/api/v1/job-types/list")
    CommonResult<CommonResult.PageData<JobTypeApiResponse>> list(
            @RequestBody @Valid JobTypeListApiRequest request);
    
    @PostMapping("/inner/api/v1/job-types/create")
    CommonResult<Long> create(@RequestBody @Valid JobTypeCreateApiRequest request);
    
    @PutMapping("/inner/api/v1/job-types/update")
    CommonResult<Boolean> update(@RequestBody @Valid JobTypeUpdateApiRequest request);
    
    @PutMapping("/inner/api/v1/job-types/status")
    CommonResult<Boolean> updateStatus(@RequestBody @Valid JobTypeStatusApiRequest request);
}
```

### 2.4 错误码规范

遵循 [project-error-code-standards.md](../../../../.qoder/rules/project-error-code-standards.md) 错误码规范：

| 错误码 | 描述 | HTTP 状态 | 使用场景 |
|--------|------|-----------|----------|
| 30011001 | 参数错误 | 200 | 通用参数校验失败 |
| 30011002 | 岗位类型名称不能为空 | 200 | name为空或空字符串 |
| 30011003 | 岗位类型名称长度超限 | 200 | name长度超过64字符 |
| 30011004 | 状态值无效 | 200 | status 不是 0 或 1 |
| 30012001 | 岗位类型不存在 | 200 | 查询/更新的ID不存在 |
| 30015001 | 调用岗位类型服务失败 | 200 | Feign 调用 mall-agent 失败 |

**错误码格式说明**: `SSMMTNNN`
- SS=30: Admin 系统
- MM=01: 岗位类型模块（自定义）
- T=1: 客户端错误 / T=2: 业务错误 / T=5: 服务调用错误
- NNN: 顺序号

---

## 3. 架构设计

### 3.1 服务调用关系

```
前端（管理后台）
    ↓ HTTP
mall-admin (门面服务)
    ├── JobTypeAdminController      # 接收请求，参数校验
    ├── JobTypeFeignClient          # Feign 客户端（继承 JobTypeRemoteService）
    └── Request/Response DTO        # mall-admin 层 DTO
    ↓ Feign
mall-agent (应用服务)
    ├── JobTypeInnerController      # Inner API 控制器
    ├── JobTypeApplicationService   # 应用服务（业务编排）
    ├── JobTypeQueryService         # 查询服务
    ├── JobTypeManageService        # 管理服务
    └── AimJobTypeService           # MP 数据服务
```

### 3.2 时序图

#### 查询岗位类型列表

```
前端    JobTypeAdminController    JobTypeFeignClient    JobTypeRemoteService    JobTypeInnerController
 |              |                       |                      |                        |
 |--list()---->|                       |                      |                        |
 |              |--list()------------->|                      |                        |
 |              |                       |--list()------------>|                        |
 |              |                       |                      |--list()--------------->|
 |              |                       |                      |                        |--查询数据
 |              |                       |                      |<-----------------------|
 |              |                       |<--PageData-----------|                        |
 |              |<--PageData-----------|                      |                        |
 |<-response---|                       |                      |                        |
```

#### 创建岗位类型

```
前端    JobTypeAdminController    JobTypeFeignClient    JobTypeRemoteService    JobTypeInnerController    JobTypeApplicationService
 |              |                       |                      |                        |                        |
 |--create()-->|                       |                      |                        |                        |
 |              |--create()----------->|                      |                        |                        |
 |              |                       |--create()---------->|                        |                        |
 |              |                       |                      |--create()------------>|                        |
 |              |                       |                      |                        |--create()------------>|
 |              |                       |                      |                        |                        |--生成编码
 |              |                       |                      |                        |                        |--保存数据
 |              |                       |                      |                        |<-----------------------|
 |              |                       |                      |<-----------------------|                        |
 |              |                       |<--id-----------------|                        |                        |
 |              |<--id-----------------|                      |                        |                        |
 |<-response---|                       |                      |                        |                        |
```

### 3.3 代码结构

遵循 [project-naming-standards.md](../../../../.qoder/rules/project-naming-standards.md) 包结构规范：

```
mall-admin/src/main/java/com/aim/mall/admin/
└── jobtype/                                    # 岗位类型模块
    ├── controller/
    │   └── JobTypeAdminController.java         # Admin 接口控制器
    ├── domain/
    │   └── dto/
    │       ├── request/
    │       │   ├── JobTypeListRequest.java     # 列表查询请求
    │       │   ├── JobTypeCreateRequest.java   # 创建请求
    │       │   ├── JobTypeUpdateRequest.java   # 更新请求
    │       │   └── JobTypeStatusRequest.java   # 状态变更请求
    │       └── response/
    │           ├── JobTypeResponse.java        # 岗位类型响应
    │           └── JobTypePageResponse.java    # 分页响应（包装 items/totalCount）
    └── feign/
        └── JobTypeFeignClient.java             # Feign 客户端
```

**命名规范说明**:
- Request 类: `JobTypeListRequest`, `JobTypeCreateRequest`（大驼峰 + Request 后缀）
- Response 类: `JobTypeResponse`, `JobTypePageResponse`（大驼峰 + Response 后缀）
- Feign 客户端: `JobTypeFeignClient`（继承 `JobTypeRemoteService`）
- Controller: `JobTypeAdminController`（Admin 后缀标识门面层）

---

## 4. DTO 设计

### 4.1 Request DTO

**JobTypeListRequest** - 列表查询请求
```java
@Data
public class JobTypeListRequest {
    private String keyword;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
```

**JobTypeCreateRequest** - 创建请求
```java
@Data
public class JobTypeCreateRequest {
    @NotBlank(message = "岗位类型名称不能为空")
    @Size(max = 64, message = "岗位类型名称长度不能超过64字符")
    private String name;
    
    @Size(max = 255, message = "岗位描述长度不能超过255字符")
    private String description;
    
    private Integer status = 1;  // 默认启用
}
```

**JobTypeUpdateRequest** - 更新请求
```java
@Data
public class JobTypeUpdateRequest {
    @NotBlank(message = "岗位类型名称不能为空")
    @Size(max = 64, message = "岗位类型名称长度不能超过64字符")
    private String name;
    
    @Size(max = 255, message = "岗位描述长度不能超过255字符")
    private String description;
    
    private Integer status;  // 不传则不修改状态
}
```

**JobTypeStatusRequest** - 状态变更请求
```java
@Data
public class JobTypeStatusRequest {
    @NotNull(message = "状态不能为空")
    @Pattern(regexp = "[01]", message = "状态只能是0或1")
    private Integer status;
}
```

### 4.2 Response DTO

**JobTypeResponse** - 岗位类型响应
```java
@Data
public class JobTypeResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer status;
    private Integer sortOrder;
    private Integer employeeCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

**JobTypePageResponse** - 分页响应
```java
@Data
public class JobTypePageResponse {
    private List<JobTypeResponse> items;
    private Long totalCount;
}
```

### 4.3 DTO 转换

mall-admin 层负责将 Request 转换为 ApiRequest，将 ApiResponse 转换为 Response：

```java
// Request -> ApiRequest 转换示例
private JobTypeCreateApiRequest convertToApiRequest(JobTypeCreateRequest request) {
    JobTypeCreateApiRequest apiRequest = new JobTypeCreateApiRequest();
    apiRequest.setName(request.getName());
    apiRequest.setDescription(request.getDescription());
    apiRequest.setSortOrder(request.getSortOrder());
    return apiRequest;
}

// ApiResponse -> Response 转换示例
private JobTypeResponse convertToResponse(JobTypeApiResponse apiResponse) {
    JobTypeResponse response = new JobTypeResponse();
    response.setId(apiResponse.getId());
    response.setName(apiResponse.getName());
    // ... 其他字段
    return response;
}
```

---

## 5. 业务规则实现

### 5.1 门面服务职责

mall-admin 作为门面服务，职责边界如下：

| 职责 | mall-admin | mall-agent |
|------|-----------|-----------|
| 参数校验 | ✅ 基础格式校验 | ✅ 业务规则校验 |
| 响应包装 | ✅ CommonResult 包装 | ✅ 内部数据封装 |
| 权限控制 | ✅ 登录态校验 | ❌ 不处理 |
| 业务逻辑 | ❌ 不下沉 | ✅ 业务编排 |
| 数据操作 | ❌ 不处理 | ✅ 数据库访问 |
| 编码生成 | ❌ 不处理 | ✅ ID 生成服务调用 |

### 5.2 校验规则

| 校验项 | 规则 | 错误码 | 处理位置 |
|--------|------|--------|----------|
| 名称必填 | name != null && !name.isBlank() | 30011002 | mall-admin |
| 名称长度 | 1 <= name.length() <= 64 | 30011003 | mall-admin |
| 描述长度 | description == null \|\| description.length() <= 255 | 30011001 | mall-admin |
| 状态范围 | status == null \|\| (status == 0 \|\| status == 1) | 30011004 | mall-admin |
| ID 存在性 | 记录存在且未删除 | 30012001 | mall-agent |

### 5.3 业务逻辑流程

#### 5.3.1 列表查询流程

1. **Controller 层**: 接收 Query 参数，转换为 `JobTypeListRequest`
2. **参数校验**: 校验 pageNum、pageSize 范围
3. **DTO 转换**: 将 `JobTypeListRequest` 转换为 `JobTypeListApiRequest`
4. **Feign 调用**: 调用 `JobTypeRemoteService.list()`
5. **响应转换**: 将 `JobTypeApiResponse` 转换为 `JobTypeResponse`
6. **包装返回**: 使用 `CommonResult.success()` 包装响应

#### 5.3.2 创建流程

1. **Controller 层**: 接收 `@RequestBody @Valid JobTypeCreateRequest`
2. **参数校验**: 自动触发 Bean Validation（name 必填、长度限制、status 范围）
3. **DTO 转换**: 将 `JobTypeCreateRequest` 转换为 `JobTypeCreateApiRequest`
   - status 不传时默认为 1（启用）
   - sortOrder 不传，由 mall-agent 自动生成（当前最大排序号 + 1）
4. **Feign 调用**: 调用 `JobTypeRemoteService.create()`
5. **响应处理**: 返回创建成功的 ID

#### 5.3.3 更新流程

1. **Controller 层**: 接收路径参数 `jobTypeId` 和 `@RequestBody @Valid JobTypeUpdateRequest`
2. **参数校验**: 自动触发 Bean Validation
3. **DTO 转换**: 将 `JobTypeUpdateRequest` 转换为 `JobTypeUpdateApiRequest`，设置 ID
   - status 不传时不修改状态
   - sortOrder 不传，保持原值不变
4. **Feign 调用**: 调用 `JobTypeRemoteService.update()`
5. **响应处理**: 返回更新结果

#### 5.3.4 状态变更流程

1. **Controller 层**: 接收路径参数 `jobTypeId` 和 `@RequestBody @Valid JobTypeStatusRequest`
2. **参数校验**: 校验 status 只能是 0 或 1
3. **DTO 转换**: 构建 `JobTypeStatusApiRequest`
4. **Feign 调用**: 调用 `JobTypeRemoteService.updateStatus()`
5. **响应处理**: 返回变更结果

---

## 6. 验收标准

- [ ] 岗位列表接口支持分页查询，返回关联员工数
- [ ] 新增岗位接口支持创建岗位类型
- [ ] 编辑岗位接口支持修改岗位信息
- [ ] 启用/禁用接口支持状态切换
- [ ] 所有接口通过 HTTP 测试验证
- [ ] 代码符合项目规范，通过质量检查

---

## 7. 附录

### 7.1 相关文档

- [PROGRAM.md](../PROGRAM.md) - 任务定义
- [decomposition.md](../../P-2026-001-decomposition/workspace/decomposition.md) - 需求分解
- [REQ-038 技术规格](../P-2026-001-REQ-038/artifacts/tech-spec.md) - 下游服务设计
- [common-coding-standards.md](../../../../.qoder/rules/common-coding-standards.md) - Java 编码规范
- [common-architecture-standards.md](../../../../.qoder/rules/common-architecture-standards.md) - 架构规范
- [project-naming-standards.md](../../../../.qoder/rules/project-naming-standards.md) - 项目命名规范
- [project-error-code-standards.md](../../../../.qoder/rules/project-error-code-standards.md) - 错误码规范
- [project-common-result-standards.md](../../../../.qoder/rules/project-common-result-standards.md) - 响应格式规范

### 7.2 需求追溯矩阵

| 子需求 | 设计章节 | API 接口 | 实现类 |
|--------|----------|----------|--------|
| REQ-018 | 2.2, 3.2, 4.3, 5.3 | /admin/api/v1/job-types/* | JobTypeAdminController, JobTypeFeignClient |

### 7.3 变更影响分析

| 变更类型 | 影响范围 | 是否需更新需求文档 | 更新目标 |
|----------|----------|-------------------|----------|
| 接口路径变更 | API 设计 | 是 | decomposition.md |
| 校验规则变更 | 业务逻辑 | 是 | 本规格书 |
| Feign 接口变更 | 跨服务调用 | 是 | 本规格书、REQ-038 |

### 7.4 变更记录

| 版本 | 日期 | 变更内容 | 变更人 |
|------|------|----------|--------|
| 1.0 | 2026-03-03 | 初始版本，按照新规范重构 | AI Agent |

---
