---
trigger: manual
alwaysApply: false
---
# 规则文件说明

本目录包含项目开发各阶段的规则和规范文件。

---

## 文件索引

### 阶段规则（Spec 模式）

| 文件 | 阶段 | 说明 | 触发时机 |
|------|------|------|----------|
| `01-prd-decomposition.md` | Phase 1 | 需求拆分与依赖分析规范 | 用户输入 PRD 时 |
| `02-requirement-clarification.md` | Phase 2 | 需求澄清与确认规范 | 需求拆分完成后 |
| `03-tech-spec-generation.md` | Phase 3 | 技术规格书生成规范 | 需求澄清完成后 |

### 编码与架构规范

| 文件 | 说明 |
|------|------|
| `04-coding-standards.md` | Java 编码规范、命名规范、注释规范等 |
| `05-architecture-standards.md` | 分层架构、服务职责、接口风格、数据库设计规范等 |

---

## 规则文件格式

所有规则文件使用 YAML frontmatter 定义元数据：

```yaml
---
trigger: model_decision
description: 规则描述
---
```

---

## 使用方式

1. **阶段规则**：由主 Agent 在 Spec 模式各阶段自动读取
2. **编码规范**：由代码生成 Skill 在生成代码时遵循
3. **架构规范**：由技术规格书生成阶段参考

---

## 规范与流程的关系

- **规范（Rules）**：定义"做什么"和"怎么做"的标准
- **流程（Orchestrator）**：定义"何时做"和"按什么顺序做"

两者解耦，便于独立维护和更新。
