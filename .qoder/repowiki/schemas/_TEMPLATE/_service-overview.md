---
service: {service-name}
description: {service-description}
tables_count: {count}
created_at: {create-date}
updated_at: {update-date}
---

# {service-name} 数据库 Schema

## 概述

{service-description}

## 表清单

| 表名 | 中文名 | 说明 | 设计需求 | 功能引用 |
|------------|--------------|-------------|-------------|-------------|
| {table-name} | {table-comment} | {description} | {req-id} | {feature-id} |

## ER 图

```mermaid
erDiagram
    {TABLE_A} ||--|| {TABLE_B} : "belongs_to"
    {TABLE_A} ||--o{ {TABLE_C} : "has_many"
    
    {TABLE_A} {
        bigint id PK
        varchar code UK
    }
    
    {TABLE_B} {
        bigint id PK
        varchar name
    }
    
    {TABLE_C} {
        bigint id PK
        bigint table_a_id FK
    }
```

## 命名规范

- **表前缀**: `{prefix}_` ({prefix-meaning})
- **主键**: `id` (BIGINT, AUTO_INCREMENT)
- **软删除**: `is_deleted` (TINYINT, 0=未删除, 1=已删除)
- **时间字段**: `create_time`, `update_time` (DATETIME)
- **状态字段**: `status` (TINYINT)

## 公共字段

所有表均包含以下公共字段：

| 字段名 | 类型 | 可空 | 默认值 | 说明 |
|------------|------|----------|---------|------|
| id | BIGINT | NO | AUTO_INCREMENT | 主键 |
| is_deleted | TINYINT | NO | 0 | 软删除标志 |
| create_time | DATETIME | NO | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

## 表详情链接

- [{table-name}](./{table-name}.md) - {table-comment}
