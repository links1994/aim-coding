---
table_name: aim_job_type
description: 岗位类型表，存储智能员工的岗位分类信息
database: mall_agent
service: mall-agent
engine: InnoDB
charset: utf8mb4
designed_by: REQ-038
designed_at: 2026-03-02
feature_ref: F-001
version: v1.0
---

# aim_job_type

## 基本信息

| 属性 | 值 |
|-----------|-------|
| 表名 | aim_job_type |
| 中文名 | 岗位类型表 |
| 服务 | mall-agent |
| 数据库 | mall_agent |
| 设计者 | REQ-038 |
| 功能引用 | F-001 |

---

## 字段列表

| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 说明 |
|------------|-----------|----------|---------|------------|---------|
| id | BIGINT | 否 | AUTO_INCREMENT | 主键 | 主键ID |
| name | VARCHAR(64) | 否 | - | - | 岗位类型名称（如：销售顾问、客服专员） |
| code | VARCHAR(32) | 否 | - | 唯一 | 岗位类型编码（唯一，如：SALES、CS） |
| description | VARCHAR(255) | 是 | NULL | - | 岗位描述 |
| status | TINYINT | 否 | 1 | - | 状态：0-禁用，1-启用 |
| sort_order | INT | 否 | 0 | - | 排序号 |
| create_time | DATETIME | 否 | CURRENT_TIMESTAMP | - | 创建时间 |
| update_time | DATETIME | 否 | CURRENT_TIMESTAMP | 更新时 | 更新时间 |
| is_deleted | TINYINT | 否 | 0 | - | 逻辑删除标记：0-未删除，1-已删除 |
| creator_id | BIGINT | 是 | NULL | - | 创建人ID |
| updater_id | BIGINT | 是 | NULL | - | 更新人ID |

---

## 索引信息

| 索引名 | 类型 | 字段 | 说明 |
|------------|------|--------|---------|
| PRIMARY | 主键 | id | 主键 |
| uk_code | 唯一 | code | 编码唯一约束 |
| idx_status | 普通 | status | 状态查询索引 |
| idx_sort_order | 普通 | sort_order | 排序查询索引 |

---

## DDL

```sql
CREATE TABLE aim_job_type
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    -- 业务字段
    name        VARCHAR(64)  NOT NULL COMMENT '岗位类型名称（如：销售顾问、客服专员）',
    code        VARCHAR(32)  NOT NULL COMMENT '岗位类型编码（唯一，如：SALES、CS）',
    description VARCHAR(255) COMMENT '岗位描述',
    status      TINYINT      DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    sort_order  INT          DEFAULT 0 COMMENT '排序号',
    -- 通用字段
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted  TINYINT      DEFAULT 0 COMMENT '逻辑删除标记：0-未删除 1-已删除',
    creator_id  BIGINT COMMENT '创建人ID',
    updater_id  BIGINT COMMENT '更新人ID',
    -- 索引
    UNIQUE INDEX uk_code (code),
    INDEX idx_status (status),
    INDEX idx_sort_order (sort_order)
) COMMENT '岗位类型表'
    DEFAULT CHARSET = utf8mb4;
```

---

## 业务规则

1. **编码规则**: 仅允许大写字母、数字、下划线（正则：`^[A-Z0-9_]+$`）
2. **编码唯一**: code 字段全局唯一，不可重复
3. **状态默认**: 新建时默认状态为启用（status = 1）
4. **软删除**: 使用 is_deleted 字段，禁止物理删除
5. **不可修改编码**: 更新操作不允许修改 code 字段
6. **删除限制**: 有关联员工的岗位类型不可删除（TODO：待实现）

---

## 使用场景

### 按编码查询
```sql
SELECT * FROM aim_job_type 
WHERE code = 'SALES' AND is_deleted = 0;
```

### 按状态查询
```sql
SELECT * FROM aim_job_type 
WHERE status = 1 AND is_deleted = 0 
ORDER BY sort_order ASC, create_time DESC;
```

### 关键词搜索
```sql
SELECT * FROM aim_job_type 
WHERE is_deleted = 0 
  AND (name LIKE '%关键词%' OR code LIKE '%关键词%')
ORDER BY sort_order ASC, create_time DESC;
```

---

## 关联表

| 表 | 关系 | 说明 |
|-------|--------------|-------------|
| aim_agent_employee | 1:N | 岗位类型下有多个员工（待实现） |

---

## 变更历史

| 版本 | 日期 | 变更 | REQ |
|---------|------|---------|-----|
| v1.0 | 2026-03-02 | 初始设计 | REQ-038 |

---

## 功能引用

- 功能档案: [F-001 岗位类型管理](../../features/F-001-job-type-management/archive.md)
- 技术规格书: [workspace/tech-spec.md](../../../orchestrator/PROGRAMS/P-2026-001-REQ-038/workspace/tech-spec.md)
