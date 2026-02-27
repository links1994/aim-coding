---
name: requirement-clarification
description: 使用 ReAct 模式进行交互式需求澄清，逐个问题确认并实时更新结果
tools: Read, Write, Grep, ask_user_question
language: zh-CN
output_language: 中文
---

# 需求澄清 Skill

> **语言约束**: 本 Skill 的所有输出必须使用中文，包括问题、选项、文档内容和状态报告。

使用 ReAct (Reasoning + Acting) 设计模式，与用户进行交互式需求澄清。逐个问题确认，提供选项供用户选择，实时更新问题清单和确认结果。

> **规范引用**：本 Skill 遵循的规范定义在 `.qoder/rules/02-requirement-clarification.md`，包括澄清流程、问题优先级定义、决策记录格式等。

---

## 触发条件

- 用户指令："澄清 REQ-xxx" 或 "确认需求 REQ-xxx"
- 当前 Program 为 Implementation 类型（非 decomposition 类型）
- 需要从 decomposition Program 引用分解文档

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
  - 从当前 Program ID 推导 decomposition Program ID
  - 示例：当前 `P-2026-001-REQ-031` → 读取 `P-2026-001-decomposition/workspace/decomposition.md`
- `.qoder/rules/02-requirement-clarification.md` — 需求澄清规范
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

### Step 1: 初始化

1. **读取输入**
   - 读取 decomposition.md 定位目标需求
   - 读取澄清规范

2. **知识库查询（可选）**
   - 使用 knowledge-base-query Skill 查询是否已有类似功能实现
   - 查询不确定是否存在的功能或接口
   - 参考已有功能的设计，辅助生成澄清问题

3. **检查现有状态**
   - 检查是否已有 `workspace/questions.md`
   - 检查是否已有 `workspace/answers.md`
   - 确定当前澄清进度
   - 检查当前 Program STATUS.yml 确认处于"需求澄清"阶段

3. **初始化内存状态**
   ```json
   {
     "req_id": "REQ-001",
     "status": "in_progress",
     "current_question_index": 0,
     "total_questions": 5,
     "questions": [
       {
         "id": "Q1",
         "content": "智能员工名称唯一性要求",
         "priority": "high",
         "status": "pending",
         "options": [],
         "user_answer": null,
         "final_decision": null
       }
     ]
   }
   ```

### Step 2: ReAct 循环 - 逐个问题澄清

**对于每个未澄清的问题，执行以下循环：**

#### 2.1 Reason - 分析问题

分析当前问题的业务背景，生成 2-4 个澄清选项：

- 选项应覆盖常见的业务场景
- 提供推荐的默认选项（标记"推荐"）
- 必须包含一个"其他（自定义）"选项

**示例：**

```
问题：智能员工名称唯一性要求？

分析：
- 影响数据库索引设计
- 影响业务校验逻辑
- 需要考虑多租户场景

生成选项：
A. 全局唯一（整个系统名称唯一）
B. 租户内唯一（同一租户下名称唯一）[推荐]
C. 不限制唯一性，允许重复
D. 其他（请描述具体需求）
```

#### 2.2 Action - 询问用户

使用 `ask_user_question` 工具，每次只问一个问题：

```json
{
  "questions": [{
    "question": "【REQ-001 | 问题 1/5】智能员工名称唯一性要求？\n\n影响：数据库索引设计、业务校验逻辑\n",
    "options": [
      {"label": "全局唯一", "description": "整个系统名称唯一"},
      {"label": "租户内唯一", "description": "同一租户下名称唯一（推荐）"},
      {"label": "不限制", "description": "允许重复名称"},
      {"label": "其他", "description": "自定义规则"}
    ]
  }]
}
```

#### 2.3 Observe - 记录答案

根据用户选择，记录结果：

- 如果选择 A/B/C：直接记录选项值
- 如果选择 D（其他）：需要继续追问具体需求

**处理"其他"选项：**
```
用户选择：其他
↓
追问：请描述您的具体需求？
↓
用户输入：需要支持同名，但编码必须唯一
↓
记录：名称不唯一，编码全局唯一
```

#### 2.4 Update - 实时更新文档

**实时更新 `workspace/questions.md`**：
```markdown
## 问题列表

### 高优先级（阻塞开发）

| 序号 | 问题 | 提出原因 | 建议方案 | 状态 | 用户确认 |
|------|------|----------|----------|------|----------|
| 1 | 智能员工名称唯一性 | 影响数据库索引 | 租户内唯一 | ✅ 已确认 | 租户内唯一 |
| 2 | 创建时绑定技能 | 影响接口参数 | 可选技能列表 | 🔄 进行中 | - |
```

