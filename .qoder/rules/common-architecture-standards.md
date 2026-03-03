---
trigger: model_decision
description: 架构通用规范 - 定义分层架构、DDD领域设计、服务职责、接口风格、数据库设计原则等
globs: [ "**/*.java", "**/*.xml", "**/*.yaml", "**/*.yml" ]
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

| 层级              | 服务类型 | 命名规范                    | 职责                                  | 调用规则                                     |
|-----------------|------|-------------------------|-------------------------------------|------------------------------------------|
| **Controller**  | 控制器  | `XxxController`         | 接收请求，参数校验，调用应用服务                    | 只能调用 XxxApplicationService               |
| **Application** | 应用服务 | `XxxApplicationService` | 业务编排，协调查询和管理服务                      | 可调用 QueryService、ManageService           |
| **Query**       | 查询服务 | `XxxQueryService`       | 只读查询，封装查询逻辑                         | 只引用 `AimXxxService` 接口                      |
| **Manage**      | 管理服务 | `XxxManageService`      | 增删改操作，封装写逻辑                         | 只引用 `AimXxxService` 接口                      |
| **Data**        | 数据服务接口 | `AimXxxService`         | 继承 MyBatis-Plus IService，**仅用于增删改** | 位于 `service/mp/` 目录                          |
| **Data**        | 数据服务实现 | `AimXxxServiceImpl`     | 继承 `ServiceImpl<AimXxxMapper, AimXxxDO>` | 位于 `service/impl/mp/` 目录                     |
| **Data**        | 数据映射 | `AimXxxMapper`          | 原生 MyBatis Mapper，**所有查询必须走这里**     | 所有查询 SQL 在 XML 中定义                       |

**MyBatis-Plus 模式调用链**（面向接口编程）：

```
Controller
    ↓
XxxApplicationService(接口) ──→ impl/XxxApplicationServiceImpl
    ↓
    ├── XxxQueryService(接口) ──→ impl/XxxQueryServiceImpl ──→ AimXxxService(接口)
    ↓                                                          ↓
    └── XxxManageService(接口) ──→ impl/XxxManageServiceImpl ──→ impl/mp/AimXxxServiceImpl
                                                                  ↓
                                                              AimXxxMapper (查询)
                                                                  ↓
                                                              save/update/remove (增删改)
```

**原生 MyBatis 模式调用链**（不使用 MyBatis-Plus）：

```
Controller
    ↓
XxxApplicationService
    ↓
    ├── XxxQueryService ──→ AimXxxMapper
    ↓
    └── XxxManageService ──→ AimXxxMapper
```

**核心原则**：

- **DO 类命名**：必须严格遵循 `AimXxxDO` 格式，**以 `Aim` 开头，以 `DO` 结尾**
- **Service 层面向接口编程**：上层只引用接口，不直接依赖实现类
- **AimXxxService 接口+实现分离**：
  - 接口 `AimXxxService` 位于 `service/mp/` 目录，继承 `IService<AimXxxDO>`
  - 实现 `AimXxxServiceImpl` 位于 `service/impl/mp/` 目录，继承 `ServiceImpl<AimXxxMapper, AimXxxDO>`
- **MyBatis-Plus 仅用于增删改**：查询操作**必须**使用原生 `AimXxxMapper`，禁止用 MP 的查询方法
- **模式一致性**：一旦选择使用或不使用 MyBatis-Plus，整个调用链必须遵循对应模式，**禁止混用**

#### 1.2.1 MyBatis-Plus 使用限制

**核心原则：MyBatis-Plus 仅用于增删改，禁止用于查询**

| 操作类型    | 实现方式                  | 说明                                             |
|---------|-----------------------|------------------------------------------------|
| **增删改** | MyBatis-Plus IService | 可使用 `save()`, `updateById()`, `removeById()` 等 |
| **查询**  | **原生 MyBatis Mapper** | **必须**在 XML 中编写 SQL，禁止用 MP 查询方法                |

**模式选择决策**：

```
是否需要使用 MyBatis-Plus 的 IService 功能？
    ├─ 是 → MyBatis-Plus 模式
    │       - 创建 AimXxxService 接口（service/mp/）继承 IService<AimXxxDO>
    │       - 创建 AimXxxServiceImpl（service/impl/mp/）继承 ServiceImpl<AimXxxMapper, AimXxxDO>
    │       - **增删改**使用 MP 方法（save/update/remove）
    │       - **查询**在 AimXxxServiceImpl 中通过 baseMapper 调用原生 SQL
    │       - Query/ManageService 只引用 AimXxxService 接口
    │       - AimXxxMapper 只对 AimXxxServiceImpl 暴露
    │
    └─ 否 → 原生 MyBatis 模式
            - 不创建 AimXxxService/AimXxxServiceImpl
            - Query/ManageService 直接引用 AimXxxMapper
            - 所有 SQL 在 XML 中编写
```

#### 1.2.2 Service 层调用规范（MyBatis-Plus 模式）

**核心规范**：使用 MyBatis-Plus 模式时，上层服务**面向接口编程**，只引用接口不依赖实现。

