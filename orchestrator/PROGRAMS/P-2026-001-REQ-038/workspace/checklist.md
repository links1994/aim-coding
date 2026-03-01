# 验收清单 - P-2026-001-REQ-038

## 数据库设计

- [ ] `aim_agent_job_type` 表创建成功
- [ ] 表结构符合规范（含通用字段：create_time, update_time, is_deleted, creator_id, updater_id）
- [ ] 索引创建正确（uk_code, idx_status, idx_sort_order）
- [ ] 字符集为 utf8mb4

## 实体类

- [ ] `AimJobTypeDO` 实体类创建
- [ ] 字段与表结构一致
- [ ] 使用 Lombok @Data 注解
- [ ] 继承 BaseDO（如有）

## Mapper 层

- [ ] `AimJobTypeMapper` 接口创建
- [ ] `AimJobTypeMapper.xml` 映射文件创建
- [ ] 继承 MyBatis-Plus BaseMapper

## Service 层

- [ ] `AimJobTypeService` 数据服务创建（继承 MyBatis-Plus IService）
- [ ] `JobTypeQueryService` 查询服务创建
- [ ] `JobTypeManageService` 管理服务创建
- [ ] `JobTypeDomainService` 业务域服务创建

## Controller 层

- [ ] `JobTypeInnerController` 创建
- [ ] 实现 `/inner/api/v1/job-types/list` 接口
- [ ] 实现 `/inner/api/v1/job-types/create` 接口
- [ ] 实现 `/inner/api/v1/job-types/update` 接口
- [ ] 实现 `/inner/api/v1/job-types/status` 接口
- [ ] 实现 `/inner/api/v1/job-types/delete` 接口

## 业务规则

- [ ] 创建时默认状态为启用
- [ ] 编码唯一性校验
- [ ] 编码格式校验（大写字母、数字、下划线）
- [ ] 删除时校验关联员工数为0
- [ ] 更新时不允许修改 code

## DTO/枚举

- [ ] `JobTypeDTO` 创建
- [ ] `JobTypeCreateDTO` 创建
- [ ] `JobTypeUpdateDTO` 创建
- [ ] `JobTypePageQuery` 创建
- [ ] `JobTypeStatusEnum` 枚举创建

## 错误码

- [ ] 10091001 参数错误
- [ ] 10091002 岗位类型名称不能为空
- [ ] 10091003 岗位类型编码不能为空
- [ ] 10091004 岗位类型编码格式错误
- [ ] 10092001 岗位类型已存在
- [ ] 10092002 岗位类型不存在
- [ ] 10092003 岗位类型已被删除
- [ ] 10092004 岗位类型有关联员工，无法删除
- [ ] 10092005 岗位类型编码已存在

## TODO 标记

- [ ] 列表查询中员工数量统计添加 TODO 注释
- [ ] 删除校验中员工数量查询添加 TODO 注释

## 文档归档

- [ ] 表结构归档到 feature-archive.md
