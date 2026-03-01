---
name: prd-decomposition
description: 将 PRD 分解为可实现的子需求。在用户说"分解 PRD"、"拆分需求"或需要将产品需求分解为带有服务归属、依赖关系和验收标准的 REQ 项时使用。
---

# PRD 分解 Skill

将产品需求文档（PRD）分解为可实现的子需求。

> **前置条件**：执行前必须先读取 `.qoder/rules/01-prd-decomposition.md` 以获取规范标准。

---

## 触发条件

- 用户指令："分解 PRD" 或 "拆分需求"
- 为分解类型创建的新 Program
- 需要将 PRD 分解为 REQ 项

---

## 输入

- PRD 文档路径（如：`inputs/prd/ai-agent-platform-prd.md`）
- 原型文档（可选，如：`inputs/prd/ai-agent-platform-wireframes.md`）
- `.qoder/rules/01-prd-decomposition.md` — 分解规范（**必须先读取**）
- 当前 Program 的 STATUS.yml

---

## 输出

- 分解文档 → `orchestrator/PROGRAMS/{program_id}/workspace/decomposition.md`
- 更新 STATUS.yml → 标记分解完成

---

## 工作流程

### 步骤 1：读取 PRD 和规范

1. 读取 PRD 文档
2. 读取原型文档（如果存在）
3. **必须先读取** 分解规范（01-prd-decomposition.md）
4. 检查 STATUS.yml

### 步骤 2：分析 PRD 结构

分析 PRD 以识别：
- 功能模块
- 用户角色和场景
- 业务流程
- 接口需求

**确认检查点**：如果 PRD 包含模糊的需求、缺失的业务规则或不明确的范围，在继续之前触发选项式询问向用户确认。

### 步骤 3：服务归属分析

基于架构标准，确定每个需求的服务归属：

| 服务类型 | 职责 | 示例 |
|--------------|----------------|----------|
| mall-admin | 管理门面 | 管理接口、仪表盘 |
| mall-app | 客户端门面 | 用户界面、移动端 API |
| mall-chat | 对话门面 | AI 对话、流式 API |
| mall-agent | 核心业务 | 员工、配额、激活服务 |
| mall-user | 用户支撑 | 用户信息、等级查询 |

### 步骤 4：生成 REQ 项

为每个功能点生成 REQ 项：

```markdown
### REQ-XXX: [服务] 功能名称

- **来源**：PRD 第 X.X 节，原型 U-XXX
- **描述**：简要描述
- **代码位置**：`repos/{service}/src/main/java/...`
- **接口路径**：`METHOD /path`
- **依赖关系**：
  - 依赖服务：xxx
  - 依赖表：xxx
- **验收标准**：
  - [ ] 标准 1
  - [ ] 标准 2
```

### 步骤 5：依赖分析

分析 REQ 之间的依赖关系：
- 数据库表依赖
- 服务调用依赖
- 前后端依赖

### 步骤 6：生成分解文档

生成完整的 decomposition.md：

```markdown
# 分解结果

## Program: {program-id}

### 概述
- **来源**：PRD 文档
- **模块**：X 个功能模块
- **REQ 数量**：Y 个子需求

## 模块 1：XXX

### REQ-001: [服务] 功能
...

## 依赖矩阵

| REQ | 依赖 | 依赖表 | 被依赖 |
|-----|--------------|------------------|-------------|
| REQ-001 | mall-agent | aim_xxx | REQ-002, REQ-003 |

## 开发顺序

1. REQ-XXX: 原因
2. REQ-XXX: 原因
```

---

## 返回格式

```
状态：已完成
报告：orchestrator/PROGRAMS/{program_id}/workspace/decomposition.md
REQ 数量：X
模块数：Y

Program 状态更新：
  - phase: done
  - status: deployed

下一步：
  - 为每个 REQ 创建实现 Program
```
