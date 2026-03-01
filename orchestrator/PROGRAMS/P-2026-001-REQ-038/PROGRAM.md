# P-2026-001-REQ-038: 岗位类型管理服务

## 目标

实现 mall-agent 服务的岗位类型管理功能，包括岗位类型的CRUD操作及关联员工数统计。

## 背景

- 来源: PRD 第 5.2 节，线框图 A-002/A-003
- 所属需求: REQ-038
- 前置依赖: 无（基础数据表）
- 被依赖: REQ-018（mall-admin岗位管理接口）、REQ-031（智能员工服务）

## 方案

### 整体思路

1. 设计 `aim_job_type` 表结构存储岗位类型数据
2. 实现 JobTypeService 服务层，提供岗位CRUD及状态变更
3. 提供 Inner API 供其他服务调用
4. 完成后归档表结构到 feature-archive.md

### 关键设计决策

- 岗位类型支持启用/禁用状态控制
- 删除岗位时需校验是否有关联员工
- 返回数据包含关联员工数量统计

### 涉及的模块

- mall-agent 服务

## 涉及文件

### 数据库
- 新建 `aim_job_type` 表（本需求设计并归档）

### 服务层
- `repos/mall-agent/src/main/java/com/aim/mall/agent/service/JobTypeService.java` — 岗位类型业务逻辑
- `repos/mall-agent/src/main/java/com/aim/mall/agent/service/impl/JobTypeServiceImpl.java` — 实现类

### 数据访问层
- `repos/mall-agent/src/main/java/com/aim/mall/agent/mapper/JobTypeMapper.java` — MyBatis Mapper接口
- `repos/mall-agent/src/main/resources/mapper/JobTypeMapper.xml` — Mapper XML

### 实体/DTO
- `repos/mall-agent/src/main/java/com/aim/mall/agent/entity/JobType.java` — 实体类
- `repos/mall-agent/src/main/java/com/aim/mall/agent/dto/inner/JobTypeDTO.java` — 内部DTO
- `repos/mall-agent/src/main/java/com/aim/mall/agent/dto/inner/JobTypeCreateRequest.java` — 创建请求
- `repos/mall-agent/src/main/java/com/aim/mall/agent/dto/inner/JobTypeUpdateRequest.java` — 更新请求

### 控制器
- `repos/mall-agent/src/main/java/com/aim/mall/agent/controller/inner/JobTypeInnerController.java` — Inner API

## 接口清单

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /inner/api/v1/job-types/list | 岗位类型列表 |
| POST | /inner/api/v1/job-types/create | 创建岗位类型 |
| PUT | /inner/api/v1/job-types/update | 更新岗位类型 |
| PUT | /inner/api/v1/job-types/status | 状态变更（启用/禁用） |

## 验收标准

- [ ] 设计 aim_job_type 表结构并归档
- [ ] 支持岗位类型CRUD操作
- [ ] 返回关联员工数统计
- [ ] 支持启用/禁用状态控制
- [ ] 删除时校验关联员工
