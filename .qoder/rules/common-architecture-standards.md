---
trigger: model_decision
description: 架构规范 - 通用规范，定义分层架构、服务职责、接口风格、数据库设计原则等
---

# 架构规范

本规范定义系统架构设计标准，包括分层架构、服务职责、接口风格、数据库设计等。

---

## 1. 分层架构

### 1.1 四层架构强制要求

必须严格遵循：**接口层 → 应用层 → 领域层 → 基础设施层**

```
┌─────────────────────────────────────┐
│           接口层 (Controller)         │  ← 接收请求、参数校验、返回响应
├─────────────────────────────────────┤
│           应用层 (Service)            │  ← 业务流程编排、事务管理
├─────────────────────────────────────┤
│           领域层 (Domain)             │  ← 业务逻辑、领域模型
├─────────────────────────────────────┤
│         基础设施层 (Mapper)           │  ← 数据访问、外部服务调用
└─────────────────────────────────────┘
```

**约束**：

- 禁止跨层直接调用，必须通过接口抽象
- 高层模块不依赖低层模块的具体实现
- 遵循依赖倒置原则

### 1.2 服务职责划分（五层架构）

| 层级 | 服务类型 | 命名规范 | 职责 | 调用规则 |
|------|----------|----------|------|----------|
| **Controller** | 控制器 | `XxxController` | 接收请求，参数校验，调用应用服务 | 只能调用 XxxApplicationService / XxxService |
| **Application** | 应用服务 | `XxxApplicationService` / `XxxService` | 业务编排，协调查询和管理服务 | 可调用 QueryService、ManageService |
| **Query** | 查询服务 | `XxxQueryService` | 只读查询，封装查询逻辑 | **只能调用 XxxService**，禁止直接调用 XxxMapper |
| **Manage** | 管理服务 | `XxxManageService` | 增删改操作，封装写逻辑 | **只能调用 XxxService**，禁止直接调用 XxxMapper |
| **Data** | 数据服务 | `XxxService` | 继承 MyBatis-Plus IService，封装所有数据访问 | 可调用 XxxMapper 进行原生查询 |
| **Data** | 数据映射 | `XxxMapper` | 原生 MyBatis，复杂查询 | 仅供 XxxService 调用 |

**调用关系图**：

```
Controller
    ↓
XxxApplicationService
    ↓
    ├── XxxQueryService ──→ XxxService ──→ XxxMapper (如需原生SQL)
    ↓
    └── XxxManageService ──→ XxxService ──→ XxxMapper (如需原生SQL)
```

**核心原则**：

- **XxxQueryService** 和 **XxxManageService** 只能依赖 `XxxService`
- **XxxService** 封装所有数据访问逻辑，包括 MP 分页和原生 Mapper 查询
- **XxxMapper** 只对 `XxxService` 暴露

### 1.3 模块类型设计规范

| 模块类型 | 路径前缀 | 说明 |
|----------|----------|------|
| **门面模块-管理端** | `/admin/api/v1/` | 供管理后台调用 |
| **门面模块-客户端** | `/app/api/v1/` | 供 APP 端调用 |
| **服务模块** | `/inner/api/v1/` | 供其他服务 RPC 调用 |

---

## 2. 服务架构

### 2.1 服务分层

```
前端应用
    ↓
┌─────────────┐
│  API 网关   │  （基础设施，由运维维护）
└─────────────┘
    ↓
┌─────────────┬─────────────┬─────────────┐
│  门面服务    │   门面服务   │   门面服务   │  ← 门面服务层
│  (管理后台)  │   (客户端)   │   (其他)     │
└─────────────┴─────────────┴─────────────┘
    ↓                    ↓         ↓
    └──────────┬─────────┘         │
               ↓                   │
        ┌─────────────┐            │
        │  应用服务    │  ← 应用服务层（核心业务）
        └─────────────┘            │
               ↓                   │
        ┌─────────────┬─────────────┐
        │  支撑服务    │   支撑服务   │  ← 基础业务数据服务层
        │  (用户服务)  │   (商品服务) │     禁止调用其他业务服务
        └─────────────┴─────────────┘
               ↓                   ↓
        ┌─────────────┐
        │  基础配置服务│  ← 基础配置服务层（最底层）
        │  (配置/字典) │     仅提供基础配置数据
        └─────────────┘
```

### 2.2 服务调用规则

