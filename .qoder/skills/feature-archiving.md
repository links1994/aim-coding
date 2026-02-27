---
name: feature-archiving
description: 业务功能归档 Skill，将已完成功能归档保存，支持下次迭代基于归档文件直接复用
tools: Read, Write, Grep, Glob
---

# 业务功能归档 Skill

将已完成的业务功能进行结构化归档，生成可复用的功能档案，支持下次迭代时快速理解和复用。

---

## 触发条件

- 业务功能开发完成并通过测试
- 用户明确指令："归档功能" 或 "委托: 归档功能"
- 主 Agent 在功能验收后调用

---

## 输入

- `orchestrator/PROGRAMS/{program_id}/workspace/tech-spec.md` — 技术规格书
- `orchestrator/PROGRAMS/{program_id}/workspace/openapi.yaml` — API 定义
- `{service}/src/main/java/` — 相关 Java 源代码（相对于项目根目录）
- `orchestrator/ALWAYS/RESOURCE-MAP.yml` — 项目资源映射

---

## 输出

- 功能档案：`.qoder/repowiki/feature/{feature-id}/feature-archive.md`
- 功能索引：`.qoder/repowiki/feature/index.md`
- 复用指南：`.qoder/repowiki/feature/{feature-id}/reuse-guide.md`

---

## 工作流程

### Step 1: 读取功能信息

1. 读取技术规格书，提取功能定义
2. 读取代码生成报告，获取实现范围
3. 扫描相关源代码文件

### Step 2: 生成功能档案

1. 读取模板：`.qoder/repowiki/feature/_TEMPLATE/feature-archive.md`
2. 根据功能信息填充模板变量
3. 生成档案文件：`.qoder/repowiki/feature/{feature-id}/feature-archive.md`

**档案包含内容**：
- 基本信息（ID、名称、服务、版本等）
- 功能描述（业务背景、目标、用户故事）
- 接口清单（REST API、Feign、MQ）
- 核心类清单（Controller、Service、Mapper等）
- 数据库变更（表、索引）
- 依赖关系
- 关键设计决策
- 测试用例
- 使用约束

### Step 3: 更新功能索引

在 `.qoder/repowiki/feature/index.md` 中添加条目：

```markdown
| 功能ID | 功能名称 | 服务 | 归档日期 | 状态 |
|--------|----------|------|----------|------|
| F-001 | 智能员工创建 | mall-agent | 2026-02-25 | 可用 |
```

### Step 4: 生成复用指南

生成 `.qoder/repowiki/feature/{feature-id}/reuse-guide.md`，包含：
- 快速复用场景（完整功能复用、部分组件复用）
- 修改建议
- 问题解答

---

## 归档目录结构

```
.qoder/repowiki/feature/              # 功能归档目录（按 repowiki 格式组织）
├── index.md                          # 功能索引
├── _TEMPLATE/                        # 功能归档模板
│   └── feature-archive.md
├── F-001-create-agent/
│   ├── feature-archive.md            # 功能档案（repowiki 格式）
│   ├── reuse-guide.md                # 复用指南
│   └── snippets/                     # 代码片段
│       ├── controller-snippet.java
│       └── service-snippet.java
└── F-002-agent-dialogue/
    └── ...
```

---

## 返回格式

```
状态：已完成
报告：.qoder/repowiki/feature/{feature-id}/feature-archive.md
产出：3 个文件（档案、索引更新、复用指南）
决策点：无
```

---

## 使用示例

### 归档单个功能

```
用户: "归档功能 F-001"
主 Agent:
  → 调用 feature-archiving Skill
  → 读取 tech-spec.md 和源代码
  → 生成 feature-archive.md
  → 更新 index.md
  → 生成 reuse-guide.md
```

### 基于档案复用

```
用户: "基于 F-001 档案创建新功能"
主 Agent:
  → 读取 .qoder/repowiki/feature/F-001/feature-archive.md
  → 理解功能结构和设计决策
  → 调用 java-code-generation Skill 生成新代码
```

---

## 相关文档

- **知识库查询 Skill**: `.qoder/skills/knowledge-base-query.md`（用于检索已归档功能）
- **功能归档目录**: `.qoder/repowiki/feature/`
- **功能归档模板**: `.qoder/repowiki/feature/_TEMPLATE/feature-archive.md`
- **代码生成 Skill**: `.qoder/skills/java-code-generation.md`
