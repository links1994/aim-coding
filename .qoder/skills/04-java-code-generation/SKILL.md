---
name: java-code-generation
description: 根据项目规范生成 Java 微服务代码。在用户说"生成代码"、"委托：生成代码"或技术规格书已确认并准备实现时使用。
---

# Java 代码生成 Skill

基于技术规格书生成符合项目规范的 Java 微服务代码。

> **强制规范**：此 Skill 生成的代码必须严格遵循以下规范（按优先级排序）：
> 1. **通用编码规范**：`.qoder/rules/common-coding-standards.md` - 代码质量、异常处理、日志规范等
> 2. **通用架构规范**：`.qoder/rules/common-architecture-standards.md` - 分层架构、服务职责、接口风格等
> 3. **项目命名规范**：`.qoder/rules/project-naming-standards.md` - 包路径、服务名、表前缀、类命名模式
> 4. **项目错误码规范**：`.qoder/rules/project-error-code-standards.md` - 错误码格式、系统/模块映射、生成规则
> 5. **项目响应格式规范**：`.qoder/rules/project-common-result-standards.md` - CommonResult 统一响应格式
> 6. **项目操作人ID规范**：`.qoder/rules/project-operator-id-standards.md` - 操作人ID获取与传递标准
>
> **生成代码前必须先读取上述规范文件**，确保代码符合项目标准。

---

## 触发条件

- 技术规格书（design.md）已生成并确认
- 用户明确指令："生成代码" 或 "委托：生成代码"
- 主 Agent 在第 4 阶段调用

---

## 输入

- `orchestrator/PROGRAMS/{program_id}/artifacts/tech-spec.md` — 技术规格书
- `orchestrator/PROGRAMS/{program_id}/artifacts/openapi.yaml` — API 定义
- `orchestrator/ALWAYS/RESOURCE-MAP.yml` — 项目资源映射
- `orchestrator/PROGRAMS/{program_id}/SCOPE.yml` — 写入范围控制
- `.qoder/rules/common-coding-standards.md` — 通用编码规范
- `.qoder/rules/common-architecture-standards.md` — 通用架构规范
- `.qoder/rules/project-naming-standards.md` — 项目命名规范
- `.qoder/rules/project-error-code-standards.md` — 项目错误码规范
- `.qoder/rules/project-common-result-standards.md` — 项目响应格式规范
- `.qoder/rules/project-operator-id-standards.md` — 项目操作人ID规范

---

## 输出

- 每个服务的 Java 源代码 → `{service}/src/main/java/`
- 代码生成报告 `orchestrator/PROGRAMS/{program_id}/artifacts/code-generation-report.md`

---

## 工作流程

### 步骤 1：读取配置和知识库查询（自动复用现有代码）

1. **读取输入文档**
   - 读取技术规格书（tech-spec.md）
   - 读取 API 定义（openapi.yaml）
   - 读取项目资源映射（RESOURCE-MAP.yml）
   - 读取写入范围（SCOPE.yml）
   - **必须先读取** 通用编码规范、通用架构规范、项目命名规范、项目错误码规范、项目响应格式规范和项目操作人ID规范

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
├── agent/                                         # 业务模块（如：agent, admin, user）
│   ├── {业务域}/                                   # 业务域（如：employee, order）
│   │   ├── controller/                            # 接口层
│   │   │   └── inner/                             # 内部接口（用于 Feign 调用）
│   │   ├── service/                               # 应用层
│   │   │   ├── mp/                                # MyBatis-Plus 数据服务
│   │   │   │   └── AimXxxService.java             # 继承 IService<AimXxxDO>
│   │   │   ├── XxxApplicationService.java         # 应用服务（业务编排）
│   │   │   ├── XxxQueryService.java               # 查询服务（只读）
│   │   │   └── XxxManageService.java              # 管理服务（增删改）
│   │   ├── mapper/                                # 基础设施层
│   │   │   └── AimXxxMapper.java                  # 原生 MyBatis
│   │   └── domain/                                # 领域层
│   │       ├── entity/                            # 实体类（AimXxxDO）
│   │       ├── dto/                               # 内部 DTO（XxxDTO、XxxQuery）
│   │       ├── enums/                             # 内部枚举
│   │       └── exception/                         # 异常类
│   ├── config/                                    # 全局配置类（模块级共享）
│   └── constants/                                 # 全局常量（模块级共享）
```

**说明**：
- 一个模块可包含多个业务域（如 agent 模块下有 employee、order 等）
- 一个业务域可包含多个功能（如 employee 业务域下有 job-type、skill、employee 等）
- config/ 和 constants/ 位于模块下，与业务域同一级，作为模块内共享
- 每个业务域内部保持完整的 controller/service/mapper/domain 分层结构

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
├── admin/                                         # 业务模块
│   ├── {业务域}/                                   # 业务域（如：agent, user）
│   │   ├── controller/                            # 接口层
│   │   │   └── admin/                             # 管理接口
│   │   └── domain/                                # 领域层
│   │       └── dto/
│   │           ├── request/                       # 前端请求参数（本地使用）
│   │           └── response/                      # 前端响应值/VO（本地使用）
│   ├── config/                                    # 全局配置类（模块级共享）
│   └── constants/                                 # 全局常量（模块级共享）
```