**目录结构**：

```
service/
├── mp/                              # MP Service 接口目录
│   └── AimXxxService.java           # 接口，继承 IService<AimXxxDO>
├── impl/                            # 所有 Service 实现目录
│   ├── mp/                          # MP Service 实现目录
│   │   └── AimXxxServiceImpl.java   # 实现，继承 ServiceImpl
│   ├── XxxQueryServiceImpl.java     # 查询服务实现
│   ├── XxxManageServiceImpl.java    # 管理服务实现
│   └── XxxApplicationServiceImpl.java # 应用服务实现
├── XxxQueryService.java             # 查询服务接口
├── XxxManageService.java            # 管理服务接口
└── XxxApplicationService.java       # 应用服务接口
```

**规范说明**：

| 层级                   | 类型   | 增删改操作                                 | 查询操作                          | 禁止事项                      |
|----------------------|--------|---------------------------------------|-------------------------------|---------------------------|
| **AimXxxService**    | 接口   | 定义 MP 方法                              | 定义查询方法                      | -                         |
| **AimXxxServiceImpl**| 实现   | 使用 MP `save/update/remove`              | 使用 `baseMapper.xxx()` 原生 SQL  | -                         |
| **XxxQueryService**  | 接口   | -                                     | 定义查询方法                      | **禁止**直接引用 `AimXxxMapper` |
| **XxxQueryServiceImpl**| 实现 | -                                     | 注入 `AimXxxService` 接口调用     | **禁止**直接引用 `AimXxxMapper` |
| **XxxManageService** | 接口   | 定义增删改方法                             | 定义查询方法                      | **禁止**直接引用 `AimXxxMapper` |
| **XxxManageServiceImpl**| 实现 | 注入 `AimXxxService` 接口调用 MP 方法      | 注入 `AimXxxService` 接口调用     | **禁止**直接引用 `AimXxxMapper` |

**重要禁止事项**：

- ❌ **禁止**在 Query/ManageService 中直接引用 `AimXxxMapper`
- ❌ **禁止**在 Query/ManageService 中直接引用 `AimXxxServiceImpl`
- ❌ **禁止**使用 MP 的查询方法：`getById()`, `list()`, `page()`, `lambdaQuery()`, `query()` 等
- ✅ **必须**在 `AimXxxServiceImpl` 中通过 `baseMapper` 调用原生 SQL 进行查询

**正确示例**：

```java

@Service
public class AimJobTypeService extends ServiceImpl<AimJobTypeMapper, AimJobTypeDO> {

    // ✅ 查询：使用 baseMapper 调用原生 SQL（在 XML 中定义）
    public AimJobTypeDO getByCode(String code) {
        return baseMapper.selectByCode(code);
    }

    // ✅ 查询：使用 baseMapper 调用原生 SQL
    public AimJobTypeDO getById(Long id) {
        return baseMapper.selectById(id);  // 这里的 selectById 是 Mapper XML 中定义的，不是 MP 的
    }

    // ✅ 查询：分页查询走原生 SQL
    public Page<AimJobTypeDO> pageByCondition(JobTypePageQuery query) {
        List<Long> ids = baseMapper.selectIdsByPage(query.getOffset(), query.getLimit());
        // ...
    }

    // ❌ 错误：禁止使用 MP 的查询方法
    // public AimJobTypeDO getByIdWrong(Long id) {
    //     return super.getById(id);  // 这是 MP 的方法，禁止！
    // }
}

@Service
@RequiredArgsConstructor
public class JobTypeQueryService {

    private final AimJobTypeService aimJobTypeService;  // 只引用 Service

    public AimJobTypeDO getByCode(String code) {
        // ✅ 正确：调用 AimJobTypeService 封装的方法（内部走原生 SQL）
        return aimJobTypeService.getByCode(code);
    }

    public Page<JobTypeDTO> pageJobType(JobTypePageQuery query) {
        // ✅ 正确：调用 AimJobTypeService 封装的分页方法（内部走原生 SQL）
        return aimJobTypeService.pageByCondition(query);
    }
}

@Service
@RequiredArgsConstructor
public class JobTypeManageService {

    private final AimJobTypeService aimJobTypeService;  // 只引用 Service

    // ✅ 增删改：使用 MP IService 方法
    public Long createJobType(JobTypeCreateDTO dto) {
        AimJobTypeDO entity = convertToEntity(dto);
        aimJobTypeService.save(entity);  // MP 方法
        return entity.getId();
    }

    public boolean updateJobType(JobTypeUpdateDTO dto) {
        // ✅ 查询：先通过 Service 查询（内部走原生 SQL）
        AimJobTypeDO entity = aimJobTypeService.getById(dto.getId());
        if (entity == null) {
            throw new BusinessException(ErrorCodeEnum.JOB_TYPE_NOT_FOUND);
        }
        // ... 更新字段
        // ✅ 更新：使用 MP 方法
        return aimJobTypeService.updateById(entity);
    }

    // ❌ 错误：禁止在 ManageService 中直接引用 Mapper
    // private final AimJobTypeMapper aimJobTypeMapper;  // 禁止！
}
```

