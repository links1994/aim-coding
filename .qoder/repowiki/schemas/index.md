---
title: 数据库 Schema 索引
services: 0
tables: 0
---

# 数据库 Schema 索引

## 服务列表

| 服务 | 表数量 | 说明 |
|---------|--------|-------------|
| [mall-agent](./mall-agent/_service-overview.md) | 1 | AI 智能员工服务 |
| [mall-user](./mall-user/_service-overview.md) | 0 | 用户服务 |

## 快速参考

### 按表名

| 表名 | 服务 | 说明 | 设计需求 |
|-------|---------|-------------|-------------|
| [aim_agent_job_type](./mall-agent/aim_agent_job_type) | mall-agent | 岗位类型表 | REQ-038 |

### 按功能

| 功能 | 表 |
|---------|--------|
| - | - |

## 命名规范

- **表前缀**: 服务简称小写 + 下划线 (如 `aim_`, `usr_`)
- **主键**: `id` (BIGINT, AUTO_INCREMENT)
- **软删除**: `is_deleted` (TINYINT)
- **时间字段**: `create_time`, `update_time` (DATETIME)

## 使用指南

### 查找表结构

1. **按服务查找**: 进入 `schemas/{service}/_service-overview.md`
2. **按表名查找**: 直接访问 `schemas/{service}/{table-name}.md`
3. **按功能查找**: 查看功能档案中的数据库设计章节

### 表结构文档格式

每个表文档包含：
- 字段定义（类型、约束、说明）
- 索引设计
- 外键关系
- DDL 语句
- 业务规则
- 使用场景示例