**说明**：
- 门面服务禁止相互调用，因此不存在 feign 目录
- 门面服务通过 mall-inner-api 中的 RemoteService 调用应用服务
- config/ 和 constants/ 位于模块下，与业务域同一级，作为模块内共享
- 一个业务域可包含多个功能模块

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

**分层调用原则（Service 层封装原则）**：

- **XxxQueryService** 只能调用 `AimXxxService` 方法，**禁止**使用 `aimXxxService.getBaseMapper()`
- **AimXxxService** 封装分页逻辑，内部可以调用 `AimXxxMapper`
- **AimXxxMapper** 只对 `AimXxxService` 暴露

**重要约束**：上层服务（QueryService/ManageService）**禁止**直接访问 `getBaseMapper()`，必须在 `AimXxxService` 中封装方法提供间接访问。详见 `common-architecture-standards.md` → "Service 层封装原则（Mapper 访问隔离）"。

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
    return orderApplicationService.pageOrder(pageNum, pageSize);
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

生成 `orchestrator/PROGRAMS/{program_id}/artifacts/code-generation-report.md`：

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

代码模板统一维护在：`.qoder/skills/04-java-code-generation/SKILL.md`（此文件包含代码模板）

模板包括：

- **Controller 模板**：门面服务（管理/客户端）+ 应用服务（内部接口）
- **Service 模板**：接口定义 + 实现类（包含完整的增删改查）
- **Mapper 模板**：Java 接口 + XML 映射
- **DTO/Request/Response 模板**：创建/更新/VO
- **Feign 客户端模板**：跨服务调用
- **Entity/DO 模板**：MyBatis-Plus 实体

---

### Mapper 模板

#### Java Mapper 接口模板

```java
package com.aim.mall.{module}.{domain}.mapper;

import com.aim.mall.{module}.{domain}.domain.entity.Aim{Xxx}DO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * {业务描述}Mapper接口
 *
 * @author AI Agent
 */
@Mapper
public interface Aim{Xxx}Mapper extends BaseMapper<Aim{Xxx}DO> {

    /**
     * 根据{唯一字段}查询（排除已删除）
     *
     * @param {field} {字段描述}
     * @return {业务描述}实体
     */
    Aim{Xxx}DO selectBy{Field}(@Param("{field}") String {field});

    /**
     * 分页查询{业务描述}列表
     *
     * @param keyword  关键词
     * @param offset   偏移量
     * @param limit    每页大小
     * @return {业务描述}列表
     */
    List<Aim{Xxx}DO> selectPageByKeyword(@Param("keyword") String keyword,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);

    /**
     * 统计总数（根据关键词）
     *
     * @param keyword 关键词
     * @return 总数
     */
    Long countByKeyword(@Param("keyword") String keyword);
}
```

