# 核心工作协议

本文件定义 Agent 的核心工作流程和协议，不包含具体的编码规范。

**编码规范**请参见：`.qoder/rules/04-coding-standards.md`
**架构规范**请参见：`.qoder/rules/05-architecture-standards.md`

---

## Spec 模式（Quest Mode）

当用户输入产品 PRD 时，自动进入 Spec 模式，按以下六阶段执行：

```
PRD → Phase 1: 需求拆分 → Phase 2: 需求澄清 → Phase 3: 技术规格书 → Phase 4: 代码生成 → Phase 5: HTTP 测试生成 → Phase 6: 代码质量优化 → Phase 7: 功能归档
```

### Phase 1: 需求拆分与依赖分析

- **输入**: PRD 文档
- **Skill**: `.qoder/skills/prd-decomposition.md`
- **规则**: `.qoder/rules/01-prd-decomposition.md`
- **说明**: 生成 decomposition.md 后，本 Program 即结束。后续为每个 REQ 创建独立的 Implementation Program。

### Phase 2: 需求澄清与确认

- **输入**: 需求拆分结果（从 decomposition Program 读取）
- **Skill**: `.qoder/skills/requirement-clarification.md`
- **规则**: `.qoder/rules/02-requirement-clarification.md`
- **注意**: 此阶段在 Implementation Program 中执行，需要用户参与确认

### Phase 3: 技术规格书生成

- **输入**: 确认后的需求 + 技术决策
- **Skill**: `.qoder/skills/tech-spec-generation.md`
- **规则**: `.qoder/rules/03-tech-spec-generation.md`
- **规范**: `.qoder/rules/05-architecture-standards.md`

### Phase 4: 代码生成

- **输入**: 技术规格书
- **Skill**: `.qoder/skills/java-code-generation.md`
- **规范**: `.qoder/rules/04-coding-standards.md`

### Phase 5: HTTP 测试生成

- **输入**: 生成的 Controller 代码
- **Skill**: `.qoder/skills/http-test-generation.md`

### Phase 6: 代码质量优化

- **输入**: 生成的代码
- **Skill**: `.qoder/skills/code-quality-analysis.md`

### Phase 7: 功能归档

- **输入**: 已完成的代码、技术规格书、测试用例
- **Skill**: `.qoder/skills/feature-archiving.md`
- **触发时机**: 功能开发完成并通过测试后
- **输出**: 
  - 功能档案: `.qoder/repowiki/features/{feature-id}/feature-archive.md`
  - 功能索引: `.qoder/repowiki/features/index.md`
- **说明**: 将已完成功能结构化归档，支持后续复用和知识积累

---

**注意**: 各阶段的具体输出文件路径和格式，以对应的 Skill 文件定义为准。

---

## Scope 控制

- **只修改** SCOPE.yml 中 `write` 允许的路径
- **超出范围**时询问用户，不要擅自修改
- `forbidden` 列表中的路径绝对不能写入

---

## 状态持久化

工作过程中维护以下文件（都在 Program 的 workspace/ 下）：

| 文件              | 时机          | 内容          |
|-----------------|-------------|-------------|
| `STATUS.yml`    | 持续更新        | 阶段进度、任务状态   |
| `CHECKPOINT.md` | 上下文紧张时      | 当前状态快照，便于恢复 |
| `HANDOFF.md`    | 会话结束未完成时    | 下次继续需要知道的信息 |
| `RESULT.md`     | Program 完成时 | 最终成果总结      |

### STATUS.yml 同步规范

**核心原则：内存状态与文件状态必须保持一致**

无论何时更新 Program 的执行状态（阶段变更、任务进度、完成标记等），**必须同步更新 STATUS.yml 文件**：

```
内存状态变更 → 立即同步到 STATUS.yml
```

这包括：
- 阶段状态变更（如：需求澄清 → 技术规格）
- 任务进度更新
- 产出物状态变更
- Program 完成标记

**状态映射关系**：

| 工具状态 | STATUS.yml 状态 | 说明 |
|---------|----------------|------|
| `PENDING` | `pending` | 待开始 |
| `IN_PROGRESS` | `in-progress` | 进行中 |
| `COMPLETE` | `done` | 已完成 |
| `ERROR` | `blocked` | 阻塞/出错 |
| `CANCELLED` | `cancelled` | 已取消 |

