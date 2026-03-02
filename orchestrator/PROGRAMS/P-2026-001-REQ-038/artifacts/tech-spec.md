# 技术规格书 - P-2026-001-REQ-038

## 1. 概述

### 1.1 目标

实现 mall-agent 服务的岗位类型管理功能，包括岗位类型的CRUD操作及关联员工数统计。

### 1.2 背景

- **来源**: PRD 第 5.2 节，线框图 A-002/A-003
- **所属需求**: REQ-038
- **前置依赖**: 无（基础数据表）
- **被依赖**: REQ-018（mall-admin岗位管理接口）、REQ-031（智能员工服务）

### 1.3 涉及子需求

| 子需求 | 说明 |
|--------|------|
| REQ-038 | 岗位类型管理服务（本需求） |

### 1.4 技术选型理由

- **数据访问**: MyBatis-Plus（小表，数据量 < 1000 条，使用 MP 分页）
- **服务分层**: 遵循五层架构（Controller → DomainService → Query/Manage Service → AimXxxService → AimXxxMapper）
- **编码规范**: 遵循 [common-coding-standards.md](../../../../.qoder/rules/common-coding-standards.md) Java 编码规范
  - 使用 Lombok 注解（`@Data`, `@Slf4j` 等）
  - DO/DTO 转换使用手动 convert 方法，禁止使用 `BeanUtils.copyProperties`
  - SQL 统一放在 XML Mapper 中
  - 仅使用三种标准异常：`MethodArgumentValidationException`, `BusinessException`, `RemoteApiCallException`
- **员工数量统计**: 通过员工 Service 单独查询（TODO 标记，待智能员工需求完成后实现）

---

## 2. 数据模型设计

### 2.1 ER 图

```
┌─────────────────────────────────────┐
│           aim_agent_job_type              │
├─────────────────────────────────────┤
│ PK  id              BIGINT          │
│     name            VARCHAR(64)     │
│ UQ  code            VARCHAR(32)     │
│     description     VARCHAR(255)    │
│     status          TINYINT         │
│     sort_order      INT             │
│     create_time     DATETIME        │
│     update_time     DATETIME        │
│     is_deleted      TINYINT         │
│     creator_id      BIGINT          │
│     updater_id      BIGINT          │
└─────────────────────────────────────┘
```

**实体类命名**: `AimJobTypeDO`（遵循大驼峰 + DO 后缀规范）

### 2.2 表结构定义

```sql
CREATE TABLE aim_agent_job_type
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    -- 业务字段
    name        VARCHAR(64)  NOT NULL COMMENT '岗位类型名称（如：销售顾问、客服专员）',
    code        VARCHAR(32)  NOT NULL COMMENT '岗位类型编码（唯一，如：SALES、CS）',
    description VARCHAR(255) COMMENT '岗位描述',
    status      TINYINT      DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    sort_order  INT          DEFAULT 0 COMMENT '排序号',
    -- 通用字段
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted  TINYINT      DEFAULT 0 COMMENT '逻辑删除标记：0-未删除 1-已删除',
    creator_id  BIGINT COMMENT '创建人ID',
    updater_id  BIGINT COMMENT '更新人ID',
    -- 索引
    UNIQUE INDEX uk_code (code),
    INDEX idx_status (status),
    INDEX idx_sort_order (sort_order)
) COMMENT '岗位类型表'
    DEFAULT CHARSET = utf8mb4;
```

### 2.3 表变更清单

| 操作 | 表名 | 说明 |
|------|------|------|
| 新增 | aim_agent_job_type | 岗位类型表 |

---

## 3. API 接口定义

### 3.1 接口概览

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /inner/api/v1/job-types/list | 岗位类型列表 |
| POST | /inner/api/v1/job-types/create | 创建岗位类型 |
| PUT | /inner/api/v1/job-types/update | 更新岗位类型 |
| PUT | /inner/api/v1/job-types/status | 状态变更（启用/禁用） |
| DELETE | /inner/api/v1/job-types/delete | 删除岗位类型 |

### 3.2 接口详情