**实时更新 `workspace/answers.md`**：
```markdown
## 问题确认结果

| 序号 | 原始问题 | 用户确认 | 最终决策 | 确认时间 |
|------|----------|----------|----------|----------|
| 1 | 名称唯一性 | 租户内唯一 | 同一租户下名称唯一 | 2026-02-25 10:30 |
```

#### 2.5 Next - 判断继续或结束

- 如果还有未澄清问题 → 继续下一个问题
- 如果所有问题已澄清 → 生成决策记录，结束

### Step 3: 生成技术决策记录

所有问题澄清完成后，生成 `workspace/decisions.md`，并更新 STATUS.yml：

```yaml
# STATUS.yml 更新为
current_phase: 技术规格
phases:
  - name: 需求澄清
    status: completed
  - name: 技术规格
    status: pending
```

```markdown
# 技术决策记录 (ADR)

## 关联需求

REQ-001: 管理后台创建智能员工接口

## 决策汇总

| 决策 ID | 问题 | 决策内容 | 影响范围 |
|---------|------|----------|----------|
| ADR-001-1 | 名称唯一性 | 租户内唯一 | 数据库索引、Service 校验 |
| ADR-001-2 | 技能绑定 | 必填至少一个 | Request DTO、Service 校验 |

## 详细决策

### ADR-001-1: 智能员工唯一性约束

- **日期**: 2026-02-25
- **状态**: 已确认
- **背景**: 用户确认同一租户下名称唯一
- **决策**: 数据库添加联合唯一索引（tenant_id, name）
- **理由**: ...
- **影响**: ...
```

---

## 问题优先级策略

生成问题时，按优先级分类：

| 优先级 | 说明 | 处理策略 |
|--------|------|----------|
| **High** | 阻塞开发，必须确认 | 必须全部澄清后才能进入下一阶段 |
| **Medium** | 影响设计，建议确认 | 尽量澄清，部分可延后 |
| **Low** | 细节优化，可延后 | 可选澄清，可进入设计阶段后处理 |

---

## 交互示例

### 完整对话流程

```
Agent: 开始澄清 REQ-001 [mall-admin] 创建智能员工接口
       共 5 个问题，预计需要 3-5 分钟

Agent: 【问题 1/5 | 高优先级】智能员工名称唯一性要求？
       影响：数据库索引设计、业务校验逻辑
       
       A. 全局唯一（整个系统名称唯一）
       B. 租户内唯一（同一租户下名称唯一）[推荐]
       C. 不限制唯一性，允许重复
       D. 其他（请描述具体需求）

User: 选择 B

Agent: ✅ 已记录：租户内唯一
       
       【问题 2/5 | 高优先级】创建时是否需要绑定技能？
       影响：接口参数设计、业务校验逻辑
       
       A. 必须绑定至少一个技能 [推荐]
       B. 可选绑定，允许空技能
       C. 创建时不绑定，后续单独配置
       D. 其他（请描述具体需求）

User: 选择 A

Agent: ✅ 已记录：必须绑定至少一个技能
       
       ...

Agent: ✅ REQ-001 所有问题已澄清！
       
       已生成文档：
       - workspace/questions/REQ-001.md
       - workspace/answers/REQ-001.md
       - workspace/decisions/REQ-001.md
       
       可以进入技术规格书生成阶段
```

---

## 内存状态管理

在整个澄清过程中，维护以下内存状态：

```json
{
  "clarification_session": {
    "req_id": "REQ-001",
    "program_id": "P-2026-001",
    "start_time": "2026-02-25T10:00:00",
    "status": "in_progress",
    "progress": {
      "total": 5,
      "confirmed": 2,
      "pending": 3
    },
    "questions": [
      {
        "id": "Q1",
        "content": "...",
        "priority": "high",
        "status": "confirmed",
        "user_answer": "租户内唯一",
        "final_decision": "同一租户下名称唯一"
      }
    ]
  }
}
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
  - phases.需求澄清.status: completed

决策点：
  - 高优先级问题已全部确认：是/否
  - 是否可以进入技术规格书阶段：是/否
```

---

## 异常处理

### 用户中断澄清

```
User: 先暂停，我确认一下业务规则

Agent: 已保存当前进度（2/5 个问题已确认）
       可随时继续澄清 REQ-001
```

### 用户改变主意

```
User: 问题 1 我想改成全局唯一

Agent: 确认修改问题 1 的答案？
       原答案：租户内唯一
       新答案：全局唯一
       
       这将影响：数据库索引设计
       
       A. 确认修改
       B. 保持原答案
```
