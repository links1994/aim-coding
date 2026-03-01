---
name: java-code-generation
description: 根据项目规范生成 Java 微服务代码。在用户说"生成代码"、"委托：生成代码"或技术规格书已确认并准备实现时使用。支持 mall-admin、mall-app、mall-agent、mall-user 服务，具有正确的分层和 Feign 集成。
---

# Java 代码生成 Skill

基于技术规格书生成符合项目规范的 Java 微服务代码。

> **强制规范**：此 Skill 生成的代码必须严格遵循以下规范（按优先级排序）：
> 1. **编码规范**：`.qoder/rules/04-coding-standards.md` - 命名约定、响应格式、异常处理等
> 2. **架构规范**：`.qoder/rules/05-architecture-standards.md` - 分层架构、服务职责、接口风格等
>
> **生成代码前必须先读取上述规范文件**，确保代码符合项目标准。

---

## 触发条件

- 技术规格书（design.md）已生成并确认
- 用户明确指令："生成代码" 或 "委托：生成代码"
- 主 Agent 在第 4 阶段调用

---

## 输入

- `orchestrator/PROGRAMS/{program_id}/workspace/tech-spec.md` — 技术规格书
- `orchestrator/PROGRAMS/{program_id}/workspace/openapi.yaml` — API 定义
- `orchestrator/ALWAYS/RESOURCE-MAP.yml` — 项目资源映射
- `orchestrator/PROGRAMS/{program_id}/SCOPE.yml` — 写入范围控制
- `.qoder/rules/04-coding-standards.md` — 编码规范
- `.qoder/rules/05-architecture-standards.md` — 架构规范

---

## 输出

- 每个服务的 Java 源代码 → `{service}/src/main/java/`
- 代码生成报告 `orchestrator/PROGRAMS/{program_id}/workspace/code-generation-report.md`

---

## 工作流程

### 步骤 1：读取配置和知识库查询（自动复用现有代码）

1. **读取输入文档**
   - 读取技术规格书（tech-spec.md）
   - 读取 API 定义（openapi.yaml）
   - 读取项目资源映射（RESOURCE-MAP.yml）
   - 读取写入范围（SCOPE.yml）
   - **必须先读取** 编码规范和架构规范

2. **知识库查询（自动复用现有代码）**

   **使用 knowledge-base-query Skill 查询**：
   ```
   查询类型：feature
   关键词：{当前功能关键词}
   
   目的：
   - 查询类似功能的代码结构
   - 复用现有的 Controller/Service/Mapper 模式
   - 参考现有的 DTO/VO 定义
   ```

   **查询类型和目的：**

   | 查询类型 | 目的 | 复用内容 |
   |------------|---------|---------------|
   | feature | 类似功能归档 | Controller/Service/Mapper 代码结构 |
   | schema | 数据库表结构 | 现有字段定义，避免命名冲突 |
   | api | 内部/外部 API | Feign 接口定义，第三方 API 调用模式 |
   | pitfall | 历史陷阱 | 避免已知陷阱（如 Feign 客户端重复创建） |

   **查询结果处理**：
   - 如果类似功能有代码 → 参考其代码结构，复用可复用的代码片段
   - 如果没有类似功能 → 按照规范生成新代码

### 步骤 2：服务分析

分析每个服务：

- 确定服务类型（门面服务 / 应用服务 / 支撑服务）
- 确定要生成的代码层（Controller/Service/Mapper/Entity/DTO/Feign）
- 确定依赖关系（哪些服务需要 Feign 调用）

### 步骤 3：代码生成

并行按服务生成代码，遵循规范：

#### 包结构

**应用服务（mall-agent）**：

```
mall-agent/src/main/java/com/aim/mall/
├── agent/               # 业务模块
│   ├── controller/      # 接口层
│   │   └── inner/       # 内部接口（用于 Feign 调用）
│   ├── service/         # 应用层
│   │   ├── XxxDomainService.java    # 业务域服务（业务编排）
│   │   ├── XxxQueryService.java     # 查询服务（只读）
│   │   ├── XxxManageService.java    # 管理服务（增删改）
│   │   └── mp/                      # MyBatis-Plus 数据服务
│   │       └── AimXxxService.java   # 继承 IService<AimXxxDO>
│   ├── mapper/          # 基础设施层
│   │   └── AimXxxMapper.java        # 原生 MyBatis
│   └── domain/          # 领域层
│       ├── entity/      # 实体类（AimXxxDO）
│       ├── dto/         # 内部 DTO（XxxDTO、XxxQuery）
│       ├── enums/       # 内部枚举
│       └── exception/   # 异常类
├── config/              # 全局配置类（共享）
└── constants/           # 全局常量（共享）
```

