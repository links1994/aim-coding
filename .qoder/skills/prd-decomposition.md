---
name: prd-decomposition
description: 根据 PRD 或简要需求描述进行需求拆解，生成包含子需求详细信息和依赖关系的需求分解文档
tools: Read, Write, Grep
---

# PRD 需求分解 Skill

根据输入的 PRD 文档或简要需求描述，按照规范进行需求拆解。

> **前置条件**：执行前必须先读取 `.qoder/rules/01-prd-decomposition.md` 获取规范标准

---

## 触发条件

- 用户提供了 PRD 文档路径或简要需求描述
- 用户明确指令："分解需求" 或 "委托: 需求分解"
- 主 Agent 在 Phase 1 调用

---

## 输入

- PRD 文档路径（如 `inputs/prd/xxx-prd.md`）或简要需求描述
- `orchestrator/ALWAYS/RESOURCE-MAP.yml` — 项目资源映射
- `.qoder/rules/01-prd-decomposition.md` — 需求拆分规范（**必须先读取**）

---

## 输出

- 需求分解文档 → `orchestrator/PROGRAMS/{program_id}/workspace/decomposition.md`
- 更新 STATUS.yml → 标记为 done，仅保留"需求拆分"阶段

---

## Program 命名规范

拆分类型 Program 命名：`{原始ID}-decomposition`

示例：
- 原始需求：P-2026-001-ai-agent-platform
- 拆分 Program：P-2026-001-decomposition

---

## 工作流程

### Step 1: 读取输入与规范

1. 读取 PRD 文档或解析简要需求描述
2. 读取项目资源映射（RESOURCE-MAP.yml）
3. **必须读取**需求拆分规范（01-prd-decomposition.md）

### Step 2: 知识库查询（自动复用已有功能）

**使用 knowledge-base-query Skill 查询**：

```
查询类型: feature
关键词: {PRD功能关键词}

目的:
- 发现是否已有类似功能实现
- 参考已有功能的拆分方式
- 识别可复用的子需求
```

**查询结果处理**：
- 如有相似功能档案 → 参考其拆分结构，标注可复用部分
- 如无相似功能 → 按标准流程进行拆分

### Step 3: 功能分析

1. **解析 PRD 结构**
   - 提取功能模块列表
   - 识别用户故事和验收标准
   - 识别数据实体和业务流程

2. **按规范标记服务归属**（引用 Rule 中的定义）
   - 管理后台功能 → 标记 `[mall-admin]`
   - APP/商家端功能 → 标记 `[mall-app]`
   - AI 对话功能 → 标记 `[mall-chat]`
   - 智能员工相关逻辑 → 标记 `[mall-agent]`
   - 用户相关操作 → 标记 `[mall-user]`
   - 数据库设计 → 标记 `[DB]`

### Step 4: 依赖分析

分析以下内容并记录到各子需求中：

1. **服务调用链**：门面服务 → 应用服务 → 支撑服务
2. **模块依赖**：Controller → Service → Repository
3. **Feign 调用关系**：调用方依赖被调用方
4. **数据依赖**：哪些子需求依赖哪些数据库表

### Step 5: 生成需求分解文档

按规范格式生成 `decomposition.md`，包含：
- 需求概述
- 子需求列表（带服务标记，标注可复用部分）
- 数据库设计汇总
- 依赖关系图
- 依赖矩阵
- 开发顺序建议
- **复用建议**（基于知识库查询结果）
- 检查清单

**文档格式参考**：`.qoder/rules/01-prd-decomposition.md` 中的"输出格式规范"章节

---

## 返回格式

```
状态：已完成
报告：orchestrator/PROGRAMS/{program_id}/workspace/decomposition.md
产出：1 个文件（需求分解文档）

Program 状态：
- 名称: {program_id}-decomposition
- 阶段: 需求拆分 done
- 状态: done（本 Program 已结束）

后续操作：
为单个 REQ 创建新的 Implementation Program：
  "创建 Program {program_id}-REQ-xxx 实现 [需求名称]"

例如：
  "创建 Program P-2026-001-REQ-031 实现智能员工应用服务"
```
