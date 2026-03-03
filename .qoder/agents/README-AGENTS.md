# Agent 定义说明

本目录用于存放 Agent 定义文件。

## 何时使用 Agent 定义

当需要定义一个完整的、独立的工作单元时使用 Agent：

1. **复杂的多步骤任务**：需要完整的输入处理、执行流程、输出生成
2. **独立的决策逻辑**：需要根据上下文做出复杂决策
3. **特定的业务领域**：如"数据库设计 Agent"、"安全审计 Agent"

---

## Agent vs Skill 的区别

| 特性   | Agent    | Skill        |
|------|----------|--------------|
| 复杂度  | 高（完整工作流） | 低（单一能力）      |
| 独立性  | 可独立执行    | 需被调用         |
| 输入输出 | 完整的上下文处理 | 明确的参数和返回值    |
| 使用场景 | 复杂任务     | 代码生成、分析等具体能力 |

---

## 本项目的选择

本项目使用 **Skill + Sub-Agent 混合模式**：

### Skill 模式（标准化任务）

- 代码生成 → `skills/04-java-code-generation/SKILL.md`
- HTTP 测试生成 → `skills/05-http-test-generation/SKILL.md`
- 代码质量分析 → `skills/06-code-quality-analysis/SKILL.md`

这些能力由主 Agent 在适当时机调用，按规范执行。

### Sub-Agent 模式（复杂并行任务）

- 代码生成专家 → `agents/code-generator.md`
- 代码审查专家 → `agents/code-reviewer.md`
- 技术调研专家 → `agents/tech-researcher.md`
- 文档生成专家 → `agents/doc-generator.md`

这些 Agent 用于并行处理复杂任务，具有独立决策能力。

---

## Sub-Agent 说明

本项目使用 Sub-Agent 进行任务委托，但**不引入代理模式**。

Sub-Agent 是简单的任务并行执行机制：

- 由主 Agent 直接委托
- 通过文件通信
- 返回简洁的执行结果

详见：`orchestrator/ALWAYS/SUB-AGENT.md`

---

## Agent 列表

| Agent | 文件 | 职责 | 使用场景 | 降级 Skill |
|-------|------|------|----------|-----------|
| code-generator | `code-generator.md` | Java 微服务代码生成 | Phase 4 并行生成多服务代码 | `java-code-generation` |
| code-reviewer | `code-reviewer.md` | 代码质量审查 | Phase 6 代码质量检查 | `code-quality-analysis` |
| tech-researcher | `tech-researcher.md` | 技术方案调研 | Phase 2/3 技术选型调研 | - |
| doc-generator | `doc-generator.md` | 技术文档生成 | Phase 3/7 文档生成 | `tech-spec-generation` / `feature-archiving` |

**执行机制**：系统优先尝试使用 Agent 执行；如果 Sub-Agent 不可用，自动降级为对应的 Skill 执行

---

## 与 Skill 的关系

### Agent 与 Skill 的选择

| 特性 | Skill | Agent |
|------|-------|-------|
| 复杂度 | 低（单一能力） | 高（完整工作流） |
| 决策能力 | 无（按规范执行） | 有（自主判断） |
| 上下文管理 | 输入即上下文 | 需要维护上下文 |
| 使用方式 | 被调用执行 | 独立运行或被委托 |
| 适用场景 | 标准化任务 | 复杂并行任务 |

### 各阶段执行者

| 阶段 | 执行者 | 类型 |
|------|--------|------|
| Phase 1: PRD 分解 | `prd-decomposition` | Skill |
| Phase 2: 需求澄清 | `requirement-clarification` + `tech-researcher` | Skill + Agent |
| Phase 3: 技术规格 | `doc-generator` | Agent |
| Phase 4: 代码生成 | `code-generator` | Agent |
| Phase 5: 测试验证 | `http-test-generation` | Skill |
| Phase 6: 代码质量 | `code-reviewer` | Agent |
| Phase 7: 功能归档 | `doc-generator` | Agent |