**API 模块（mall-agent-api）- 统一在 mall-inner-api 目录**：

```
repos/mall-inner-api/mall-agent-api/src/main/java/com/aim/mall/agent/
└── api/
    ├── dto/
    │   ├── request/     # 远程调用请求参数
    │   └── response/    # 远程调用响应值
    ├── enums/           # 远程接口使用的枚举
    └── feign/           # Feign 客户端（命名：{业务名}RemoteService）
        └── JobTypeRemoteService.java
```

**门面服务（mall-admin）**：

```
mall-admin/src/main/java/com/aim/mall/
├── admin/               # 业务模块
│   ├── controller/      # 接口层
│   │   └── admin/       # 管理接口
│   └── domain/          # 领域层
│       └── dto/
│           ├── request/ # 前端请求参数（本地使用，不对外暴露）
│           └── response/# 前端响应值/VO（本地使用，不对外暴露）
├── config/              # 全局配置类（共享）
└── constants/           # 全局常量（共享）

注意：门面服务禁止相互调用，因此不存在 feign 目录。门面服务通过 mall-inner-api 中的 RemoteService 调用应用服务。
```

#### 对象类型规范

| 对象类型 | 模块 | 包位置 | 使用场景 | 命名规则 |
|-------------|--------|------------------|----------------|-------------|
| **远程 Request** | API 模块 | `api/dto/request/` | 远程调用请求参数 | `XxxRequest` |
| **远程 Response** | API 模块 | `api/dto/response/` | 远程调用响应值 | `XxxResponse` |
| **Feign 客户端** | API 模块 | `api/feign/` | 远程服务调用接口 | `{模块名}RemoteService` |
| **前端 Request** | 门面服务 | `domain/dto/request/` | 前端请求参数（本地） | `XxxRequest` |
| **前端 Response** | 门面服务 | `domain/dto/response/` | 前端响应值（本地） | `XxxResponse` / `XxxVO` |
| **Query** | 应用服务 | `domain/dto/` | Service 层查询参数 | `XxxQuery` |
| **PageQuery** | 应用服务 | `domain/dto/` | 分页查询参数 | `XxxPageQuery` |
| **ListQuery** | 应用服务 | `domain/dto/` | 列表查询参数 | `XxxListQuery` |
| **DTO** | 应用服务 | `domain/dto/` | 内部数据传输 | `XxxDTO` |
| **DO** | 应用服务 | `domain/entity/` | 数据库实体 | `AimXxxDO` |

**强制约束**：

- **Controller 层**：请求参数必须以 `Request` 结尾，响应参数必须以 `Response` 或 `VO` 结尾
- **Service 层内部**：请求参数以 `Query` 结尾，响应以 `DTO` 结尾
- **禁止混用**：Controller 层不得使用 `Query` 或 `DTO` 作为参数/返回类型后缀

**分层职责**：

- **API 模块**（mall-{模块}-api）：存储远程调用接口，被其他服务引用
- **门面服务**：前端 Request/Response 放在本地 `domain/dto/`，不对外暴露
- **应用服务**：内部 DTO/Query/DO 不对外暴露，远程接口通过 API 模块暴露
- Request/Response **仅用于 Controller 层**，不直接传递到 Service 层
- Controller 层负责转换：Request → Query/DTO → 调用 Service
- Service 层参数只能是 Query 或 DTO，不能是 Request

**查询参数 vs 更新请求命名区分**：

| 场景 | 命名后缀 | 使用位置 | 示例 |
|----------|---------------|----------------|---------|
| **分页查询** | `PageQuery` | Service 层内部 | `JobTypePageQuery` |
| **列表查询** | `ListQuery` | Service 层内部 | `JobTypeListQuery` |
| **部分更新** | `Request` | Controller 层 | `JobTypeStatusRequest` |

**关键区分**：
- `PageQuery/ListQuery`：用于查询场景，包含查询条件 + 分页信息，Service 层内部使用
- `Request`：用于更新场景（特别是部分字段更新），Controller 层接收前端请求

**门面服务调用远程服务命名区分**：

| 对象类型 | 门面服务（本地） | 远程服务（API 模块） | 命名示例 |
|-------------|------------------------|----------------------------|----------------|
| 请求参数 | `XxxRequest` | `XxxApiRequest` | 本地：`JobTypeCreateRequest`，远程：`JobTypeCreateApiRequest` |
| 响应 | `XxxResponse` | `XxxApiResponse` | 本地：`JobTypeResponse`，远程：`JobTypeApiResponse` |

**命名策略**：

- 远程服务对象统一添加 `Api` 后缀（如 `JobTypeCreateApiRequest`）
- 本地服务对象保持标准命名（如 `JobTypeCreateRequest`）
- VO 仅用于复杂对象聚合场景，不作为区分手段

