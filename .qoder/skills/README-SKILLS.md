# Skill 使用指南

本目录包含可复用的能力模块（Skills），用于执行特定的代码生成和分析任务。

> **职责边界**：Skill 文件只定义"如何执行"，规范标准在 `.qoder/rules/` 中定义。Skill 执行前必须先读取对应的 Rule 文件。

---

## Skill 列表

### 核心开发 Skills（Spec 模式）

| Skill     | 目录                           | 说明                   | 使用时机    | 依赖 Rule                                                  |
|-----------|------------------------------|----------------------|---------|----------------------------------------------------------|
| 需求分解      | `01-prd-decomposition/`         | 根据 PRD 进行需求拆解        | Phase 1 | `prd-decomposition-standards.md`                         |
| 需求澄清      | `02-requirement-clarification/` | 使用 ReAct 模式进行交互式需求澄清 | Phase 2 | `requirement-clarification-standards.md`                 |
| 技术规格生成    | `03-tech-spec-generation/`      | 生成技术规格书              | Phase 3 | `tech-spec-generation-standards.md`                      |
| Java 代码生成 | `04-java-code-generation/`      | 生成 Java 微服务代码        | Phase 4 | `coding-standards.md`, `architecture-standards.md`       |
| HTTP 测试生成 | `05-http-test-generation/`      | 生成 HTTP 测试文件         | Phase 5 | -                                                        |
| 代码质量分析    | `06-code-quality-analysis/`     | 代码质量分析和优化            | Phase 6 | -                                                        |
| 功能归档      | `07-feature-archiving/`         | 功能归档到 repowiki 知识库   | Phase 7 | -                                                        |

### 归档类 Skills（按需加载）

| Skill  | 目录                           | 说明                   | 使用时机      |
|--------|------------------------------|----------------------|-----------|
| API 归档 | `api-archiving/`             | 归档 API 定义到 repowiki  | API 设计完成后 |
| 数据库归档  | `database-schema-archiving/` | 归档数据库表结构到 repowiki   | 表设计完成后    |
| 规格书归档  | `spec-archiving/`            | 归档技术规格书到 repowiki    | 规格书完成后    |
| 陷阱归档   | `pitfalls-archiving/`        | 归档代码陷阱和反模式到 repowiki | 发现问题时     |

> **注意**: 功能归档 Skill (`feature-archiving`) 已集成数据库表结构归档功能，在归档功能时会自动归档相关的表结构到 `repowiki/schemas/` 目录。

### 上下文管理 Skills（按需加载）

| Skill | 目录                      | 说明                | 使用时机    |
|-------|-------------------------|-------------------|---------|
| 知识库查询 | `knowledge-base-query/` | 统一查询 repowiki 知识库 | 任何需要查询时 |

---

## Skill 定义

Skill 是**可复用的执行模块**，特点：

1. **单一职责**：每个 Skill 只做一件事
2. **明确的输入输出**：有清晰的接口定义
3. **可被调用**：由主 Agent 在适当时机调用
4. **不维护状态**：无持久化状态，纯函数式
5. **依赖规范**：执行前必须先读取对应的 Rule 文件

---

## Skill 使用方式

### 方式一：主 Agent 直接调用

```
用户: "生成技术规格书"
主 Agent: 
  → 读取 .qoder/rules/tech-spec-generation-standards.md（规范）
  → 读取 artifacts/answers.md（输入）
  → 调用 03-tech-spec-generation Skill（执行）
  → 生成 tech-spec.md（输出）
```

### 方式二：通过 Sub-Agent 委托

```
用户: "委托: 生成代码"
主 Agent:
  → 创建 Sub-Agent
  → Sub-Agent 读取对应的 Rule 文件（获取规范）
  → Sub-Agent 读取对应的 Skill 文件（获取执行流程）
  → 执行代码生成
  → 返回结果
```

---

## Skill 文件格式

```yaml
---
name: skill-name
description: Skill 描述，包含触发关键词。在用户说"xxx"或需要做 yyy 时使用。
---

# Skill 标题

  > **前置条件**：执行前必须先读取 `.qoder/rules/xxx-standards.md` 以获取规范标准。

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

  详细的工作流程步骤...

  ## 返回格式

```

状态：已完成/失败/需要决策
报告：workspace/xxx.md
输出：N 个文件
决策点：[如有]

```
```

---

## Skill 与 Rule 的关系

```
┌─────────────────────────────────────────────────────────────┐
│                        执行流程                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  用户指令: "生成技术规格书"                                   │
│       ↓                                                     │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 主 Agent                                            │   │
│  │  1. 读取 Rule: tech-spec-generation-standards.md（规范）    │   │
│  │  2. 读取 Skill: 03-tech-spec-generation/SKILL.md（执行流程）   │   │
│  │  3. 按 Skill 流程执行，遵循 Rule 规范                 │   │
│  └─────────────────────────────────────────────────────┘   │
│       ↓                                                     │
│  输出: tech-spec.md（符合规范）                              │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 与 Agent 的区别

| 特性    | Agent    | Skill     |
|-------|----------|-----------|
| 复杂度   | 高（完整工作流） | 低（单一能力）   |
| 决策能力  | 有        | 无（按规范执行）  |
| 上下文管理 | 需要维护上下文  | 输入即上下文    |
| 使用方式  | 独立运行或被委托 | 被调用执行     |
| 规范依赖  | 自主判断     | 必须读取 Rule |

---

## 新增 Skill

如需新增 Skill：

1. 创建 `{skill-name}/SKILL.md` 文件（注意：使用目录结构，不是单个文件）
2. 遵循上述文件格式（只保留 `name` 和 `description` 在 Frontmatter）
3. 更新本 README 的 Skill 列表
4. 如有对应规范，在 `.qoder/rules/` 创建 Rule 文件