#### 2.2.1 基础调用规则

| 调用方 | 可调用 | 禁止调用 |
|--------|--------|----------|
| 门面服务 | 应用服务、基础业务服务 | - |
| 应用服务 | 基础业务服务 | 门面服务 |
| 基础业务服务 | 基础配置服务 | 门面服务、应用服务、其他基础业务服务 |
| 基础配置服务 | - | 门面服务、应用服务、基础业务服务 |

**所有跨服务调用必须通过 OpenFeign 实现。**

#### 2.2.2 远程服务调用位置选择

远程服务调用可以在以下两个位置进行，根据依赖强度选择：

| 调用位置 | 适用场景 | 说明 |
|----------|----------|------|
| **门面服务** | 弱依赖、数据组装 | 仅用于聚合多个应用服务的数据，组装 VO 返回给前端。被调用服务故障时应有降级处理。 |
| **应用服务 ApplicationService** | 强依赖 | 业务逻辑必需的服务调用，被调用服务故障会导致当前功能不可用。 |

**调用位置决策流程：**

```
是否需要调用远程服务？
    ↓
是否是业务逻辑强依赖？（缺少该数据功能无法完成）
    ├─ 是 → 在 ApplicationService 中调用
    ↓
    └─ 否 → 仅用于数据组装/增强？
        ├─ 是 → 在门面服务中调用，组装 VO
        ↓
        └─ 否 → 重新评估是否需要调用
```

**示例场景：**

| 场景 | 调用位置 | 说明 |
|------|----------|------|
| 订单详情需要商品基础信息 | ApplicationService | 强依赖，无商品信息订单不完整 |
| 订单列表需要显示商品缩略图 | 门面服务 | 弱依赖，图片加载失败仍可显示订单 |
| 智能员工详情需要用户信息 | ApplicationService | 强依赖，需要用户信息进行权限判断 |
| 仪表盘统计需要聚合多服务数据 | 门面服务 | 纯数据组装，各服务数据独立展示 |

### 2.3 基础业务数据服务特殊限制

基础业务数据服务层处于架构底层，有以下特殊限制：

#### 2.3.1 禁止调用范围

| 禁止调用 | 说明 | 例外情况 |
|----------|------|----------|
| 门面服务 | 禁止调用门面服务 | 无 |
| 应用服务 | 禁止调用应用服务 | 无 |
| 其他基础业务服务 | 禁止相互调用 | 无 |
| 基础配置服务以外的服务 | - | 无 |

#### 2.3.2 允许的调用

| 允许调用 | 说明 | 示例 |
|----------|------|------|
| 基础配置服务 | 获取系统配置、字典数据等 | 配置中心、字典服务 |
| 基础设施 | 缓存、消息队列、文件存储等 | Redis、OSS、MQ |

#### 2.3.3 设计原则

**为什么基础业务数据服务不能调用其他业务服务？**

1. **职责单一**：基础业务服务只提供基础数据查询，不包含业务逻辑
2. **避免循环依赖**：防止基础服务之间相互调用形成循环依赖
3. **稳定性保障**：基础服务被大量上层服务依赖，应保持最小依赖
4. **数据一致性**：基础数据是业务的核心，不应受其他业务逻辑影响
5. **服务平等**：各基础业务服务是平级关系，互不依赖

**正确的数据流向：**

```
应用服务 → 基础服务A（查询数据A）
         → 基础服务B（查询数据B）
                ↓
         数据A + 数据B
                ↓
         在应用层组装业务数据
```

**错误的数据流向：**

```
基础服务A → 调用基础服务B获取数据 → 再返回数据A  ❌
基础服务B → 调用基础服务A获取数据 → 再返回数据B  ❌
```

---

## 3. 接口风格规范

### 3.1 门面服务

- **职责**：对外暴露给前端的接口，负责参数校验和请求转发
- **风格**：完整 RESTful（GET/POST/PUT/DELETE 语义清晰）
- **路径**：
    - 管理端：`/admin/api/v1/{模块名}`
    - 客户端：`/app/api/v1/{模块名}`

**路径参数规范**：

- 允许使用路径参数，但**最多只有一个**
- 路径参数必须放在 URL 最后
- 推荐格式：`/resource/action/{id}`

### 3.2 应用服务

