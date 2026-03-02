---
name: requirement-clarification
description: 使用 ReAct 模式进行交互式需求澄清。在用户说"澄清 REQ-xxx"、"确认需求 REQ-xxx"或需要在技术设计前澄清业务规则时使用。支持逐题确认和实时文档更新。
---

# 需求澄清 Skill

> **语言约束**：此 Skill 的所有输出必须使用中文，包括问题、选项、文档内容和状态报告。

使用 ReAct（推理 + 行动）设计模式与用户进行交互式需求澄清。

> **前置条件**：执行前必须先读取 `.qoder/rules/requirement-clarification-standards.md` 以获取规范标准。

---

## 触发条件

- 用户指令："澄清 REQ-xxx" 或 "确认需求 REQ-xxx"
- 当前 Program 是实现类型（不是分解类型）
- 需要引用分解 Program 的分解文档

---

## Program 类型

此 Skill 适用于 **实现 Program**（implementation 类型），不适用于分解 Program。

实现 Program 命名约定：`{parent-ID}-REQ-xxx`

示例：
- 分解 Program：`P-2026-001-decomposition`
- 实现 Program：`P-2026-001-REQ-031`（此 Skill 在此类 Program 中运行）

---

## 输入

- 目标需求 ID（如：REQ-031）
- `orchestrator/PROGRAMS/{decomposition_program_id}/artifacts/decomposition.md` — 需求分解文档
- `.qoder/rules/requirement-clarification-standards.md` — 需求澄清规范（**必须先读取**）
- 当前 Program 的 STATUS.yml — 更新阶段状态

---

## 输出

- 确认结果 → `orchestrator/PROGRAMS/{current_program_id}/workspace/answers.md`
- 技术决策 → `orchestrator/PROGRAMS/{current_program_id}/workspace/decisions.md`
- 更新 STATUS.yml → 阶段从"需求澄清"变为"技术规格书"

---

## ReAct 工作流程

```
┌─────────────────────────────────────────────────────────────┐
│                      ReAct 循环                              │
├─────────────────────────────────────────────────────────────┤
│  1. 推理：分析问题，生成选项                                   │
│      ↓                                                      │
│  2. 行动：调用 ask_user_question 获取用户选择                 │
│      ↓                                                      │
│  3. 观察：记录用户答案，更新记忆状态                           │
│      ↓                                                      │
│  4. 更新：实时更新 questions.md 和 answers.md                 │
│      ↓                                                      │
│  5. 下一步：下一题或完成                                       │
└─────────────────────────────────────────────────────────────┘
```

---

## 工作流程

### 步骤 1：初始化和知识库查询

1. **读取输入**
   - 读取 decomposition.md 定位目标需求
   - **必须先读取** 澄清规范（requirement-clarification-standards.md）

2. **知识库查询（自动复用现有决策）**

   **使用 knowledge-base-query Skill 查询**：
   ```
   查询类型：feature
   关键词：{当前需求功能关键词}
   
   目的：
   - 查询类似功能的澄清问题列表
   - 参考现有技术决策
   - 避免重复澄清已确认的业务规则
   ```
   
   **查询结果处理**：
   - 如果类似功能有 decisions.md → 预填充技术决策，减少澄清问题
   - 如果没有类似功能 → 按照标准流程生成澄清问题

3. **检查现有状态**
   - 检查 `workspace/answers.md` 是否已存在
   - 确定当前澄清进度
   - 检查当前 Program STATUS.yml 确认处于"需求澄清"阶段

### 步骤 2：ReAct 循环 - 逐题澄清

**对每个未澄清的问题，执行以下循环：**

#### 2.1 推理 - 分析问题

分析当前问题的业务背景，生成 2-4 个澄清选项：

- 选项应覆盖常见业务场景
- 提供推荐的默认选项（标记"推荐"）
- 必须包含一个"其他（自定义）"选项

**零假设原则**：不要基于"常见做法"或"典型实现"推断答案。每个不确定性都必须通过选项式询问确认。

#### 2.2 行动 - 询问用户

使用 `ask_user_question` 工具，一次只问一个问题。

#### 2.3 观察 - 记录答案

根据用户选择记录结果：
- 如果选择了预设选项：直接记录选项值
- 如果选择了"其他"：继续询问具体需求

#### 2.4 更新 - 实时文档更新

**实时更新 `workspace/answers.md`**

文档格式参考：`.qoder/rules/requirement-clarification-standards.md` 中的"输出格式规范"章节

#### 2.5 下一步 - 确定继续或完成

- 如果还有更多未澄清的问题 → 继续下一题
- 如果所有问题都已澄清 → 生成决策记录，完成

### 步骤 3：生成技术决策记录

所有问题澄清后：

1. 生成 `workspace/decisions.md`（格式参考规则中的 ADR 规范）
2. 更新 STATUS.yml：
   ```yaml
   current_phase: technical specification
   phases:
     - name: requirement clarification
       status: done
     - name: technical specification
       status: pending
   ```

---

## 返回格式

```
状态：已完成 / 进行中 / 需要进一步澄清
报告：
  - workspace/answers.md
  - workspace/decisions.md
进度：X/Y 个问题已确认

Program 状态更新：
  - current_phase: technical specification
  - phases.requirement_clarification.status: done

决策点：
  - 所有高优先级问题已确认：是/否
  - 可以进入技术规格书阶段：是/否
```
