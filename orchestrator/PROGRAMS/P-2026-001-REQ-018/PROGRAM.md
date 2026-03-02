# P-2026-001-REQ-018: 岗位类型管理服务（mall-admin 门面层）

## 目标

实现 mall-admin 管理后台的岗位类型管理接口组，包括岗位列表、新增岗位、编辑岗位、启用/禁用功能。

## 背景

### 需求来源
- **PRD**: 第 5.2 节
- **线框图**: A-002/A-003
- **所属需求**: REQ-018（mall-admin 门面层）

### 业务场景
管理后台需要管理智能员工的岗位类型。岗位类型是智能员工的基础配置数据，用于用户申请成为智能员工时选择岗位。

### 依赖关系
- **前置依赖**: REQ-038（mall-agent 岗位类型基础服务）
- **被依赖**: 无

### 服务分层
```
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

## 方案

### 整体思路

1. **mall-agent 层**（REQ-038）：提供岗位类型的基础 CRUD 和状态管理 Inner 接口
2. **mall-admin 层**（本需求）：通过 Feign 调用 mall-agent 服务，对外暴露管理后台接口

### 接口设计

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 岗位列表 | GET | `/admin/api/v1/job-types` | 分页查询，支持关键词搜索 |
| 新增岗位 | POST | `/admin/api/v1/job-types` | 创建岗位类型 |
| 编辑岗位 | PUT | `/admin/api/v1/job-types/{jobTypeId}` | 修改岗位信息 |
| 启用/禁用 | PUT | `/admin/api/v1/job-types/{jobTypeId}/status` | 状态切换 |

### 关键设计决策

- **mall-admin 作为门面服务**：负责参数校验、响应包装、权限控制
- **业务逻辑下沉**：所有业务逻辑和数据操作在 mall-agent 中实现
- **跨服务调用**：通过 mall-inner-api/mall-agent-api 定义 Feign 接口
- **DTO 转换**：mall-admin 层使用 Request/Response 对象，通过 Feign 调用转换为 ApiRequest/ApiResponse

## 涉及文件

### mall-admin（门面层）- 本需求实现

| 文件路径 | 说明 |
|----------|------|
| `repos/mall-admin/src/main/java/com/aim/mall/admin/jobtype/controller/JobTypeAdminController.java` | 岗位管理 Controller |
| `repos/mall-admin/src/main/java/com/aim/mall/admin/jobtype/domain/dto/request/JobTypeListRequest.java` | 列表查询请求 |
| `repos/mall-admin/src/main/java/com/aim/mall/admin/jobtype/domain/dto/request/JobTypeCreateRequest.java` | 创建岗位请求 |
| `repos/mall-admin/src/main/java/com/aim/mall/admin/jobtype/domain/dto/request/JobTypeUpdateRequest.java` | 更新岗位请求 |
| `repos/mall-admin/src/main/java/com/aim/mall/admin/jobtype/domain/dto/request/JobTypeStatusRequest.java` | 状态变更请求 |
| `repos/mall-admin/src/main/java/com/aim/mall/admin/jobtype/domain/dto/response/JobTypeResponse.java` | 岗位响应 |
| `repos/mall-admin/src/main/java/com/aim/mall/admin/jobtype/domain/dto/response/JobTypePageResponse.java` | 分页响应 |
| `repos/mall-admin/src/main/java/com/aim/mall/admin/jobtype/feign/JobTypeFeignClient.java` | Feign 客户端 |

### mall-agent-api（API 定义层）- 由 REQ-038 提供（只读引用）

| 文件路径 | 说明 |
|----------|------|
| `repos/mall-inner-api/mall-agent-api/src/main/java/com/aim/mall/agent/api/feign/JobTypeRemoteService.java` | Feign 客户端接口 |
| `repos/mall-inner-api/mall-agent-api/src/main/java/com/aim/mall/agent/api/dto/request/JobTypeListApiRequest.java` | 列表查询请求 |
| `repos/mall-inner-api/mall-agent-api/src/main/java/com/aim/mall/agent/api/dto/request/JobTypeCreateApiRequest.java` | 创建请求 |
| `repos/mall-inner-api/mall-agent-api/src/main/java/com/aim/mall/agent/api/dto/request/JobTypeUpdateApiRequest.java` | 更新请求 |
| `repos/mall-inner-api/mall-agent-api/src/main/java/com/aim/mall/agent/api/dto/request/JobTypeStatusApiRequest.java` | 状态变更请求 |
| `repos/mall-inner-api/mall-agent-api/src/main/java/com/aim/mall/agent/api/dto/response/JobTypeApiResponse.java` | 岗位类型响应 |

### mall-agent（应用服务层）- 由 REQ-038 提供（只读引用）

| 文件路径 | 说明 |
|----------|------|
| `repos/mall-agent/src/main/java/com/aim/mall/agent/employee/controller/inner/JobTypeInnerController.java` | Inner API Controller |
| `repos/mall-agent/src/main/java/com/aim/mall/agent/employee/service/JobTypeApplicationService.java` | 应用服务（业务编排） |
| `repos/mall-agent/src/main/java/com/aim/mall/agent/employee/service/JobTypeQueryService.java` | 查询服务 |
| `repos/mall-agent/src/main/java/com/aim/mall/agent/employee/service/JobTypeManageService.java` | 管理服务 |
| `repos/mall-agent/src/main/java/com/aim/mall/agent/employee/domain/entity/AimJobTypeDO.java` | 岗位类型实体 |

## 验收标准

- [ ] 岗位列表接口支持分页查询，返回关联员工数
- [ ] 新增岗位接口支持创建岗位类型
- [ ] 编辑岗位接口支持修改岗位信息
- [ ] 启用/禁用接口支持状态切换
- [ ] 所有接口通过 HTTP 测试验证
- [ ] 代码符合项目规范，通过质量检查

## 相关文档

- [技术规格书](./artifacts/tech-spec.md) - 详细技术设计
- [decomposition.md](../P-2026-001-decomposition/workspace/decomposition.md) - 需求分解
- [REQ-038 技术规格](../P-2026-001-REQ-038/artifacts/tech-spec.md) - 下游服务设计
