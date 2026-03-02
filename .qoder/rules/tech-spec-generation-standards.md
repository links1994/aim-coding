---
trigger: model_decision
description: 技术规格书生成规范（纯规范定义）
---

# 技术规格书规范

本文档只定义**规范标准**，不涉及执行流程。执行流程见 Skill 文件。

---

## 1. 输入要求

- `orchestrator/PROGRAMS/{decomposition_program_id}/artifacts/decomposition.md` — 需求拆分结果
- `orchestrator/PROGRAMS/{program_id}/artifacts/answers.md` — 澄清确认结果
- `orchestrator/PROGRAMS/{program_id}/artifacts/decisions.md` — 技术决策记录
- `.qoder/rules/common-architecture-standards.md` — 通用架构规范
- `.qoder/rules/project-naming-standards.md` — 项目命名规范
- `.qoder/rules/project-error-code-standards.md` — 项目错误码规范
- 用户补充的上下文（现有表结构、接口文档等）

---

## 2. 输出要求

- `orchestrator/PROGRAMS/{program_id}/artifacts/tech-spec.md` — 主技术规格书
- `orchestrator/PROGRAMS/{program_id}/artifacts/openapi.yaml` — OpenAPI 定义
- `orchestrator/PROGRAMS/{program_id}/artifacts/checklist.md` — 验收标准
- （可选）更新的需求文档（decomposition.md, answers.md, decisions.md）

---

## 3. 技术规格书结构标准

### 3.1 必须包含的章节

```markdown
# 技术规格书

## 1. 概述
### 1.1 目标
### 1.2 背景
### 1.3 涉及子需求
### 1.4 技术选型理由

## 2. 数据模型设计
### 2.1 ER 图
### 2.2 表结构定义
### 2.3 表变更清单（新增/修改/删除）

## 3. API 接口定义
### 3.1 接口概览
### 3.2 接口详情（按服务分组）
### 3.3 Feign 接口定义
### 3.4 错误码规范

## 4. 架构设计
### 4.1 服务调用关系
### 4.2 时序图（关键流程）
### 4.3 代码结构

## 5. 业务规则实现
### 5.1 状态机设计
### 5.2 校验规则
### 5.3 业务逻辑流程

## 6. 验收标准

## 7. 附录
### 7.1 相关文档
### 7.2 需求追溯矩阵
### 7.3 变更影响分析
### 7.4 变更记录
```

---

## 4. 错误码规范

> **参考文档**: `.qoder/rules/project-error-code-standards.md`

错误码采用 **8位数字** 格式：`SSMMTNNN`

- **SS**: 系统代码 (System) - 20=Common, 30=Admin, 40=Agent, 50=User
- **MM**: 模块代码 (Module) - 如 01=Employee, 02=JobType
- **T**: 错误类型 (Type) - 0=Success, 1=Client Error, 2=Server Error, 3=Business Error
- **NNN**: 错误序号 (Number) - 001-999

### 4.1 错误类型定义

| 类型值 | 类型名称 | HTTP状态 | 说明 |
|--------|----------|----------|------|
| 0 | 成功 | 200 | 正常业务处理成功 |
| 1 | 客户端错误 | 200 | 参数校验失败、非法请求 |
| 2 | 服务端错误 | 200 | 系统异常、数据库错误 |
| 3 | 业务错误 | 200 | 业务规则违反、状态不允许 |

### 4.2 错误序号分段（按类型）

| 范围 | 用途 |
|------|------|
| 001-099 | 通用错误（模块级通用错误码） |
| 100-199 | 参数错误（参数校验相关） |
| 200-299 | 业务错误（业务规则相关） |
| 300-399 | 数据错误（数据不存在、重复等） |
| 400-499 | 状态错误（状态不允许、冲突等） |
| 500-599 | 权限错误（权限不足、未授权等） |
| 900-999 | 系统错误（系统级异常） |

### 4.3 示例错误码

| 错误码 | 类型 | 描述 | 使用场景 |
|--------|------|------|----------|
| 40031001 | Client | 参数错误 | 通用参数校验失败 |
| 40031002 | Client | 员工姓名不能为空 | name 字段为空 |
| 40032001 | Business | 员工已存在 | 手机号已注册 |
| 40032002 | Business | 员工不存在 | 查询的员工 ID 不存在 |
| 40033001 | Data | 员工信息不存在 | 根据 ID 查询无记录 |

**说明**：所有错误响应 HTTP 状态码均为 200，错误类型通过响应体中的 `code` 字段区分。

### 4.4 错误消息规范

- 错误消息应简洁明了，直接说明问题
- 所有含动态内容的错误消息使用 `String.format()` 或占位符方式
- 示例：`String.format("当前有 %d 名员工关联", employeeCount)`

---

## 5. 变更影响分析矩阵

当技术规格发生变更时，使用以下矩阵判断是否需同步更新需求文档：

| 变更类型 | 影响范围 | 是否需更新需求文档 | 更新目标 |
|----------|----------|-------------------|----------|
| 数据库表名/字段变更 | 数据模型 | 是 | decomposition.md |
| 接口路径/方法变更 | API 设计 | 是 | decomposition.md |
| 接口字段增减 | API 设计 | 判断 | answers.md（如影响业务规则） |
| 依赖服务变更 | 架构设计 | 是 | decomposition.md |
| 状态机变更 | 业务逻辑 | 是 | answers.md, decisions.md |
| 校验规则变更 | 业务逻辑 | 是 | answers.md |
| 代码分层调整 | 实现细节 | 否 | - |
| 缓存/日志策略 | 技术实现 | 否 | - |

---

## 6. 需求追溯矩阵

| 子需求 | 设计章节 | 数据库表 | API 接口 | 实现类 |
|--------|----------|----------|----------|--------|
| REQ-001 | 3.1, 4.2 | aim_agent_employee | POST /admin/api/v1/agent | AgentAdminController |
| REQ-003 | 3.3, 4.2 | aim_agent_employee | POST /inner/api/v1/agent | AgentService |
| REQ-005 | 2.2 | aim_agent_employee, aim_skill | - | - |