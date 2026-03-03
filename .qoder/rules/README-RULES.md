---
trigger: manual
alwaysApply: false
---

# 规则文件索引

本目录包含**纯规范定义**文件，只定义"做什么"和"怎么做"的标准，**不涉及执行流程**。

> **职责边界**：Rule 文件只包含规范标准，执行流程在 Skill 文件中定义。

---

## 命名约定

| 前缀         | 含义             | 示例                            |
|------------|----------------|-------------------------------|
| `common-`  | 通用规范，适用于任何项目   | `common-coding-standards.md`  |
| `project-` | 项目特定规范，仅适用于本项目 | `project-naming-standards.md` |

---

## 文件索引

### 通用规范（common-）

| 文件                                 | 说明                                               | 适用范围       |
|------------------------------------|--------------------------------------------------|------------|
| `common-coding-standards.md`       | Java 编码规范、命名规范、注释规范、日志规范等                        | 任何 Java 项目 |
| `common-architecture-standards.md` | 分层架构、DDD领域设计、服务职责、接口风格、数据库设计原则、MyBatis-Plus使用规范等 | 任何 Java 项目 |

### 项目规范（project-）

| 文件                                   | 说明                                         | 适用范围 |
|--------------------------------------|--------------------------------------------|------|
| `project-naming-standards.md`        | 包路径、服务名、表前缀、类命名模式（AimXxxDO/AimXxxService等） | 本项目  |
| `project-error-code-standards.md`    | 错误码格式 `SSMMTNNN`、系统/模块映射、生成规则              | 本项目  |
| `project-common-result-standards.md` | CommonResult 统一响应格式规范                      | 本项目  |
| `project-operator-id-standards.md`   | 操作人ID获取与传递标准                               | 本项目  |

### 阶段规范（Spec模式）

| 文件                                       | 阶段      | 说明                   |
|------------------------------------------|---------|----------------------|
| `prd-decomposition-standards.md`         | Phase 1 | 需求拆分规范、服务归属规则、输出格式标准 |
| `requirement-clarification-standards.md` | Phase 2 | 需求澄清规范、问题优先级、ADR格式标准 |
| `tech-spec-generation-standards.md`      | Phase 3 | 技术规格书结构、错误码规范        |

---

## 规则文件格式

```yaml
---
trigger: model_decision    # 触发方式：model_decision/manual/alwaysOn
description: 规则描述      # 简短描述，用于模型决策
globs: [ "**/*.java" ]       # 可选，适用文件模式
alwaysApply: false         # 可选，是否始终应用
---
```

---

## 使用方式

1. **Skill 执行前**：必须先读取对应的 Rule 文件获取规范
2. **代码生成时**：遵循所有相关的规范文件
3. **规范优先级**：`project-` > `common-`（项目规范覆盖通用规范）

---

## 规范与流程的关系

| 类型                   | 定义             | 位置                     | 说明             |
|----------------------|----------------|------------------------|----------------|
| **规范（Rules）**        | "做什么"和"怎么做"    | `.qoder/rules/`        | 纯标准定义，无执行逻辑    |
| **流程（Orchestrator）** | "何时做"和"按什么顺序做" | `orchestrator/ALWAYS/` | 流程控制           |
| **执行（Skills）**       | "如何执行"         | `.qoder/skills/`       | 读取 Rule 后按规范执行 |

三者解耦，便于独立维护和更新。
