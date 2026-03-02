---
trigger: manual
alwaysApply: false
---
# 规则文件说明

本目录包含**纯规范定义**文件，只定义"做什么"和"怎么做"的标准，**不涉及执行流程**。

> **职责边界**：Rule 文件只包含规范标准，执行流程在 Skill 文件中定义。

---

## 文件索引

### 阶段规则（Spec 模式）

| 文件 | 阶段 | 说明 | 内容 |
|------|------|------|------|
| `prd-decomposition-standards.md` | Phase 1 | 需求拆分规范 | 服务归属规则、输出格式标准 |
| `requirement-clarification-standards.md` | Phase 2 | 需求澄清规范 | 问题优先级、ADR格式标准 |
| `tech-spec-generation-standards.md` | Phase 3 | 技术规格规范 | 规格书结构、错误码规范 |

### 编码与架构规范

| 文件 | 说明 |
|------|------|
| `coding-standards.md` | Java 编码规范、命名规范、注释规范等 |
| `architecture-standards.md` | 分层架构、服务职责、接口风格、数据库设计规范等 |

### 通用规范

| 文件 | 说明 |
|------|------|
| `common-result-standards.md` | 统一响应格式 CommonResult 规范 |
| `operator-id-standards.md` | 操作人 ID 传递规范 |

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

1. **阶段规则**：Skill 文件在执行前必须先读取对应的 Rule 文件
2. **编码规范**：由代码生成 Skill 在生成代码时遵循
3. **架构规范**：由技术规格书生成阶段参考

---

## 规范与流程的关系

| 类型 | 定义 | 位置 | 说明 |
|------|------|------|------|
| **规范（Rules）** | "做什么"和"怎么做" | `.qoder/rules/` | 纯标准定义，无执行逻辑 |
| **流程（Orchestrator）** | "何时做"和"按什么顺序做" | `orchestrator/ALWAYS/` | 流程控制 |
| **执行（Skills）** | "如何执行" | `.qoder/skills/` | 读取 Rule 后按规范执行 |

三者解耦，便于独立维护和更新。