#### 3.2.1 岗位类型列表

**接口信息**
- **路径**: `POST /inner/api/v1/job-types/list`
- **说明**: 查询岗位类型列表，支持分页和关键词搜索

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | String | 否 | 关键词（匹配名称/编码） |
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页大小，默认10 |

**请求示例**

```json
{
  "keyword": "销售",
  "pageNum": 1,
  "pageSize": 10
}
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
        "code": "SALES",
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

> **注意**: 分页数据遵循 [CommonResult 响应格式规范](../../../../.qoder/repowiki/specs/common-result-spec.md)，使用 `items` 和 `totalCount` 字段。

#### 3.2.2 创建岗位类型

**接口信息**
- **路径**: `POST /inner/api/v1/job-types/create`
- **说明**: 创建新的岗位类型

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 岗位类型名称（1-64字符） |
| code | String | 是 | 岗位类型编码（1-32字符，唯一） |
| description | String | 否 | 岗位描述（0-255字符） |
| sortOrder | Integer | 否 | 排序号，默认0 |

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

#### 3.2.3 更新岗位类型

**接口信息**
- **路径**: `PUT /inner/api/v1/job-types/update`
- **说明**: 更新岗位类型信息

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 岗位类型ID |
| name | String | 是 | 岗位类型名称（1-64字符） |
| description | String | 否 | 岗位描述（0-255字符） |
| sortOrder | Integer | 否 | 排序号 |

**响应数据**

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

#### 3.2.4 状态变更

**接口信息**
- **路径**: `PUT /inner/api/v1/job-types/status`
- **说明**: 启用或禁用岗位类型

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 岗位类型ID |
| status | Integer | 是 | 状态（0-禁用，1-启用） |

**响应数据**

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

#### 3.2.5 删除岗位类型

**接口信息**
- **路径**: `DELETE /inner/api/v1/job-types/delete`
- **说明**: 删除岗位类型（需校验无关联员工）

**请求参数**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 岗位类型ID |

**响应数据**

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 3.3 Feign 接口定义

本服务为被调用方，对外提供 Inner API，由 mall-admin 通过 Feign 调用。

Feign 客户端定义位于 `mall-agent-api` 模块：

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
    
    @DeleteMapping("/inner/api/v1/job-types/delete")
    CommonResult<Boolean> delete(@RequestParam("id") Long id);
}
```

**请求对象定义**:

```java
@Data
public class JobTypeListApiRequest implements Serializable {
    private static final long serialVersionUID = -1L;
    
    private String keyword;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
```

### 3.4 错误码规范

遵循 [common-coding-standards.md](../../../../.qoder/rules/common-coding-standards.md) 异常处理规范，仅使用三种标准异常类型：

| 异常类型 | 使用场景 | 示例 |
|---------|---------|------|
| `MethodArgumentValidationException` | 参数校验失败 | 必填字段为空、格式错误 |
| `BusinessException` | 业务规则违反 | 记录不存在、编码重复 |
| `RemoteApiCallException` | 远程服务调用失败 | Feign 调用超时 |

**错误码定义**:

| 错误码 | 描述 | HTTP 状态 | 使用场景 | 异常类型 |
|--------|------|-----------|----------|---------|
| 10091001 | 参数错误 | 200 | 通用参数校验失败 | `MethodArgumentValidationException` |
| 10091002 | 岗位类型名称不能为空 | 200 | name为空或空字符串 | `MethodArgumentValidationException` |
| 10091003 | 岗位类型编码不能为空 | 200 | code为空或空字符串 | `MethodArgumentValidationException` |
| 10091004 | 岗位类型编码格式错误 | 200 | code不符合规范 | `MethodArgumentValidationException` |
| 10092001 | 岗位类型已存在 | 200 | code重复 | `BusinessException` |
| 10092002 | 岗位类型不存在 | 200 | 查询/更新的ID不存在 | `BusinessException` |
| 10092003 | 岗位类型已被删除 | 200 | 操作已删除的记录 | `BusinessException` |
| 10092004 | 岗位类型有关联员工，无法删除 | 200 | 删除时员工数>0 | `BusinessException` |
| 10092005 | 岗位类型编码已存在 | 200 | 创建/更新时code冲突 | `BusinessException` |