**错误示例**（❌ 禁止在 Query/ManageService 中直接访问 getBaseMapper）：

```java

@Service
@RequiredArgsConstructor
public class JobTypeQueryService {

    private final AimJobTypeService aimJobTypeService;

    public AimJobTypeDO getByCode(String code) {
        // ❌ 错误：直接访问 getBaseMapper()
        return aimJobTypeService.getBaseMapper().selectByCode(code);
    }
}
```

**规范目的**：

1. **SQL 可控**：所有查询 SQL 显式定义在 XML 中，便于 review 和优化
2. **性能保障**：避免 MP 自动生成低效 SQL，强制开发者关注查询性能
3. **统一查询入口**：所有查询走原生 Mapper，统一数据访问模式
4. **明确职责分离**：增删改用 MP 简化开发，查询用原生 SQL 保证性能

#### 1.2.3 原生 MyBatis 模式规范

**核心规范**：不使用 MyBatis-Plus 时，QueryService/ManageService **直接引用 AimXxxMapper**，所有操作通过原生 SQL 实现。

**规范说明**：

| 层级                   | 增删改操作                   | 查询操作                    |
|----------------------|-------------------------|-------------------------|
| **XxxQueryService**  | 调用 `aimXxxMapper.xxx()` | 调用 `aimXxxMapper.xxx()` |
| **XxxManageService** | 调用 `aimXxxMapper.xxx()` | 调用 `aimXxxMapper.xxx()` |

**代码示例**：

```java
// 原生 MyBatis 模式 - ManageService 直接调用 Mapper
@Service
@RequiredArgsConstructor
public class IdGenManageService {

    private final AimIdGenRuleMapper idGenRuleMapper;  // 直接引用 Mapper

    // 增删改都走原生 Mapper
    public void createRule(IdGenRuleDTO dto) {
        AimIdGenRuleDO entity = convertToEntity(dto);
        idGenRuleMapper.insert(entity);
    }

    public void updateRule(IdGenRuleDTO dto) {
        AimIdGenRuleDO entity = idGenRuleMapper.selectById(dto.getId());
        // ... 更新
        idGenRuleMapper.updateById(entity);
    }
}

// 原生 MyBatis 模式 - QueryService 直接调用 Mapper
@Service
@RequiredArgsConstructor
public class IdGenQueryService {

    private final AimIdGenRuleMapper idGenRuleMapper;  // 直接引用 Mapper

    public AimIdGenRuleDO getByPrefixAndPattern(String prefix, String datePattern) {
        return idGenRuleMapper.selectByPrefixAndPattern(prefix, datePattern);
    }
}
```

### 1.3 模块类型设计规范

| 模块类型         | 路径前缀             | 说明           |
|--------------|------------------|--------------|
| **门面模块-管理端** | `/admin/api/v1/` | 供管理后台调用      |
| **门面模块-客户端** | `/app/api/v1/`   | 供 APP 端调用    |
| **服务模块**     | `/inner/api/v1/` | 供其他服务 RPC 调用 |

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

| 调用方    | 可调用         | 禁止调用               |
|--------|-------------|--------------------|
| 门面服务   | 应用服务、基础业务服务 | -                  |
| 应用服务   | 基础业务服务      | 门面服务               |
| 基础业务服务 | 基础配置服务      | 门面服务、应用服务、其他基础业务服务 |
| 基础配置服务 | -           | 门面服务、应用服务、基础业务服务   |

**所有跨服务调用必须通过 OpenFeign 实现。**

#### 2.2.2 远程服务调用位置选择

远程服务调用可以在以下两个位置进行，根据依赖强度选择：

| 调用位置                        | 适用场景     | 说明                                         |
|-----------------------------|----------|--------------------------------------------|
| **门面服务**                    | 弱依赖、数据组装 | 仅用于聚合多个应用服务的数据，组装 VO 返回给前端。被调用服务故障时应有降级处理。 |
| **应用服务 ApplicationService** | 强依赖      | 业务逻辑必需的服务调用，被调用服务故障会导致当前功能不可用。             |

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

| 场景             | 调用位置               | 说明               |
|----------------|--------------------|------------------|
| 订单详情需要商品基础信息   | ApplicationService | 强依赖，无商品信息订单不完整   |
| 订单列表需要显示商品缩略图  | 门面服务               | 弱依赖，图片加载失败仍可显示订单 |
| 智能员工详情需要用户信息   | ApplicationService | 强依赖，需要用户信息进行权限判断 |
| 仪表盘统计需要聚合多服务数据 | 门面服务               | 纯数据组装，各服务数据独立展示  |

### 2.3 基础业务数据服务特殊限制

基础业务数据服务层处于架构底层，有以下特殊限制：

#### 2.3.1 禁止调用范围

| 禁止调用        | 说明       | 例外情况 |
|-------------|----------|------|
| 门面服务        | 禁止调用门面服务 | 无    |
| 应用服务        | 禁止调用应用服务 | 无    |
| 其他基础业务服务    | 禁止相互调用   | 无    |
| 基础配置服务以外的服务 | -        | 无    |

