# 编排系统说明

编排系统（Orchestrator）负责任务的管理、调度和执行。

---

## 目录结构

```
orchestrator/
├── ALWAYS/                    # 核心配置（每次必读）
│   ├── BOOT.md                # 启动加载顺序
│   ├── CORE.md                # 核心工作协议（流程）
│   ├── DEV-FLOW.md            # 开发流程规范
│   ├── SUB-AGENT.md           # Sub-Agent 委托规范
│   ├── RESOURCE-MAP.yml       # 资源索引
│   └── README-ALWAYS.md       # 核心配置说明
│
├── PROGRAMS/                  # 开发任务
│   ├── _TEMPLATE/             # 任务模板
│   ├── README-PROGRAMS.md     # Program 管理指南
│   └── P-YYYY-NNN-{name}/     # 具体任务目录
│       ├── PROGRAM.md         # 任务定义
│       ├── STATUS.yml         # 状态跟踪
│       ├── SCOPE.yml          # 写入范围
│       └── workspace/         # 工作文档
│
└── README-ORCHESTRATOR.md     # 本文件
```

---

## 核心概念

### Program（任务）

一个 Program 代表一个完整的开发任务，包含：

- **PROGRAM.md**：任务定义（目标、背景、方案、验收标准）
- **STATUS.yml**：当前状态和进度
- **SCOPE.yml**：写入范围控制
- **workspace/**：工作文档（产出物、报告等）

### ALWAYS 目录

包含 Agent 每次启动都必须读取的核心配置：

- 启动流程（BOOT.md）
- 工作协议（CORE.md）
- 开发流程（DEV-FLOW.md）
- Sub-Agent 规范（SUB-AGENT.md）
- 资源索引（RESOURCE-MAP.yml）

---

## 工作流程

### 1. Agent 启动

```
Agent 启动
  ↓
读取 ALWAYS/BOOT.md
  ↓
按顺序加载核心配置
  ↓
等待用户指令
```

### 2. 继续已有任务

```
用户: "继续 P-2026-001"
  ↓
读取 PROGRAMS/P-2026-001/PROGRAM.md
  ↓
读取 PROGRAMS/P-2026-001/STATUS.yml
  ↓
读取 PROGRAMS/P-2026-001/SCOPE.yml
  ↓
恢复上下文，继续执行
```

### 3. 创建新任务

```
用户: "新 Program: xxx"
  ↓
从 _TEMPLATE 复制模板
  ↓
创建 P-YYYY-NNN-{name}/
  ↓
编辑 PROGRAM.md、SCOPE.yml
  ↓
开始执行
```

---

## 与 .qoder 的关系

| 目录 | 用途 |
|------|------|
| `.qoder/rules/` | 规范定义（编码标准、阶段规则） |
| `.qoder/skills/` | 能力模块（代码生成、测试生成等） |
| `orchestrator/` | 任务管理（Program、状态、流程） |

**关系**：
- Orchestrator 定义"何时做"（流程）
- Rules 定义"按什么标准做"（规范）
- Skills 定义"具体怎么做"（能力）

---

## 快速参考

| 操作 | 命令/文件 |
|------|----------|
| 查看任务列表 | 扫描 `PROGRAMS/` 目录 |
| 查看任务状态 | 读取 `PROGRAMS/{id}/STATUS.yml` |
| 创建新任务 | 复制 `_TEMPLATE` 到 `P-YYYY-NNN-{name}/` |
| 更新任务状态 | 编辑 `STATUS.yml` |
| 控制写入范围 | 编辑 `SCOPE.yml` |
