---
trigger: manual
alwaysApply: false
---

# Repowiki 知识库说明

本目录包含项目的知识库，用于功能归档、表结构归档、API 归档等，供后续开发参考和复用。

---

## 目录结构

```
.qoder/repowiki/
├── README-REPO-WIKI.md      # 本文件
├── features/                 # 功能归档
│   ├── index.md             # 功能索引
│   ├── _TEMPLATE/           # 功能归档模板
│   │   ├── feature-template.md
│   │   └── reuse-guide.md
│   └── F-{seq}-{name}/      # 具体功能归档
│       ├── archive.md       # 功能档案
│       ├── reuse-guide.md   # 复用指南
│       └── snippets/        # 代码片段
├── schemas/                  # 数据库表结构归档
│   ├── index.md             # 表结构索引
│   ├── _TEMPLATE/           # 表结构模板
│   │   ├── schema-template.md
│   │   └── _service-overview.md
│   └── {service}/           # 服务级表结构目录
│       ├── _service-overview.md
│       └── {table-name}.md
├── apis/                     # API 归档
│   └── index.md
└── pitfalls/                 # 陷阱与反模式归档
    └── index.md
```

---

## 归档文件规范

### 所有归档文件必须包含 YAML Frontmatter

```yaml
---
# 功能归档
feature_id: F-001
feature_name: 岗位类型管理
description: 岗位类型CRUD及状态管理
service: mall-agent
program: P-2026-001-REQ-038
tags: [job-type, crud, basic-data]
created_at: 2026-03-01
updated_at: 2026-03-01

# 表结构归档
table_name: aim_agent_job_type
description: 岗位类型表，存储岗位类型配置信息
database: mall_agent
service: mall-agent
designed_by: REQ-038
feature_ref: F-001
version: v1.0

# API 归档
api_name: JobTypeInnerController
service: mall-agent
type: inner
version: v1.0

# 陷阱归档
pitfall_id: P-001
title: Feign 客户端重复创建
category: feign
severity: high
discovered_at: 2026-03-01
---
```

### Frontmatter 必填字段

| 归档类型 | 必填字段 |
|----------|----------|
| 功能归档 | `feature_id`, `feature_name`, `description`, `service` |
| 表结构归档 | `table_name`, `description`, `service`, `database` |
| API 归档 | `api_name`, `service`, `type` |
| 陷阱归档 | `pitfall_id`, `title`, `category`, `severity` |

---

## 索引文件规范

### 功能索引 (`features/index.md`)

```yaml
---
title: 功能档案索引
features_count: 10
services: 3
---
```

### 表结构索引 (`schemas/index.md`)

```yaml
---
title: 数据库 Schema 索引
services: 5
tables: 50
---
```

---

## 查询方式

### 按功能查找表结构

1. 打开 `features/index.md`
2. 找到功能对应的 `F-{id}`
3. 打开 `features/F-{id}/archive.md`
4. 查看"数据库设计归档"章节

### 按表查找功能

1. 打开 `schemas/index.md`
2. 在"按表名"表格中找到表
3. 打开对应的表结构文档
4. 查看 Frontmatter 中的 `feature_ref` 和 `designed_by`

### 按服务查找所有表

1. 打开 `schemas/{service}/_service-overview.md`
2. 查看该服务的所有表列表

---

## 与开发工作流集成

### 何时归档

| 阶段 | 行动 | 输出 |
|------|------|------|
| REQ 实现后 | 归档功能 | `features/F-{id}/` |
| 表设计后 | 归档表结构 | `schemas/{service}/{table}.md` |
| API 设计后 | 归档 API | `apis/{service}/{api}.md` |
| 发现问题后 | 归档陷阱 | `pitfalls/P-{id}.md` |

### 归档触发

- **自动**：作为 implementation Program 的最后一个 task 自动执行
- **手动**：用户指令 "归档功能" / "归档表结构"

详见：`.qoder/skills/08-feature-archiving/SKILL.md`

---

## 复用指南

开发新功能时，先查询知识库：

1. **查找类似功能**：`features/index.md` → 按标签查找
2. **参考代码结构**：`features/F-{id}/snippets/`
3. **查看表结构设计**：`schemas/{service}/{table}.md`
4. **避免已知陷阱**：`pitfalls/index.md`