#### 查询返回对象规范

##### 应用服务（mall-agent/mall-user）

| 层 | 返回类型 | 说明 |
|-------|-------------|-------------|
| **Mapper** | `AimXxxDO` / `AimXxxDTO` | 原始数据对象 |
| **Service** | `CommonResult<CommonResult.PageData<XxxResponse>>` | 业务逻辑 + DO 转 Response |
| **InnerController** | `CommonResult<CommonResult.PageData<XxxResponse>>` | 直接转发 Service 结果 |

**应用服务约束**：

- **只返回 Response**，不包装 VO
- **参数规则**：≤2 个基础类型使用 `@RequestParam`，否则一律 `@RequestBody`
- **Service 层**：完成 DO 到 Response 的转换

##### 门面服务（mall-admin/mall-app）

| 场景 | 返回类型 | 说明 |
|----------|-------------|-------------|
| **简单转发** | `CommonResult<CommonResult.PageData<XxxResponse>>` | 直接返回单个 Feign 结果 |
| **数据聚合** | `CommonResult<XxxVO>` | VO 包装多个 Response |

**门面服务约束**：

- **允许使用 VO**：当需要聚合多个应用服务数据时
- **参数规则**：GET 可使用多个 `@RequestParam`，POST 使用 `@RequestBody`
- **无 Service 层**：Controller 直接调用 Feign

#### 新增/更新/删除返回规范

**应用服务 Service 层**：

| 操作类型 | Service 返回 | InnerController 返回 | 说明 |
|----------------|----------------|------------------------|-------------|
| **新增** | `Long`（新 ID）/ `boolean` | `CommonResult<Long>` / `CommonResult<Void>` | 返回新记录 ID 或成功标志 |
| **更新** | `boolean` / `int`（影响行数） | `CommonResult<Void>` | 返回成功或影响行数 |
| **删除** | `boolean` / `int`（影响行数） | `CommonResult<Void>` | 返回成功或影响行数 |

**规范说明**：

- **不返回完整对象**：新增/更新操作不返回完整实体对象
- **返回操作结果**：返回 ID、影响行数或成功标志
- **查询和修改分离**：需要获取更新后数据时，单独调用查询方法
- **InnerController 职责**：将 Service 操作结果转换为 Response

**VO 使用示例**（仅门面服务）：

```java
// VO 聚合多个本地 Response（从 ApiResponse 转换而来）
@Data
public class AgentDetailVO implements Serializable {
    private AgentResponse agent;           // 本地 Response（从 AgentApiResponse 转换）
    private UserResponse creator;          // 本地 Response（从 UserApiResponse 转换）
    private List<SkillResponse> skills;    // 本地 Response（从 SkillApiResponse 转换）
}

// 门面服务 Controller
@GetMapping("/{agentId}/detail")
public CommonResult<AgentDetailVO> getAgentDetail(@PathVariable("agentId") Long agentId) {
    // 调用多个 RemoteService 获取远程 ApiResponse
    AgentApiResponse agentApi = agentRemoteService.getById(agentId).getData();
    UserApiResponse creatorApi = userRemoteService.getById(agentApi.getCreatorId()).getData();

    // 转换为本地 Response
    AgentResponse agent = convertToAgentResponse(agentApi);
    UserResponse creator = convertToUserResponse(creatorApi);

    // 组装 VO
    AgentDetailVO vo = new AgentDetailVO();
    vo.setAgent(agent);
    vo.setCreator(creator);

    return CommonResult.success(vo);
}
```

#### 接口风格

| 服务类型 | 风格 | 路径前缀 |
|--------------|-------|-------------|
| 门面服务 | RESTful | `/admin/api/v1/` 或 `/app/api/v1/` |
| 应用服务 | 简化风格 | `/inner/api/v1/` |

#### 分页方案选择

**接口设计必须预估数据量**：

| 数据量 | 分页方案 | QueryService 调用 | AimXxxService 实现 | 说明 |
|-------------|-------------------|-------------------|------------------------------|-------------|
| **小表**（< 100万） | MyBatis-Plus 分页 | `aimXxxService.page()` | MP `page()` 方法 | 简单高效 |
| **大表**（>= 100万） | 索引覆盖分页 | `aimXxxService.pageByCoveringIndex()` | 调用 `AimXxxMapper.selectIdsByPage()` + `selectBatchByIds()` | 避免回表，有 total |

**分层调用原则**：

- **XxxQueryService** 只能调用 `AimXxxService` 方法
- **AimXxxService** 封装分页逻辑，内部可以调用 `AimXxxMapper`
- **AimXxxMapper** 只对 `AimXxxService` 暴露

