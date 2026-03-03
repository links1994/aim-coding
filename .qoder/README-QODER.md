# Qoder 配置目录

本目录包含项目的 Qoder 配置，包括规则（Rules）和能力模块（Skills）。

---

## 目录结构

```
.qoder/
├── README-QODER.md                    # 本文件
├── rules/                             # 规则文件
│   ├── README-RULES.md                # 规则说明与索引
│   ├── prd-decomposition-standards.md       # 需求拆分规范
│   ├── requirement-clarification-standards.md # 需求澄清规范
│   ├── tech-spec-generation-standards.md    # 技术规格书规范
│   ├── common-coding-standards.md     # Java 编码通用规范
│   ├── common-architecture-standards.md # 架构通用规范
│   ├── project-naming-standards.md    # 项目命名规范
│   ├── project-error-code-standards.md # 错误码规范
│   ├── project-common-result-standards.md # 响应格式规范
│   └── project-operator-id-standards.md # 操作人ID传递规范
├── repowiki/                          # 知识库归档
│   ├── features/                      # 功能归档
│   ├── schemas/                       # 数据库表结构归档
│   ├── apis/                          # API 归档
│   └── pitfalls/                      # 代码陷阱归档
└── skills/                            # Skill 能力模块
    ├── README-SKILLS.md               # Skill 使用指南
    ├── 01-prd-decomposition/SKILL.md  # 需求分解 (Phase 1)
    ├── 02-requirement-clarification/SKILL.md # 需求澄清 (Phase 2)
    ├── 03-tech-spec-generation/SKILL.md # 技术规格生成 (Phase 3)
    ├── 04-java-code-generation/SKILL.md # Java 代码生成 (Phase 4)
    ├── 05-http-test-generation/SKILL.md # HTTP 测试生成 (Phase 5)
    ├── 06-code-quality-analysis/SKILL.md # 代码质量分析 (Phase 6)
    ├── 07-unit-test-generation/SKILL.md  # 生成单元测试 (Phase 6.5)
    ├── 08-feature-archiving/SKILL.md  # 功能归档 (Phase 8)
    ├── api-archiving/SKILL.md         # API 归档
    ├── database-schema-archiving/SKILL.md # 数据库归档
    ├── pitfalls-archiving/SKILL.md    # 陷阱归档
    └── knowledge-base-query/SKILL.md  # 知识库查询
```

---

## 规则（Rules）

规则定义了"按什么标准做"，包括：

### 阶段规则（Spec 模式）

| 文件                                       | 阶段      | 说明              |
|------------------------------------------|---------|-----------------|
| `prd-decomposition-standards.md`         | Phase 1 | PRD 需求拆分与依赖分析规范 |
| `requirement-clarification-standards.md` | Phase 2 | 需求澄清与确认规范       |
| `tech-spec-generation-standards.md`      | Phase 3 | 技术规格书生成规范       |

### 编码与架构规范

| 文件                           | 说明                      |
|------------------------------|-------------------------|
| `common-coding-standards.md`        | Java 编码通用规范、命名规范、注释规范等    |
| `common-architecture-standards.md`  | 分层架构、DDD领域设计、服务职责、接口风格、数据库设计规范等 |

### 项目规范

| 文件                        | 说明                      |
|---------------------------|-------------------------|
| `project-naming-standards.md` | 项目命名规范（AimXxxDO/AimXxxService等） |
| `project-error-code-standards.md` | 错误码规范（SSMMTNNN格式） |
| `project-common-result-standards.md` | 统一响应格式 CommonResult 规范 |
| `project-operator-id-standards.md`   | 操作人 ID 传递规范 |

---

## 能力模块（Skills）

Skills 是可复用的能力模块，定义了"具体怎么做"：

| Skill     | 文件                                  | 说明                        | 使用时机    |
|-----------|-------------------------------------|---------------------------|---------|
| 需求分解      | `01-prd-decomposition/SKILL.md`     | 根据 PRD 进行需求拆解            | Phase 1 |
| 需求澄清      | `02-requirement-clarification/SKILL.md` | 使用 ReAct 模式进行交互式需求澄清 | Phase 2 |
| 技术规格生成    | `03-tech-spec-generation/SKILL.md`  | 生成技术规格书                   | Phase 3 |
| Java 代码生成 | `04-java-code-generation/SKILL.md`  | 根据技术规格书生成 Java 微服务代码      | Phase 4 |
| HTTP 测试生成 | `05-http-test-generation/SKILL.md`  | 为 Controller 生成 HTTP 测试文件 | Phase 5 |
| 代码质量分析    | `06-code-quality-analysis/SKILL.md` | 对生成的代码进行质量分析和优化           | Phase 6 |
| 功能归档      | `07-feature-archiving/SKILL.md`     | 功能归档到 repowiki 知识库            | Phase 7 |
| API 归档     | `api-archiving/SKILL.md`            | 归档 API 定义到 repowiki           | 按需      |
| 数据库归档     | `database-schema-archiving/SKILL.md` | 归档数据库表结构到 repowiki          | 按需      |
| 陷阱归档      | `pitfalls-archiving/SKILL.md`       | 归档代码陷阱和反模式到 repowiki        | 按需      |
| 知识库查询     | `knowledge-base-query/SKILL.md`     | 查询 repowiki 知识库              | 按需      |

---

## 与 Orchestrator 的关系

| 目录               | 定义             | 示例              |
|------------------|----------------|-----------------|
| `.qoder/rules/`  | "按什么标准做"       | 编码规范、架构规范       |
| `.qoder/skills/` | "具体怎么做"        | 代码生成、测试生成       |
| `orchestrator/`  | "何时做"、"按什么顺序做" | 启动流程、Spec 模式六阶段 |

**关系**：

- Orchestrator 定义工作流程（When & In what order）
- Rules 定义规范标准（What standard）
- Skills 定义具体实现（How to do）

---

## 工作流程

```
PRD → Phase 1: 需求拆分 (prd-decomposition-standards.md)
  ↓
Phase 2: 需求澄清 (requirement-clarification-standards.md)
  ↓
Phase 3: 技术规格书 (tech-spec-generation-standards.md + architecture-standards.md)
  ↓
Phase 4: 代码生成 (04-java-code-generation/SKILL.md + coding-standards.md)
  ↓
Phase 5: HTTP 测试生成 (05-http-test-generation/SKILL.md)
  ↓
Phase 6: 代码质量优化 (06-code-quality-analysis/SKILL.md)
  ↓
Phase 6.5: 单元测试生成 (07-unit-test-generation/SKILL.md) [可选]
  ↓
Phase 8: 功能归档 (08-feature-archiving/SKILL.md)
```

---

## 使用方式

1. **Agent 自动读取**：主 Agent 在 Spec 模式各阶段自动读取对应的规则
2. **Skill 调用**：主 Agent 在代码生成阶段调用相应的 Skill
3. **Sub-Agent 委托**：用户明确指令"委托: xxx"时，Sub-Agent 读取 Skill 执行任务

---

## 扩展指南

### 新增规则

1. 在 `rules/` 目录创建 `{name}-standards.md`
2. 添加 YAML frontmatter
3. 更新 `rules/README-RULES.md`
4. 更新本文件

### 新增 Skill

1. 在 `skills/` 目录创建 `{name}/SKILL.md`（流程中的 skill 使用 `01-xxx` 前缀）
2. 遵循 Skill 文件格式
3. 更新 `skills/README-SKILLS.md`
4. 更新本文件
