---
trigger: always_on
description: 项目命名规范 - AIM 电商平台专用，定义包路径、服务名、表前缀、类命名模式
---

# 项目命名规范

## 1. 项目基本信息

| 属性 | 值 |
|------|-----|
| 项目名称 | aim-coding |
| 基础包路径 | `com.aim.mall` |
| 技术栈 | Java 21 + Spring Cloud |

## 2. 服务命名

### 2.1 服务列表

| 服务类型 | 服务名 | 路径前缀 | 说明 |
|----------|--------|----------|------|
| 门面-管理端 | mall-admin | `/admin/api/v1/` | 管理后台门面服务（供 AI 智能平台管理后台调用） |
| 门面-客户端 | mall-app | `/app/api/v1/` | 客户端门面服务（供 APP 端） |
| 门面-AI对话 | mall-chat | `/chat/api/v1/` | AI 对话门面服务（供 APP 端对话功能，直接调用大模型） |
| 应用服务 | mall-agent | `/inner/api/v1/` | AI 智能体员工服务（核心业务） |
| 支撑服务 | mall-user | `/inner/api/v1/` | 用户服务 |

### 2.2 服务调用关系

```
前端应用
    ↓
API 网关
    ↓
┌─────────────┬─────────────┬─────────────┐
│  mall-admin │   mall-app  │  mall-chat  │  ← 门面服务层
└─────────────┴─────────────┴─────────────┘
    ↓                    ↓         ↓
    └──────────┬─────────┘         │
               ↓                   │
        ┌─────────────┐            │
        │  mall-agent │  ← 应用服务层（核心业务）
        └─────────────┘            │
               ↓                   │
        ┌─────────────┐            │
        │  mall-user  │  ← 支撑服务层
        └─────────────┘
```

**调用规则**：
- 门面服务（mall-admin/mall-app/mall-chat）可调用应用服务和支撑服务
- 应用服务（mall-agent）可调用支撑服务（mall-user）
- mall-chat 可直接调用大模型 API
- 禁止跨层级调用
- 所有跨服务调用通过 OpenFeign 实现

## 3. 数据库命名规范

### 3.1 表名命名

**表名格式**：`aim_{模块}_{业务名}`

| 组成部分 | 说明 | 示例 |
|---------|------|------|
| `aim_` | 固定前缀，标识 AIM 平台业务表 | - |
| `{模块}` | 业务模块名，小写，多个单词用下划线分隔 | `agent`、`user`、`product` |
| `{业务名}` | 业务实体名，小写，多个单词用下划线分隔 | `job_type`、`order_info` |

**示例**：

| 表名 | 说明 |
|------|------|
| `aim_agent_employee` | 智能员工主表 |
| `aim_agent_job_type` | 智能员工岗位类型表 |
| `aim_user_profile` | 用户资料表 |

**约束**：
- 表名必须使用小写字母和下划线
- 禁止使用数字开头或纯数字表名
- 禁止超过 64 个字符

### 3.2 DO 类命名

严格按表名转换为大驼峰 + DO 后缀：

| 表名 | DO 类名 |
|------|---------|
| `aim_agent` | `AimAgentDO` |
| `aim_agent_job_type` | `AimAgentJobTypeDO` |
| `aim_product_info` | `AimProductInfoDO` |

## 4. 类命名模式

### 4.1 应用服务层类命名

| 类型 | 命名模式 | 示例 |
|------|----------|------|
| 实体类 | `Aim{Name}DO` | `AimAgentDO`、`AimJobTypeDO` |
| MP Service | `Aim{Name}Service` | `AimAgentService` |
| Mapper | `Aim{Name}Mapper` | `AimAgentMapper` |
| QueryService | `{Name}QueryService` | `AgentQueryService` |
| ManageService | `{Name}ManageService` | `AgentManageService` |
| ApplicationService | `{Name}ApplicationService` | `AgentApplicationService` |

### 4.2 DTO/Request/Response 命名

| 对象类型 | 使用位置 | 命名模式 | 示例 |
|----------|----------|----------|------|
| Request | Controller层入参 | `{Name}Request` | `AgentCreateRequest` |
| Response | Controller层出参 | `{Name}Response` | `AgentResponse` |
| VO | 门面服务聚合 | `{Name}VO` | `AgentDetailVO` |
| Query | Service层查询参数 | `{Name}Query` / `{Name}PageQuery` / `{Name}ListQuery` | `AgentPageQuery` |
| DTO | Service层内部传输 | `{Name}DTO` | `AgentCreateDTO` |
| ApiRequest | API模块远程请求 | `{Name}ApiRequest` | `AgentCreateApiRequest` |
| ApiResponse | API模块远程响应 | `{Name}ApiResponse` | `AgentApiResponse` |