- **职责**：核心业务逻辑处理，供门面服务通过 Feign 调用
- **风格**：简化风格（仅 GET/POST）
    - GET：单参数查询
    - POST：多参数查询、创建、更新、删除（使用 @RequestBody 传递参数）
- **路径**：`/inner/api/v1/{模块名}`

**路径参数规范（Feign 调用）**：

- **禁止在 URL 中使用路径参数**
- 所有参数通过 Query 参数或 RequestBody 传递
- 查询类：使用 Query 参数（GET 请求）
- 操作类：使用 RequestBody（POST 请求）

### 3.3 调用关系

```
前端 → 门面服务（参数校验）→ Feign → 应用服务（业务逻辑）
```

**关键约束**：

- 前端**禁止**直接调用应用服务
- 参数必要性校验在门面层完成
- 应用服务专注业务逻辑，信任门面层已做基础校验

### 3.4 API 路径参数规范对比

| 层级 | 路径参数 | 示例 |
|------|----------|------|
| 门面层 | 允许一个 | `GET /admin/api/v1/agent/{id}` |
| 应用层 | 禁止 | `GET /inner/api/v1/agent/detail?agentId=xxx` |

---

## 4. 数据访问与分页规范

### 4.1 MyBatis-Plus 使用规范

#### 增删改操作（ManageService）

**推荐直接使用 MyBatis-Plus IService**：

```java
@Service
@RequiredArgsConstructor
public class JobTypeManageService {

    private final JobTypeService jobTypeService;

    public Long createJobType(JobTypeCreateDTO dto) {
        JobTypeDO entity = new JobTypeDO();
        // ... 设置字段
        jobTypeService.save(entity);
        return entity.getId();
    }

    public boolean updateJobType(JobTypeUpdateDTO dto) {
        JobTypeDO entity = jobTypeService.getById(dto.getId());
        // ... 更新字段
        return jobTypeService.updateById(entity);
    }

    public boolean deleteJobType(Long id) {
        return jobTypeService.removeById(id);
    }
}
```

#### 查询操作（QueryService）

**小表分页查询**（预估数据量 < 100万）：

```java
@Service
@RequiredArgsConstructor
public class JobTypeQueryService {

    private final JobTypeService jobTypeService;

    public Page<JobTypeDO> pageJobType(JobTypePageQuery query) {
        LambdaQueryWrapper<JobTypeDO> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(query.getKeyword())) {
            wrapper.like(JobTypeDO::getName, query.getKeyword())
                    .or()
                    .like(JobTypeDO::getDescription, query.getKeyword());
        }

        wrapper.orderByAsc(JobTypeDO::getSortOrder);

        return jobTypeService.page(
                new Page<>(query.getPageNum(), query.getPageSize()),
                wrapper
        );
    }
}
```

**大表分页查询**（预估数据量 >= 100万）：

**索引覆盖分页（避免深分页回表）**

```java
@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderService orderService;

    /**
     * 索引覆盖分页 - 先查ID，再批量查数据
     */
    public Page<OrderDO> pageByCoveringIndex(OrderPageQuery query) {
        // 只能调用 OrderService，不直接调用 OrderMapper
        return orderService.pageByCoveringIndex(query.getPageNum(), query.getPageSize());
    }
}

@Service
public class OrderService extends ServiceImpl<OrderMapper, OrderDO> {

    /**
     * 索引覆盖分页实现 - 内部调用 OrderMapper
     */
    public Page<OrderDO> pageByCoveringIndex(Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;

        // 第一步：只从索引查ID（很快）
        List<Long> ids = baseMapper.selectIdsByPage(offset, pageSize);

        if (ids.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        // 第二步：用ID批量查询（走主键索引）
        List<OrderDO> records = baseMapper.selectBatchByIds(ids);

        // 获取总数（可缓存）
        Long total = baseMapper.selectCount(null);

        Page<OrderDO> page = new Page<>(pageNum, pageSize, total);
        page.setRecords(records);
        return page;
    }
}
```

```xml
<!-- OrderMapper.xml -->
<!-- 先只查ID（走索引，不回表） -->
<select id="selectIdsByPage" resultType="java.lang.Long">
    SELECT id FROM order_table
    WHERE is_deleted = 0
    ORDER BY id DESC
    LIMIT #{offset}, #{limit}
</select>

<!-- 再用ID批量查询（走主键索引） -->
<select id="selectBatchByIds" resultType="com.example.OrderDO">
    SELECT
    <include refid="Base_Column_List"/>
    FROM
    <include refid="Base_Table"/>
    WHERE id IN
    <foreach collection="ids" item="id" open="(" separator="," close=")">
        #{id}
    </foreach>
    ORDER BY FIELD(id,
    <foreach collection="ids" item="id" separator=",">#{id}</foreach>
    )
</select>
```