#### 2.3.2 允许的调用

| 允许调用   | 说明            | 示例           |
|--------|---------------|--------------|
| 基础配置服务 | 获取系统配置、字典数据等  | 配置中心、字典服务    |
| 基础设施   | 缓存、消息队列、文件存储等 | Redis、OSS、MQ |

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

### 3.2 应用服务（远程调用服务端）

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

**参数校验规范（重要）**：

| 层级 | 校验方式 | 说明 |
|------|---------|------|
| **门面服务** | ✅ 使用 `jakarta.validation`（`@Valid`、`@NotNull` 等） | 自动校验，快速失败 |
| **应用服务（远程接口）** | ❌ **禁止使用 `jakarta.validation`** | 手动编写验证方法进行校验 |

**应用服务手动校验示例**：

```java
@RestController
@RequestMapping("/inner/api/v1/agent")
public class JobTypeInnerController {
    
    @PostMapping("/create")
    public CommonResult<Long> create(@RequestBody JobTypeCreateRequest request) {
        // ✅ 手动校验参数
        validateCreateRequest(request);
        
        // ... 业务逻辑
    }
    
    private void validateCreateRequest(JobTypeCreateRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCodeEnum.PARAM_ERROR, "请求参数不能为空");
        }
        if (StringUtils.isBlank(request.getName())) {
            throw new BusinessException(ErrorCodeEnum.PARAM_ERROR, "岗位类型名称不能为空");
        }
        if (request.getName().length() > 50) {
            throw new BusinessException(ErrorCodeEnum.PARAM_ERROR, "岗位类型名称长度不能超过50个字符");
        }
        // ... 其他校验
    }
}
```

### 3.3 调用关系

```
前端 → 门面服务（参数校验）→ Feign → 应用服务（业务逻辑）
```

**关键约束**：

- 前端**禁止**直接调用应用服务
- 参数必要性校验在门面层完成
- 应用服务专注业务逻辑，信任门面层已做基础校验

### 3.4 API 路径参数规范对比

| 层级  | 路径参数 | 示例                                           |
|-----|------|----------------------------------------------|
| 门面层 | 允许一个 | `GET /admin/api/v1/agent/{id}`               |
| 应用层 | 禁止   | `GET /inner/api/v1/agent/detail?agentId=xxx` |

---

## 4. 数据访问与分页规范

### 4.1 MyBatis-Plus 使用规范

#### 增删改操作（ManageService）

**MyBatis-Plus 仅用于增删改**：

```java

@Service
@RequiredArgsConstructor
public class JobTypeManageService {

    private final AimJobTypeService aimJobTypeService;  // 只引用 AimXxxService

    public Long createJobType(JobTypeCreateDTO dto) {
        AimJobTypeDO entity = new AimJobTypeDO();
        // ... 设置字段
        aimJobTypeService.save(entity);  // ✅ 使用 MP 的 save 方法
        return entity.getId();
    }

    public boolean updateJobType(JobTypeUpdateDTO dto) {
        // ✅ 查询：通过 AimJobTypeService 内部调用 baseMapper（原生 SQL）
        AimJobTypeDO entity = aimJobTypeService.getById(dto.getId());
        if (entity == null) {
            throw new BusinessException(ErrorCodeEnum.JOB_TYPE_NOT_FOUND);
        }
        // ... 更新字段
        return aimJobTypeService.updateById(entity);  // ✅ 使用 MP 的 updateById 方法
    }

    public boolean deleteJobType(Long id) {
        return aimJobTypeService.removeById(id);  // ✅ 使用 MP 的 removeById 方法
    }
}
```

#### 查询操作（QueryService）

**所有查询必须使用原生 Mapper，禁止用 MP 的查询方法**

```java

@Service
@RequiredArgsConstructor
public class JobTypeQueryService {

    private final AimJobTypeService aimJobTypeService;  // 只引用 AimXxxService

    public Page<AimJobTypeDO> pageJobType(JobTypePageQuery query) {
        // ✅ 正确：调用 AimJobTypeService 封装的方法（内部走 baseMapper 原生 SQL）
        return aimJobTypeService.pageByCondition(query);
    }
}

@Service
public class AimJobTypeService extends ServiceImpl<AimJobTypeMapper, AimJobTypeDO> {

    /**
     * 分页查询 - 内部使用 baseMapper 调用原生 SQL
     */
    public Page<AimJobTypeDO> pageByCondition(JobTypePageQuery query) {
        int offset = (query.getPageNum() - 1) * query.getPageSize();

        // ✅ 使用 baseMapper 调用原生 SQL
        List<AimJobTypeDO> records = baseMapper.selectByCondition(
                query.getKeyword(),
                offset,
                query.getPageSize()
        );

        Long total = baseMapper.selectCountByCondition(query.getKeyword());

        Page<AimJobTypeDO> page = new Page<>(query.getPageNum(), query.getPageSize(), total);
        page.setRecords(records);
        return page;
    }
}
```