**说明**：所有错误响应 HTTP 状态码均为 200，错误类型通过响应体中的 `code` 字段区分。

**异常使用示例**:
```java
// 参数校验异常
throw new MethodArgumentValidationException(ErrorCodeEnum.PARAM_ERROR, "岗位类型名称不能为空");

// 业务异常
throw new BusinessException(ErrorCodeEnum.JOB_TYPE_NOT_FOUND, "岗位类型不存在");
```

---

## 4. 架构设计

### 4.1 服务调用关系

```
mall-admin (门面服务)
    ↓ Feign
mall-agent (应用服务)
    ├── JobTypeInnerController
    ├── JobTypeDomainService
    │   ├── JobTypeQueryService
    │   └── JobTypeManageService
    │       └── AimJobTypeService
    │           └── AimJobTypeMapper
    └── (TODO: EmployeeService - 待实现)
```

### 4.2 时序图

#### 创建岗位类型

```
mall-admin    JobTypeInnerController    JobTypeDomainService    JobTypeManageService    AimJobTypeService    AimJobTypeMapper
    |                  |                        |                        |                      |                    |
    |---create()----->|                        |                        |                      |                    |
    |                  |---create()----------->|                        |                      |                    |
    |                  |                        |---create()----------->|                      |                    |
    |                  |                        |                        |---save()------------>|                    |
    |                  |                        |                        |                      |---insert()------->|
    |                  |                        |                        |                      |<------------------|
    |                  |                        |                        |<---------------------|                    |
    |                  |                        |<-----------------------|                      |                    |
    |                  |<-----------------------|                        |                      |                    |
    |<--return id-----|                        |                        |                      |                    |
```

#### 删除岗位类型（带校验）

```
mall-admin    JobTypeInnerController    JobTypeDomainService    JobTypeManageService    AimJobTypeService    AimJobTypeMapper
    |                  |                        |                        |                      |                    |
    |---delete()----->|                        |                        |                      |                    |
    |                  |---delete()----------->|                        |                      |                    |
    |                  |                        |---delete()----------->|                      |                    |
    |                  |                        |                        |---getById()--------->|                    |
    |                  |                        |                        |                      |---selectById()--->|
    |                  |                        |                        |                      |<------------------|
    |                  |                        |                        |<---------------------|                    |
    |                  |                        |                        |---check employee count (TODO)              |
    |                  |                        |                        |                      |                    |
    |                  |                        |                        |---removeById()------>|                    |
    |                  |                        |                        |                      |---delete()------->|
    |                  |                        |                        |                      |<------------------|
    |                  |                        |                        |<---------------------|                    |
    |                  |                        |<-----------------------|                      |                    |
    |                  |<-----------------------|                        |                      |                    |
    |<--return--------|                        |                        |                      |                    |
```

### 4.3 代码结构

遵循 [common-coding-standards.md](../../../../.qoder/rules/common-coding-standards.md) 和 [project-naming-standards.md](../../../../.qoder/rules/project-naming-standards.md) 包结构规范：

```
mall-agent/src/main/java/com/aim/mall/agent/
├── controller/
│   └── inner/
│       └── JobTypeInnerController.java    # Inner API 控制器
├── service/
│   ├── JobTypeDomainService.java          # 业务域服务（业务编排）
│   ├── JobTypeQueryService.java           # 查询服务（只读）
│   ├── JobTypeManageService.java          # 管理服务（增删改）
│   └── mp/
│       └── AimJobTypeService.java         # MyBatis-Plus 数据服务
├── mapper/
│   └── AimJobTypeMapper.java              # MyBatis Mapper接口
├── domain/
│   ├── entity/
│   │   └── AimJobTypeDO.java              # 数据库实体（DO后缀）
│   ├── dto/
│   │   ├── JobTypeDTO.java                # 内部DTO（DTO后缀）
│   │   ├── JobTypeCreateDTO.java          # 创建DTO
│   │   ├── JobTypeUpdateDTO.java          # 更新DTO
│   │   ├── JobTypeStatusDTO.java          # 状态更新DTO
│   │   └── JobTypePageQuery.java          # 分页查询（PageQuery后缀）
│   └── enums/
│       └── JobTypeStatusEnum.java         # 状态枚举（Enum后缀）
└── resources/
    └── mapper/
        └── AimJobTypeMapper.xml           # Mapper XML
```

