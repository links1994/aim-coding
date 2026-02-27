---
trigger: model_decision
description: Java 编码规范
---

# Java 编码规范

本规范定义 Java 代码的编写标准，所有生成的代码必须遵循。

---

## 1. 代码质量要求

### 1.1 Java 21 新特性

推荐使用以下 Java 21 新特性：

- **记录类（Record）**：用于 DTO、VO 等数据传输对象
- **密封类（Sealed Class）**：用于定义受限的类层次结构
- **模式匹配**：用于 instanceof 和 switch 表达式
- **文本块**：用于多行字符串（SQL、JSON 等）

### 1.2 Lombok 注解

合理使用 Lombok 注解减少样板代码：

| 注解                    | 用途                                        | 使用场景     |
|-----------------------|-------------------------------------------|----------|
| `@Data`               | 生成 getter/setter/toString/equals/hashCode | 实体类、DTO  |
| `@Getter`             | 只生成 getter                                | 只读对象     |
| `@Setter`             | 只生成 setter                                | 可变对象     |
| `@Builder`            | 生成建造者模式                                   | 复杂对象的创建  |
| `@Slf4j`              | 生成日志对象                                    | 需要日志的类   |
| `@NoArgsConstructor`  | 无参构造                                      | 需要无参构造的类 |
| `@AllArgsConstructor` | 全参构造                                      | 需要全参构造的类 |

### 1.3 序列化规范

Request/Response 对象必须实现 `Serializable` 接口：

```java

@Data
public class AgentCreateRequest implements Serializable {
    private static final long serialVersionUID = -1L;
    // ...
}
```

**规范要求**：`serialVersionUID` 统一设置为 `-1L`。

### 1.4 时间格式化

**`LocalDateTime` 字段 `@JsonFormat` 使用规则**：

| 对象类型                       | 是否需要 `@JsonFormat` | 说明            |
|----------------------------|--------------------|---------------|
| **DO (AimXxxDO)**          | ❌ 不需要              | 数据库实体，不涉及序列化  |
| **DTO (AimXxxDTO)**        | ❌ 不需要              | 内部数据传输，不直接返回  |
| **Response (XxxResponse)** | ✅ 必须               | 直接返回给前端，需要格式化 |
| **VO (XxxVO)**             | ✅ 必须               | 视图对象，直接返回给前端  |

```java
// ✅ Response/VO 中使用
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
private LocalDateTime createTime;

// ✅ DO/DTO 中不使用
private LocalDateTime createTime;
```

---

## 2. 实现细节规范

### 2.1 DO/DTO 转换方式

**禁止使用** `BeanUtils.copyProperties`，使用手动 convert 方法。

#### 查询返回对象规范

根据查询类型确定返回对象：

| 查询类型     | Mapper返回    | Service返回                                          | 说明                       |
|----------|-------------|----------------------------------------------------|--------------------------|
| **单表查询** | `AimXxxDO`  | `CommonResult<CommonResult.PageData<XxxResponse>>` | Service层完成DO到Response转换  |
| **联表查询** | `AimXxxDTO` | `CommonResult<CommonResult.PageData<XxxResponse>>` | Service层完成DTO到Response转换 |

**规范说明：**

1. **单表查询**：Mapper返回DO，Service转换为Response后返回
2. **联表查询**：Mapper返回DTO，Service转换为Response后返回
3. **QueryService职责**：只读查询，封装查询逻辑（小表用 MP 分页，大表用游标/索引覆盖分页）
4. **ManageService职责**：增删改操作，封装写逻辑（直接使用 MP IService）
5. **Controller层职责**：参数转换（Request→Query/DTO）+ 调用Service + 直接返回

#### 新增/更新返回规范

**Service 层新增/更新方法返回类型**：

| 操作类型   | Service返回                  | 说明                 |
|--------|----------------------------|--------------------|
| **新增** | `int` / `boolean` / `Long` | 返回影响行数、是否成功、或新记录ID |
| **更新** | `int` / `boolean`          | 返回影响行数或是否成功        |
| **删除** | `int` / `boolean`          | 返回影响行数或是否成功        |

