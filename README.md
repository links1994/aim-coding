# AI Coding 工作区

本项目是一个基于 AI Agent 的软件开发工作区，采用 **Spec 模式** 进行需求分析、技术设计和代码生成。通过结构化的流程和规范，实现从
PRD 到可部署代码的全自动化开发。

---

## 目录结构

```
aim-coding/
├── AGENTS.md                      # Agent 工作指导文件
├── README.md                      # 本文件
├── .gitignore                     # Git 忽略配置
├── .qoder/                        # Qoder 配置目录
│   ├── README-QODER.md            # Qoder 配置说明
│   ├── agents/                    # Agent 定义（预留）
│   │   └── README-AGENTS.md       # Agent 说明文档
│   ├── repowiki/                  # 知识库归档目录
│   ├── rules/                     # 规则文件（规范定义）
│   │   ├── README-RULES.md        # 规则说明与索引
│   │   ├── 01-prd-decomposition.md        # Phase 1: 需求拆分规则
│   │   ├── 02-requirement-clarification.md # Phase 2: 需求澄清规则
│   │   ├── 03-tech-spec-generation.md     # Phase 3: 技术规格书规则
│   │   ├── 04-coding-standards.md         # Java 编码规范
│   │   └── 05-architecture-standards.md   # 架构规范
│   └── skills/                    # Skill 能力模块
│       ├── README-SKILLS.md       # Skill 使用指南
│       ├── api-archiving/         # API 归档 Skill (skill.md)
│       ├── code-quality-analysis/ # 代码质量分析 Skill (skill.md)
│       ├── database-schema-archiving/ # 数据库归档 Skill (skill.md)
│       ├── feature-archiving/     # 功能归档 Skill (skill.md)
│       ├── http-test-generation/  # HTTP 测试生成 Skill (skill.md)
│       ├── java-code-generation/  # Java 代码生成 Skill (skill.md)
│       ├── knowledge-base-query/  # 知识库查询 Skill (skill.md)
│       ├── pitfalls-archiving/    # 代码陷阱归档 Skill (skill.md)
│       ├── prd-decomposition/     # 需求分解 Skill (skill.md)
│       ├── requirement-clarification/ # 需求澄清 Skill (skill.md)
│       ├── spec-archiving/        # 规格书归档 Skill (skill.md)
│       └── tech-spec-generation/  # 技术规格生成 Skill (skill.md)
│
├── inputs/                        # 输入文件目录
│   ├── README-INPUTS.md           # 输入文件说明
│   └── prd/                       # 产品需求文档
│       ├── ai-agent-platform-prd.md       # AI 智能体平台 PRD
│       └── ai-agent-platform-wireframes.md # AI 智能体平台线框图
│
└── orchestrator/                  # 编排系统（任务管理）
    ├── README-ORCHESTRATOR.md     # 编排系统说明
    ├── ALWAYS/                    # 核心配置（每次必读）
    │   ├── README-ALWAYS.md       # 核心配置说明
    │   ├── BOOT.md                # 启动加载顺序
    │   ├── CORE.md                # 核心工作协议
    │   ├── DEV-FLOW.md            # 开发流程规范
    │   ├── SUB-AGENT.md           # Sub-Agent 委托规范
    │   └── RESOURCE-MAP.yml       # 资源索引
    └── PROGRAMS/                  # 开发任务
        ├── README-PROGRAMS.md     # Program 管理指南
        ├── _TEMPLATE/             # 任务模板
        │   ├── PROGRAM.md         # 任务定义模板
        │   ├── SCOPE.yml          # 写入范围模板
        │   ├── STATUS.yml         # 状态跟踪模板
        │   └── workspace/         # 工作文档目录
        └── P-2026-001-decomposition/  # 具体 Program 示例
            ├── PROGRAM.md         # 任务定义
            ├── SCOPE.yml          # 写入范围
            ├── STATUS.yml         # 状态跟踪
            └── workspace/         # 工作文档
                ├── decomposition.md   # 需求拆分结果
                └── RESULT.md          # 最终成果
```

---

## 核心概念

### 1. Spec 模式（七阶段开发流程）

```
PRD → Phase 1 → Phase 2 → Phase 3 → Phase 4 → Phase 5 → Phase 6 → Phase 7
      需求拆分    需求澄清    技术规格    代码生成    测试验证    代码质量    功能归档
```

