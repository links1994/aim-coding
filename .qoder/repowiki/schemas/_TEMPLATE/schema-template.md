---
table_name: {表名}
description: {一句话描述表的用途和核心功能}
database: {数据库名}
service: {所属服务}
module: {所属模块}
engine: {存储引擎}
charset: {字符集}
collation: {排序规则}
estimated_rows: {数据量预估}
status: active
archive_date: {归档日期}
archived_by: {归档人}
---

# {表名}

## 基本信息

| 属性 | 值 |
|------|-----|
| 表名 | {table-name} |
| 中文名 | {中文名称} |
| 所属数据库 | {database-name} |
| 所属服务 | {service-name} |
| 所属模块 | {module-name} |
| 存储引擎 | {InnoDB/MyISAM等} |
| 字符集 | {utf8mb4等} |
| 数据量预估 | {数据量} |
| 归档日期 | {YYYY-MM-DD} |
| 状态 | {active/deprecated} |

### 表用途

{详细描述表的用途、存储什么数据、在系统中的角色}

### 业务场景

- {场景1}: {说明1}
- {场景2}: {说明2}
- {场景3}: {说明3}

---

## 字段清单

### 字段详情

| 序号 | 字段名 | 数据类型 | 长度/精度 | 可空 | 默认值 | 约束 | 注释 |
|------|--------|----------|-----------|------|--------|------|------|
| 1 | {id} | {BIGINT} | {20} | {NO} | {AUTO_INCREMENT} | {PK} | {主键ID} |
| 2 | {field1} | {VARCHAR} | {64} | {NO} | {''} | {NOT NULL} | {字段1说明} |
| 3 | {field2} | {INT} | {11} | {YES} | {NULL} | {} | {字段2说明} |
| 4 | {created_at} | {DATETIME} | {0} | {NO} | {CURRENT_TIMESTAMP} | {} | {创建时间} |
| 5 | {updated_at} | {DATETIME} | {0} | {NO} | {CURRENT_TIMESTAMP} | {ON UPDATE} | {更新时间} |

### 字段说明

#### {field1}

- **用途**: {字段用途}
- **取值范围**: {取值范围}
- **业务规则**: {业务规则}
- **注意事项**: {注意事项}

#### {field2}

- **用途**: {字段用途}
- **取值范围**: {取值范围}
- **枚举值**: {如有枚举值请列出}
  - `value1`: {含义1}
  - `value2`: {含义2}
- **注意事项**: {注意事项}

### 敏感字段

| 字段名 | 敏感类型 | 加密方式 | 脱敏规则 |
|--------|----------|----------|----------|
| {phone} | 手机号 | {AES} | {中间4位*} |
| {id_card} | 身份证号 | {AES} | {仅显示后4位} |

---

## 索引信息

### 主键

| 索引名 | 字段 | 说明 |
|--------|------|------|
| PRIMARY | {id} | {主键索引说明} |

### 唯一索引

| 索引名 | 字段 | 说明 |
|--------|------|------|
| {uk_field1} | {field1} | {唯一索引说明} |
| {uk_field2_field3} | {field2, field3} | {组合唯一索引说明} |

### 普通索引

| 索引名 | 字段 | 类型 | 说明 |
|--------|------|------|------|
| {idx_field4} | {field4} | {BTREE} | {普通索引说明} |
| {idx_field5_field6} | {field5, field6} | {BTREE} | {组合索引说明} |

### 全文索引（如有）

| 索引名 | 字段 | 说明 |
|--------|------|------|
| {ft_idx_content} | {content} | {全文索引说明} |

### 索引优化建议

- **建议1**: {索引优化建议}
- **建议2**: {索引优化建议}

---

## 外键关系

### 引用其他表（外键）

| 字段 | 引用表 | 引用字段 | 级联删除 | 级联更新 | 说明 |
|------|--------|----------|----------|----------|------|
| {user_id} | {tb_user} | {id} | {NO} | {NO} | {关联用户表} |
| {order_id} | {tb_order} | {id} | {CASCADE} | {NO} | {关联订单表} |

### 被其他表引用

