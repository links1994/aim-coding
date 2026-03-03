---
trigger: manual
alwaysApply: false
---

# Skill 索引

本目录包含可复用的执行模块（Skills），用于执行特定的代码生成和分析任务。

> **职责边界**：Skill 只定义"如何执行"，规范标准在 `.qoder/rules/` 中定义。Skill 执行前**必须先读取对应的 Rule 文件**。

---

## 核心开发 Skills（Spec 模式）

| Skill | 目录 | 阶段 | 依赖 Rule | 产出物 |
|-------|------|------|-----------|--------|
| 需求分解 | `01-prd-decomposition/` | Phase 1 | `prd-decomposition-standards.md` | `decomposition.md` |
| 需求澄清 | `02-requirement-clarification/` | Phase 2 | `requirement-clarification-standards.md` | `answers.md`, `decisions.md` |
| 技术规格生成 | `03-tech-spec-generation/` | Phase 3 | `tech-spec-generation-standards.md` | `tech-spec.md`, `openapi.yaml` |
| Java 代码生成 | `04-java-code-generation/` | Phase 4 | 所有编码/架构/项目规范 | Java 源代码 |
| HTTP 测试生成 | `05-http-test-generation/` | Phase 5 | - | `.http` 文件, SQL 脚本 |
| 代码质量分析 | `06-code-quality-analysis/` | Phase 6 | 所有编码/架构/项目规范 | `quality-report.md` |
| 单元测试生成 | `07-unit-test-generation/` | Phase 6.5 | `common-coding-standards.md` | 单元测试类 |
| 功能归档 | `08-feature-archiving/` | Phase 8 | - | `repowiki/features/` |

> **阶段编号说明**：Phase 6.5 是可选阶段，位于 Phase 6 之后，Phase 8 之前（无 Phase 7）

### 阶段规则引用关系

```
Phase 1: prd-decomposition
         ↓ 读取
         prd-decomposition-standards.md

Phase 2: requirement-clarification
         ↓ 读取
         requirement-clarification-standards.md

Phase 3: tech-spec-generation
         ↓ 读取
         tech-spec-generation-standards.md
         project-error-code-standards.md

Phase 4: java-code-generation
         ↓ 读取（按优先级）
         1. common-coding-standards.md
         2. common-architecture-standards.md
         3. project-naming-standards.md
         4. project-error-code-standards.md
         5. project-common-result-standards.md
         6. project-operator-id-standards.md

Phase 6: code-quality-analysis
         ↓ 读取（同上 Phase 4）
         所有编码/架构/项目规范
```

---

## 归档类 Skills

| Skill | 目录 | 使用时机 | 产出位置 |
|-------|------|----------|----------|
| API 归档 | `api-archiving/` | API 设计完成后 | `repowiki/apis/` |
| 数据库归档 | `database-schema-archiving/` | 表设计完成后 | `repowiki/schemas/` |
| 陷阱归档 | `pitfalls-archiving/` | 发现问题时 | `repowiki/pitfalls/` |
| 知识库查询 | `knowledge-base-query/` | 任何需要查询时 | - |

> 注：`feature-archiving` 已集成数据库表结构归档功能。

---

## Skill 文件格式

```yaml
---
name: skill-name
description: Skill 描述，包含触发关键词
---

# Skill 标题

> **前置依赖**：执行前必须先读取以下 Rule 文件：
> - `.qoder/rules/xxx-standards.md`

## 触发条件

何时使用此 Skill。

## 输入

- 输入项 1
- 输入项 2
- `.qoder/rules/xxx-standards.md` — 规范文件（**必须先读取**）

## 输出

- 输出项 1
- 输出项 2

## 工作流程

1. 步骤 1
2. 步骤 2
3. ...

## 检查清单（Checklist）

生成类 Skill 必须包含最终检查步骤：

- [ ] 检查项 1
- [ ] 检查项 2
- [ ] 检查项 3

## 返回格式

```
状态：已完成/失败/需要决策
报告：workspace/xxx.md
输出：N 个文件
决策点：[如有]
```
```

---

## 与 Agent 的关系

本项目使用 **能力检测 + 自动降级** 机制：

```
任务到达
    ↓
检测 Sub-Agent 可用性
    ↓
├─ 可用 ──→ 委托 Agent 执行（并行、独立决策）
│
└─ 不可用 ──→ 调用 Skill 执行（标准化流程）
    ↓
产出格式保持一致
```

### 阶段执行映射

| 阶段 | 首选执行者 | 降级 Skill |
|------|-----------|-----------|
| Phase 1 | `prd-decomposition` (Skill) | - |
| Phase 2 | `requirement-clarification` (Skill) + `tech-researcher` (Agent) | - |
| Phase 3 | `doc-generator` (Agent) | `tech-spec-generation` (Skill) |
| Phase 4 | `code-generator` (Agent) | `java-code-generation` (Skill) |
| Phase 5 | `http-test-generation` (Skill) | - |
| Phase 6 | `code-reviewer` (Agent) | `code-quality-analysis` (Skill) |
| Phase 6.5 | `unit-test-generation` (Skill) | - |
| Phase 8 | `doc-generator` (Agent) | `feature-archiving` (Skill) |

---

## Agent vs Skill 对比

| 特性 | Skill | Agent |
|------|-------|-------|
| 复杂度 | 低（单一能力） | 高（完整工作流） |
| 决策能力 | 无（按规范执行） | 有（自主判断） |
| 上下文管理 | 输入即上下文 | 需要维护上下文 |
| 使用方式 | 被调用执行 | 独立运行或被委托 |
| 规范依赖 | 必须读取 Rule | 自主判断 |

---

## 新增 Skill 步骤

1. 创建 `{skill-name}/SKILL.md` 文件（使用目录结构）
2. 遵循上述文件格式（Frontmatter 只保留 `name` 和 `description`）
3. **生成类 Skill 必须包含 Checklist 作为最后步骤**
4. **涉及代码生成的必须引用规范检测**
5. 更新本 README 的 Skill 列表
6. 如有对应规范，在 `.qoder/rules/` 创建 Rule 文件
