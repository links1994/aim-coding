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

本项目使用 **Skill 模式**：

- 代码生成 → `skills/java-code-generation.md`
- HTTP 测试生成 → `skills/http-test-generation.md`
- 代码质量分析 → `skills/code-quality-analysis.md`

这些能力由主 Agent 在适当时机调用，而非作为独立 Agent 运行。

---

## Sub-Agent 说明

本项目使用 Sub-Agent 进行任务委托，但**不引入代理模式**。

Sub-Agent 是简单的任务并行执行机制：

- 由主 Agent 直接委托
- 通过文件通信
- 返回简洁的执行结果

详见：`orchestrator/ALWAYS/SUB-AGENT.md`