| 阶段      | 名称   | 说明                   | 规则文件                                                     | Skill                        |
|---------|------|----------------------|----------------------------------------------------------|------------------------------|
| Phase 1 | 需求拆分 | 将 PRD 拆分为可独立实现的子需求   | `01-prd-decomposition.md`                                | `prd-decomposition/`         |
| Phase 2 | 需求澄清 | 使用 ReAct 模式进行交互式需求澄清 | `02-requirement-clarification.md`                        | `requirement-clarification/` |
| Phase 3 | 技术规格 | 生成技术规格书              | `03-tech-spec-generation.md`                             | `tech-spec-generation/`      |
| Phase 4 | 代码生成 | 生成 Java 微服务代码        | `04-coding-standards.md`, `05-architecture-standards.md` | `java-code-generation/`      |
| Phase 5 | 测试验证 | 生成 HTTP 测试文件         | -                                                        | `http-test-generation/`      |
| Phase 6 | 代码质量 | 代码质量分析和优化            | -                                                        | `code-quality-analysis/`     |
| Phase 7 | 功能归档 | 功能归档到知识库             | -                                                        | `feature-archiving/`         |

### 2. Program（开发任务）

Program 是开发任务的基本单元，每个 Program 对应一个具体的功能开发或技术任务。

**Program 类型**：

- **decomposition**：需求拆分类型，将 PRD 拆分为子需求
- **implementation**：需求实现类型，基于某个 REQ 进行完整开发

**Program 结构**：

```
P-YYYY-NNN-{name}/
├── PROGRAM.md         # 任务定义（目标、背景、方案、验收标准）
├── STATUS.yml         # 状态跟踪（阶段、任务列表、产出物）
├── SCOPE.yml          # 写入范围（write/forbidden 路径）
└── workspace/         # 工作文档
    ├── decomposition.md     # 需求拆分结果
    ├── questions.md         # 澄清问题
    ├── answers.md           # 确认答案
    ├── decisions.md         # 技术决策
    ├── tech-spec.md         # 技术规格书
    ├── CHECKPOINT.md        # 上下文快照
    ├── HANDOFF.md           # 交接文档
    └── RESULT.md            # 最终成果
```

### 3. 三层架构

| 层级               | 目录               | 定义           | 示例           |
|------------------|------------------|--------------|--------------|
| **Rules**        | `.qoder/rules/`  | "按什么标准做"     | 编码规范、架构规范    |
| **Orchestrator** | `orchestrator/`  | "何时做、按什么顺序做" | 启动流程、Spec 模式 |
| **Skills**       | `.qoder/skills/` | "具体怎么做"      | 代码生成、测试生成    |

---

## 使用指南

### 快速开始

#### 1. 查看现有任务

```
用户: "列出所有 Program"
Agent: 扫描 orchestrator/PROGRAMS/ 目录，展示任务列表
```

#### 2. 继续已有任务

```
用户: "继续 P-2026-001"
Agent:
  → 读取 orchestrator/ALWAYS/BOOT.md
  → 按顺序加载核心配置
  → 读取 PROGRAMS/P-2026-001/PROGRAM.md
  → 读取 PROGRAMS/P-2026-001/STATUS.yml
  → 读取 PROGRAMS/P-2026-001/SCOPE.yml
  → 恢复上下文，继续执行
```

#### 3. 创建新任务

```
用户: "新 Program: 实现用户登录功能"
Agent:
  → 从 _TEMPLATE 复制模板
  → 创建 P-2026-002-user-login/
  → 生成初始 PROGRAM.md
  → 询问写入范围
  → 初始化 STATUS.yml
```

#### 4. 使用 Sub-Agent 委托

```
用户: "委托: 分析依赖关系"
Agent:
  → 创建 Sub-Agent
  → 分配任务
  → Sub-Agent 执行并返回结果
```

### 完整开发周期示例

#### 场景：基于 PRD 开发新功能

**Step 1: 创建 decomposition Program**

```
用户: "基于 inputs/prd/ai-agent-platform-prd.md 进行需求拆分"
Agent:
  → 创建 P-2026-002-ai-agent-decomposition/
  → 读取 PRD 文件
  → 执行 Phase 1: 需求拆分
  → 生成 workspace/decomposition.md
```

**Step 2: 查看拆分结果**

拆分结果 `decomposition.md` 包含：

- REQ-001: 功能模块 A
- REQ-002: 功能模块 B
- REQ-003: 功能模块 C

**Step 3: 为每个 REQ 创建 implementation Program**

```
用户: "为 REQ-001 创建 implementation Program"
Agent:
  → 创建 P-2026-003-req-001-implementation/
  → 基于 REQ-001 定义任务
  → 开始 Spec 模式执行
```