**非分页查询**：

```java
@Service
@RequiredArgsConstructor
public class JobTypeQueryService {

    private final JobTypeService jobTypeService;

    // ✅ 使用 JobTypeService 封装的原生查询
    public JobTypeDO getByCode(String code) {
        return jobTypeService.getByCode(code);
    }
}

@Service
public class JobTypeService extends ServiceImpl<JobTypeMapper, JobTypeDO> {

    // JobTypeService 内部调用 JobTypeMapper
    public JobTypeDO getByCode(String code) {
        return baseMapper.selectByCode(code);
    }
}
```

### 4.2 分页方案选择指南

| 场景 | 数据量预估 | 推荐方案 | 说明 |
|------|------------|----------|------|
| **小表分页** | < 100万条 | MyBatis-Plus `page()` | 简单高效 |
| **大表分页** | >= 100万条 | 索引覆盖分页 | 避免回表，有 total |
| **非分页查询** | 任意 | 原生 MyBatis XML | 性能可控 |

### 4.3 分页接口设计规范

**接口设计时预估数据量**：

```java
/**
 * 岗位类型列表
 *
 * 数据量预估：预计 < 1000 条（小表）
 * 分页方案：MyBatis-Plus 分页
 */
@GetMapping
public CommonResult<CommonResult.PageData<JobTypeResponse>> pageJobType(...) {
    // ...
}

/**
 * 订单列表
 *
 * 数据量预估：预计 > 1000万条（大表）
 * 分页方案：索引覆盖分页
 */
@GetMapping("/orders")
public CommonResult<CommonResult.PageData<OrderResponse>> pageOrder(...) {
    // ...
}
```

**ORDER BY 字段必须有索引**：

```sql
-- ✅ 有索引的排序
CREATE INDEX idx_sort_order ON job_type_table (sort_order, create_time);

-- ✅ 主键排序（默认有索引）
ORDER BY id DESC

-- ❌ 无索引排序（全表扫描+文件排序）
ORDER BY create_time DESC -- 如果 create_time 没有索引
```

---

## 5. 数据库设计规范

### 5.1 表名命名规范

所有业务表必须遵循统一的表名命名规则：

**表名格式**：`{prefix}_{模块}_{业务名}`

| 组成部分 | 说明 | 示例 |
|---------|------|------|
| `{prefix}` | 项目特定前缀 | `aim_`, `tb_`, `t_` |
| `{模块}` | 业务模块名，小写，多个单词用下划线分隔 | `agent`、`user`、`product` |
| `{业务名}` | 业务实体名，小写，多个单词用下划线分隔 | `job_type`、`order_info` |

**约束**：

- 表名必须使用小写字母和下划线
- 禁止使用数字开头或纯数字表名
- 禁止超过 64 个字符

### 5.2 通用字段规范

所有业务表必须包含以下通用字段：

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| `id` | BIGINT | PRIMARY KEY AUTO_INCREMENT | 主键，自增 |
| `create_time` | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| `update_time` | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间，自动更新 |
| `is_deleted` | TINYINT | DEFAULT 0 | 逻辑删除标记（0-未删除，1-已删除） |
| `creator_id` | BIGINT | NULLABLE | 创建人ID（可选） |
| `updater_id` | BIGINT | NULLABLE | 更新人ID（可选） |

### 5.3 数据类型选择规范

| 数据类别 | 推荐类型 | 使用场景 |
|----------|----------|----------|
| **布尔值** | TINYINT(1) | 0 表示 false，1 表示 true |
| **小范围整数** | TINYINT | -128~127，如状态码（0-禁用，1-启用） |
| **中等范围整数** | SMALLINT | -32768~32767 |
| **常规整数** | INT | -21亿~21亿 |
| **大整数** | BIGINT | 主键、用户ID、金额计算等 |
| **短字符串** | VARCHAR(n) | 名称、编码等，根据实际长度指定 n |
| **大文本** | TEXT | 描述、JSON 配置、长文本内容 |
| **日期时间** | DATETIME | 无时区要求的日期时间（推荐） |
| **时间戳** | TIMESTAMP | 需要自动时区转换的场景 |