```xml
<!-- AimJobTypeMapper.xml -->
<select id="selectByCondition" resultType="com.aim.mall.agent.domain.entity.AimJobTypeDO">
    SELECT * FROM aim_agent_job_type
    WHERE is_deleted = 0
    <if test="keyword != null and keyword != ''">
        AND (name LIKE CONCAT('%', #{keyword}, '%')
        OR description LIKE CONCAT('%', #{keyword}, '%'))
    </if>
    ORDER BY sort_order ASC
    LIMIT #{offset}, #{limit}
</select>

<select id="selectCountByCondition" resultType="java.lang.Long">
SELECT COUNT(*) FROM aim_agent_job_type
WHERE is_deleted = 0
<if test="keyword != null and keyword != ''">
    AND (name LIKE CONCAT('%', #{keyword}, '%')
    OR description LIKE CONCAT('%', #{keyword}, '%'))
</if>
</select>
```

**大表分页查询**（预估数据量 >= 100万）：

**索引覆盖分页（避免深分页回表）**

```java

@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final AimOrderService aimOrderService;  // 只引用 AimXxxService

    /**
     * 索引覆盖分页 - 先查ID，再批量查数据
     */
    public Page<AimOrderDO> pageByCoveringIndex(OrderPageQuery query) {
        // ✅ 正确：调用 AimOrderService 封装的方法
        return aimOrderService.pageByCoveringIndex(query.getPageNum(), query.getPageSize());
    }
}

@Service
public class AimOrderService extends ServiceImpl<AimOrderMapper, AimOrderDO> {

    /**
     * 索引覆盖分页实现 - 内部使用 baseMapper 调用原生 SQL
     */
    public Page<AimOrderDO> pageByCoveringIndex(Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;

        // ✅ 第一步：只从索引查ID（很快）- 使用 baseMapper 原生 SQL
        List<Long> ids = baseMapper.selectIdsByPage(offset, pageSize);

        if (ids.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        // ✅ 第二步：用ID批量查询（走主键索引）- 使用 baseMapper 原生 SQL
        List<AimOrderDO> records = baseMapper.selectBatchByIds(ids);

        // ✅ 获取总数 - 使用 baseMapper 原生 SQL
        Long total = baseMapper.selectCount();

        Page<AimOrderDO> page = new Page<>(pageNum, pageSize, total);
        page.setRecords(records);
        return page;
    }
}
```

```xml
<!-- AimOrderMapper.xml -->
<!-- 先只查ID（走索引，不回表） -->
<select id="selectIdsByPage" resultType="java.lang.Long">
    SELECT id FROM aim_order
    WHERE is_deleted = 0
    ORDER BY id DESC
    LIMIT #{offset}, #{limit}
</select>

<!-- 再用ID批量查询（走主键索引） -->
<select id="selectBatchByIds" resultType="com.aim.mall.order.domain.entity.AimOrderDO">
    SELECT
    <include refid="Base_Column_List"/>
    FROM aim_order
    WHERE id IN
    <foreach collection="ids" item="id" open="(" separator="," close=")">
        #{id}
    </foreach>
    ORDER BY FIELD(id,
    <foreach collection="ids" item="id" separator=",">#{id}</foreach>
    )
</select>

<!-- 查询总数 -->
<select id="selectCount" resultType="java.lang.Long">
    SELECT COUNT(*) FROM aim_order WHERE is_deleted = 0
</select>
```

#### 4.1.1 Mapper XML 表名规范

**禁止抽取表名为公共 SQL 片段**，每个 SQL 语句必须直接写完整表名。

**❌ 错误示例（禁止）**：

```xml
<!-- 禁止定义 Base_Table 片段 -->
<sql id="Base_Table">order_table</sql>

        <!-- 禁止引用表名片段 -->
<select id="selectById" resultType="OrderDO">
SELECT * FROM
<include refid="Base_Table"/>
WHERE id = #{id}
</select>
```

**✅ 正确示例**：

```xml

<select id="selectById" resultType="OrderDO">
    SELECT * FROM order_table WHERE id = #{id}
</select>
```

**规范说明**：

- 表名必须直接在 SQL 语句中写完整，提高可读性
- 禁止定义 `<sql id="Base_Table">` 之类的表名片段
- 字段列表 `<sql id="Base_Column_List">` 可以继续使用

**非分页查询**：

```java

@Service
@RequiredArgsConstructor
public class JobTypeQueryService {

    private final AimJobTypeService aimJobTypeService;

    // ✅ 正确：使用 AimJobTypeService 封装的原生查询
    // 禁止：aimJobTypeService.getBaseMapper().selectByCode(code)
    public AimJobTypeDO getByCode(String code) {
        return aimJobTypeService.getByCode(code);
    }
}

@Service
public class AimJobTypeService extends ServiceImpl<AimJobTypeMapper, AimJobTypeDO> {

    // AimJobTypeService 内部调用 AimJobTypeMapper
    public AimJobTypeDO getByCode(String code) {
        return baseMapper.selectByCode(code);
    }
}
```

