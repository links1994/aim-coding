---
name: java-code-generation
description: 根据项目规范生成 Java 微服务代码。在用户说"生成代码"、"委托：生成代码"或技术规格书已确认并准备实现时使用。
---

# Java 代码生成 Skill

基于技术规格书生成符合项目规范的 Java 微服务代码。

> **执行方式**：优先委托给 `code-generator` Agent 执行；如果 Sub-Agent 不可用，则使用本 Skill 执行

---

## 前置依赖（必须读取）

按优先级排序：

1. `.qoder/rules/common-coding-standards.md` — 通用编码规范
2. `.qoder/rules/common-architecture-standards.md` — 通用架构规范
3. `.qoder/rules/project-naming-standards.md` — 项目命名规范
4. `.qoder/rules/project-error-code-standards.md` — 项目错误码规范
5. `.qoder/rules/project-common-result-standards.md` — 项目响应格式规范
6. `.qoder/rules/project-operator-id-standards.md` — 项目操作人ID规范

---

## 触发条件

- 技术规格书（`tech-spec.md`）已生成并确认
- 用户明确指令："生成代码" 或 "委托：生成代码"
- 主 Agent 在第 4 阶段调用

---

## 输入

- `artifacts/tech-spec.md` — 技术规格书
- `artifacts/openapi.yaml` — API 定义
- `orchestrator/ALWAYS/RESOURCE-MAP.yml` — 项目资源映射
- `SCOPE.yml` — 写入范围控制
- 上述所有规范文件

---

## 输出

- Java 源代码 → `{service}/src/main/java/`
- `artifacts/code-generation-report.md` — 代码生成报告

---

## 工作流程

### 步骤 1：读取输入和查询知识库

1. 读取技术规格书、API 定义、RESOURCE-MAP.yml、SCOPE.yml
2. **读取所有前置依赖的规范文件**
3. 使用 `knowledge-base-query` Skill 查询类似功能作为参考

### 步骤 2：服务分析

- 确定服务类型（门面/应用/支撑）
- 确定代码层（Controller/Service/Mapper/Entity/DTO/Feign）
- 确定依赖关系

### 步骤 3：代码生成

按依赖顺序生成代码：

1. 被依赖方（如 mall-agent）
2. 调用方（如 mall-admin）
3. API 模块

**命名规范（参考 project-naming-standards.md）**：

| 类型 | 命名模式 | 示例 |
|------|----------|------|
| 实体类 | `Aim{Name}DO` | `AimJobTypeDO` |
| Mapper | `Aim{Name}Mapper` | `AimJobTypeMapper` |
| MP Service | `Aim{Name}Service` | `AimJobTypeService` |
| QueryService | `{Name}QueryService` | `JobTypeQueryService` |
| ManageService | `{Name}ManageService` | `JobTypeManageService` |
| Controller | `{Name}InnerController` | `JobTypeInnerController` |
| Feign | `{Name}RemoteService` | `JobTypeRemoteService` |

### 步骤 4：规范检查（强制）

使用检查清单逐条验证（见下文）。

### 步骤 5：生成报告

生成 `artifacts/code-generation-report.md`。

---

## 代码模板

代码模板统一维护在：`.qoder/skills/04-java-code-generation/_TEMPLATE/`

| 模板文件 | 说明 |
|----------|------|
| `mapper-java.md` | Mapper 接口模板 |
| `mapper-xml.md` | Mapper XML 模板 |
| `entity-do.md` | DO 实体模板 |

---

## 检查清单（强制）

代码生成完成后，**必须**逐条检查：

### 命名规范

- [ ] 实体类：`Aim{Xxx}DO` 格式
- [ ] Service：`Aim{Xxx}Service` / `{Name}QueryService` / `{Name}ManageService`
- [ ] Mapper：`Aim{Xxx}Mapper`，继承 `BaseMapper<Aim{Xxx}DO>`
- [ ] Controller：`{Name}InnerController`
- [ ] Feign：`{Name}RemoteService`
- [ ] 表名：`aim_{模块}_{业务名}`，小写下划线

### 类结构

- [ ] DO 实体继承 `BaseDO`，包含 `serialVersionUID = -1L`
- [ ] Request/Response 实现 `Serializable`，`serialVersionUID = -1L`
- [ ] Mapper 标注 `@Mapper` 注解
- [ ] MP Service 继承 `ServiceImpl<XxxMapper, XxxDO>`

### 时间格式化

- [ ] DO/DTO **禁止**使用 `@JsonFormat`
- [ ] Response/VO **必须**使用 `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")`

### 数据转换

- [ ] **禁止**使用 `BeanUtils.copyProperties`
- [ ] Service 层提供 `convertToXXX` 方法
- [ ] Controller 负责 Request→Query/DTO，Service 负责 DO→Response

### 数据库访问

- [ ] **禁止**在 QueryService/ManageService 中使用 `QueryWrapper`
- [ ] **禁止**直接调用 `getBaseMapper()`
- [ ] XML 必须抽取 `Base_Column_List`，禁止 `SELECT *`
- [ ] 所有查询必须包含软删除过滤 `is_deleted = 0`

### 异常处理

- [ ] 仅使用标准异常：`MethodArgumentValidationException`、`RemoteApiCallException`、`BusinessException`
- [ ] Controller 层不捕获异常，直接抛出

### 响应格式

- [ ] Controller 返回 `CommonResult<T>`
- [ ] 分页返回 `CommonResult<CommonResult.PageData<XxxResponse>>`
- [ ] 使用 `CommonResult.success()` / `CommonResult.pageSuccess()` / `CommonResult.failed()`

### 参数注解

- [ ] 门面服务：GET 可用多个 `@RequestParam`，POST 用 `@RequestBody`
- [ ] 应用服务：≤2 个基础类型用 `@RequestParam`，否则 `@RequestBody`

### 操作人 ID

- [ ] 门面服务：使用 `@RequestHeader` + `UserInfoUtil.getUserInfo(user).getId()`
- [ ] ApiRequest 必须包含 `operatorId` 字段
- [ ] 应用服务：从 ApiRequest 获取 `operatorId`

### 枚举使用

- [ ] 状态使用 `StatusEnum`（ENABLE=1, DISABLE=0）
- [ ] 删除标志使用 `DeleteStatusEnum`（UNDELETE=0, DELETE=1）

### 错误码

- [ ] 格式符合 `SSMMTNNN`（8位数字）
- [ ] 系统代码：20=Common, 30=Admin, 40=Agent, 50=User

---

## 返回格式

```
状态：已完成/失败
报告：artifacts/code-generation-report.md
输出：N 个文件
  - repos/mall-agent/...
  - repos/mall-admin/...
决策点：[如有]
```