**规范说明：**

1. **不返回完整对象**：新增/更新操作不需要返回完整的实体对象
2. **返回操作结果**：返回影响行数、是否成功标志、或新记录ID
3. **查询和修改分离**：需要获取更新后数据时，单独调用查询方法
4. **InnerController 转换**：Service 返回操作结果，Controller 根据结果构建 Response

**DDD风格例外**：

- 如果采用DDD（领域驱动设计）风格，可以不完全遵循上述模式
- DDD风格下可以在Application层直接返回Domain对象或使用专门的Assembler进行转换

### 2.2 数据库访问规范

**SQL 编写方式**：

- **优先使用 XML Mapper**：SQL 统一放在 `src/main/resources/mapper/` 目录下的 `AimXxxMapper.xml` 文件中
- **禁止在注解中写复杂 SQL**：`@Select`、`@Insert`、`@Update`、`@Delete` 注解仅用于极简单的单表操作
- **XML 中必须抽取重复 SQL**：使用 `<sql>` 标签定义可复用的 SQL 片段

**MyBatis-Plus 使用规则**：

| 场景                 | 调用方                | 实现方                              | 说明                                                        |
|--------------------|--------------------|----------------------------------|-----------------------------------------------------------|
| **增删改**            | `XxxManageService` | `AimXxxService` (MP)             | 使用 MP IService 提供的 `save()`/`updateById()`/`removeById()` |
| **小表分页** (< 100万)  | `XxxQueryService`  | `AimXxxService` (MP)             | 使用 MP `page()` 方法                                         |
| **大表分页** (>= 100万) | `XxxQueryService`  | `AimXxxService` → `AimXxxMapper` | AimXxxService 封装索引覆盖分页逻辑                                  |
| **非分页查询**          | `XxxQueryService`  | `AimXxxService` → `AimXxxMapper` | AimXxxService 封装原生 MyBatis XML 查询                         |

**分层调用原则**：

- **XxxQueryService / XxxManageService** 只能调用 `AimXxxService`
- **AimXxxService** 封装所有数据访问，包括 MP 方法和原生 Mapper 调用
- **AimXxxMapper** 只对 `AimXxxService` 暴露，其他层禁止直接调用

**禁止使用的特性**：

- `QueryWrapper` / `LambdaQueryWrapper`（非分页查询场景）
- `SELECT *`
- MyBatis 的 `<script>` 标签（注解方式）
- 在 Mapper 接口注解中写复杂 SQL（超过一行或包含动态条件）
- XxxQueryService / XxxManageService 直接调用 AimXxxMapper

### 2.3 批量操作规范

**禁止在循环中单条 CRUD**，必须使用批量操作。

### 2.4 Excel 处理规范

- 使用流式读取（Streaming）
- 监控内存使用
- 分批处理（推荐批次大小 1000 条）

---

## 3. 编码规范

### 3.1 命名规范

#### 类命名