### 4.2 分页方案选择指南

**所有查询必须使用原生 Mapper，禁止用 MP 的查询方法**

| 场景        | 数据量预估    | 推荐方案                  | 说明           |
|-----------|----------|-----------------------|--------------|
| **小表分页**  | < 100万条  | 原生 MyBatis XML 分页 | 简单高效，SQL 可控 |
| **大表分页**  | >= 100万条 | 索引覆盖分页（原生 XML） | 避免回表，有 total |
| **非分页查询** | 任意       | 原生 MyBatis XML        | 性能可控         |

### 4.3 分页接口设计规范

**接口设计时预估数据量**：

```java
/**
 * 岗位类型列表
 *
 * 数据量预估：预计 < 1000 条（小表）
 * 分页方案：原生 MyBatis XML 分页
 */
@GetMapping
public CommonResult<CommonResult.PageData<JobTypeResponse>> pageJobType(...) {
    // ...
}

/**
 * 订单列表
 *
 * 数据量预估：预计 > 1000万条（大表）
 * 分页方案：索引覆盖分页（原生 MyBatis XML）
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
ORDER BY id
DESC

-- ❌ 无索引排序（全表扫描+文件排序）
    ORDER BY create_time DESC -- 如果 create_time 没有索引
```

---

## 5. 数据库设计规范

### 5.1 表名命名规范

所有业务表必须遵循统一的表名命名规则：

**表名格式**：`{prefix}_{模块}_{业务名}`

| 组成部分       | 说明                  | 示例                       |
|------------|---------------------|--------------------------|
| `{prefix}` | 项目特定前缀              | `aim_`, `tb_`, `t_`      |
| `{模块}`     | 业务模块名，小写，多个单词用下划线分隔 | `agent`、`user`、`product` |
| `{业务名}`    | 业务实体名，小写，多个单词用下划线分隔 | `job_type`、`order_info`  |

**约束**：

- 表名必须使用小写字母和下划线
- 禁止使用数字开头或纯数字表名
- 禁止超过 64 个字符

### 5.2 通用字段规范

所有业务表必须包含以下通用字段：

| 字段名           | 类型       | 约束                                                    | 说明                  |
|---------------|----------|-------------------------------------------------------|---------------------|
| `id`          | BIGINT   | PRIMARY KEY AUTO_INCREMENT                            | 主键，自增               |
| `create_time` | DATETIME | DEFAULT CURRENT_TIMESTAMP                             | 创建时间                |
| `update_time` | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间，自动更新           |
| `is_deleted`  | TINYINT  | DEFAULT 0                                             | 逻辑删除标记（0-未删除，1-已删除） |
| `creator_id`  | BIGINT   | NULLABLE                                              | 创建人ID（可选）           |
| `updater_id`  | BIGINT   | NULLABLE                                              | 更新人ID（可选）           |

### 5.3 数据类型选择规范

| 数据类别       | 推荐类型       | 使用场景                     |
|------------|------------|--------------------------|
| **布尔值**    | TINYINT(1) | 0 表示 false，1 表示 true     |
| **小范围整数**  | TINYINT    | -128~127，如状态码（0-禁用，1-启用） |
| **中等范围整数** | SMALLINT   | -32768~32767             |
| **常规整数**   | INT        | -21亿~21亿                 |
| **大整数**    | BIGINT     | 主键、用户ID、金额计算等            |
| **短字符串**   | VARCHAR(n) | 名称、编码等，根据实际长度指定 n        |
| **大文本**    | TEXT       | 描述、JSON 配置、长文本内容         |
| **日期时间**   | DATETIME   | 无时区要求的日期时间（推荐）           |
| **时间戳**    | TIMESTAMP  | 需要自动时区转换的场景              |

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

**强制格式**：`Aim{Name}DO`

- **必须以 `Aim` 开头**
- **必须以 `DO` 结尾**
- 中间部分为表名业务名的大驼峰转换

| 表名                   | DO 类名               | 说明   |
|----------------------|---------------------|------|
| `aim_agent`          | `AimAgentDO`        | ✅ 正确 |
| `aim_agent_job_type` | `AimAgentJobTypeDO` | ✅ 正确 |
| `aim_product_info`   | `AimProductInfoDO`  | ✅ 正确 |
| `aim_idgen_rule`     | `AimIdGenRuleDO`    | ✅ 正确 |

**命名检查清单**：

- [ ] 以 `Aim` 开头
- [ ] 以 `DO` 结尾
- [ ] 中间部分使用大驼峰命名

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

| 对象类型               | 门面服务                   | 应用服务              | API 模块              |
|--------------------|------------------------|-------------------|---------------------|
| **前端 Request**     | `domain/dto/request/`  | -                 | -                   |
| **前端 Response/VO** | `domain/dto/response/` | -                 | -                   |
| **远程 Request**     | 引用 API 模块              | -                 | `api/dto/request/`  |
| **远程 Response**    | 引用 API 模块              | -                 | `api/dto/response/` |
| **RemoteService**  | 引用 API 模块              | -                 | `api/feign/`        |
| **内部 DTO**         | `domain/dto/`          | `domain/dto/`     | -                   |
| **全局配置**           | `config/` (共享)         | `config/` (共享)    | -                   |
| **全局常量**           | `constants/` (共享)      | `constants/` (共享) | -                   |
| **内部枚举**           | `domain/enums/`        | `domain/enums/`   | -                   |

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