### 5.4 字符集规范

- **默认字符集**：`DEFAULT CHARSET=utf8mb4`
- **排序规则**：**禁止指定具体的 COLLATE**，使用数据库默认排序规则

### 5.5 建表示例

```sql
CREATE TABLE example_table
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    -- 业务字段
    name        VARCHAR(100) NOT NULL COMMENT '名称',
    status      TINYINT  DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    config      JSON COMMENT '配置信息',
    -- 通用字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted  TINYINT  DEFAULT 0 COMMENT '逻辑删除标记：0-未删除 1-已删除',
    creator_id  BIGINT COMMENT '创建人ID',
    updater_id  BIGINT COMMENT '更新人ID',
    -- 索引
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) COMMENT '示例表'
    DEFAULT CHARSET = utf8mb4;
```

### 5.6 DO 类命名规范

严格按表名转换为大驼峰 + DO 后缀：

| 表名 | DO 类名 |
|------|---------|
| `{prefix}_agent` | `{Prefix}AgentDO` |
| `{prefix}_agent_job_type` | `{Prefix}AgentJobTypeDO` |
| `{prefix}_product_info` | `{Prefix}ProductInfoDO` |

---

## 6. Feign 接口规范

### 6.1 Feign 客户端定义规范

#### 命名规范

**Feign 客户端命名**：`{模块名}RemoteService`

- 位于 `{模块}-api` 模块的 `api/feign/` 包下
- 统一使用 `RemoteService` 后缀
- 示例：`AgentRemoteService`、`UserRemoteService`

#### 接口定义规范

- 查询类：使用 Query 参数，**禁止路径参数**
- 操作类：使用 RequestBody

### 6.2 Feign 调用方使用

- 参数校验（门面层负责）
- 转换为 DTO
- 调用应用服务

---

## 7. API 模块规范

### 7.1 设计原则

**独立 API 模块**：每个业务模块提供独立的 API 模块（`{模块}-api`），统一放在内部 API 目录下，供其他服务引用。

**目的**：

- 解耦服务间调用接口
- 避免被调用服务暴露内部实现
- 统一管理和版本控制远程调用接口
- 集中管理所有内部服务间调用的 API 定义

### 7.2 目录结构

所有 API 模块统一放在内部 API 目录下：

```
internal-api/
├── agent-api/                      # 智能员工模块 API
│   ├── src/main/java/com/example/agent/
│   │   └── api/
│   │       ├── dto/
│   │       │   ├── request/             # 远程调用请求参数
│   │       │   └── response/            # 远程调用返回值
│   │       ├── enums/                   # 远程接口使用的枚举
│   │       └── feign/                   # Feign 客户端
│   │           └── AgentRemoteService.java
│   └── pom.xml
├── user-api/                       # 用户模块 API
│   └── ...
└── pom.xml                         # 父 POM
```

### 7.3 使用方式

**调用方**引用 API 模块：

```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>agent-api</artifactId>
    <version>${project.version}</version>
</dependency>
```

```java
// 调用方中的调用
@Service
@RequiredArgsConstructor
public class JobTypeAdminService {

    private final JobTypeRemoteService jobTypeRemoteService;  // 来自 agent-api

    public JobTypeResponse getJobType(Long id) {
        return jobTypeRemoteService.getJobTypeById(id).getData();
    }
}
```

### 7.4 被调用方的变化

**删除被调用服务的 `api/` 目录**：

被调用服务不再包含 `api/` 目录，所有对外接口定义在 `{模块}-api` 模块中。

### 7.5 门面服务的特殊处理

**门面服务的 Request/Response**：

由于门面服务的 Request/Response **仅用于前端交互，不被其他模块引用**，因此放在本地 `domain/dto/` 中。

**重要规范**：门面服务 **禁止相互调用**，因此门面服务中 **不存在 feign 目录**

### 7.6 对象归属总结