| 类型            | 命名规则                   | 示例                       | 所属模块     | 说明                        |
|---------------|------------------------|--------------------------|----------|---------------------------|
| 实体类           | 大驼峰 + DO 后缀            | `AimAgentDO`、`AimUserDO` | 应用服务     | 数据库实体，统一前缀 Aim            |
| VO            | 大驼峰 + VO 后缀            | `AgentVO`                | 门面服务     | 视图对象，仅门面服务使用              |
| 远程 Request    | 大驼峰 + ApiRequest 后缀    | `AgentCreateApiRequest`  | API 模块   | 远程调用请求参数                  |
| 远程 Response   | 大驼峰 + ApiResponse 后缀   | `AgentApiResponse`       | API 模块   | 远程调用返回值                   |
| 前端 Request    | 大驼峰 + Request 后缀       | `AgentCreateRequest`     | 门面服务     | 前端请求参数（本地使用）              |
| 前端 Response   | 大驼峰 + Response 后缀      | `AgentResponse`          | 门面服务     | 前端返回值（本地使用）               |
| Query         | 大驼峰 + Query 后缀         | `AgentListQuery`         | 应用服务     | **Service层内部查询参数**（仅内部使用） |
| PageQuery     | 大驼峰 + PageQuery 后缀     | `JobTypePageQuery`       | 应用服务     | **分页查询参数**（Service层内部使用） |
| ListQuery     | 大驼峰 + ListQuery 后缀     | `JobTypeListQuery`       | 应用服务     | **列表查询参数**（Service层内部使用） |
| DTO           | 大驼峰 + DTO 后缀           | `AgentDTO`               | 应用服务     | 内部数据传输对象                  |
| DomainService | 大驼峰 + DomainService 后缀 | `AgentDomainService`     | 应用服务     | 业务域服务                     |
| QueryService  | 大驼峰 + QueryService 后缀  | `AgentQueryService`      | 应用服务     | 查询服务                      |
| ManageService | 大驼峰 + ManageService 后缀 | `AgentManageService`     | 应用服务     | 管理服务                      |
| AimService    | Aim + 大驼峰 + Service 后缀 | `AimAgentService`        | 应用服务     | MP数据服务，继承IService         |
| Mapper        | Aim + 大驼峰 + Mapper 后缀  | `AimAgentMapper`         | 应用服务     | 数据访问层                     |
| Controller    | 大驼峰 + Controller 后缀    | `AgentController`        | 门面/应用    | 控制器                       |
| FeignClient   | 大驼峰 + FeignClient 后缀   | `AgentFeignClient`       | API 模块   | Feign客户端                  |
| Config        | 大驼峰 + Config 后缀        | `FeignConfig`            | 任意       | 配置类                       |
| Exception     | 大驼峰 + Exception 后缀     | `BusinessException`      | 应用服务     | 异常类                       |
| Enum          | 大驼峰 + Enum 后缀          | `AgentStatusEnum`        | 应用服务/API | 枚举类                       |

**强制约束**：

- **Controller 层**：请求参数必须以 `Request` 结尾，返回参数必须以 `Response` 或 `VO` 结尾
- **Service 层内部**：请求参数以 `Query` 结尾，返回以 `DTO` 结尾
- **禁止混用**：Controller 层不得使用 `Query` 或 `DTO` 作为参数/返回类型后缀

#### 查询参数 vs 更新参数命名区分

**列表/分页查询参数**（Service层内部使用）：
- 使用 `ListQuery` 或 `PageQuery` 后缀
- 包含查询条件 + 分页信息
- 示例：`JobTypeListQuery`, `AgentPageQuery`

**部分更新请求参数**（Controller层使用）：
- 使用 `Request` 后缀
- 表示部分字段更新（非全量）
- 示例：`JobTypeUpdateRequest`, `AgentStatusRequest`

**区分示例**：
```java
// 分页查询（Service层内部）
public Page<AimJobTypeDO> pageJobType(JobTypePageQuery query);

// 列表查询（Service层内部）
public List<AimJobTypeDO> listJobType(JobTypeListQuery query);

// 部分更新（Controller层）
public CommonResult<Void> updateStatus(@RequestBody JobTypeStatusRequest request);
```

#### 方法命名

| 操作   | 命名规则                 | 示例                                                  |
|------|----------------------|-----------------------------------------------------|
| 查询单个 | `getById`、`getByXXX` | `getById(Long id)`                                  |
| 查询列表 | `listXXX`、`queryXXX` | `listByStatus(Integer status)`                      |
| 分页查询 | `pageXxx`            | `pageAgent(AgentQuery query)`                       |
| 创建   | `createXXX`、`save`   | `createAgent(AgentCreateRequest request)`           |
| 更新   | `updateXXX`          | `updateAgent(AgentUpdateRequest request)`           |
| 删除   | `deleteXXX`、`remove` | `deleteById(Long id)`                               |
| 转换   | `convertToXXX`       | `convertToVO(Agent agent)`                          |
| 校验   | `validateXXX`        | `validateCreateRequest(AgentCreateRequest request)` |