| 引用表 | 引用字段 | 本表字段 | 说明 |
|--------|----------|----------|------|
| {tb_user_role} | {user_id} | {id} | {用户角色关联表} |
| {tb_user_address} | {user_id} | {id} | {用户地址表} |

---

## 分区信息（如有）

### 分区类型

{HASH/RANGE/LIST等}

### 分区详情

| 分区名 | 分区条件 | 数据量预估 |
|--------|----------|------------|
| {p0} | {条件0} | {数据量} |
| {p1} | {条件1} | {数据量} |

---

## DDL 语句

### 建表语句

```sql
CREATE TABLE `{table_name}` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `field1` varchar(64) NOT NULL DEFAULT '' COMMENT '字段1',
  `field2` int(11) DEFAULT NULL COMMENT '字段2',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_field1` (`field1`),
  KEY `idx_field2` (`field2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='{表注释}';
```

### 变更历史 DDL

#### v1.1 - {日期} - {变更人}

```sql
-- 添加新字段
ALTER TABLE `{table_name}` ADD COLUMN `new_field` varchar(32) DEFAULT NULL COMMENT '新字段' AFTER `field1`;

-- 添加索引
ALTER TABLE `{table_name}` ADD INDEX `idx_new_field` (`new_field`);
```

---

## 变更历史

| 版本 | 日期 | 变更类型 | 变更内容 | 变更人 | 影响评估 |
|------|------|----------|----------|--------|----------|
| v1.0 | {日期} | 创建 | 初始建表 | {作者} | {无} |
| v1.1 | {日期} | 新增字段 | 添加 {field_name} 字段 | {作者} | {低} |
| v1.2 | {日期} | 新增索引 | 添加 {index_name} 索引 | {作者} | {中} |
| v2.0 | {日期} | 字段修改 | 修改 {field_name} 长度 | {作者} | {高} |

### 变更详情

#### v1.1 - {日期}

**变更内容**: {详细描述}

**变更原因**: {为什么做这个变更}

**影响范围**: {影响了哪些功能}

**回滚方案**: {如何回滚}

---

## 关联功能

### 使用本表的功能

| 功能ID | 功能名称 | 使用方式 | 说明 |
|--------|----------|----------|------|
| {F-001} | {功能名称1} | {CRUD} | {功能说明} |
| {F-002} | {功能名称2} | {只读查询} | {功能说明} |

### 相关代码位置

| 层 | 类名 | 路径 | 说明 |
|----|------|------|------|
| Entity | {UserEntity} | {repos/mall-user/.../entity/} | {实体类} |
| Mapper | {UserMapper} | {repos/mall-user/.../mapper/} | {数据访问层} |
| Service | {UserService} | {repos/mall-user/.../service/} | {业务逻辑层} |

---

## 性能指标

### 查询性能

| 查询类型 | 平均耗时 | P99耗时 | 执行频率 | 优化建议 |
|----------|----------|---------|----------|----------|
| {主键查询} | {1ms} | {5ms} | {高频} | {无需优化} |
| {条件查询} | {10ms} | {50ms} | {中频} | {建议添加索引} |
| {分页查询} | {50ms} | {200ms} | {低频} | {建议优化} |

### 数据增长趋势

| 时间周期 | 数据增长量 | 增长率 | 备注 |
|----------|------------|--------|------|
| {日} | {1000条} | {1%} | {正常增长} |
| {月} | {30000条} | {30%} | {需关注} |

---

## 注意事项

### 设计约束

- {约束1}: {说明1}
- {约束2}: {说明2}

### 性能提示

- **提示1**: {性能相关提示}
- **提示2**: {性能相关提示}

### 常见陷阱

**陷阱1**: {陷阱描述}
- **原因**: {原因分析}
- **解决方案**: {解决方案}

**陷阱2**: {陷阱描述}
- **原因**: {原因分析}
- **解决方案**: {解决方案}

### 维护建议

- {建议1}
- {建议2}

---

## 相关文档

- **功能档案**: [.qoder/repowiki/features/{feature-id}/feature-archive.md](../../features/{feature-id}/feature-archive.md)
- **数据库归档Skill**: [.qoder/skills/database-schema-archiving.md](../../../skills/database-schema-archiving.md)
- **表索引**: [../index.md](../index.md)
