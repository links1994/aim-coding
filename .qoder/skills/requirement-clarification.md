---
name: requirement-clarification
description: 使用 ReAct 模式进行交互式需求澄清，逐个问题确认并实时更新结果
tools: Read, Write, Grep, ask_user_question
language: zh-CN
output_language: 中文
---

# 需求澄清 Skill

> **语言约束**: 本 Skill 的所有输出必须使用中文，包括问题、选项、文档内容和状态报告。

使用 ReAct (Reasoning + Acting) 设计模式，与用户进行交互式需求澄清。

> **前置条件**：执行前必须先读取 `.qoder/rules/02-requirement-clarification.md` 获取规范标准

---

## 触发条件

- 用户指令："澄清 REQ-xxx" 或 "确认需求 REQ-xxx"
- 当前 Program 为 Implementation 类型（非 decomposition 类型）
- 需要从 decomposition Program 引用分解文档

---

## Program 类型

本 Skill 适用于 **Implementation Program**（实现类型），而非 Decomposition Program。

Implementation Program 命名规范：`{父ID}-REQ-xxx`

示例：
- Decomposition Program: `P-2026-001-decomposition`
- Implementation Program: `P-2026-001-REQ-031`（本 Skill 在此类 Program 中运行）

---

## 输入

- 目标需求 ID（如 REQ-031）
- `orchestrator/PROGRAMS/{decomposition_program_id}/workspace/decomposition.md` — 需求分解文档
- `.qoder/rules/02-requirement-clarification.md` — 需求澄清规范（**必须先读取**）
- 当前 Program 的 STATUS.yml — 更新阶段状态

---

## 输出

- 问题清单 → `orchestrator/PROGRAMS/{current_program_id}/workspace/questions.md`
- 确认结果 → `orchestrator/PROGRAMS/{current_program_id}/workspace/answers.md`
- 技术决策 → `orchestrator/PROGRAMS/{current_program_id}/workspace/decisions.md`
- 更新 STATUS.yml → 阶段从"需求澄清"更新为"技术规格"

---

## ReAct 工作模式

```
┌─────────────────────────────────────────────────────────────┐
│                      ReAct Loop                             │
├─────────────────────────────────────────────────────────────┤
│  1. Reason: 分析问题，生成选项                              │
│      ↓                                                      │
│  2. Action: 调用 ask_user_question 获取用户选择             │
│      ↓                                                      │
│  3. Observe: 记录用户答案，更新内存状态                     │
│      ↓                                                      │
│  4. Update: 实时更新 questions.md 和 answers.md             │
│      ↓                                                      │
│  5. Next: 下一个问题或结束                                  │
└─────────────────────────────────────────────────────────────┘
```

---

## 工作流程

### Step 1: 初始化与知识库查询

1. **读取输入**
   - 读取 decomposition.md 定位目标需求
   - **必须读取**澄清规范（02-requirement-clarification.md）

2. **知识库查询（自动复用已有决策）**
   
   **使用 knowledge-base-query Skill 查询**：
   ```
   查询类型: feature
   关键词: {当前需求功能关键词}
   
   目的:
   - 查询类似功能的澄清问题清单
   - 参考已有的技术决策
   - 避免重复澄清已确认的业务规则
   ```
   
   **查询结果处理**：
   - 如有相似功能的 decisions.md → 预填充技术决策，减少澄清问题
   - 如无相似功能 → 按标准流程生成澄清问题

3. **检查现有状态**
   - 检查是否已有 `workspace/questions.md`
   - 检查是否已有 `workspace/answers.md`
   - 确定当前澄清进度
   - 检查当前 Program STATUS.yml 确认处于"需求澄清"阶段

### Step 2: ReAct 循环 - 逐个问题澄清

**对于每个未澄清的问题，执行以下循环：**

#### 2.1 Reason - 分析问题

分析当前问题的业务背景，生成 2-4 个澄清选项：

- 选项应覆盖常见的业务场景
- 提供推荐的默认选项（标记"推荐"）
- 必须包含一个"其他（自定义）"选项

#### 2.2 Action - 询问用户

使用 `ask_user_question` 工具，每次只问一个问题。

#### 2.3 Observe - 记录答案

根据用户选择，记录结果：
- 如果选择预设选项：直接记录选项值
- 如果选择"其他"：继续追问具体需求

#### 2.4 Update - 实时更新文档

**实时更新 `workspace/questions.md` 和 `workspace/answers.md`**

文档格式参考：`.qoder/rules/02-requirement-clarification.md` 中的"输出格式规范"章节

#### 2.5 Next - 判断继续或结束

- 如果还有未澄清问题 → 继续下一个问题
- 如果所有问题已澄清 → 生成决策记录，结束

### Step 3: 生成技术决策记录

所有问题澄清完成后：

1. 生成 `workspace/decisions.md`（格式参考 Rule 中的 ADR 规范）
2. 更新 STATUS.yml：
   ```yaml
   current_phase: 技术规格
   phases:
     - name: 需求澄清
       status: done
     - name: 技术规格
       status: pending
   ```

---

## 返回格式

```
状态：已完成 / 进行中 / 需要继续澄清
报告：
  - workspace/questions.md
  - workspace/answers.md
  - workspace/decisions.md
进度：X/Y 个问题已确认

Program 状态更新：
  - current_phase: 技术规格
  - phases.需求澄清.status: done

决策点：
  - 高优先级问题已全部确认：是/否
  - 是否可以进入技术规格书阶段：是/否
```