#### 变量命名

- 使用驼峰命名法
- **禁止**使用拼音或不规范缩写
- 布尔变量使用 `is`、`has`、`can` 等前缀

```java
// 正确
private String agentName;
private boolean isActive;
private List<Agent> agentList;

// 错误
private String agent_name;  // 下划线命名
private String xm;          // 拼音缩写
private boolean active;     // 布尔值无前缀
```

### 3.2 包结构组织

```
{服务名}/src/main/java/com/aim/mall/
├── {模块}/              # 业务模块（如：agent, admin, user）
│   ├── controller/      # 接口层
│   │   ├── admin/       # 管理端接口（门面服务）
│   │   ├── app/         # 客户端接口（门面服务）
│   │   └── inner/       # 内部接口（应用/支撑服务，供Feign调用）
│   ├── service/         # 应用层
│   │   ├── XxxDomainService.java    # 业务域服务（业务编排）
│   │   ├── XxxQueryService.java     # 查询服务（只读）
│   │   ├── XxxManageService.java    # 管理服务（增删改）
│   │   └── mp/                      # MyBatis-Plus 数据服务
│   │       └── AimXxxService.java   # 继承 IService<AimXxxDO>
│   ├── mapper/          # 基础设施层
│   │   └── AimXxxMapper.java        # 原生 MyBatis
│   └── domain/          # 领域层
│       ├── entity/      # 实体类 (AimXxxDO)
│       ├── dto/         # 内部DTO (XxxDTO, XxxQuery)
│       ├── enums/       # 枚举类（非远程调用相关）
│       ├── vo/          # 视图对象 (XxxVO) - 仅门面服务
│       └── exception/   # 异常类
├── config/              # 全局配置类（共享，所有模块共用）
└── constants/           # 全局常量（共享，所有模块共用）
```

**说明**：
- `config/` 和 `constants/` 位于 `com.aim.mall` 包下，与业务模块同级，可被多个模块共享
- 门面服务不存在 `feign/` 目录，通过引用 `mall-inner-api` 中的 RemoteService 调用应用服务
- 应用服务的 `api/` 目录已移除，所有对外接口定义在 `mall-{模块}-api` 模块中

#### 对象类型使用规范

**严格区分以下对象类型：**

| 对象类型         | 包位置                 | 使用场景                    | 命名示例                   | 使用服务  |
|--------------|---------------------|-------------------------|------------------------|-------|
| **Request**  | `api/dto/request/`  | **仅Controller层入参**      | `JobTypeCreateRequest` | 门面服务  |
| **Response** | `api/dto/response/` | **Controller层出参**       | `JobTypeResponse`      | 所有服务  |
| **VO**       | `api/dto/vo/`       | **聚合多个Response**（仅门面服务） | `JobTypeDetailVO`      | 仅门面服务 |
| **Query**    | `domain/dto/`       | **Service层查询参数**        | `JobTypeListQuery`     | 应用服务  |
| **DTO**      | `domain/dto/`       | **内部数据传输**              | `JobTypeDTO`           | 应用服务  |

**关键约束：**

1. **Request/Response 仅用于Controller层**，不直接传递到Service层
2. **VO 仅用于门面服务**：当需要聚合多个应用服务的 Response 时使用
3. **Controller层负责转换**：`XxxRequest` → `XxxQuery`/`XxxDTO` → 调用Service
4. **Service层使用Query/DTO**：Service接口入参只能是 `XxxQuery` 或 `XxxDTO`，不能是 `XxxRequest`
5. **非对外对象不要放在api目录**：内部使用的DTO统一放在 `domain/dto/`

**VO 使用场景**（仅门面服务）：

```java
// 门面服务：聚合多个本地 Response（从 ApiResponse 转换而来）
@Data
public class AgentDetailVO implements Serializable {
    private AgentResponse agent;           // 本地 Response（从 AgentApiResponse 转换）
    private UserResponse creator;          // 本地 Response（从 UserApiResponse 转换）
    private List<SkillResponse> skills;    // 本地 Response（从 SkillApiResponse 转换）
}
```

