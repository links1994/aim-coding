# 核心工作协议

本文件定义 Agent 的**核心工作流程、状态管理和执行协议**。

> 注意：本文件只定义流程控制，不涉及具体规范。规范定义见 `.qoder/rules/` 目录。

---

## Program 类型

### decomposition（需求拆分）

- **目标**：将 PRD 拆分为可独立实现的子需求
- **产出**：decomposition.md（包含所有 REQ 定义）
- **典型 tasks**：需求拆分
- **后续**：拆分完成后，为每个 REQ 创建独立的 implementation Program

### implementation（需求实现）

- **目标**：基于某个 REQ 进行完整开发流程
- **输入**：decomposition 产出的某个 REQ
- **标准流程**（Spec 模式七阶段）：
  ```
  Phase 1: 需求拆分 → Phase 2: 需求澄清 → Phase 3: 技术规格 → Phase 4: 代码生成 → Phase 5: 测试验证 → Phase 6: 代码质量 → Phase 7: 功能归档
  ```
- **说明**：实际 tasks 由流程控制根据需求动态生成

---

## 多 Program 协作

```
PRD → [decomposition Program] → decomposition.md
                                    ↓
                            REQ-001, REQ-002, REQ-003...
                                    ↓
                    [implementation Program] → 代码实现
```

1. 创建 decomposition Program 完成需求拆分
2. 根据 decomposition.md 中的 REQ，手动创建 implementation Program
3. implementation Program 执行完整的开发流程

---

## 状态定义

状态定义见 `orchestrator/PROGRAMS/_TEMPLATE/STATUS.yml`。

**关键字段**：

- `status`: Program 整体状态（not-started / active / blocked / deployed）
- `phase`: 当前执行阶段（planning / in-progress / review / done）
- `tasks`: 任务列表，由流程控制动态填充
- `artifacts`: 产出物列表，跟踪各产出物的完成状态

### Phase 状态判定规则

`phase` 的最终状态由 **tasks** 和 **artifacts** 共同决定：

| 条件 | Phase 状态 |
|------|-----------|
| 所有 tasks = done 且所有 artifacts = done | `done` |
| 任一 task 或 artifact = blocked | `blocked` |
| 任一 task 或 artifact = in-progress | `in-progress` |
| 其他情况 | `planning` |

---

## 执行流程

### 启动时状态判断

```
1. 读取 STATUS.yml
2. 状态一致性检查：
   ├─ 所有 tasks = done 且所有 artifacts = done，但 status ≠ deployed
   │   → 触发"Program 完成流程"（状态未同步场景）
   │   → 根据 Program 类型执行完成后分支
   │
   └─ 状态正常 → 继续步骤3

3. 遍历 tasks 和 artifacts，找到第一个非 done 的项：
   ├─ status = pending
   │   → 更新 task.status = in-progress
   │   → 更新 phase = in-progress, status = active
   │   → 开始执行
   │
   ├─ status = in-progress
   │   → 继续执行当前任务
   │
   ├─ status = blocked
   │   → 等待用户输入，解除阻塞后继续
   │
   └─ 所有 tasks = done 且所有 artifacts = done
       → 触发"Program 完成流程"
```

### task 完成后

1. **更新 task 状态**：`status = done`
2. **同步更新相关 artifact 状态**（如有对应产出物）：`status = done`
3. **检查剩余 tasks 和 artifacts**：
    - 还有 pending/in-progress → 继续下一个
    - 所有 tasks = done 但还有 artifacts ≠ done → 继续完成剩余产出物
    - 所有 tasks = done 且所有 artifacts = done → 触发 Program 完成流程

### Program 完成流程

当所有 tasks 和 artifacts 状态均为 `done` 时：

1. 更新 `phase = done`, `status = deployed`
2. 生成 `workspace/RESULT.md`（最终成果总结，如不存在）
3. 根据 Program 类型执行完成后分支：

   **decomposition 类型**：
   - 读取 decomposition.md 统计子需求
   - 输出：需求拆分已完成，共 X 个子需求
   - 展示子需求列表（按模块分组）
   - 提示下一步：创建 implementation Program

   **implementation 类型**：
   - 进入只读模式，不再执行任何 Skill
   - 提示：功能已实现，如需修改请新建 Program

---

## 状态同步规范

**核心原则：内存状态与文件状态必须保持一致**

无论何时更新状态，**必须同步更新 STATUS.yml**：

### Tasks 状态更新
- task 开始 → 更新为 in-progress
- task 完成 → 更新为 done
- task 阻塞 → 更新为 blocked

### Artifacts 状态更新
- artifact 开始生成 → 更新为 in-progress
- artifact 生成完成 → 更新为 done
- artifact 生成失败/阻塞 → 更新为 blocked

### Program 状态更新
- Program 完成（所有 tasks 和 artifacts = done）→ 更新 phase = done, status = deployed

---

## Scope 控制

- **只修改** SCOPE.yml 中 `write` 允许的路径
- **超出范围**时询问用户，不要擅自修改
- `forbidden` 列表中的路径绝对不能写入

---

## 决策确认协议（Decision Confirmation Protocol）