**API 模块请求/响应对象**（位于 `mall-agent-api` 模块）：

```
mall-agent-api/src/main/java/com/aim/mall/agent/api/
├── dto/
│   ├── request/
│   │   ├── JobTypeListApiRequest.java     # 列表查询请求
│   │   ├── JobTypeCreateApiRequest.java   # 创建请求
│   │   ├── JobTypeUpdateApiRequest.java   # 更新请求
│   │   └── JobTypeStatusApiRequest.java   # 状态变更请求
│   └── response/
│       └── JobTypeApiResponse.java        # 岗位类型响应
└── feign/
    └── JobTypeRemoteService.java          # Feign 客户端
```

**命名规范说明**:
- 实体类: `AimJobTypeDO`（大驼峰 + DO 后缀）
- DTO 类: `JobTypeDTO`, `JobTypeCreateDTO`（大驼峰 + DTO 后缀）
- 查询参数: `JobTypePageQuery`（大驼峰 + PageQuery 后缀）
- 服务类: `JobTypeDomainService`, `JobTypeQueryService`, `JobTypeManageService`（大驼峰 + Service 后缀）
- MP 服务: `AimJobTypeService`（Aim + 大驼峰 + Service 后缀）
- Mapper: `AimJobTypeMapper`（Aim + 大驼峰 + Mapper 后缀）
- 枚举: `JobTypeStatusEnum`（大驼峰 + Enum 后缀）

---

## 5. 业务规则实现

### 5.1 状态机设计

```
┌─────────┐    创建    ┌─────────┐
│  不存在  │ ────────> │  启用   │
└─────────┘           └────┬────┘
                           │
                    禁用操作 │ 启用操作
                           ↓
                      ┌─────────┐
                      │  禁用   │
                      └─────────┘
```

**状态规则**:
- 新建时默认状态：**启用** (status = 1)
- 禁用时：允许有在职员工关联，但不能新增关联
- 启用时：暂无其他限制条件

### 5.2 校验规则

| 校验项 | 规则 | 错误码 |
|--------|------|--------|
| 名称必填 | name != null && !name.isBlank() | 10091002 |
| 名称长度 | 1 <= name.length() <= 64 | 10091002 |
| 编码必填 | code != null && !code.isBlank() | 10091003 |
| 编码格式 | 仅允许大写字母、数字、下划线 | 10091004 |
| 编码唯一 | 数据库唯一约束 | 10092005 |
| 删除校验 | 关联员工数必须为 0 | 10092004 |

### 5.3 业务逻辑流程

遵循 [common-architecture-standards.md](../../../../.qoder/rules/common-architecture-standards.md) 分层调用规范：
- `JobTypeQueryService` / `JobTypeManageService` 只能调用 `AimJobTypeService`
- `AimJobTypeService` 封装所有数据访问
- 禁止 `QueryWrapper` / `LambdaQueryWrapper`，SQL 统一放在 XML 中

#### 5.3.1 创建流程

1. 校验参数（name、code必填，格式正确）
2. 校验 code 是否已存在（调用 `AimJobTypeService` 查询）
3. 创建实体，设置默认状态为启用
4. 调用 `AimJobTypeService.save()` 保存到数据库
5. 返回新创建记录的 ID

#### 5.3.2 更新流程

1. 校验参数（id、name必填）
2. 调用 `AimJobTypeService.getById()` 查询记录是否存在且未删除
3. 更新字段（name、description、sortOrder）
4. **注意**：code 不允许修改
5. 调用 `AimJobTypeService.updateById()` 保存到数据库

