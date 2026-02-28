---
name: tech-spec-generation
description: 生成详细技术规格书，支持迭代修正和双向追溯，确保设计文档与需求文档同步
tools: Read, Write, Grep, ask_user_question
language: zh-CN
output_language: 中文
---

# 技术规格书生成 Skill

> **语言约束**: 本 Skill 的所有输出必须使用中文，包括技术规格书、OpenAPI定义、验收清单和状态报告。

基于需求分解和澄清结果，生成详细的技术规格书。

> **前置条件**：执行前必须先读取 `.qoder/rules/03-tech-spec-generation.md` 和 `.qoder/rules/05-architecture-standards.md` 获取规范标准

---

## 触发条件

- 用户指令："生成技术规格书" 或 "设计 SPEC"
- 当前 Program 为 Implementation 类型
- 需求澄清已完成（workspace/answers.md 已生成）

---

## Program 类型

本 Skill 适用于 **Implementation Program**（实现类型）。

示例：在 `P-2026-001-REQ-031` Program 中运行本 Skill

---

## 输入

- `orchestrator/PROGRAMS/{decomposition_program_id}/workspace/decomposition.md` — 需求分解文档
- `orchestrator/PROGRAMS/{current_program_id}/workspace/answers.md` — 需求澄清结果
- `orchestrator/PROGRAMS/{current_program_id}/workspace/decisions.md` — 技术决策记录
- `.qoder/rules/03-tech-spec-generation.md` — 技术规格规范（**必须先读取**）
- `.qoder/rules/05-architecture-standards.md` — 架构规范（**必须先读取**）
- 当前 Program 的 STATUS.yml — 更新阶段状态

---

## 输出

- 技术规格书 → `orchestrator/PROGRAMS/{current_program_id}/workspace/tech-spec.md`
- OpenAPI 定义 → `orchestrator/PROGRAMS/{current_program_id}/workspace/openapi.yaml`
- 验收清单 → `orchestrator/PROGRAMS/{current_program_id}/workspace/checklist.md`
- 更新 STATUS.yml → 阶段从"技术规格"更新为"代码生成"

---

## 工作流程

### Step 1: 读取输入文档与规范

1. **推导 decomposition Program ID**
   - 当前 Program: `P-2026-001-REQ-031`
   - Decomposition Program: `P-2026-001-decomposition`

2. 读取需求分解文档（decomposition.md）
   - 只提取当前 REQ 相关的部分

3. 读取当前 Program 的需求澄清结果（workspace/answers.md）
4. 读取当前 Program 的技术决策记录（workspace/decisions.md）
5. **必须读取**技术规格规范和架构规范
6. 检查 STATUS.yml 确认处于"技术规格"阶段

### Step 2: 知识库查询（自动复用已有设计）

**使用 knowledge-base-query Skill 收集现有系统信息：**

```
查询类型: feature
关键词: {当前需求功能关键词}

目的:
- 查询类似功能的技术规格书
- 参考已有的数据模型设计
- 复用已有的接口定义模式
- 了解已有的业务规则实现
```

**查询类型及用途：**

| 查询类型 | 用途 | 示例 |
|----------|------|------|
| feature | 类似功能档案 | 智能员工创建 |
| spec | 技术规范 | 错误码规范、架构规范 |
| api | 内部服务API | user-service-api |
| schema | 数据库表结构 | 现有用户表字段 |

**查询结果处理**：
- 如有相似功能的 tech-spec.md → 参考其设计，标注可复用部分
- 如无相似功能 → 按标准流程进行设计

### Step 3: 分析依赖与上下文

基于知识库查询结果，分析：

- 需要复用的表结构
- 需要新增的表
- 可复用的接口模式
- 需要遵循的技术规范

### Step 4: 生成技术规格书（初稿）

按规范格式生成 `tech-spec.md`。

**文档结构参考**：`.qoder/rules/03-tech-spec-generation.md` 中的"技术规格书结构标准"章节

**错误码规范参考**：Rule 中的"错误码规范"章节

**复用标注**：
- 在章节开头标注"参考 F-xxx 功能档案"
- 对复用的设计添加说明"复用自 F-xxx"
- 对新增/修改的设计明确标注

### Step 5: 用户评审与迭代

**呈现技术规格书给用户：**

```
技术规格书已生成：orchestrator/PROGRAMS/{current_program_id}/workspace/tech-spec.md

主要内容预览：
├── 数据模型: X 张表（Y 张新增，Z 张修改）
├── API 接口: N 个端点
├── 涉及服务: mall-admin, mall-app, mall-agent
├── 复用参考: F-001, F-003（如有）
└── 关键决策: [列出关键设计决策]

请评审技术规格书，如有以下情况请告知：
1. 需要补充上下文（现有表结构、接口文档等）
2. 设计方案需要调整
3. 缺少必要的实现细节
```

#### 5.1 处理用户反馈

**场景 A: 用户补充上下文**
- 读取提供的表结构/接口文档
- 更新 tech-spec.md
- 标记修订记录

**场景 B: 用户要求修改设计方案**
- 分析变更影响（使用 Rule 中的"变更影响分析矩阵"）
- 判断是否需要同步更新需求文档
- 询问用户确认
- 更新 tech-spec.md 和相关需求文档

**场景 C: 用户要求补充实现细节**
- 在 tech-spec.md 中新增相应章节
- 如涉及业务规则变更，判断是否需更新需求文档

### Step 6: 生成关联文档

技术规格确认后，生成：

1. **验收清单** (`workspace/checklist.md`)
   - 按子需求组织验收项

### Step 7: 建立双向追溯

在 tech-spec.md 中建立需求追溯矩阵（格式参考 Rule）。

---

## 返回格式

```
状态：已完成 / 需要迭代
报告：orchestrator/PROGRAMS/{current_program_id}/workspace/tech-spec.md
关联文档：
  - workspace/openapi.yaml
  - workspace/checklist.md

Program 状态更新：
  - current_phase: 代码生成
  - phases.技术规格.status: done

产出变更：
  - 需求文档更新: 是/否
  - 更新文件列表: [xxx.md, yyy.md]
决策点：
  - 是否需要补充上下文: 是/否
  - 是否需要调整设计: 是/否
  - 是否进入代码生成阶段: 是/否
```