### 零假设原则（Zero Assumption Principle）

**定义**：Agent 在面对任何不确定信息时，禁止自行假设或补充论据，必须通过用户确认获得确定性后方可执行。

> 核心口诀：**不确定 → 必须确认 → 获得明确答复 → 方可执行**

### 确认触发条件

以下场景**必须**触发确认流程：

| 场景 | 示例 |
|------|------|
| 信息缺失 | PRD 中未定义字段校验规则 |
| 逻辑歧义 | "支持多种登录方式"（具体指哪些？） |
| 方案选择 | 使用缓存 vs 直接查询数据库 |
| 数值不确定 | 分页大小、超时时间、重试次数 |
| 范围模糊 | "管理员可以管理用户"（哪些操作权限？） |
| 优先级不明 | 多个实现方案时的选择偏好 |

### 确认交互格式：逐个选项式提问（Sequential Options-Based Inquiry）

采用**逐个提问**的方式，确保每个问题都得到明确确认后再进行下一个：

1. **一次只提一个问题**，等待用户明确答复
2. **提供预设选项**（2-4个）
3. **包含"其他"选项**（允许用户自定义）
4. **给出推荐项标识**（如有明确偏好）
5. **确认后记录，再进入下一个问题**

**标准流程**：
```
问题 1：[明确的问题描述]

选项：
A. [选项描述]
B. [选项描述]（推荐）
C. [选项描述]
D. 其他：______

→ 等待用户选择 → 记录确认结果 → 进入问题 2
```

**禁止行为**：
- ❌ 一次性列出所有问题让用户批量回答
- ❌ 未等用户确认当前问题就继续下一个
- ❌ 假设用户会同意某个选项而不等待确认

### 禁止行为（Prohibited Actions）

以下行为**严格禁止**：

- ❌ **自行选择默认值** — 如"假设分页大小为20"
- ❌ **基于"常见做法"推断** — 如"通常使用JWT认证"
- ❌ **延迟确认** — 如"我先按A方案做，不对再改"
- ❌ **模糊确认** — 未获得明确答复就继续执行
- ❌ **假设用户意图** — 如"用户应该是这个意思"

### 确认结果记录

所有确认结果必须记录到对应工作文档：

| 阶段 | 记录文件 | 记录格式 |
|------|----------|----------|
| 需求澄清 | `artifacts/answers.md` | 问题-选项-确认答案 |
| 技术规格 | `artifacts/decisions.md` | ADR 格式记录决策 |
| 代码生成 | 代码注释 + 变更说明 | 关键决策点注释 |

---

## 上下文管理

Agent 的上下文窗口有限。大任务必然跨越多次会话，以下机制保证跨会话零信息损失。

### workspace/ 文件说明

| 文件                  | 时机          | 内容        |
|---------------------|-------------|-----------|
| `STATUS.yml`        | 持续更新        | 阶段进度、任务状态 |
| `HANDOFF.md`        | 会话结束未完成时    | 交接信息      |
| `CHECKPOINT.md`     | 上下文紧张时      | 状态快照      |
| `RESULT.md`         | Program 完成时 | 最终成果总结    |
| `artifacts/`        | 任务执行过程中     | 生成的产出物目录 |

### 功能归档

功能归档是 implementation Program 的最后一个 task。

**归档位置**: `.qoder/repowiki/features/{feature-name}/`

**归档内容**：

- `feature-archive.md`: 功能描述、接口清单、核心类、设计决策
- `reuse-guide.md`: 如何复用此功能
- `snippets/`: 可复用的代码片段

**触发方式**：

- 作为标准流程的最后一个 task 自动执行
- 或用户指令: "归档功能" / "委托: 归档功能"

### HANDOFF 格式

```markdown
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

### CHECKPOINT 格式

```markdown
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

---

## 文件通信原则

- 大段内容（代码、分析报告）写入文件，不要塞进对话
- 引用文件路径而非复制内容
- 保护上下文窗口

---

## 相关文档

| 文档 | 路径 | 说明 |
|------|------|------|
| **需求拆分规范** | `.qoder/rules/prd-decomposition-standards.md` | 服务归属、架构约束 |
| **需求澄清规范** | `.qoder/rules/requirement-clarification-standards.md` | 澄清流程、ADR格式 |
| **技术规格规范** | `.qoder/rules/tech-spec-generation-standards.md` | 规格书结构、错误码 |
| **通用编码规范** | `.qoder/rules/common-coding-standards.md` | Java编码通用规范 |
| **通用架构规范** | `.qoder/rules/common-architecture-standards.md` | 分层架构通用规范 |
| **项目命名规范** | `.qoder/rules/project-naming-standards.md` | 包路径、服务名、表前缀 |
| **项目响应格式规范** | `.qoder/rules/project-common-result-standards.md` | CommonResult使用规范 |
| **项目操作人ID规范** | `.qoder/rules/project-operator-id-standards.md` | 操作人ID传递规范 |
| **开发流程** | `orchestrator/ALWAYS/DEV-FLOW.md` | Git Flow、分支规范 |
| **Sub-Agent** | `orchestrator/ALWAYS/SUB-AGENT.md` | 委托规范 |