### 4.3 远程服务命名

**Feign 客户端命名**：`{模块名}RemoteService`

- 位于 `mall-{模块}-api` 模块的 `api/feign/` 包下
- 统一使用 `RemoteService` 后缀
- 示例：`AgentRemoteService`、`UserRemoteService`

## 5. 包结构规范

### 5.1 应用服务包结构

```
{服务名}/src/main/java/com/aim/mall/
├── {模块}/              # 业务模块（如：agent, user）
│   ├── {业务域}/        # 业务域（如：employee, order）
│   │   ├── controller/  # 接口层
│   │   │   └── inner/   # 内部接口（用于 Feign 调用）
│   │   ├── service/     # 应用层
│   │   │   ├── mp/                      # MyBatis-Plus 数据服务
│   │   │   │   └── AimXxxService.java   # 继承 IService<AimXxxDO>
│   │   │   ├── XxxApplicationService.java # 应用服务（业务编排）
│   │   │   ├── XxxQueryService.java     # 查询服务（只读）
│   │   │   └── XxxManageService.java    # 管理服务（增删改）
│   │   ├── mapper/      # 基础设施层
│   │   │   └── AimXxxMapper.java        # 原生 MyBatis
│   │   └── domain/      # 领域层
│   │       ├── entity/  # 实体类 (AimXxxDO)
│   │       ├── dto/     # 内部DTO (XxxDTO, XxxQuery)
│   │       ├── enums/   # 枚举类（非远程调用相关）
│   │       └── exception/ # 异常类
│   ├── config/          # 全局配置类（模块级共享）
│   └── constants/       # 全局常量（模块级共享）
```

### 5.2 API 模块包结构

```
repos/mall-inner-api/mall-agent-api/src/main/java/com/aim/mall/agent/
└── api/
    ├── dto/
    │   ├── request/     # 远程调用请求参数
    │   └── response/    # 远程调用响应值
    ├── enums/           # 远程接口使用的枚举
    └── feign/           # Feign 客户端
        └── AgentRemoteService.java
```

### 5.3 门面服务包结构

```
mall-admin/src/main/java/com/aim/mall/
├── admin/                               # 业务模块
│   ├── {业务域}/
│   │   ├── controller/
│   │   │   └── admin/                   # 管理端接口
│   │   └── domain/
│   │       └── dto/
│   │           ├── request/             # 前端请求参数（本地使用）
│   │           └── response/            # 前端响应值/VO（本地使用）
│   ├── config/                          # 全局配置（共享）
│   └── constants/                       # 全局常量（共享）
```

## 6. 错误码规范

错误码遵循 `SSMMTNNN` 格式：

| 段 | 含义 | 说明 |
|----|------|------|
| SS | 系统 | 20=Common, 30=Admin, 40=Agent |
| MM | 模块 | 01=User, 02=Order, 03=Employee |
| T | 类型 | 0=Success, 1=Client Error, 2=Server Error, 3=Business Error |
| NNN | 序号 | 顺序号 (001-999) |

**系统映射**：

| 系统 | 代码 |
|------|------|
| Common | 20 |
| Admin | 30 |
| Agent | 40 |

---

## 7. 相关规范引用

| 规范 | 路径 | 说明 |
|------|------|------|
| 通用编码规范 | `.qoder/rules/common-coding-standards.md` | Java 编码通用规范 |
| 通用架构规范 | `.qoder/rules/common-architecture-standards.md` | 分层架构通用规范 |
| 项目错误码规范 | `.qoder/rules/project-error-code-standards.md` | 错误码格式、系统/模块映射、生成规则 |
| 项目响应格式规范 | `.qoder/rules/project-common-result-standards.md` | CommonResult 使用规范 |
| 项目操作人ID规范 | `.qoder/rules/project-operator-id-standards.md` | 操作人ID获取与传递标准 |

---

## 版本历史

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0 | 2026-03-02 | AI Agent | 初始版本，整合项目特定命名规范 |