| 对象类型 | 门面服务（本地）      | 远程服务（API模块）      |
|------|---------------|------------------|
| 请求参数 | `XxxRequest`  | `XxxApiRequest`  |
| 返回响应 | `XxxResponse` | `XxxApiResponse` |

---

## 8. 门面服务 vs 应用服务对比

### 8.1 术语定义

| 术语         | 英文                    | 定义                             | 调用方       |
|------------|-----------------------|--------------------------------|-----------|
| **门面服务**   | Facade Service        | 直接面向前端（浏览器/APP）的服务，接收 HTTP 请求  | 前端应用      |
| **应用服务**   | Application Service   | 包含业务逻辑编排的后端服务，通过 Feign 供门面服务调用 | 门面服务      |
| **基础业务服务** | Base Business Service | 提供基础业务数据查询的后端服务                | 门面服务、应用服务 |
| **基础配置服务** | Base Config Service   | 提供系统配置、字典等基础数据的服务              | 所有服务      |

### 8.2 核心差异对比

| 特性           | 门面服务（Facade）                            | 应用服务（Application）                                      |
|--------------|-----------------------------------------|--------------------------------------------------------|
| **调用来源**     | 前端（浏览器/APP）                             | 内部服务（Feign）                                            |
| **路径前缀**     | `/admin/api/v1/` `/app/api/v1/`         | `/inner/api/v1/`                                       |
| **参数传递**     | 灵活：GET+多RequestParam 或 POST+RequestBody | 严格：≤2个基础类型用RequestParam，否则一律RequestBody                |
| **返回类型**     | Response 或 VO（可聚合多个Response）            | 仅 Response（不包装VO）                                      |
| **Service层** | XxxApplicationService（业务编排层）            | XxxApplicationService/XxxQueryService/XxxManageService |
| **VO使用**     | ✅ 允许（聚合多个Response）                      | ❌ 禁止                                                   |
| **远程服务调用**   | ✅ 允许（弱依赖、数据组装场景）                        | ✅ 允许（强依赖场景）                                            |

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
public class AgentApiResponse { ...
}

public class UserApiResponse { ...
}

// 门面服务本地的 Response（从 ApiResponse 转换而来）
public class AgentResponse { ...
}

public class UserResponse { ...
}

// 门面服务的 VO（聚合多个本地 Response）
@Data
public class AgentDetailVO implements Serializable {
    private AgentResponse agent;           // 智能员工信息（本地 Response）
    private UserResponse creator;          // 创建者信息（本地 Response）
    private List<SkillResponse> skills;    // 技能列表（本地 Response）
    private Integer sessionCount;          // 会话数量统计
}

// 门面服务 ApplicationService（业务编排层）
@Service
@RequiredArgsConstructor
public class AgentApplicationService {

    private final AgentRemoteService agentRemoteService;
    private final UserRemoteService userRemoteService;
    private final SkillRemoteService skillRemoteService;
    private final SessionRemoteService sessionRemoteService;

    public AgentDetailVO getAgentDetail(Long agentId) {
        // 调用多个 Feign 客户端获取远程 ApiResponse
        AgentApiResponse agentApi = agentRemoteService.getById(agentId).getData();
        UserApiResponse creatorApi = userRemoteService.getById(agentApi.getCreatorId()).getData();
        List<SkillApiResponse> skillsApi = skillRemoteService.listByAgentId(agentId).getData();
        Integer sessionCount = sessionRemoteService.countByAgentId(agentId).getData();

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
        return vo;
    }
}

// 门面服务 Controller
@RestController
@RequiredArgsConstructor
public class AgentAdminController {

    private final AgentApplicationService agentApplicationService;

    @GetMapping("/{agentId}/detail")
    public CommonResult<AgentDetailVO> getAgentDetail(@PathVariable("agentId") Long agentId) {
        return CommonResult.success(agentApplicationService.getAgentDetail(agentId));
    }
}
```

---

## 9. 门面服务分层规范

### 9.1 分层结构

门面服务必须遵循以下分层结构：

```
Controller（接口层）
    ↓
XxxApplicationService（业务编排层）
    ↓
FeignClient（远程服务调用）
```

### 9.2 各层职责

| 层级                     | 类命名                                       | 职责                                   | 禁止事项                         |
|------------------------|-------------------------------------------|--------------------------------------|------------------------------|
| **Controller**         | `XxxAdminController` / `XxxAppController` | 接收请求、参数校验、调用 ApplicationService、包装响应 | 禁止直接调用 FeignClient、禁止业务逻辑    |
| **ApplicationService** | `XxxApplicationService`                   | 业务编排、组装多个 Feign 调用、数据转换、VO 组装        | 禁止直接访问数据库（Mapper/Repository） |
| **FeignClient**        | 引用 API 模块                                 | 远程服务调用                               | -                            |

### 9.3 代码示例

#### ✅ 正确示例

```java
// Controller：仅负责参数校验和调用 ApplicationService
@RestController
@RequiredArgsConstructor
public class JobTypeAdminController {