#### XML Mapper 映射文件模板

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.aim.mall.{module}.{domain}.mapper.Aim{Xxx}Mapper">

    <!-- 基础字段列表 -->
    <sql id="Base_Column_List">
        id, {field1}, {field2}, status, sort_order,
        create_time, update_time, is_deleted, creator_id, updater_id
    </sql>

    <!-- 根据{唯一字段}查询 -->
    <select id="selectBy{Field}" resultType="com.aim.mall.{module}.{domain}.domain.entity.Aim{Xxx}DO">
        SELECT
        <include refid="Base_Column_List"/>
        FROM aim_{module}_{table_name}
        WHERE is_deleted = 0
        AND {field} = #{field}
        LIMIT 1
    </select>

    <!-- 分页查询列表 -->
    <select id="selectPageByKeyword" resultType="com.aim.mall.{module}.{domain}.domain.entity.Aim{Xxx}DO">
        SELECT
        <include refid="Base_Column_List"/>
        FROM aim_{module}_{table_name}
        WHERE is_deleted = 0
        <if test="keyword != null and keyword != ''">
            AND ({field1} LIKE CONCAT('%', #{keyword}, '%') OR {field2} LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        ORDER BY sort_order ASC, create_time DESC
        LIMIT #{limit} OFFSET #{offset}
    </select>

    <!-- 统计总数 -->
    <select id="countByKeyword" resultType="java.lang.Long">
        SELECT COUNT(*)
        FROM aim_{module}_{table_name}
        WHERE is_deleted = 0
        <if test="keyword != null and keyword != ''">
            AND ({field1} LIKE CONCAT('%', #{keyword}, '%') OR {field2} LIKE CONCAT('%', #{keyword}, '%'))
        </if>
    </select>

</mapper>
```

**Mapper 规范要点**：

1. **命名规范**：
   - Java 接口：`Aim{Xxx}Mapper`，继承 `BaseMapper<Aim{Xxx}DO>`
   - XML 文件：`Aim{Xxx}Mapper.xml`，放在 `src/main/resources/mapper/` 目录

2. **注解要求**：
   - 接口必须标注 `@Mapper` 注解
   - 方法参数使用 `@Param` 注解指定参数名

3. **XML 规范**：
   - **必须抽取重复字段**：使用 `<sql id="Base_Column_List">` 定义字段列表
   - **禁止抽取表名**：每个 SQL 必须直接写明完整表名（如 `aim_agent_job_type`）
   - **软删除过滤**：所有查询必须包含 `WHERE is_deleted = 0`
   - **禁止 SELECT ***：必须显式列出所有字段

4. **常用方法**：
   - `selectBy{Field}`：根据唯一字段查询单条记录
   - `selectPageByKeyword`：分页查询（配合关键词搜索）
   - `countByKeyword`：统计总数（配合关键词搜索）
   - 基础 CRUD：继承自 `BaseMapper`（`insert`, `selectById`, `updateById`, `deleteById` 等）

**命名约定**：具体命名模式（如 `AimXxxDO`、`mall-admin` 等）参考 `project-naming-standards.md`

### 枚举使用规范

生成代码时必须统一使用 `mall-common` 中的枚举：

| 场景 | 使用枚举 | 示例 |
|------|----------|------|
| 状态（启用/停用） | `StatusEnum` | `entity.setStatus(StatusEnum.ENABLE.getCode())` |
| 逻辑删除 | `DeleteStatusEnum` | `entity.setIsDeleted(DeleteStatusEnum.UNDELETE.getCode())` |

**DO 实体类状态字段默认值**：
```java
// 默认启用
private Integer status = StatusEnum.ENABLE.getCode();

// 默认未删除
private Integer isDeleted = DeleteStatusEnum.UNDELETE.getCode();
```

**XML 查询条件模板**：
```xml
<sql id="Base_Where">
    WHERE is_deleted = 0
</sql>
```
> 注：XML 中直接使用数值 0/1，代码中使用枚举

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
        aim_agent_job_type
    </sql>

    <sql id="Base_Where">
        WHERE is_deleted = 0
    </sql>

    <!-- 分页查询 -->
    <select id="selectPageByKeyword" resultType="com.aim.mall.agent.employee.domain.entity.AimJobTypeDO">
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

## 步骤 6：代码生成检查清单（强制）

代码生成完成后，**必须**逐条检查以下清单，确保生成的代码符合所有规范：

### 6.1 命名规范检查

| 检查项 | 规范要求 | 检查方法 |
|--------|----------|----------|
| 实体类命名 | `Aim{Xxx}DO` 格式，大驼峰 + DO 后缀 | 检查 `domain/entity/` 目录下所有类 |
| Service 命名 | `Aim{Xxx}Service`（MP Service）、`{Name}QueryService`、`{Name}ManageService`、`{Name}ApplicationService` | 检查 `service/` 目录下所有类 |
| Mapper 命名 | `Aim{Xxx}Mapper`，继承 `BaseMapper<Aim{Xxx}DO>` | 检查 `mapper/` 目录下所有类 |
| Controller 命名 | `{Name}Controller`、`{Name}InnerController` | 检查 `controller/` 目录下所有类 |
| Request/Response 命名 | Controller 层：`{Name}Request`/`{Name}Response`；Service 层：`{Name}Query`/`{Name}DTO` | 检查 `dto/`、`domain/dto/` 目录 |
| 远程对象命名 | API 模块：`{Name}ApiRequest`/`{Name}ApiResponse` | 检查 `api/dto/` 目录 |
| Feign 客户端命名 | `{模块名}RemoteService` | 检查 `api/feign/` 目录 |
| 表名命名 | `aim_{模块}_{业务名}`，小写下划线 | 检查 DO 类上的 `@TableName` 注解 |
| 包路径 | 符合服务类型规范（门面/应用/支撑） | 检查 `package` 声明 |

### 6.2 类结构检查

| 检查项 | 规范要求 | 检查方法 |
|--------|----------|----------|
| DO 实体类 | 继承 `BaseDO`，包含 `serialVersionUID = -1L` | 检查 DO 类定义 |
| Request/Response | 实现 `Serializable`，`serialVersionUID = -1L` | 检查类实现 |
| Lombok 注解 | `@Data` 用于实体/DTO，`@Slf4j` 用于需要日志的类 | 检查类注解 |
| Mapper 注解 | 必须标注 `@Mapper` | 检查 Mapper 接口 |
| Service 继承 | MP Service 继承 `ServiceImpl<XxxMapper, XxxDO>` | 检查 Service 类定义 |

### 6.3 时间格式化检查

| 检查项 | 规范要求 | 检查方法 |
|--------|----------|----------|
| DO/DTO 时间字段 | **禁止**使用 `@JsonFormat` | 检查 `domain/entity/`、`domain/dto/` 下的类 |
| Response/VO 时间字段 | **必须**使用 `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")` | 检查 `dto/response/`、`domain/dto/response/` 下的类 |

### 6.4 数据转换检查

| 检查项 | 规范要求 | 检查方法 |
|--------|----------|----------|
| 禁止 BeanUtils | **禁止**使用 `BeanUtils.copyProperties` | 全局搜索 `BeanUtils` |
| 手动转换 | Service 层必须提供 `convertToXXX` 方法 | 检查 Service 类中的转换方法 |
| 分层转换 | Controller 负责 Request→Query/DTO，Service 负责 DO→Response | 检查 Controller 和 Service 代码 |

### 6.5 数据库访问检查

| 检查项 | 规范要求 | 检查方法 |
|--------|----------|----------|
| 禁止 QueryWrapper | **禁止**在 QueryService/ManageService 中使用 `QueryWrapper`/`LambdaQueryWrapper` | 全局搜索 `QueryWrapper` |
| 禁止直接访问 Mapper | QueryService/ManageService **禁止**直接调用 `getBaseMapper()` | 检查 Service 类中的方法调用 |
| 分层调用 | QueryService/ManageService 只能调用 XxxService，XxxService 封装 Mapper 访问 | 检查 Service 层调用关系 |
| XML 规范 | 必须抽取 `Base_Column_List`，禁止 `SELECT *`，必须软删除过滤 | 检查 XML 文件 |
| 批量操作 | 禁止循环单条 CRUD，必须使用批量操作 | 检查批量插入/更新逻辑 |

### 6.6 异常处理检查

| 检查项 | 规范要求 | 检查方法 |
|--------|----------|----------|
| 标准异常 | 仅使用 `MethodArgumentValidationException`、`RemoteApiCallException`、`BusinessException` | 检查异常使用 |
| Controller 异常 | Controller 层**不捕获**异常，直接抛出 | 检查 Controller 方法 |
| 异常构造 | 支持错误码、错误码+消息、错误码+原因等多种构造方式 | 检查异常抛出代码 |

### 6.7 响应格式检查

| 检查项 | 规范要求 | 检查方法 |
|--------|----------|----------|
| Controller 返回 | 所有方法必须返回 `CommonResult<T>` | 检查 Controller 方法签名 |
| 分页返回 | 使用 `CommonResult<CommonResult.PageData<XxxResponse>>` | 检查分页方法 |
| 成功响应 | 使用 `CommonResult.success()` / `CommonResult.pageSuccess()` | 检查返回语句 |
| 失败响应 | 使用 `CommonResult.failed()` | 检查全局异常处理器 |

### 6.8 参数注解检查

| 检查项 | 规范要求 | 检查方法 |
|--------|----------|----------|
| 门面服务参数 | GET 可使用多个 `@RequestParam`，POST 使用 `@RequestBody` | 检查门面服务 Controller |
| 应用服务参数 | ≤2 个基础类型用 `@RequestParam`，否则 `@RequestBody` | 检查应用服务 InnerController |
| 参数校验 | 使用 `@Valid` 进行参数校验 | 检查方法参数 |

### 6.9 操作人 ID 检查

| 检查项 | 规范要求 | 检查方法 |
|--------|----------|----------|
| 门面服务获取 | 使用 `@RequestHeader(AuthConstant.USER_TOKEN_HEADER)` + `UserInfoUtil.getUserInfo(user).getId()` | 检查门面服务 Controller |
| ApiRequest 字段 | 必须包含 `operatorId` 字段 | 检查 API 模块 Request 类 |
| 应用服务接收 | 从 ApiRequest 获取 `operatorId`，不直接解析 Header | 检查 InnerController |
| 数据库记录 | 正确设置 `createBy` / `updateBy` 字段 | 检查 Service 层代码 |

### 6.10 枚举使用检查

| 检查项 | 规范要求 | 检查方法 |
|--------|----------|----------|
| 状态枚举 | 统一使用 `StatusEnum`（ENABLE=1, DISABLE=0） | 检查状态字段赋值 |
| 删除枚举 | 统一使用 `DeleteStatusEnum`（UNDELETE=0, DELETE=1） | 检查删除逻辑 |
| 禁止自定义 | 禁止各模块自行定义状态/删除枚举 | 全局搜索 `StatusEnum`、`DeleteStatusEnum` |

### 6.11 日志规范检查

| 检查项 | 规范要求 | 检查方法 |
|--------|----------|----------|
| 日志级别 | DEBUG 用于入口参数和关键步骤，INFO 用于重要操作，WARN 用于警告，ERROR 用于系统异常 | 检查日志语句 |
| 日志内容 | 包含关键上下文信息（如 ID、数量等） | 检查日志参数 |

### 6.12 错误码检查

| 检查项 | 规范要求 | 检查方法 |
|--------|----------|----------|
| 错误码格式 | 符合 `SSMMTNNN` 格式（8位数字） | 检查 ErrorCodeEnum |
| 系统代码 | 20=Common, 30=Admin, 40=Agent, 50=User | 检查错误码前缀 |
| 模块代码 | 符合模块映射表 | 检查错误码第3-4位 |
| 错误类型 | 0=Success, 1=Client, 2=Server, 3=Business | 检查错误码第5位 |

### 检查清单使用方式

1. **生成代码后立即检查**：每生成一个文件，对照清单逐项检查
2. **标记检查结果**：在代码生成报告中记录每项检查结果（通过/不通过）
3. **修复问题**：对不通过的项立即修复，不得跳过
4. **最终确认**：所有检查项通过后，方可提交代码

---

## 返回格式

```
状态：已完成
报告：orchestrator/PROGRAMS/{program_id}/artifacts/code-generation-report.md
输出：N 个文件（列出每个服务的代码路径）
决策点：无
```
