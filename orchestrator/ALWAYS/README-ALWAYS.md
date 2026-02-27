# 核心配置说明

ALWAYS 目录包含 Agent 每次启动都必须读取的核心配置文件。

---

## 文件说明

| 文件 | 说明 | 读取时机 |
|------|------|----------|
| `BOOT.md` | 启动加载顺序 | 首次启动 |
| `CORE.md` | 核心工作协议（流程） | 每次启动 |
| `DEV-FLOW.md` | 开发流程规范 | 需要时 |
| `SUB-AGENT.md` | Sub-Agent 委托规范 | 委托任务时 |
| `RESOURCE-MAP.yml` | 项目资源索引 | 每次启动 |

---

## 加载顺序

```
Agent 启动
  ↓
1. BOOT.md（确定加载顺序）
  ↓
2. CORE.md（核心协议）
3. DEV-FLOW.md（开发流程）
4. RESOURCE-MAP.yml（资源索引）
  ↓
5. PROGRAM.md（如果指定了 Program）
6. STATUS.yml（当前状态）
7. SCOPE.yml（写入范围）
```

---

## 各文件职责

### BOOT.md

- 定义首次启动流程
- 定义正常启动流程
- 定义加载顺序
- 定义新建 Program 流程

### CORE.md

- Spec 模式六阶段流程
- Scope 控制规范
- 状态持久化规范（STATUS.yml、CHECKPOINT.md、HANDOFF.md）
- 文件通信原则
- 工作节奏

**注意**：CORE.md 只包含流程控制，不包含编码规范。
编码规范在 `.qoder/rules/04-coding-standards.md` 中定义。

### DEV-FLOW.md

- Git Flow 工作流
- 完整开发周期（确认任务 → 创建 Worktree → 开发 → PR → 合并 → 清理）
- 分支说明
- Commit 规范

### SUB-AGENT.md

- 何时使用 Sub-Agent
- 核心原则（文件通信、保护上下文）
- 返回规范（4 行格式）
- 任务编号规则
- 委托模板

**注意**：本项目不使用代理模式，Sub-Agent 是简单的任务委托机制。

### RESOURCE-MAP.yml

- 仓库列表（mall-admin、mall-app、mall-chat、mall-agent、mall-user 等）
- 服务调用关系
- 基础设施配置（可选）

---

## 修改建议

这些文件定义了 Agent 的工作方式，修改前请考虑：

1. **BOOT.md**：修改加载顺序会影响所有 Agent 启动
2. **CORE.md**：修改流程会影响所有 Program 的执行
3. **DEV-FLOW.md**：修改 Git 流程会影响代码提交方式
4. **SUB-AGENT.md**：修改委托规范会影响并行任务执行
5. **RESOURCE-MAP.yml**：修改仓库配置会影响代码生成路径

如需修改，请确保：
- 通知所有使用该项目的人
- 更新相关文档
- 测试修改后的效果