    private final JobTypeApplicationService jobTypeApplicationService;

    @GetMapping("/admin/api/v1/job-types")
    public CommonResult<CommonResult.PageData<JobTypeResponse>> pageJobType(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        return CommonResult.success(jobTypeApplicationService.pageJobType(keyword, pageNum, pageSize));
    }
}

// ApplicationService：负责业务编排和 Feign 调用
@Service
@RequiredArgsConstructor
public class JobTypeApplicationService {

    private final JobTypeRemoteService jobTypeRemoteService;

    public CommonResult.PageData<JobTypeResponse> pageJobType(String keyword, Integer pageNum, Integer pageSize) {
        // 调用远程服务
        CommonResult<CommonResult.PageData<JobTypeApiResponse>> result =
                jobTypeRemoteService.pageJobType(keyword, pageNum, pageSize);

        // 数据转换
        List<JobTypeResponse> records = result.getData().getList().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        // 组装分页数据
        CommonResult.PageData<JobTypeResponse> pageData = new CommonResult.PageData<>();
        pageData.setList(records);
        pageData.setTotal(result.getData().getTotal());
        pageData.setPageNum(pageNum);
        pageData.setPageSize(pageSize);
        return pageData;
    }

    private JobTypeResponse convertToResponse(JobTypeApiResponse apiResponse) {
        // 转换逻辑
    }
}
```

#### ❌ 错误示例

```java
// 错误：Controller 直接调用 FeignClient
@RestController
@RequiredArgsConstructor
public class JobTypeAdminController {

    private final JobTypeRemoteService jobTypeRemoteService;  // ❌ 错误：直接注入 FeignClient

    @GetMapping("/admin/api/v1/job-types")
    public CommonResult<CommonResult.PageData<JobTypeApiResponse>> pageJobType(...) {
        // ❌ 错误：Controller 直接调用远程服务
        return jobTypeRemoteService.pageJobType(...);
    }
}
```

---

## 10. 检查清单

### 架构设计检查

- [ ] 遵循四层架构（接口层→应用层→领域层→基础设施层）
- [ ] 服务职责划分清晰（QueryService/ManageService/ApplicationService）
- [ ] 模块类型区分正确（门面模块/服务模块）
- [ ] 服务调用关系符合规范
- [ ] 远程服务调用位置选择正确（强依赖在应用层，弱依赖在门面层）
- [ ] 基础业务服务不调用其他业务服务
- [ ] **Service 层封装原则**：QueryService/ManageService 禁止直接使用 `getBaseMapper()`，必须通过 AimXxxService 封装方法间接访问

### 门面服务分层校验

- [ ] **必须定义 XxxApplicationService**：门面服务必须包含业务编排层，禁止 Controller 直接调用 FeignClient
- [ ] **Controller 职责单一**：仅负责参数校验、调用 ApplicationService、包装响应
- [ ] **ApplicationService 职责**：负责组装多个 Feign 调用、数据转换、VO 组装
- [ ] **禁止跨层调用**：Controller 禁止直接调用 FeignClient 或 Mapper/Repository
- [ ] **依赖方向正确**：Controller → ApplicationService → FeignClient

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

| 规范        | 路径                                                | 说明                  |
|-----------|---------------------------------------------------|---------------------|
| 项目命名规范    | `.qoder/rules/project-naming-standards.md`        | 包路径、服务名、表前缀、类命名模式   |
| 通用编码规范    | `.qoder/rules/common-coding-standards.md`         | Java 编码通用规范         |
| 项目响应格式规范  | `.qoder/rules/project-common-result-standards.md` | CommonResult 详细使用规范 |
| 项目操作人ID规范 | `.qoder/rules/project-operator-id-standards.md`   | 操作人ID获取与传递标准        |

---

## 版本历史

| 版本   | 日期         | 修改人      | 修改内容                                                              |
|------|------------|----------|-------------------------------------------------------------------|
| v1.0 | 2026-03-02 | AI Agent | 初始版本，从原 architecture-standards.md 提取通用内容                          |
| v1.1 | 2026-03-03 | AI Agent | 新增门面服务分层规范（第9章），明确 XxxApplicationService 作为业务编排层                  |
| v1.2 | 2026-03-03 | AI Agent | 新增 MyBatis-Plus 使用规范：MP 仅用于增删改，查询必须用原生 Mapper；DO 类必须是 AimXxxDO 格式 |
| v1.3 | 2026-03-03 | AI Agent | 新增远程调用接口参数校验规范：应用服务（服务端）禁止使用 jakarta.validation，必须手动校验 |
| v1.4 | 2026-03-03 | AI Agent | 更新 Service 层架构规范：接口+实现分离，面向接口编程；定义 service/mp/ 和 service/impl/mp/ 目录结构 |