**示例**：
```yaml
# 当任务 t1 完成，t2 开始时
tasks:
  - id: t1
    status: done           # 从 in-progress 更新为 done
  - id: t2
    status: in-progress    # 从 pending 更新为 in-progress
```

## HANDOFF 格式

```
## HANDOFF

### 当前状态

- 已完成: xxx
- 进行中: yyy

### 分支

`feature/42-xxx` @ <commit-sha>

### 下一步

1. 完成 zzz
2. 测试 aaa

### 注意事项

- bbb 需要特别处理
```

## CHECKPOINT 格式

当上下文窗口紧张时，写入 CHECKPOINT.md 保存当前工作快照：

```
## CHECKPOINT

### 目标

当前 Program 的目标（一句话）

### 已完成

- [x] 任务 1
- [x] 任务 2

### 进行中

- [ ] 任务 3（进度：60%，卡在 xxx）

### 关键决策

- 选择方案 A 因为 yyy

### 文件变更

- `src/foo.ts` — 新增 xxx 功能
- `src/bar.ts` — 修改 yyy 逻辑
```

## 上下文管理

Agent 的上下文窗口有限。大任务必然跨越多次会话，HANDOFF / CHECKPOINT 机制保证跨会话零信息损失。

### 主动保存（推荐）

当对话已经很长、接近上下文上限时，用户会要求你保存进度：

1. **push** 当前分支的所有 commit
2. 更新 **STATUS.yml**（任务进度）
3. 写入 **workspace/HANDOFF.md**（交接文档）
4. 如果上下文特别紧张，额外写 **workspace/CHECKPOINT.md**（更完整的状态快照）
5. 用户开新会话，说 "继续 P-YYYY-NNN"，Agent 从 HANDOFF / CHECKPOINT 恢复

### Compress 后恢复

如果上下文已被自动压缩（compress），信息可能丢失：

1. 回退到 compress 之前的状态
2. 在回退后的完整上下文中执行 HANDOFF 流程
3. 开新会话，从 HANDOFF 恢复

HANDOFF 写入的是结构化的交接文档，信息密度远高于 compress 后的残留上下文。

---

## 文件通信原则

- 大段内容（代码、分析报告）写入文件，不要塞进对话
- 引用文件路径而非复制内容
- 保护上下文窗口

## 工作节奏

1. 明确当前任务
2. 执行并记录进度
3. 遇到决策点询问用户
4. 完成后更新状态

---

## Program 执行判断

**每次执行任何操作前，必须先判断当前 Program 状态**：

### 判断流程

```
1. 读取 STATUS.yml
   ↓
2. 检查 current_phase 和 status
   ↓
3. 判断是否可以执行当前操作
   ↓
4. 执行后更新 STATUS.yml
```

### Program 类型判断

| Program 类型 | 特征 | 可执行操作 |
|-------------|------|-----------|
| **decomposition** | 仅含"需求拆分"阶段，status: completed | 只读，不再执行任何 Skill |
| **implementation** | 含多个阶段（需求澄清/技术规格/代码生成...） | 根据 current_phase 执行对应 Skill |

### 阶段执行规则

```yaml
# 当前阶段判断示例
current_phase: 需求澄清
→ 可执行: requirement-clarification Skill
→ 不可执行: tech-spec-generation, code-generation 等

current_phase: 技术规格  
→ 可执行: tech-spec-generation Skill
→ 需先完成: 需求澄清（answers.md 已生成）
```

### 执行前检查清单

- [ ] 当前 Program 类型是否支持该操作？
- [ ] 当前阶段是否允许执行该 Skill？
- [ ] 前置条件是否满足（前置阶段已完成、必要文件已生成）？
- [ ] 执行后是否需要更新 STATUS.yml？

---

## 相关文档

- **编码规范**: `.qoder/rules/04-coding-standards.md`
- **架构规范**: `.qoder/rules/05-architecture-standards.md`
- **需求拆分**: `.qoder/rules/01-prd-decomposition.md`
- **需求澄清**: `.qoder/rules/02-requirement-clarification.md`
- **技术规格书**: `.qoder/rules/03-tech-spec-generation.md`
- **开发流程**: `orchestrator/ALWAYS/DEV-FLOW.md`
- **Sub-Agent**: `orchestrator/ALWAYS/SUB-AGENT.md`
