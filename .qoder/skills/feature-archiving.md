---
name: feature-archiving
description: 业务功能归档 Skill，将已完成功能归档到知识库
tools: Read, Write, Grep, Glob
---

# 业务功能归档 Skill

将已完成的业务功能进行结构化归档到知识库，供后续开发自动检索复用。

---

## 触发条件

- 业务功能开发完成并通过测试
- 用户明确指令："归档功能" 或 "委托: 归档功能"
- 主 Agent 在功能验收后调用（作为最后一个 task）

---

## 输入

- `orchestrator/PROGRAMS/{program_id}/workspace/tech-spec.md` — 技术规格书
- `orchestrator/PROGRAMS/{program_id}/workspace/openapi.yaml` — API 定义
- `orchestrator/PROGRAMS/{program_id}/workspace/decisions.md` — 技术决策记录
- `{service}/src/main/java/` — 相关 Java 源代码（相对于项目根目录）
- `orchestrator/ALWAYS/RESOURCE-MAP.yml` — 项目资源映射

---

## 输出

- 功能档案：`.qoder/repowiki/features/{feature-id}/feature-archive.md`
- 功能索引：`.qoder/repowiki/features/index.md`
- 复用指南：`.qoder/repowiki/features/{feature-id}/reuse-guide.md`

---

## 工作流程

### Step 1: 读取功能信息

1. **读取输入文档**
   - 读取技术规格书，提取功能定义
   - 读取技术决策记录，获取关键设计决策
   - 扫描相关源代码文件

2. **知识库更新判断**
   - 更新功能档案（feature）：归档新功能设计
   - 更新坑点档案（pitfall）：如发现新的违规模式
   - 更新表结构（schema）：如功能涉及旧表改造

### Step 2: 生成功能档案

1. 读取模板：`.qoder/repowiki/features/_TEMPLATE/feature-archive.md`
2. 根据功能信息填充模板变量
3. 生成档案文件：`.qoder/repowiki/features/{feature-id}/feature-archive.md`

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

在 `.qoder/repowiki/features/index.md` 中添加条目：

```markdown
| 功能ID | 功能名称 | 服务 | 归档日期 | 状态 |
|--------|----------|------|----------|------|
| F-001 | 智能员工创建 | mall-agent | 2026-02-25 | 可用 |
```

### Step 4: 生成复用指南

生成 `.qoder/repowiki/features/{feature-id}/reuse-guide.md`，包含：
- 快速复用场景（完整功能复用、部分组件复用）
- 修改建议
- 问题解答

---

## 归档目录结构

```
.qoder/repowiki/features/              # 功能归档目录
├── index.md                           # 功能索引
├── _TEMPLATE/                         # 功能归档模板
│   └── feature-archive.md
├── F-001-create-agent/
│   ├── feature-archive.md             # 功能档案
│   ├── reuse-guide.md                 # 复用指南
│   └── snippets/                      # 代码片段
│       ├── controller-snippet.java
│       └── service-snippet.java
└── F-002-agent-dialogue/
    └── ...
```

---

## 返回格式

```
状态：已完成
报告：.qoder/repowiki/features/{feature-id}/feature-archive.md
产出：3 个文件（档案、索引更新、复用指南）

功能档案已归档到知识库，后续开发可通过 knowledge-base-query Skill 自动检索复用。
```

---

## 相关文档

- **知识库查询 Skill**: `.qoder/skills/knowledge-base-query.md`
- **功能归档目录**: `.qoder/repowiki/features/`
- **API归档 Skill**: `.qoder/skills/api-archiving.md`
- **代码生成 Skill**: `.qoder/skills/java-code-generation.md`
