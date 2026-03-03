---
trigger: manual
alwaysApply: false
---

# Agent 索引

本目录包含 Sub-Agent 定义文件，用于并行执行复杂任务。

> **职责边界**：Agent 用于复杂并行任务，具有独立决策能力。标准化任务使用 Skill 执行。

---

## Agent 列表

| Agent | 文件 | 职责 | 使用阶段 | 降级 Skill |
|-------|------|------|----------|-----------|
| code-generator | `code-generator.md` | Java 微服务代码生成 | Phase 4 | `java-code-generation` |
| code-reviewer | `code-reviewer.md` | 代码质量审查 | Phase 6 | `code-quality-analysis` |
| tech-researcher | `tech-researcher.md` | 技术方案调研 | Phase 2/3 | - |
| doc-generator | `doc-generator.md` | 技术文档生成 | Phase 3/8 | `tech-spec-generation` / `feature-archiving` |

---

## 执行机制

```
任务到达
    ↓
检测 Sub-Agent 可用性
    ↓
├─ 可用 ──→ 委托 Agent 执行（并行、独立决策）
│
└─ 不可用 ──→ 调用 Skill 执行（标准化流程）
    ↓
产出格式保持一致
```

---

## Agent vs Skill 对比

| 特性 | Agent | Skill |
|------|-------|-------|
| 复杂度 | 高（完整工作流） | 低（单一能力） |
| 决策能力 | 有（自主判断） | 无（按规范执行） |
| 上下文管理 | 需要维护上下文 | 输入即上下文 |
| 使用方式 | 独立运行或被委托 | 被调用执行 |
| 规范依赖 | 自主判断读取 | 必须读取 Rule |

---

## Agent 文件格式

```yaml
---
name: agent-name
description: Agent 描述，包含触发关键词
tools: [Read, Edit, Write, ...]  # 可用工具列表
---

# Agent 标题

## 核心职责

1. 职责 1
2. 职责 2

## 工作流程

1. 步骤 1
2. 步骤 2

## 返回格式

```
状态：已完成/失败/需要决策
报告：workspace/{X.Y}-report.md
产出：N 个文件
决策点：[如有]
```

## 注意事项

- 必须先读取规范文件
- 使用 knowledge-base-query Skill 查询知识库
```

---

## 使用方式

### 自动委托（推荐）

主 Agent 根据任务特点自动决定：

| 场景 | 执行方式 | 理由 |
|------|----------|------|
| 单一规范任务 | Skill 直接执行 | 流程标准化，无需决策 |
| 复杂并行任务 | Agent 委托 | 需要独立决策和并行处理 |
| 调研查询任务 | Agent 委托 | 需要独立查询和分析 |

### 用户显式委托

用户明确说"委托"时：

```
用户: "委托：生成代码"
→ 创建 Agent 执行

用户: "生成代码"
→ 主 Agent 判断（可能使用 Skill 或委托 Agent）
```

---

## 新增 Agent 步骤

1. 创建 `{agent-name}.md` 文件
2. 遵循上述文件格式
3. 明确降级 Skill（如有）
4. 更新本 README 的 Agent 列表
