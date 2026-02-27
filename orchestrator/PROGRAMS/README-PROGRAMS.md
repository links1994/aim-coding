# Program 管理指南

Program 是开发任务的基本单元，每个 Program 对应一个具体的功能开发或技术任务。

---

## Program 结构

```
P-YYYY-NNN-{name}/
├── PROGRAM.md         # 任务定义
├── STATUS.yml         # 状态跟踪
├── SCOPE.yml          # 写入范围
└── workspace/         # 工作文档
    ├── decomposition.md     # 需求拆分结果（Phase 1）
    ├── dependencies.md      # 依赖关系图
    ├── questions.md         # 澄清问题（Phase 2）
    ├── answers.md           # 确认答案
    ├── decisions.md         # 技术决策
    ├── CHECKPOINT.md        # 上下文快照
    ├── HANDOFF.md           # 交接文档
    └── RESULT.md            # 最终成果
```

---

## 文件说明

### PROGRAM.md

任务定义文件，包含：

- **目标**：一句话描述要实现什么
- **背景**：为什么要做这个任务
- **方案**：技术方案概述
  - 整体思路
  - 关键设计决策
  - 涉及的模块
- **涉及文件**：需要修改的主要文件
- **验收标准**：完成条件

### STATUS.yml

状态跟踪文件，格式：

```yaml
program: P-YYYY-NNN
name: 任务标题
phase: planning          # planning / in-progress / review / done
status: not-started      # not-started / active / blocked / deployed

# Spec Mode 阶段跟踪（用于 implementation program）
spec_phases:
  - name: 需求澄清
    status: pending      # pending / in-progress / done / blocked
  - name: 技术规格
    status: pending
  - name: 开发实现
    status: pending
  - name: 测试验证
    status: pending
  - name: 功能归档
    status: pending

tasks:
  - id: t1
    name: 任务描述
    status: pending      # pending / in-progress / done / blocked
  - id: t2
    name: 任务描述
    status: pending

# 产出物跟踪
artifacts:
  - path: workspace/questions.md
    status: pending
    desc: 问题清单
  - path: workspace/answers.md
    status: pending
    desc: 确认结果
  - path: workspace/decisions.md
    status: pending
    desc: 技术决策记录
  - path: workspace/tech-spec.md
    status: pending
    desc: 技术规格书
  - path: workspace/openapi.yaml
    status: pending
    desc: OpenAPI 定义
  - path: workspace/checklist.md
    status: pending
    desc: 验收清单
  - path: .qoder/repowiki/feature/{feature-id}/feature-archive.md
    status: pending
    desc: 功能归档档案
```

### SCOPE.yml

写入范围控制文件，格式：

```yaml
write:
  - orchestrator/PROGRAMS/P-YYYY-NNN-{name}/workspace/
  - repos/mall-agent/src/main/java/com/aim/mall/agent/

forbidden:
  - repos/mall-common/
  - .env
```

- **write**：Agent 可以修改的文件/目录
- **forbidden**：绝对不能写入的文件

### workspace/

工作文档目录，存放：

- 需求拆分结果（Phase 1）
- 澄清问题和答案（Phase 2）
- 技术决策记录
- 上下文快照（CHECKPOINT.md）
- 交接文档（HANDOFF.md）
- 最终成果（RESULT.md）

---

## 创建新 Program

### 方式一：从模板创建

```bash
cp -r orchestrator/PROGRAMS/_TEMPLATE orchestrator/PROGRAMS/P-2026-002-feature-name
```

然后编辑：
1. `PROGRAM.md` - 填写任务定义
2. `SCOPE.yml` - 设置写入范围
3. `STATUS.yml` - 初始化状态

### 方式二：通过 Agent 创建

```
用户: "新 Program: 实现用户登录功能"
Agent:
  → 从模板创建 P-2026-002-user-login/
  → 生成初始 PROGRAM.md
  → 询问写入范围
  → 初始化 STATUS.yml
```

---

## Program 生命周期

```
创建 → 规划 → 执行 → 完成

1. 创建：从模板创建目录结构
2. 规划：编写 PROGRAM.md，明确目标和方案
3. 执行：按 Spec 模式推进（Phase 1-6）
4. 完成：编写 RESULT.md，更新 STATUS.yml
```

---

## 命名规范

Program ID 格式：`P-YYYY-NNN-{name}`

- **P**：Program 的缩写
- **YYYY**：年份
- **NNN**：序号（001-999）
- **name**：功能名称（小写，用连字符连接）

示例：
- `P-2026-001-ai-agent-decomposition`
- `P-2026-002-user-login`
- `P-2026-003-order-management`

---

## 快速命令

| 命令 | 作用 |
|------|------|
| `继续 P-2026-001` | 加载并继续指定 Program |
| `新 Program: xxx` | 创建新的开发任务 |
| `查看 P-2026-001 状态` | 读取 STATUS.yml |