### 3.3 参数注解规范

#### 门面服务参数规则（mall-admin/mall-app）

门面服务直接面向前端，参数传递灵活：

```java
// ✅ GET + 多个 RequestParam（适合简单查询）
@GetMapping("/list")
public CommonResult<CommonResult.PageData<AgentResponse>> pageAgent(
        @RequestParam(name = "keyword", required = false) String keyword,
        @RequestParam(name = "status", required = false) Integer status,
        @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
    // Feign 返回 ApiResponse，转换为本地 Response
    CommonResult<CommonResult.PageData<AgentApiResponse>> result =
            agentFeignClient.pageAgent(keyword, status, pageNum, pageSize);
    return convertToPageResponse(result);
}

// ✅ POST + RequestBody（适合复杂查询或多参数）
@PostMapping("/search")
public CommonResult<CommonResult.PageData<AgentResponse>> pageAgent(
        @RequestBody @Valid AgentListRequest request) {
    // Feign 返回 ApiResponse，转换为本地 Response
    CommonResult<CommonResult.PageData<AgentApiResponse>> result =
            agentFeignClient.pageAgent(request);
    return convertToPageResponse(result);
}

// ✅ 路径参数（最多一个，放在URL最后）
@GetMapping("/{agentId}")
public CommonResult<AgentResponse> getAgentById(@PathVariable("agentId") Long agentId) {
    // Feign 返回 ApiResponse，转换为本地 Response
    AgentApiResponse apiResponse = agentFeignClient.getAgentById(agentId).getData();
    return CommonResult.success(convertToAgentResponse(apiResponse));
}
```

#### 应用服务参数规则（mall-agent/mall-user）

应用服务仅供内部 Feign 调用，参数封装严格：

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

// ❌ 错误：应用服务禁止路径参数
@GetMapping("/{agentId}/detail")
public CommonResult<AgentApiResponse> getAgentById(@PathVariable("agentId") Long agentId) {
    // 错误！应该使用 @RequestParam("agentId")
}
```

### 3.4 Swagger 文档注解

使用 OpenAPI 3.0 规范注解：

```java

@RestController
@RequestMapping("/admin/api/v1/agent")
@Tag(name = "智能员工接口", description = "智能员工管理相关接口")
@Slf4j
public class AgentController {

    @PostMapping("/create")
    @Operation(summary = "创建智能员工")
    public CommonResult<Long> createAgent(
            @RequestBody @Valid AgentCreateRequest request) {
        // ...
    }
}
```

---

## 4. 异常处理规范

### 4.1 三种标准异常

仅允许使用以下三种标准异常类型，均支持自定义错误消息：

| 异常类型                                | 使用场景     | 示例              |
|-------------------------------------|----------|-----------------|
| `MethodArgumentValidationException` | 参数校验失败   | 必填字段为空、格式错误     |
| `RemoteApiCallException`            | 远程服务调用失败 | Feign 调用超时、返回错误 |
| `BusinessException`                 | 业务规则违反   | 用户不存在、余额不足      |

**三种异常均支持以下构造方式**：

```java
// 1. 仅错误码 - 使用错误码的默认消息
throw new BusinessException(ErrorCodeEnum.AGENT_BUSINESS_ERROR);
throw new MethodArgumentValidationException(ErrorCodeEnum.PARAM_ERROR);
throw new RemoteApiCallException(ErrorCodeEnum.REMOTE_ERROR);

// 2. 错误码 + 自定义消息 - 优先使用自定义消息
throw new BusinessException(ErrorCodeEnum.AGENT_BUSINESS_ERROR, "岗位类型不存在");
throw new MethodArgumentValidationException(ErrorCodeEnum.PARAM_ERROR, "编码不能为空");
throw new RemoteApiCallException(ErrorCodeEnum.REMOTE_ERROR, "用户服务调用失败");

