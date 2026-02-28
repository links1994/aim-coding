---
trigger: model_decision
description: 技术规格书生成规范（纯规范定义）
---

# 技术规格书规范

本文档只定义**规范标准**，不涉及执行流程。执行流程见 Skill 文件。

---

## 1. 输入要求

- `orchestrator/PROGRAMS/{decomposition_program_id}/workspace/decomposition.md` — 需求拆分结果
- `orchestrator/PROGRAMS/{program_id}/workspace/answers.md` — 澄清确认结果
- `orchestrator/PROGRAMS/{program_id}/workspace/decisions.md` — 技术决策记录
- `.qoder/rules/05-architecture-standards.md` — 架构规范
- 用户补充的上下文（现有表结构、接口文档等）

---

## 2. 输出要求

- `orchestrator/PROGRAMS/{program_id}/workspace/tech-spec.md` — 主技术规格书
- `orchestrator/PROGRAMS/{program_id}/workspace/openapi.yaml` — OpenAPI 定义
- `orchestrator/PROGRAMS/{program_id}/workspace/checklist.md` — 验收标准
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

> **参考文档**: `.qoder/repowiki/specs/错误码规范.md`

错误码采用 **8位数字字符串** 格式：`SSMMTNNN`

- **SS**: 系统标识 (System Identifier) - 电商主系统为 `10`
- **MM**: 模块标识 (Module Identifier) - 智能员工模块为 `09`
- **T**: 错误类型 (Error Type)
- **NNN**: 错误序号 (Error Number)

### 4.1 错误类型定义

| 类型值 | 类型名称 | 说明 |
|--------|----------|------|
| 1 | 参数错误 | 输入参数校验失败 |
| 2 | 业务错误 | 业务规则校验失败 |
| 3 | 远程调用错误 | Feign 调用失败 |
| 4 | 数据库错误 | 数据库操作异常 |
| 5 | 系统错误 | 系统内部错误 |

### 4.2 错误序号分段（按类型）

| 范围 | 用途 |
|------|------|
| 001-099 | 基础参数校验错误 |
| 100-199 | 业务规则验证错误 |
| 200-299 | 业务状态验证错误 |
| 300-399 | 远程服务调用错误 |
| 400-499 | 数据库操作错误 |
| 500-599 | 系统级错误 |

### 4.3 示例错误码

| 错误码 | 描述 | HTTP 状态 | 使用场景 |
|--------|------|-----------|----------|
| 10091001 | 参数错误 | 400 | 通用参数校验失败 |
| 10092001 | 业务错误 | 400 | 通用业务规则错误 |
| 10092002 | 资源不存在 | 404 | 查询的资源不存在 |
| 10095001 | 系统错误 | 500 | 系统内部异常 |

### 4.4 错误消息规范

- 所有含动态内容的错误消息必须使用 `{0}, {1}, {2}...` 占位符
- 示例：`商品 {0} 库存不足，当前库存 {1}`

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
| REQ-001 | 3.1, 4.2 | aim_employee | POST /admin/api/v1/agent | AgentAdminController |
| REQ-003 | 3.3, 4.2 | aim_employee | POST /inner/api/v1/agent | AgentService |
| REQ-005 | 2.2 | aim_employee, aim_skill | - | - |