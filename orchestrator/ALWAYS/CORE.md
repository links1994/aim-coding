# 核心工作协议

本文件定义 Agent 的**核心工作流程、状态管理和执行协议**。

> 注意：本文件只定义流程控制，不涉及具体规范。规范定义见 `.qoder/rules/` 目录。

---

## Program 类型

### decomposition（需求拆分）

- **目标**：将 PRD 拆分为可独立实现的子需求
- **产出**：`decomposition.md`（包含所有 REQ 定义）
- **执行者**：Skill (`prd-decomposition`)
- **后续**：拆分完成后，为每个 REQ 创建独立的 implementation Program

### implementation（需求实现）

- **目标**：基于某个 REQ 进行完整开发流程
- **输入**：decomposition 产出的某个 REQ
- **标准流程**（Agent + Skill 混合模式）：

```
Phase 1: PRD 分解        → Skill: prd-decomposition
                              ↓ 读取 prd-decomposition-standards.md
Phase 2: 需求澄清        → Skill: requirement-clarification
          技术调研        → Agent: tech-researcher (并行)
                              ↓ 读取 requirement-clarification-standards.md
Phase 3: 技术规格        → Agent: doc-generator
                              ↓ 读取 tech-spec-generation-standards.md
Phase 4: 代码生成        → Agent: code-generator
                              ↓ 读取所有编码/架构/项目规范
Phase 5: 测试验证        → Skill: http-test-generation
Phase 6: 代码质量        → Agent: code-reviewer
Phase 6.5: 单元测试(可选) → Skill: unit-test-generation
Phase 8: 功能归档        → Agent: doc-generator
```

---

## 多 Program 协作

```
PRD → [decomposition Program] → decomposition.md
                                    ↓
                            REQ-001, REQ-002, REQ-003...
                                    ↓
                    [implementation Program] → 代码实现
```

---

## 状态定义

状态定义见 `orchestrator/PROGRAMS/_TEMPLATE/STATUS.yml`。

**关键字段**：

- `status`: Program 整体状态（not-started / active / blocked / deployed）
- `phase`: 当前执行阶段（planning / in-progress / review / done）
- `tasks`: 任务列表，由流程控制动态填充
- `artifacts`: 产出物列表，跟踪各产出物的完成状态

### Phase 状态判定规则

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
2. 状态一致性检查
3. 遍历 tasks 和 artifacts，找到第一个非 done 的项：
   ├─ pending → 更新为 in-progress，执行能力检测与自动选择
   ├─ in-progress → 继续执行当前任务
   ├─ blocked → 等待用户输入
   └─ 所有 done → 触发 Program 完成流程
```

### 能力检测与自动选择

```
检测 Sub-Agent 可用性
    ↓
├─ 可用 → 委托 Agent 执行（并行、独立决策）
└─ 不可用 → 调用 Skill 执行（标准化流程）
    ↓