| 对象类型 | 门面服务 | 应用服务 | API 模块 |
|----------|----------|----------|----------|
| **前端 Request** | `domain/dto/request/` | - | - |
| **前端 Response/VO** | `domain/dto/response/` | - | - |
| **远程 Request** | 引用 API 模块 | - | `api/dto/request/` |
| **远程 Response** | 引用 API 模块 | - | `api/dto/response/` |
| **RemoteService** | 引用 API 模块 | - | `api/feign/` |
| **内部 DTO** | `domain/dto/` | `domain/dto/` | - |
| **全局配置** | `config/` (共享) | `config/` (共享) | - |
| **全局常量** | `constants/` (共享) | `constants/` (共享) | - |
| **内部枚举** | `domain/enums/` | `domain/enums/` | - |

### 7.7 命名约束

**Controller 层强制约束**：

- 请求参数必须以 `Request` 结尾
- 返回参数必须以 `Response` 或 `VO` 结尾
- **禁止**使用 `Query`、`DTO` 作为 Controller 参数/返回类型后缀

**Service 层内部约束**：

- 分页查询参数以 `PageQuery` 结尾
- 列表查询参数以 `ListQuery` 结尾
- 普通查询参数以 `Query` 结尾
- 返回以 `DTO` 结尾
- **禁止**使用 `Request`、`Response` 作为内部 Service 参数/返回类型后缀

**命名区分规则**：

门面服务调用远程服务时，通过命名后缀区分本地和远程对象：

| 对象类型 | 门面服务（本地） | 远程服务（API模块） |
|----------|------------------|---------------------|
| 请求参数 | `XxxRequest` | `XxxApiRequest` |
| 返回响应 | `XxxResponse` | `XxxApiResponse` |

---

## 8. 门面服务 vs 应用服务对比

### 8.1 术语定义

| 术语 | 英文 | 定义 | 调用方 |
|------|------|------|--------|
| **门面服务** | Facade Service | 直接面向前端（浏览器/APP）的服务，接收 HTTP 请求 | 前端应用 |
| **应用服务** | Application Service | 包含业务逻辑编排的后端服务，通过 Feign 供门面服务调用 | 门面服务 |
| **基础业务服务** | Base Business Service | 提供基础业务数据查询的后端服务 | 门面服务、应用服务 |
| **基础配置服务** | Base Config Service | 提供系统配置、字典等基础数据的服务 | 所有服务 |

### 8.2 核心差异对比

| 特性 | 门面服务（Facade） | 应用服务（Application） |
|------|-------------------|------------------------|
| **调用来源** | 前端（浏览器/APP） | 内部服务（Feign） |
| **路径前缀** | `/admin/api/v1/` `/app/api/v1/` | `/inner/api/v1/` |
| **参数传递** | 灵活：GET+多RequestParam 或 POST+RequestBody | 严格：≤2个基础类型用RequestParam，否则一律RequestBody |
| **返回类型** | Response 或 VO（可聚合多个Response） | 仅 Response（不包装VO） |
| **Service层** | 无，直接调用Feign | 有，包含业务逻辑+DO转Response |
| **VO使用** | ✅ 允许（聚合多个Response） | ❌ 禁止 |
| **远程服务调用** | ✅ 允许（弱依赖、数据组装场景） | ✅ 允许（强依赖场景） |

### 8.3 参数传递规则详解

#### 门面服务参数规则

```java
// ✅ GET + 多个 RequestParam（适合简单查询）
@GetMapping("/list")
public CommonResult<CommonResult.PageData<AgentApiResponse>> pageAgent(
        @RequestParam(name = "keyword", required = false) String keyword,
        @RequestParam(name = "status", required = false) Integer status,
        @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
    return agentFeignClient.pageAgent(keyword, status, pageNum, pageSize);
}

// ✅ POST + RequestBody（适合复杂查询或多参数）
@PostMapping("/search")
public CommonResult<CommonResult.PageData<AgentApiResponse>> pageAgent(
        @RequestBody @Valid AgentListApiRequest request) {
    return agentFeignClient.pageAgent(request);
}
```

#### 应用服务参数规则

```java
// ✅ 参数 ≤2 个且为基础类型：使用 RequestParam
@GetMapping("/detail")
public CommonResult<AgentApiResponse> getAgentById(
        @RequestParam("agentId") Long agentId) {
    return agentService.getById(agentId);
}

// ✅ 参数 >2 个或包含对象：一律使用 RequestBody
@PostMapping("/list")
public CommonResult<CommonResult.PageData<AgentApiResponse>> pageAgent(
        @RequestBody @Valid AgentListApiRequest request) {
    return agentService.pageAgent(request);
}

// ❌ 错误：参数超过2个仍使用 RequestParam
@GetMapping("/list")
public CommonResult<CommonResult.PageData<AgentApiResponse>> pageAgent(
        @RequestParam(name = "keyword", required = false) String keyword,
        @RequestParam(name = "status", required = false) Integer status,
        @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
    // 错误！应该封装成 RequestBody
}
```

