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

> **说明**：本功能涉及的数据库表结构在此归档。如该表已在之前功能中归档，通过版本号区分迭代；无修改则无需重复归档。

### 本功能涉及的表

| 序号 | 表名 | 设计版本 | 设计需求 | 操作类型 | 说明 |
|------|------|----------|----------|----------|------|
| 1 | {table-name} | v1.0 | {req-id} | 新增/修改 | {description} |

### 表结构详情

#### {table-name} (v{version})

**基本信息**

| 属性 | 值 |
|------|-----|
| 表名 | {table-name} |
| 中文名 | {table-comment} |
| 所属服务 | {service-name} |
| 设计版本 | v{version} |
| 设计需求 | {req-id} |
| 数据量预估 | {estimate} |

**字段定义**

| 字段名 | 类型 | 长度 | 是否可空 | 默认值 | 说明 | 索引 |
|--------|------|------|----------|--------|------|------|
| {field-name} | {type} | {length} | {nullable} | {default} | {comment} | {index} |

**索引设计**

| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| {index-name} | {type} | {fields} | {description} |

**业务规则**

- {rule-1}
- {rule-2}

### 表操作流程

**读操作流程**

```
{读操作场景1}
├── 输入: {input-params}
├── 查询表: {table-name}
├── 查询条件: {query-conditions}
└── 返回: {return-fields}

{读操作场景2}
├── 输入: {input-params}
├── 查询表: {table-name}
├── 关联表: {join-table}
└── 返回: {return-fields}
```

**写操作流程**

```
{写操作场景1}
├── 前置校验: {validation-rules}
├── 操作类型: INSERT/UPDATE/DELETE
├── 操作表: {table-name}
├── 事务控制: {transaction-scope}
└── 后置操作: {post-actions}

{写操作场景2}
├── 前置校验: {validation-rules}
├── 操作类型: INSERT/UPDATE/DELETE
├── 操作表: {table-name}
└── 并发控制: {concurrency-control}
```

**批量操作**

| 场景 | 批量大小 | 优化策略 | 注意事项 |
|------|----------|----------|----------|
| {scenario} | {batch-size} | {strategy} | {notice} |

### 历史版本

| 版本 | 日期 | 变更内容 | 变更需求 |
|------|------|----------|----------|
| v1.0 | {date} | 初始设计 | {req-id} |
| v1.1 | {date} | {changes} | {req-id} |

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