**Step 4: 执行 Spec 模式**

```
Phase 1: 需求拆分（已完成）
  ↓
Phase 2: 需求澄清
  → 生成 questions.md
  → 用户确认 answers.md
  ↓
Phase 3: 技术规格
  → 生成 tech-spec.md
  → 生成 openapi.yaml
  ↓
Phase 4: 代码生成
  → 生成 Java 代码
  → 遵循编码规范
  ↓
Phase 5: 测试验证
  → 生成 HTTP 测试
  ↓
Phase 6: 代码质量
  → 代码质量分析和优化
  ↓
Phase 7: 功能归档
  → 归档到 .qoder/repowiki/
```

---

## 文件详细说明

### AGENTS.md

AI Coding Agent 的工作指导文件，包含：

- 工作语言与风格
- Boot Sequence（启动流程）
- 目录结构说明
- 快速命令参考

### .qoder/ 目录

#### rules/ 规则文件

| 文件                                | 说明                          |
|-----------------------------------|-----------------------------|
| `01-prd-decomposition.md`         | 需求拆分规范：服务归属规则、REQ 编号规则、依赖分析 |
| `02-requirement-clarification.md` | 需求澄清规范：问题优先级、ADR 格式、确认流程    |
| `03-tech-spec-generation.md`      | 技术规格规范：规格书结构、错误码规范、接口定义     |
| `04-coding-standards.md`          | Java 编码规范：命名规范、注释规范、代码风格    |
| `05-architecture-standards.md`    | 架构规范：分层架构、服务职责、数据库设计        |

#### skills/ 能力模块

每个 Skill 目录包含 `skill.md` 文件，定义执行流程：

| Skill                        | 说明                         |
|------------------------------|----------------------------|
| `prd-decomposition/`         | 需求分解：将 PRD 拆分为 REQ         |
| `requirement-clarification/` | 需求澄清：交互式确认需求               |
| `tech-spec-generation/`      | 技术规格生成：生成技术规格书             |
| `java-code-generation/`      | Java 代码生成：生成微服务代码          |
| `http-test-generation/`      | HTTP 测试生成：生成接口测试           |
| `code-quality-analysis/`     | 代码质量分析：优化代码质量              |
| `feature-archiving/`         | 功能归档：功能归档到知识库（Phase 7）     |
| `api-archiving/`             | API 归档：归档 API 定义到 repowiki |
| `database-schema-archiving/` | 数据库归档：归档数据库表结构到 repowiki   |
| `spec-archiving/`            | 规格书归档：归档技术规格书到 repowiki    |
| `pitfalls-archiving/`        | 陷阱归档：归档代码陷阱和反模式到 repowiki  |
| `knowledge-base-query/`      | 知识库查询：查询历史功能               |

### inputs/ 输入文件

存放产品需求文档（PRD）：

- `prd/ai-agent-platform-prd.md` - AI 智能体平台需求文档
- `prd/ai-agent-platform-wireframes.md` - 线框图/原型图

### orchestrator/ 编排系统

#### ALWAYS/ 核心配置

| 文件                 | 说明                             |
|--------------------|--------------------------------|
| `BOOT.md`          | 启动加载顺序：Agent 启动时的加载流程          |
| `CORE.md`          | 核心工作协议：Program 类型、状态管理、执行流程    |
| `DEV-FLOW.md`      | 开发流程规范：Git Flow、分支规范、Commit 规范 |
| `SUB-AGENT.md`     | Sub-Agent 委托规范：委托时机、文件通信、返回格式  |
| `RESOURCE-MAP.yml` | 资源索引：仓库列表、服务依赖、基础设施            |

#### PROGRAMS/ 开发任务

