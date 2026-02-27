# Qoder 配置目录

本目录包含项目的 Qoder 配置，包括规则（Rules）和能力模块（Skills）。

---

## 目录结构

```
.qoder/
├── README-QODER.md                    # 本文件
├── rules/                             # 规则文件
│   ├── README-RULES.md                # 规则说明与索引
│   ├── 01-prd-decomposition.md        # Phase 1: 需求拆分规则
│   ├── 02-requirement-clarification.md # Phase 2: 需求澄清规则
│   ├── 03-tech-spec-generation.md     # Phase 3: 技术规格书规则
│   ├── 04-coding-standards.md         # 编码规范
│   └── 05-architecture-standards.md   # 架构规范
├── agents/                            # Agent 定义（当前不使用）
│   └── README-AGENTS.md               # Agent 说明
└── skills/                            # Skill 能力模块
    ├── README-SKILLS.md               # Skill 使用指南
    ├── java-code-generation.md        # Java 代码生成
    ├── http-test-generation.md        # HTTP 测试生成
    └── code-quality-analysis.md       # 代码质量分析
```

---

## 规则（Rules）

规则定义了"按什么标准做"，包括：

### 阶段规则（Spec 模式）

| 文件                                | 阶段      | 说明              |
|-----------------------------------|---------|-----------------|
| `01-prd-decomposition.md`         | Phase 1 | PRD 需求拆分与依赖分析规范 |
| `02-requirement-clarification.md` | Phase 2 | 需求澄清与确认规范       |
| `03-tech-spec-generation.md`      | Phase 3 | 技术规格书生成规范       |

### 编码与架构规范

| 文件                             | 说明                      |
|--------------------------------|-------------------------|
| `04-coding-standards.md`       | Java 编码规范、命名规范、注释规范等    |
| `05-architecture-standards.md` | 分层架构、服务职责、接口风格、数据库设计规范等 |

---

## 能力模块（Skills）

Skills 是可复用的能力模块，定义了"具体怎么做"：

| Skill     | 文件                         | 说明                        | 使用时机    |
|-----------|----------------------------|---------------------------|---------|
| Java 代码生成 | `java-code-generation.md`  | 根据技术规格书生成 Java 微服务代码      | Phase 4 |
| HTTP 测试生成 | `http-test-generation.md`  | 为 Controller 生成 HTTP 测试文件 | Phase 5 |
| 代码质量分析    | `code-quality-analysis.md` | 对生成的代码进行质量分析和优化           | Phase 6 |

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
PRD → Phase 1: 需求拆分 (01-prd-decomposition.md)
  ↓
Phase 2: 需求澄清 (02-requirement-clarification.md)
  ↓
Phase 3: 技术规格书 (03-tech-spec-generation.md + 05-architecture-standards.md)
  ↓
Phase 4: 代码生成 (java-code-generation.md + 04-coding-standards.md)
  ↓
Phase 5: HTTP 测试生成 (http-test-generation.md)
  ↓
Phase 6: 代码质量优化 (code-quality-analysis.md)
```

---

## 使用方式

1. **Agent 自动读取**：主 Agent 在 Spec 模式各阶段自动读取对应的规则
2. **Skill 调用**：主 Agent 在代码生成阶段调用相应的 Skill
3. **Sub-Agent 委托**：用户明确指令"委托: xxx"时，Sub-Agent 读取 Skill 执行任务

---

## 扩展指南

### 新增规则

1. 在 `rules/` 目录创建 `{序号}-{name}.md`
2. 添加 YAML frontmatter
3. 更新 `rules/README-RULES.md`
4. 更新本文件

### 新增 Skill

1. 在 `skills/` 目录创建 `{name}.md`
2. 遵循 Skill 文件格式
3. 更新 `skills/README-SKILLS.md`
4. 更新本文件
