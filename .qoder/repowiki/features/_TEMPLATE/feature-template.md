---
feature_id: {feature-id}
feature_name: {feature-name}
description: {一句话描述功能用途和核心价值}
service: {service-name}
module: {module-name}
version: v1.0
status: active
archive_date: {archive-date}
owner: {owner}
created_at: {created-date}
updated_at: {updated-date}
---

# 功能档案: {feature-name}

## 基本信息

| 属性 | 值 |
|------|-----|
| 功能ID | {feature-id} |
| 功能名称 | {feature-name} |
| 所属服务 | {service-name} |
| 所属模块 | {module-name} |
| 归档日期 | {archive-date} |
| 版本 | {version} |
| 状态 | {status} |
| 负责人 | {owner} |

---

## 功能描述

### 业务背景

{business-background}

### 功能目标

{feature-objectives}

### 用户故事

- 作为 {role}，我想要 {goal}，以便 {benefit}

---

## 接口清单

### REST API 接口

| 序号 | 接口名称 | HTTP方法 | 路径 | 调用方 | 说明 |
|------|----------|----------|------|--------|------|
| 1 | {api-name} | {method} | {path} | {caller} | {description} |

### Feign 接口

| 序号 | 接口名称 | 提供方 | 消费方 | 说明 |
|------|----------|--------|--------|------|
| 1 | {feign-name} | {provider} | {consumer} | {description} |

### MQ 消息

| 序号 | Topic/Queue | 生产者 | 消费者 | 消息类型 | 说明 |
|------|-------------|--------|--------|----------|------|
| 1 | {topic} | {producer} | {consumer} | {type} | {description} |

---

## 核心类清单

### Controller 层

| 类名 | 路径 | 职责 | 接口数量 |
|------|------|------|----------|
| {class-name} | {file-path} | {responsibility} | {count} |

### Service 层

| 类名 | 路径 | 职责 | 方法数量 |
|------|------|------|----------|
| {class-name} | {file-path} | {responsibility} | {count} |

### Mapper 层

| 类名 | 路径 | 职责 | SQL数量 |
|------|------|------|---------|
| {class-name} | {file-path} | {responsibility} | {count} |

### Domain 层

| 类名 | 路径 | 职责 | 说明 |
|------|------|------|------|
| {class-name} | {file-path} | {responsibility} | {note} |

### DTO/VO 层

| 类名 | 路径 | 用途 | 字段数量 |
|------|------|------|----------|
| {class-name} | {file-path} | {usage} | {count} |

---

## 数据库设计归档

> **说明**：本功能涉及的数据库表结构在此汇总。详细的表结构定义见 [Schema 归档](../../schemas/{service}/)。

### 本功能涉及的表

| 序号 | 表名 | 中文名 | 设计版本 | 设计需求 | 操作类型 | Schema 归档 |
|------|------|--------|----------|----------|----------|-------------|
| 1 | {table-name} | {table-comment} | v1.0 | {req-id} | 新增 | [链接](../../schemas/{service}/{table-name}.md) |

### 表关系图

```mermaid
erDiagram
    {TABLE_A} ||--o{ {TABLE_B} : "relationship"
    {TABLE_A} {
        bigint id PK
        varchar code UK
    }
    {TABLE_B} {
        bigint id PK
        bigint {table_a}_id FK
    }
```

### 表设计要点

| 设计决策 | 说明 | 理由 |
|----------|------|------|
| 表名前缀 | {prefix} | {reason} |
| 主键策略 | {strategy} | {reason} |
| 软删除 | {yes/no} | {reason} |
| 分表策略 | {strategy} | {reason} |

### 关键业务规则

- **规则1**: {rule-description}
- **规则2**: {rule-description}

### 表结构快速参考

#### {table-name}

| 字段名 | 类型 | 可空 | 默认值 | 说明 |
|--------|------|------|--------|------|
| id | BIGINT | NO | AUTO_INCREMENT | 主键 |
| ... | ... | ... | ... | ... |

**索引**: {index-summary}

**详细文档**: [../../schemas/{service}/{table-name}.md](../../schemas/{service}/{table-name}.md)

---

## 依赖关系

### 依赖的服务

```mermaid
graph LR
    A[{service-name}] --> B[mall-{dependency-1}]
    A --> C[mall-{dependency-2}]
```

| 服务 | 调用方式 | 接口数量 | 关键接口 |
|------|----------|----------|----------|
| mall-{service} | Feign/HTTP | {count} | {interfaces} |

### 被依赖的服务

| 服务 | 调用方式 | 接口数量 | 关键接口 |
|------|----------|----------|----------|
| mall-{service} | Feign/HTTP | {count} | {interfaces} |

### 外部依赖

| 依赖 | 类型 | 用途 | 版本 |
|------|------|------|------|
| {dependency} | {type} | {usage} | {version} |

---

## 关键设计决策

### 决策1: {decision-title}

**背景**: {background}

**方案**: {solution}

**理由**: {reasoning}

**影响**: {impact}

### 决策2: {decision-title}

**背景**: {background}

**方案**: {solution}

**理由**: {reasoning}

**影响**: {impact}

---

## 测试用例

### 单元测试

| 测试类 | 方法 | 覆盖率 | 说明 |
|--------|------|--------|------|
| {test-class} | {method} | {coverage}% | {description} |

### 集成测试

| 场景 | 输入 | 期望输出 | 状态 |
|------|------|----------|------|
| {scenario} | {input} | {expected} | {status} |

### HTTP 测试

| 接口 | 场景 | 文件路径 |
|------|------|----------|
| {api} | {scenario} | {file-path} |

---

## 性能指标

| 指标 | 目标值 | 实际值 | 测试环境 |
|------|--------|--------|----------|
| 响应时间 (P99) | {target}ms | {actual}ms | {env} |
| 吞吐量 | {target}TPS | {actual}TPS | {env} |
| 并发用户数 | {target} | {actual} | {env} |

---

## 注意事项

### 使用限制

- {limitation-1}
- {limitation-2}

### 常见问题

**Q**: {question-1}
**A**: {answer-1}

**Q**: {question-2}
**A**: {answer-2}

### 后续优化建议

- {improvement-1}
- {improvement-2}

---

## 变更历史

| 版本 | 日期 | 变更内容 | 变更人 |
|------|------|----------|--------|
| v1.0 | {date} | 初始版本 | {author} |
| v1.1 | {date} | {changes} | {author} |

---

## 相关文档

- 技术规格书: `workspace/design.md`
- 代码生成报告: `workspace/code-generation-report.md`
- 复用指南: `./reuse-guide.md`
