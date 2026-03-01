---
name: tech-spec-generation
description: 生成详细的技术规格书文档，支持迭代完善和双向可追溯。在用户说"生成技术规格书"、"设计 SPEC"或需求澄清完成后创建技术设计文档时使用。
---

# 技术规格书生成 Skill

> **语言约束**：此 Skill 的所有输出必须使用中文，包括技术规格书、OpenAPI 定义、验收清单和状态报告。

基于需求分解和澄清结果生成详细的技术规格书。

> **前置条件**：执行前必须先读取 `.qoder/rules/03-tech-spec-generation.md`、`.qoder/rules/04-coding-standards.md` 和 `.qoder/rules/05-architecture-standards.md` 以获取规范标准。

---

## 触发条件

- 用户指令："生成技术规格书" 或 "设计 SPEC"
- 当前 Program 是实现类型
- 需求澄清完成（已生成 workspace/answers.md）

---

## Program 类型

此 Skill 适用于 **实现 Program**（implementation 类型）。

示例：在 `P-2026-001-REQ-031` Program 中运行此 Skill

---

## 输入

- `orchestrator/PROGRAMS/{decomposition_program_id}/workspace/decomposition.md` — 需求分解文档
- `orchestrator/PROGRAMS/{current_program_id}/workspace/answers.md` — 需求澄清结果
- `orchestrator/PROGRAMS/{current_program_id}/workspace/decisions.md` — 技术决策记录
- `.qoder/rules/03-tech-spec-generation.md` — 技术规格书规范（**必须先读取**）
- `.qoder/rules/04-coding-standards.md` — 编码规范（**必须先读取**）
- `.qoder/rules/05-architecture-standards.md` — 架构规范（**必须先读取**）
- 当前 Program 的 STATUS.yml — 更新阶段状态

---

## 输出

- 技术规格书 → `orchestrator/PROGRAMS/{current_program_id}/workspace/tech-spec.md`
- OpenAPI 定义 → `orchestrator/PROGRAMS/{current_program_id}/workspace/openapi.yaml`
- 验收清单 → `orchestrator/PROGRAMS/{current_program_id}/workspace/checklist.md`
- 更新 STATUS.yml → 阶段从"技术规格书"变为"代码生成"

---

## 工作流程

### 步骤 1：读取输入文档和规范

1. **推导分解 Program ID**
   - 当前 Program：`P-2026-001-REQ-031`
   - 分解 Program：`P-2026-001-decomposition`

2. 读取需求分解文档（decomposition.md）
   - 只提取与当前 REQ 相关的部分

3. 读取当前 Program 的需求澄清结果（workspace/answers.md）
4. 读取当前 Program 的技术决策记录（workspace/decisions.md）
5. **必须先读取** 技术规格书、编码规范和架构规范
6. 检查 STATUS.yml 确认处于"技术规格书"阶段

### 步骤 2：知识库查询（自动复用现有设计）

**使用 knowledge-base-query Skill 收集现有系统信息：**

```
查询类型：feature
关键词：{当前需求功能关键词}

目的：
- 查询类似功能的技术规格书
- 参考现有数据模型设计
- 复用现有接口定义模式
- 了解现有业务规则实现
```

**查询类型和目的：**

| 查询类型 | 目的 | 示例 |
|------------|---------|---------|
| feature | 类似功能归档 | 智能员工创建 |
| spec | 技术规格书 | 错误码规范、架构规范 |
| api | 内部服务 API | user-service-api |
| schema | 数据库表结构 | 现有用户表字段 |

**查询结果处理**：
- 如果类似功能有 tech-spec.md → 参考其设计，标记可复用部分
- 如果没有类似功能 → 按照标准流程设计

### 步骤 3：分析依赖和上下文

基于知识库查询结果，分析：

- 需要复用的表
- 需要新增的表
- 可复用的接口模式
- 需要遵循的技术规范

### 步骤 4：生成技术规格书（草稿）

**确认检查点**：生成草稿前，检查不确定项：
- 数据模型字段约束未指定
- API 分页或阈值不明确
- 技术方案选择（缓存策略、异步处理等）
- 第三方集成细节缺失

如果存在任何不确定性，使用选项式询问向用户确认。

按照规范格式生成 `tech-spec.md`。

**文档结构参考**：`.qoder/rules/03-tech-spec-generation.md` 中的"技术规格书结构标准"章节

**错误码规范参考**：规则中的"错误码规范"章节

**复用标注**：
- 在章节开头标记"参考 F-xxx 功能归档"
- 对复用的设计添加"复用自 F-xxx"
- 清晰标记新增/修改的设计

### 步骤 5：用户审查和迭代

**向用户展示技术规格书：**

```
技术规格书已生成：orchestrator/PROGRAMS/{current_program_id}/workspace/tech-spec.md

主要内容预览：
├── 数据模型：X 张表（Y 张新增，Z 张修改）
├── API 端点：N 个端点
├── 涉及服务：mall-admin、mall-app、mall-agent
├── 复用参考：F-001、F-003（如有）
└── 关键决策：[列出关键设计决策]

请审查技术规格书，如有以下情况请告知：
1. 需要补充上下文（现有表结构、接口文档等）
2. 设计方案需要调整
3. 缺少必要的实现细节
```

#### 5.1 处理用户反馈

**场景 A：用户补充上下文**
- 读取提供的表结构/接口文档
- 更新 tech-spec.md
- 标记修订记录

**场景 B：用户要求调整设计方案**
- 分析变更影响（使用规则中的"变更影响分析矩阵"）
- 确定是否需要同步更新需求文档
- 请求用户确认
- 更新 tech-spec.md 和相关需求文档

**场景 C：用户要求补充实现细节**
- 在 tech-spec.md 中添加相应章节
- 如果涉及业务规则变更，确定是否需要更新需求文档

### 步骤 6：生成相关文档

技术规格书确认后，生成：

1. **验收清单**（`workspace/checklist.md`）
   - 按子需求组织验收项

### 步骤 7：建立双向可追溯性

在 tech-spec.md 中建立需求追溯矩阵（格式参考规则）。

---

## 返回格式

```
状态：已完成 / 需要迭代
报告：orchestrator/PROGRAMS/{current_program_id}/workspace/tech-spec.md
相关文档：
  - workspace/openapi.yaml
  - workspace/checklist.md

Program 状态更新：
  - current_phase: code generation
  - phases.technical_specification.status: done

输出变更：
  - 需求文档更新：是/否
  - 更新文件列表：[xxx.md, yyy.md]

决策点：
  - 需要补充上下文：是/否
  - 需要调整设计：是/否
  - 可以进入代码生成阶段：是/否
```