**_TEMPLATE/**：任务模板目录

- `PROGRAM.md` - 任务定义模板
- `STATUS.yml` - 状态跟踪模板
- `SCOPE.yml` - 写入范围模板

**P-YYYY-NNN-{name}/**：具体任务目录

- 命名规范：`P-YYYY-NNN-{name}`
- 示例：`P-2026-001-decomposition`

---

## 资源索引（RESOURCE-MAP.yml）

### 仓库列表

| 仓库               | 类型     | 说明               | 包名                    |
|------------------|--------|------------------|-----------------------|
| `mall-admin`     | 门面服务   | 管理后台门面服务         | `com.aim.mall.admin`  |
| `mall-app`       | 门面服务   | 客户端门面服务          | `com.aim.mall.client` |
| `mall-chat`      | 门面服务   | AI 对话门面服务        | `com.aim.mall.chat`   |
| `mall-agent`     | 应用服务   | AI 智能体员工服务（核心业务） | `com.aim.mall.agent`  |
| `mall-user`      | 支撑服务   | 用户服务             | `com.aim.mall.user`   |
| `mall-inner-api` | API 模块 | 内部 API 模块集合      | `com.aim.mall`        |

### 服务调用关系

```
mall-admin  →  mall-agent, mall-user
mall-app    →  mall-agent, mall-user
mall-chat   →  mall-agent
mall-agent  →  mall-user
```

---

## 开发流程

### Git Flow

```
明确任务 → worktree 开发 → 测试 → PR → 合 main → 部署
```

### 分支说明

| 分支          | 用途       |
|-------------|----------|
| `main`      | 稳定版本     |
| `dev`       | 测试集成（可选） |
| `feature/*` | 功能开发     |
| `fix/*`     | Bug 修复   |

### Commit 规范

格式：`<type>: <description>`

| Type       | 说明     |
|------------|--------|
| `feat`     | 新功能    |
| `fix`      | Bug 修复 |
| `docs`     | 文档     |
| `refactor` | 重构     |
| `test`     | 测试     |
| `chore`    | 杂项     |

---

## 快速命令参考

| 命令                 | 作用                |
|--------------------|-------------------|
| `继续 P-2026-001`    | 加载并继续指定 Program   |
| `新 Program: xxx`   | 创建新的开发任务          |
| `委托: xxx`          | 使用 Sub-Agent 执行任务 |
| `查看 P-2026-001 状态` | 读取 STATUS.yml     |

---

## 状态定义

### Program 状态

| 状态            | 说明  |
|---------------|-----|
| `not-started` | 未开始 |
| `active`      | 进行中 |
| `blocked`     | 阻塞  |
| `deployed`    | 已部署 |

### 阶段状态

| 阶段            | 说明  |
|---------------|-----|
| `planning`    | 规划中 |
| `in-progress` | 执行中 |
| `review`      | 审核中 |
| `done`        | 已完成 |

### Task 状态

| 状态            | 说明  |
|---------------|-----|
| `pending`     | 待执行 |
| `in-progress` | 执行中 |
| `done`        | 已完成 |
| `blocked`     | 阻塞  |

---

## 上下文管理

Agent 的上下文窗口有限，以下机制保证跨会话零信息损失：

| 文件              | 时机          | 内容        |
|-----------------|-------------|-----------|
| `STATUS.yml`    | 持续更新        | 阶段进度、任务状态 |
| `plan.md`       | task 执行前    | 当前执行计划    |
| `HANDOFF.md`    | 会话结束未完成时    | 交接信息      |
| `CHECKPOINT.md` | 上下文紧张时      | 状态快照      |
| `RESULT.md`     | Program 完成时 | 最终成果总结    |

---

## 功能归档

功能完成后自动归档到 `.qoder/repowiki/features/{feature-name}/`：

- `feature-archive.md`：功能描述、接口清单、核心类、设计决策
- `reuse-guide.md`：如何复用此功能
- `snippets/`：可复用的代码片段

---

## 贡献指南

### 新增规则

1. 在 `.qoder/rules/` 目录创建 `{序号}-{name}.md`
2. 添加 YAML frontmatter
3. 更新 `rules/README-RULES.md`
4. 更新 `README-QODER.md`

### 新增 Skill

1. 在 `.qoder/skills/` 目录创建 `{skill-name}/skill.md`
2. 遵循 Skill 文件格式
3. 更新 `skills/README-SKILLS.md`
4. 更新 `README-QODER.md`

---

## 相关文档

| 文档         | 路径                                         | 说明           |
|------------|--------------------------------------------|--------------|
| Agent 指导   | `AGENTS.md`                                | Agent 工作指导   |
| Qoder 配置   | `.qoder/README-QODER.md`                   | Qoder 配置说明   |
| 规则说明       | `.qoder/rules/README-RULES.md`             | 规则文件索引       |
| Skill 指南   | `.qoder/skills/README-SKILLS.md`           | Skill 使用指南   |
| 编排系统       | `orchestrator/README-ORCHESTRATOR.md`      | 编排系统说明       |
| Program 管理 | `orchestrator/PROGRAMS/README-PROGRAMS.md` | Program 管理指南 |
| 输入文件       | `inputs/README-INPUTS.md`                  | 输入文件说明       |

---

## 许可证

[待补充]
