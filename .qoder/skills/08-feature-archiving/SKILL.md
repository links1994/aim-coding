---
name: feature-archiving
description: 归档已完成的功能到 repowiki 以供未来复用。在功能实现完成、需要记录以供参考或创建可复用功能模板时使用。
---

# 功能归档 Skill

将已完成的功能归档到 `.qoder/repowiki/`，用于未来开发参考和复用。

> **执行方式**：优先委托给 `doc-generator` Agent 执行；如果 Sub-Agent 不可用，则使用本 Skill 执行

---

## 前置依赖

- `.qoder/repowiki/README-REPO-WIKI.md` — 归档体系说明
- `.qoder/repowiki/features/_TEMPLATE/feature-template.md` — 功能归档模板
- `.qoder/repowiki/schemas/_TEMPLATE/schema-template.md` — 表结构归档模板

---

## 触发条件

- 功能实现完成
- 用户指令："归档功能" 或 "委托：归档功能"
- 实现 Program 的标准工作流最终任务
- 数据库表设计完成需要归档

---

## 输入

- `artifacts/tech-spec.md` — 技术规格书
- `artifacts/answers.md` — 需求澄清结果
- `artifacts/decisions.md` — 技术决策记录
- `repos/` 中的源代码（实体类、DDL 文件）
- Program ID 和 REQ ID

---

## 输出

### 功能归档

- `.qoder/repowiki/features/F-{seq}-{name}/`
  - `archive.md` — 功能档案（必须包含 YAML Frontmatter）
  - `reuse-guide.md` — 复用指南
  - `snippets/` — 可复用代码片段

### 表结构归档

- `.qoder/repowiki/schemas/{service}/{table-name}.md`
- `.qoder/repowiki/schemas/{service}/_service-overview.md`

### 索引更新

- `.qoder/repowiki/features/index.md`
- `.qoder/repowiki/schemas/index.md`

---

## 归档结构

详见：`.qoder/repowiki/README-REPO-WIKI.md`

```
.qoder/repowiki/
├── features/
│   ├── index.md
│   ├── _TEMPLATE/
│   └── F-{seq}-{name}/
│       ├── archive.md
│       ├── reuse-guide.md
│       └── snippets/
└── schemas/
    ├── index.md
    ├── _TEMPLATE/
    └── {service}/
        ├── _service-overview.md
        └── {table-name}.md
```

---

## 工作流程

### 阶段 1：功能归档

1. **读取源文档**
   - 读取 `tech-spec.md`、`answers.md`、`decisions.md`
   - 读取关键源文件（实体类、Mapper 文件）
   - 检查现有功能索引

2. **生成功能 ID**
   - 格式：`F-{sequence:000}-{feature-name}`
   - 示例：`F-001-job-type-management`

3. **创建归档目录**
   ```bash
   mkdir -p .qoder/repowiki/features/F-{id}/snippets
   ```

4. **生成 archive.md**
   - 使用模板：`.qoder/repowiki/features/_TEMPLATE/feature-template.md`
   - **必须包含 YAML Frontmatter**（详见 README-REPO-WIKI.md）
   - 包含功能描述、接口清单、核心类、数据库表结构

5. **生成 reuse-guide.md**
   - 使用模板：`.qoder/repowiki/features/_TEMPLATE/reuse-guide.md`
   - 说明何时复用、关键组件、适配点

6. **提取代码片段**
   - 将可复用代码提取到 `snippets/`

7. **更新功能索引**
   - 更新 `.qoder/repowiki/features/index.md`

### 阶段 2：表结构归档

8. **创建服务目录**
   ```bash
   mkdir -p .qoder/repowiki/schemas/{service}
   ```

9. **生成表结构文档**
   - 使用模板：`.qoder/repowiki/schemas/_TEMPLATE/schema-template.md`
   - **必须包含 YAML Frontmatter**
   - 包含字段列表、索引、外键、DDL

10. **更新服务概览**
    - 更新 `.qoder/repowiki/schemas/{service}/_service-overview.md`

11. **更新表结构索引**
    - 更新 `.qoder/repowiki/schemas/index.md`

---

## 检查清单（强制）

### 功能归档检查

- [ ] `archive.md` 包含完整的 YAML Frontmatter
- [ ] `archive.md` Frontmatter 包含 `feature_id`, `feature_name`, `description`, `service`
- [ ] `reuse-guide.md` 已生成
- [ ] 代码片段已提取到 `snippets/`
- [ ] `features/index.md` 已更新

### 表结构归档检查

- [ ] 表结构文档包含完整的 YAML Frontmatter
- [ ] Frontmatter 包含 `table_name`, `description`, `service`, `database`
- [ ] 字段列表完整（类型、可空、默认值、约束、说明）
- [ ] 索引信息完整
- [ ] DDL 语句正确
- [ ] `_service-overview.md` 已更新
- [ ] `schemas/index.md` 已更新

### 交叉引用检查

- [ ] 功能归档中引用的表结构链接正确
- [ ] 表结构文档中的 `feature_ref` 指向正确的功能 ID
- [ ] 表结构文档中的 `designed_by` 指向正确的 REQ

---

## 返回格式

```
状态：已完成

功能归档：.qoder/repowiki/features/F-{id}-{name}/
  - archive.md
  - reuse-guide.md
  - snippets/

表结构归档：
  - .qoder/repowiki/schemas/{service}/{table-1}.md
  - .qoder/repowiki/schemas/{service}/{table-2}.md

索引已更新：
  - .qoder/repowiki/features/index.md
  - .qoder/repowiki/schemas/index.md
  - .qoder/repowiki/schemas/{service}/_service-overview.md

已归档表：X
已归档功能：1
```

---

## 与开发工作流集成

| 阶段 | 行动 | 输出 |
|------|------|------|
| REQ 实现后 | 归档功能 | 功能归档 + 表结构归档 |
| 表设计后 | 仅归档表结构 | 表结构归档 |
| 技术规格书确认后 | 预归档表结构 | 表结构草稿（可选） |

### 交叉引用

1. **功能 → 表结构**：功能归档列出所有设计的表
2. **表结构 → 功能**：表结构文档引用设计它的 REQ/F
3. **双向可追溯**：轻松找到哪个功能创建了表，或一个功能创建了哪些表

### 查询模式

**按功能查找表：**
```
查找：.qoder/repowiki/features/F-001/archive.md
章节：数据库表结构 → 设计的表
```

**按表查找功能：**
```
查找：.qoder/repowiki/schemas/mall-agent/aim_agent_employee.md
Frontmatter：designed_by, feature_ref
```