### 8.4 VO 使用场景（仅门面服务）

当门面服务需要聚合多个应用服务的数据时，使用 VO 包装：

```java
// 应用服务返回的 Response（远程服务）
public class AgentApiResponse { ... }

public class UserApiResponse { ... }

// 门面服务本地的 Response（从 ApiResponse 转换而来）
public class AgentResponse { ... }

public class UserResponse { ... }

// 门面服务的 VO（聚合多个本地 Response）
@Data
public class AgentDetailVO implements Serializable {
    private AgentResponse agent;           // 智能员工信息（本地 Response）
    private UserResponse creator;          // 创建者信息（本地 Response）
    private List<SkillResponse> skills;    // 技能列表（本地 Response）
    private Integer sessionCount;          // 会话数量统计
}

// 门面服务 Controller
@GetMapping("/{agentId}/detail")
public CommonResult<AgentDetailVO> getAgentDetail(@PathVariable("agentId") Long agentId) {
    // 调用多个 Feign 客户端获取远程 ApiResponse
    AgentApiResponse agentApi = agentFeignClient.getById(agentId).getData();
    UserApiResponse creatorApi = userFeignClient.getById(agentApi.getCreatorId()).getData();
    List<SkillApiResponse> skillsApi = skillFeignClient.listByAgentId(agentId).getData();
    Integer sessionCount = sessionFeignClient.countByAgentId(agentId).getData();

    // 转换为本地 Response
    AgentResponse agent = convertToAgentResponse(agentApi);
    UserResponse creator = convertToUserResponse(creatorApi);
    List<SkillResponse> skills = skillsApi.stream()
            .map(this::convertToSkillResponse)
            .collect(Collectors.toList());

    // 组装 VO
    AgentDetailVO vo = new AgentDetailVO();
    vo.setAgent(agent);
    vo.setCreator(creator);
    vo.setSkills(skills);
    vo.setSessionCount(sessionCount);

    return CommonResult.success(vo);
}
```

---

## 9. 检查清单

### 架构设计检查

- [ ] 遵循四层架构（接口层→应用层→领域层→基础设施层）
- [ ] 服务职责划分清晰（QueryService/ManageService/ApplicationService）
- [ ] 模块类型区分正确（门面模块/服务模块）
- [ ] 服务调用关系符合规范
- [ ] 远程服务调用位置选择正确（强依赖在应用层，弱依赖在门面层）
- [ ] 基础业务服务不调用其他业务服务

### 接口设计检查

- [ ] 门面服务使用 RESTful 风格（GET/POST/PUT/DELETE）
- [ ] 应用服务使用简化风格（仅 GET/POST）
- [ ] 门面层路径参数最多一个
- [ ] 应用层禁止路径参数（使用 Query 或 RequestBody）

### 数据库设计检查

- [ ] 包含所有通用字段（id、create_time、update_time、is_deleted 等）
- [ ] 数据类型选择符合规范
- [ ] 字符集为 utf8mb4
- [ ] 不指定 COLLATE
- [ ] DO 类命名符合规范（大驼峰+DO 后缀）

### Feign 接口检查

- [ ] 查询类使用 Query 参数
- [ ] 操作类使用 RequestBody
- [ ] 禁止路径参数

---

## 10. 相关规范

| 规范 | 路径 | 说明 |
|------|------|------|
| 项目命名规范 | `.qoder/rules/project-naming-standards.md` | 包路径、服务名、表前缀、类命名模式 |
| 通用编码规范 | `.qoder/rules/common-coding-standards.md` | Java 编码通用规范 |
| 项目响应格式规范 | `.qoder/rules/project-common-result-standards.md` | CommonResult 详细使用规范 |
| 项目操作人ID规范 | `.qoder/rules/project-operator-id-standards.md` | 操作人ID获取与传递标准 |

---

## 版本历史

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0 | 2026-03-02 | AI Agent | 初始版本，从原 architecture-standards.md 提取通用内容 |