**示例**：

```java
/**
 * 订单列表
 *
 * 数据量预估：预计 > 1000万条（大表）
 * 分页方案：索引覆盖分页
 */
@GetMapping("/orders")
public CommonResult<CommonResult.PageData<OrderResponse>> pageOrder(
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "20") Integer pageSize) {
    return orderDomainService.pageOrder(pageNum, pageSize);
}

// OrderQueryService
@Service
@RequiredArgsConstructor
public class OrderQueryService {
    private final AimOrderService aimOrderService;

    public Page<AimOrderDO> pageOrder(Integer pageNum, Integer pageSize) {
        // 只能调用 AimOrderService，不能直接调用 AimOrderMapper
        return aimOrderService.pageByCoveringIndex(pageNum, pageSize);
    }
}

// AimOrderService（继承 IService<AimOrderDO>）
@Service
public class AimOrderService extends ServiceImpl<AimOrderMapper, AimOrderDO> {

    public Page<AimOrderDO> pageByCoveringIndex(Integer pageNum, Integer pageSize) {
        // 内部调用 AimOrderMapper
        int offset = (pageNum - 1) * pageSize;
        List<Long> ids = baseMapper.selectIdsByPage(offset, pageSize);
        // ...
    }
}
```

### 步骤 4：依赖处理

- 首先为被调用方生成 Feign 接口
- 然后为调用方生成 Feign 客户端
- 确保接口定义一致性

### 步骤 5：输出报告

生成 `orchestrator/PROGRAMS/{program_id}/workspace/code-generation-report.md`：

```markdown
# 代码生成报告

## 生成摘要

| 服务 | 状态 | 代码文件数 | 主要类 |
|---------|--------|-----------------|--------------|
| mall-user | 完成 | 12 | UserController、UserService、UserMapper |
| mall-agent | 完成 | 18 | AgentController、AgentService、AgentMapper |
| mall-admin | 完成 | 8 | AgentAdminController、AgentRemoteService |
| mall-app | 完成 | 8 | AgentClientController、AgentRemoteService |

## 依赖关系

- mall-admin → mall-agent (Feign)
- mall-app → mall-agent (Feign)
- mall-agent → mall-user (Feign)

## 问题

- 无

## 下一步

1. 代码审查
2. 单元测试
3. 集成测试
```

---

## 代码模板

代码模板统一维护在：`.qoder/skills/java-code-generation/SKILL.md`（此文件包含代码模板）

模板包括：

- **Controller 模板**：门面服务（管理/客户端）+ 应用服务（内部接口）
- **Service 模板**：接口定义 + 实现类（包含完整的增删改查）
- **Mapper 模板**：Java 接口 + XML 映射
- **DTO/Request/Response 模板**：创建/更新/VO
- **Feign 客户端模板**：跨服务调用
- **Entity/DO 模板**：MyBatis-Plus 实体

## SQL 编写规范

### XML Mapper 优先

- **SQL 统一放在 XML 中**：`src/main/resources/mapper/XxxMapper.xml`
- **注解仅用于简单查询**：单表查询，无动态条件，SQL 不超过一行
- **禁止在注解中写复杂 SQL**：包含 `<script>`、`<if>`、`<foreach>` 等动态标签的 SQL

### XML 规范

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.aim.mall.agent.mapper.JobTypeMapper">

    <!-- 抽取重复 SQL -->
    <sql id="Base_Column_List">
        id, code, name, status, sort_order, description, is_default, create_time, update_time
    </sql>

    <sql id="Base_Table">
        aim_job_type
    </sql>

    <sql id="Base_Where">
        WHERE is_deleted = 0
    </sql>

    <!-- 分页查询 -->
    <select id="selectPageByKeyword" resultType="com.aim.mall.agent.domain.entity.AimJobTypeDO">
        SELECT
        <include refid="Base_Column_List"/>
        FROM
        <include refid="Base_Table"/>
        <include refid="Base_Where"/>
        <if test="keyword != null and keyword != ''">
            AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        ORDER BY sort_order ASC, create_time DESC
        LIMIT #{limit} OFFSET #{offset}
    </select>

</mapper>
```

**规范要点**：

1. **必须抽取重复 SQL**：使用 `<sql>` 标签定义列名、表名、公共条件
2. **禁止 `SELECT *`**：显式列出所有字段
3. **禁止注解动态 SQL**：复杂动态条件必须写在 XML 中

---

## 返回格式

```
状态：已完成
报告：orchestrator/PROGRAMS/{program_id}/workspace/code-generation-report.md
输出：N 个文件（列出每个服务的代码路径）
决策点：无
```