// 3. 错误码 + 异常原因 - 保留异常堆栈
throw new BusinessException(ErrorCodeEnum.AGENT_BUSINESS_ERROR, cause);
throw new MethodArgumentValidationException(ErrorCodeEnum.PARAM_ERROR, cause);
throw new RemoteApiCallException(ErrorCodeEnum.REMOTE_ERROR, cause);

// 4. 错误码 + 自定义消息 + 异常原因
throw new BusinessException(ErrorCodeEnum.AGENT_BUSINESS_ERROR, "操作失败", cause);
throw new MethodArgumentValidationException(ErrorCodeEnum.PARAM_ERROR, "参数校验失败", cause);
throw new RemoteApiCallException(ErrorCodeEnum.REMOTE_ERROR, "调用失败", cause);
```

**使用原则**：
- 优先使用方式 1 或 2，明确错误类型
- 方式 2 的自定义消息会覆盖错误码的默认消息
- 方式 3、4 用于需要保留异常堆栈的场景

### 4.2 异常处理规范

由于有全局异常捕获（`GlobalExceptionHandler`），Controller 层**不捕获任何异常**，直接抛出由全局处理器统一处理：

```java

@PostMapping("/create")
public CommonResult<Long> createAgent(
        @RequestBody @Valid AgentCreateRequest request) {
    Long agentId = agentService.createAgent(request);
    return CommonResult.success(agentId);
}
```

**说明**:

- 参数校验异常（`MethodArgumentValidationException`）→ 自动返回 400 错误
- 业务异常（`BusinessException`）→ 自动返回业务错误码
- 远程调用异常（`RemoteApiCallException`）→ 自动返回服务错误
- 其他未捕获异常 → 自动返回 500 系统错误

### 4.3 自定义异常

如需自定义异常，放在 `domain/exception` 目录下：

```java
public class AgentDomainException extends RuntimeException {
    private final String errorCode;

