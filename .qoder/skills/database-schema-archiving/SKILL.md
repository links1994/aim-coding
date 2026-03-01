---
name: database-schema-archiving
description: 归档数据库表结构到 repowiki。在表设计完成、遗留项目文档化或其他功能需要引用现有表结构时使用。
---

# 数据库表结构归档 Skill

将数据库表结构归档到 `.qoder/repowiki/schemas/`，用于参考和复用。

---

## 触发条件

- 表设计完成
- 需要为遗留项目记录现有表
- 其他功能需要引用表结构
- 用户指令："归档表结构"

---

## 输入

- 表 DDL 或实体类
- 表文档
- 服务归属信息

---

## 输出

- 表结构归档 → `.qoder/repowiki/schemas/{service}/{table-name}.md`

---

## 归档结构

```
.qoder/repowiki/schemas/
├── index.md
├── mall-user/
│   ├── _service-overview.md
│   ├── tb_user.md
│   └── tb_user_role.md
└── mall-order/
    ├── _service-overview.md
    └── tb_order.md
```

---

## 表结构文档格式

```markdown
---
table_name: tb_user
description: 用户主表，存储用户基本信息
database: mall_user
service: mall-user
engine: InnoDB
charset: utf8mb4
created_at: 2026-02-28
---

# tb_user

## 基本信息

| 属性 | 值 |
|-----------|-------|
| 表名 | tb_user |
| 说明 | 用户主表 |
| 服务 | mall-user |
| 数据库 | mall_user |

## 字段列表

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 说明 |
|------------|-----------|----------|---------|------------|---------|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键 | 主键 ID |
| username | VARCHAR(64) | 否 | - | 唯一 | 用户名 |
| phone | VARCHAR(20) | 是 | NULL | - | 手机号，AES 加密 |
| status | TINYINT | 否 | 1 | - | 状态：1-启用，0-禁用 |
| create_time | DATETIME | 否 | CURRENT_TIMESTAMP | - | 创建时间 |
| update_time | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时 | 更新时间 |

## 索引信息

| 索引名 | 类型 | 字段 | 说明 |
|------------|------|--------|---------|
| PRIMARY | 主键 | id | - |
| uk_username | 唯一 | username | 用户名唯一 |
| idx_phone | 普通 | phone | 手机号查询 |

## 外键

| 名称 | 字段 | 引用表 | 引用字段 | 删除时 |
|------|-------|-----------|-----------|-----------|
| fk_user_role | role_id | tb_role | id | CASCADE |

## DDL

```sql
CREATE TABLE `tb_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL,
  ...
) ENGINE=InnoDB;
```
```

---

## 工作流程

### 步骤 1：提取表信息

从 DDL 或实体类中提取：
- 表名
- 字段定义
- 索引定义
- 外键

### 步骤 2：确定服务

确定该表属于哪个服务。

### 步骤 3：创建归档

按照上述格式生成表结构文档。

### 步骤 4：更新索引

更新服务概览和主索引。

---

## 返回格式

```
状态：已完成
归档：.qoder/repowiki/schemas/{service}/{table}.md
服务：{service}
字段数：X
索引数：Y
```