#### 5.3.3 删除流程

1. 调用 `AimJobTypeService.getById()` 查询记录是否存在且未删除
2. **TODO**: 调用员工 Service 查询关联员工数量
3. 如果员工数 > 0，抛出 `BusinessException`
4. 调用 `AimJobTypeService.removeById()` 执行逻辑删除

#### 5.3.4 列表查询流程

1. 构建查询条件（keyword）
2. 调用 `AimJobTypeService.page()` 执行分页查询（小表使用 MP 分页）
3. **TODO**: 遍历结果，调用员工 Service 查询每个岗位的关联员工数量
4. Service 层完成 DO 到 Response 的转换
5. 组装响应数据

---

## 6. 验收标准

- [x] 设计 aim_agent_job_type 表结构并归档
- [x] 支持岗位类型CRUD操作
- [x] 返回关联员工数统计（TODO标记，待实现）
- [x] 支持启用/禁用状态控制
- [x] 删除时校验关联员工

---

## 7. 附录

### 7.1 相关文档

- [PROGRAM.md](../PROGRAM.md) - 任务定义
- [decomposition.md](../../P-2026-001-decomposition/workspace/decomposition.md) - 需求分解
- [common-coding-standards.md](../../../../.qoder/rules/common-coding-standards.md) - Java 编码规范
- [common-architecture-standards.md](../../../../.qoder/rules/common-architecture-standards.md) - 架构规范
- [project-naming-standards.md](../../../../.qoder/rules/project-naming-standards.md) - 项目命名规范

### 7.2 需求追溯矩阵

| 子需求 | 设计章节 | 数据库表 | API 接口 | 实现类 |
|--------|----------|----------|----------|--------|
| REQ-038 | 2.2, 3.2, 4.2, 5.3 | aim_agent_job_type | /inner/api/v1/job-types/* | JobTypeInnerController, JobTypeDomainService |

### 7.3 变更影响分析

| 变更类型 | 影响范围 | 是否需更新需求文档 | 更新目标 |
|----------|----------|-------------------|----------|
| 表字段变更 | 数据模型 | 是 | decomposition.md |
| 接口路径变更 | API 设计 | 是 | decomposition.md |
| 校验规则变更 | 业务逻辑 | 是 | 本规格书 |

### 7.4 变更记录

| 版本 | 日期 | 变更内容 | 变更人 |
|------|------|----------|--------|
| 1.0 | 2026-03-02 | 初始版本 | AI Agent |

---

## 8. TODO 清单

| 序号 | 描述 | 依赖 | 优先级 |
|------|------|------|--------|
| 1 | 实现员工数量统计查询 | 智能员工需求(REQ-031)完成后 | 高 |
| 2 | 创建 EmployeeService 接口及调用 | 智能员工需求完成后 | 高 |

**TODO 代码标记示例**:

遵循 [common-coding-standards.md](../../../../.qoder/rules/common-coding-standards.md) 日志规范，使用 `WARN` 级别记录待实现功能：

```java
// TODO: REQ-038 待智能员工需求完成后实现
// 调用 EmployeeService 查询岗位类型关联的员工数量
// Integer employeeCount = employeeService.countByJobTypeId(jobTypeId);
log.warn("功能暂未实现: 岗位类型关联员工数量统计, jobTypeId: {}", jobTypeId);
```

**日志规范要求**:
- `DEBUG`: 入口参数、关键步骤、详细执行过程
- `INFO`: 重要业务操作、状态变更
- `WARN`: 警告、潜在问题、暂时未实现的功能
- `ERROR`: 系统错误、异常

**示例**:
```java
// Controller 层
log.debug("创建岗位类型, request: {}", request);
log.info("创建岗位类型成功, jobTypeId: {}", jobTypeId);

// Service 层
log.debug("查询岗位类型列表, keyword: {}", keyword);
log.warn("岗位类型不存在, jobTypeId: {}", jobTypeId);
log.error("删除岗位类型失败, jobTypeId: {}", jobTypeId, e);
```
