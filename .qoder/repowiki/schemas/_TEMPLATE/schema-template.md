---
table_name: {table-name}
description: {table-description}
database: {database-name}
service: {service-name}
engine: InnoDB
charset: utf8mb4
designed_by: {req-id}
designed_at: {design-date}
feature_ref: {feature-id}
version: v1.0
status: active
---

# {table-name}

## 基本信息

| 属性 | 值 |
|-----------|-------|
| 表名 | {table-name} |
| 中文名 | {table-chinese-name} |
| 所属服务 | {service-name} |
| 所属数据库 | {database-name} |
| 设计需求 | {req-id} |
| 功能引用 | {feature-id} |
| 设计日期 | {design-date} |
| 版本 | v1.0 |
| 状态 | active |

---

## 字段列表

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 说明 |
|------------|-----------|----------|---------|------------|---------|
| id | BIGINT | NO | AUTO_INCREMENT | PK | 主键ID |
| {field-name} | {data-type} | {nullable} | {default} | {constraint} | {comment} |

---

## 索引信息

| 索引名 | 类型 | 字段 | 说明 |
|------------|------|--------|---------|
| PRIMARY | Primary | id | 主键索引 |
| {index-name} | {type} | {fields} | {description} |

---

## 外键关系

| 名称 | 字段 | 引用表 | 引用字段 | 删除规则 |
|------|-------|-----------|-----------|-----------|
| {fk-name} | {field} | {ref-table} | {ref-field} | {on-delete} |

---

## DDL 语句

```sql
CREATE TABLE `{table-name}` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `{field-name}` {data-type} {nullable} DEFAULT {default} COMMENT '{comment}',
  ...
  PRIMARY KEY (`id`),
  {index-definitions}
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='{table-comment}';
```

---

## 业务规则

1. **规则1**: {rule-description}
2. **规则2**: {rule-description}

---

## 使用场景

### 查询场景1

```sql
SELECT * FROM {table-name} 
WHERE {condition} AND is_deleted = 0;
```

### 查询场景2

```sql
SELECT * FROM {table-name} 
WHERE {condition} 
ORDER BY {order-field} {order-direction}
LIMIT {limit} OFFSET {offset};
```

---

## 关联表

| 关联表 | 关系 | 说明 |
|--------------|--------------|-------------|
| {related-table} | {relationship} | {description} |

---

## ER 图

```mermaid
erDiagram
    {TABLE_NAME} ||--o{ RELATED_TABLE : "relationship"
    {TABLE_NAME} {
        bigint id PK
        varchar code UK
        bigint related_id FK
    }
```

---

## 变更历史

| 版本 | 日期 | 变更内容 | 变更需求 |
|---------|------|----------|----------|
| v1.0 | {date} | 初始设计 | {req-id} |
| v1.1 | {date} | {changes} | {req-id} |