    public AgentDomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
```

---

## 5. 响应格式规范

### 5.1 标准响应对象

使用项目中已有的 `CommonResult<T>` 统一响应格式：

```java
// 成功响应
CommonResult.success(data);                    // 返回成功数据
CommonResult.success();                      // 返回成功无数据
CommonResult.success(data, "自定义消息");       // 返回成功数据和自定义消息

// 分页响应
CommonResult.pageSuccess(items, totalCount);   // 分页成功响应

// 失败响应
CommonResult.failed(errorCode);              // 使用错误码枚举
CommonResult.failed(errorCode, "错误信息");    // 错误码 + 自定义消息
```

### 5.2 响应规范示例

**普通列表查询**：

```java

@GetMapping("/list")
@Operation(summary = "查询智能员工列表")
public CommonResult<List<AgentApiResponse>> listAllAgents() {
    // Service层完成DO到Response的转换
    return agentService.listAllAgents();
}
```

**分页查询**：

```java

@GetMapping("/page")
@Operation(summary = "分页查询智能员工")
public CommonResult<CommonResult.PageData<AgentApiResponse>> pageAgent(
        @RequestParam(defaultValue = "1") Integer current,
        @RequestParam(defaultValue = "10") Integer size) {

    // Controller 层只做参数转换和调用 Service，不做业务逻辑
    AgentQuery query = new AgentQuery();
    query.setPageNum(current);
    query.setPageSize(size);

    // Service 层完成 DO 到 Response 的转换
    return agentService.pageAgent(query);
}
```

**规范说明**：

- **所有 Controller 返回必须是 Response 类型**
- **数据转换（DO → Response）在 Service 层完成**，Controller 层不做业务逻辑
- Controller 层职责：参数转换（Request → Query/DTO）+ 调用 Service + 直接返回

**单个对象查询**：

```java

@GetMapping("/{id}")
@Operation(summary = "根据ID查询智能员工")
public CommonResult<AgentApiResponse> getAgentById(@PathVariable("id") Long id) {
    // Service层完成DO到Response的转换，并处理不存在的情况
    return agentService.getAgentById(id);
}
```

---

## 6. 日志规范

### 6.1 日志级别使用

| 级别      | 使用场景                      | 示例             |
|---------|---------------------------|----------------|
| `ERROR` | 系统错误、异常、需要立即处理的问题         | 数据库连接失败、空指针异常  |
| `WARN`  | 警告、潜在问题、非预期的业务情况、暂时未实现的功能 | 脏数据、配置缺失、功能待实现 |
| `INFO`  | 重要业务操作、状态变更               | 创建成功、更新成功、删除成功 |
| `DEBUG` | 调试信息、入口参数、详细的执行过程         | 方法入参、查询条件、执行步骤 |

### 6.2 日志内容规范

```java
// ✅ 记录入口参数（DEBUG级别）
log.debug("创建智能员工, request: {}",request);

// ✅ 记录关键步骤（DEBUG级别）
log.debug("调用用户服务查询用户信息, userId: {}",userId);

// ✅ 记录重要业务操作（INFO级别）
log.info("创建智能员工成功, agentId: {}",agentId);

// ✅ 记录异常（ERROR级别）
log.error("创建智能员工失败, request: {}",request, e);

// ✅ 记录性能信息（INFO级别）
log.info("查询智能员工列表完成, 耗时: {}ms, 结果数: {}",duration, count);

// ✅ 记录警告（WARN级别）
log.warn("用户数据不完整, userId: {}, 缺失字段: {}",userId, missingFields);

// ✅ 记录暂时未实现的功能（WARN级别）
log.warn("功能暂未实现: {}",featureName);
```

### 6.3 分层日志规范

#### Controller 层

```java

@PostMapping("/create")
public CommonResult<Long> createAgent(@RequestBody @Valid AgentCreateRequest request) {
    // 入口参数使用 DEBUG
    log.debug("创建智能员工, request: {}", request);

    Long agentId = agentService.createAgent(request);

    // 成功操作使用 INFO
    log.info("创建智能员工成功, agentId: {}", agentId);
    return CommonResult.success(agentId);
}
```

#### Service 层

```java

@Override
@Transactional(rollbackFor = Exception.class)
public Long createAgent(AgentCreateDTO dto) {
    log.debug("创建智能员工开始, dto: {}", dto);

    // 参数校验
    validateCreateRequest(dto);

    // 业务逻辑
    AimAgentDO entity = convertToEntity(dto);
    aimAgentService.save(entity);

    log.info("创建智能员工成功, agentId: {}", entity.getId());
    return entity.getId();
}
```

#### 异常处理

```java
// 全局异常处理器
@ExceptionHandler(BusinessException.class)
public CommonResult<Void> handleBusinessException(BusinessException e) {
    // 业务异常使用 WARN（非系统错误）
    log.warn("业务异常: {}", e.getMessage());
    return CommonResult.failed(e.getErrorCode());
}

@ExceptionHandler(Exception.class)
public CommonResult<Void> handleException(Exception e) {
    // 系统异常使用 ERROR
    log.error("系统异常: {}", e.getMessage(), e);
    return CommonResult.failed(ErrorCodeEnum.SYSTEM_ERROR);
}
```

---

## 7. 检查清单

### 代码生成前检查

- [ ] 类名符合命名规范
- [ ] 包路径符合服务类型（门面/应用/支撑）
- [ ] 使用了正确的 Lombok 注解
- [ ] Request/Response 实现了 Serializable
- [ ] Response/VO 中的 LocalDateTime 字段使用了 @JsonFormat（DO/DTO 不需要）

### 代码生成后检查

- [ ] 没有使用 BeanUtils.copyProperties
- [ ] 没有使用 QueryWrapper 或 SELECT *
- [ ] 批量操作使用了 batchInsert/batchUpdate
- [ ] 异常处理使用了三种标准异常
- [ ] 响应格式使用了 CommonResult
- [ ] 包含了必要的日志记录

---

## 相关文档

- **代码生成模板**：`.qoder/skills/java-code-generation.md`
- **架构规范**：`.qoder/rules/05-architecture-standards.md`