产出格式保持一致
```

### task 完成后

1. 更新 task 状态：`status = done`
2. 同步更新相关 artifact 状态
3. 检查剩余 tasks 和 artifacts，决定下一步

### Program 完成流程

当所有 tasks 和 artifacts 状态均为 `done` 时：

1. 更新 `phase = done`, `status = deployed`
2. 生成 `workspace/RESULT.md`（最终成果总结）
3. 根据 Program 类型执行完成后分支

---

## 状态同步规范

**核心原则：内存状态与文件状态必须保持一致**

无论何时更新状态，**必须同步更新 STATUS.yml**：

- task 开始 → 更新为 in-progress
- task 完成 → 更新为 done
- task 阻塞 → 更新为 blocked
- artifact 开始生成 → 更新为 in-progress
- artifact 生成完成 → 更新为 done
- Program 完成 → 更新 phase = done, status = deployed

---

## Scope 控制

- **只修改** SCOPE.yml 中 `write` 允许的路径
- **超出范围**时询问用户，不要擅自修改
- `forbidden` 列表中的路径绝对不能写入

---

## 决策确认协议（Decision Confirmation Protocol）

### 零假设原则

> **不确定 → 必须确认 → 获得明确答复 → 方可执行**

### 确认触发条件

以下场景**必须**触发确认流程：

| 场景 | 示例 |
|------|------|
| 信息缺失 | PRD 中未定义字段校验规则 |
| 逻辑歧义 | "支持多种登录方式"（具体指哪些？） |
| 方案选择 | 使用缓存 vs 直接查询数据库 |
| 数值不确定 | 分页大小、超时时间、重试次数 |
| 范围模糊 | "管理员可以管理用户"（哪些操作权限？） |

### 确认交互格式：选项式提问

1. **一次只提一个问题**，等待用户明确答复
2. **提供预设选项**（2-4个）
3. **包含"其他"选项**（允许用户自定义）
4. **给出推荐项标识**（如有明确偏好）

**禁止行为**：

- ❌ 一次性列出所有问题让用户批量回答
- ❌ 未等用户确认当前问题就继续下一个
- ❌ 假设用户会同意某个选项而不等待确认

### 确认结果记录

| 阶段 | 记录文件 | 记录格式 |
|------|----------|----------|
| 需求澄清 | `artifacts/answers.md` | 问题-选项-确认答案 |
| 技术规格 | `artifacts/decisions.md` | ADR 格式记录决策 |
| 代码生成 | 代码注释 + 变更说明 | 关键决策点注释 |

---

## 上下文管理

### workspace/ 文件说明

| 文件 | 时机 | 内容 |
|------|------|------|
| `STATUS.yml` | 持续更新 | 阶段进度、任务状态 |
| `HANDOFF.md` | 会话结束未完成时 | 交接信息 |
| `CHECKPOINT.md` | 上下文紧张时 | 状态快照 |
| `RESULT.md` | Program 完成时 | 最终成果总结 |
| `artifacts/` | 任务执行过程中 | 生成的产出物目录 |

### 功能归档

功能归档是 implementation Program 的最后一个 task。

**归档位置**: `.qoder/repowiki/features/{feature-name}/`

**归档内容**：

- `archive.md`: 功能描述、接口清单、核心类、设计决策
- `reuse-guide.md`: 如何复用此功能
- `snippets/`: 可复用的代码片段

详见：`.qoder/skills/08-feature-archiving/SKILL.md`

---

## 文件通信原则

- 大段内容（代码、分析报告）写入文件，不要塞进对话
- 引用文件路径而非复制内容
- 保护上下文窗口

---

## 相关文档

| 文档 | 路径 | 说明 |
|------|------|------|
| **需求拆分规范** | `.qoder/rules/prd-decomposition-standards.md` | Phase 1 规范 |
| **需求澄清规范** | `.qoder/rules/requirement-clarification-standards.md` | Phase 2 规范 |
| **技术规格规范** | `.qoder/rules/tech-spec-generation-standards.md` | Phase 3 规范 |
| **通用编码规范** | `.qoder/rules/common-coding-standards.md` | 代码规范 |
| **通用架构规范** | `.qoder/rules/common-architecture-standards.md` | 架构规范 |
| **项目命名规范** | `.qoder/rules/project-naming-standards.md` | 命名规范 |
| **项目错误码规范** | `.qoder/rules/project-error-code-standards.md` | 错误码规范 |
| **项目响应格式规范** | `.qoder/rules/project-common-result-standards.md` | 响应格式 |
| **项目操作人ID规范** | `.qoder/rules/project-operator-id-standards.md` | 操作人ID规范 |
| **开发流程** | `orchestrator/ALWAYS/DEV-FLOW.md` | Git Flow、分支规范 |
| **Sub-Agent** | `orchestrator/ALWAYS/SUB-AGENT.md` | 委托规范 |
| **Agent 定义** | `.qoder/agents/README-AGENTS.md` | Agent 列表 |
| **Skill 定义** | `.qoder/skills/README-SKILLS.md` | Skill 列表 |
