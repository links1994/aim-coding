# 复用指南：岗位类型管理

## 何时复用

本功能适合作为**基础数据管理**的参考实现，特别是：

- 需要实现类似的 CRUD 基础数据管理（如部门管理、职位管理、分类管理等）
- 需要启用/禁用状态控制的基础数据
- 需要分页查询和关键词搜索的基础数据
- 需要编码唯一性校验的基础数据

---

## 关键组件

### 1. 五层架构模式

```
Controller → DomainService → Query/Manage Service → AimXxxService → AimXxxMapper
```

**复用要点**：
- `XxxDomainService`: 业务编排，协调查询和管理服务
- `XxxQueryService`: 只读查询，封装查询逻辑
- `XxxManageService`: 增删改操作，封装写逻辑
- `AimXxxService`: 继承 MyBatis-Plus IService，封装数据访问

### 2. 状态枚举模式

```java
@Getter
public enum XxxStatusEnum {
    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    private final Integer code;
    private final String desc;

    // 提供 getByCode 和 isValid 方法
}
```

### 3. 编码唯一性校验模式

```java
// 创建前校验
if (aimXxxService.isCodeExists(dto.getCode())) {
    throw new BusinessException(ErrorCodeEnum.XXX_CODE_EXISTS, "编码已存在");
}

// AimXxxService 中实现
public boolean isCodeExists(String code) {
    return getByCode(code) != null;
}
```

### 4. 分页查询模式

```java
public Page<XxxDO> pageXxx(XxxPageQuery query) {
    int offset = (query.getPageNum() - 1) * query.getPageSize();
    
    Long total = aimXxxService.getBaseMapper().countByKeyword(query.getKeyword());
    List<XxxDO> records = aimXxxService.getBaseMapper()
            .selectPageByKeyword(query.getKeyword(), offset, query.getPageSize());
    
    Page<XxxDO> page = new Page<>(query.getPageNum(), query.getPageSize(), total);
    page.setRecords(records);
    return page;
}
```

### 5. 逻辑删除模式

```java
// 删除时更新 is_deleted 字段
entity.setIsDeleted(1);
entity.setUpdateTime(LocalDateTime.now());
aimXxxService.updateById(entity);

// 查询时过滤已删除
wrapper.eq(XxxDO::getIsDeleted, 0);
```

---

## 数据库表结构复用

### 表结构参考

参见 [schemas/mall-agent/aim_agent_job_type.md](../../schemas/mall-agent/aim_agent_job_type)

### 适配点

| 原始 | 适配为 | 说明 |
|------|--------|------|
| aim_agent_job_type | aim_xxx | 修改表名前缀 |
| code | xxx_code | 修改编码字段名 |
| name | xxx_name | 修改名称字段名 |
| JobTypeStatusEnum | XxxStatusEnum | 修改枚举名 |

### 通用字段模板

所有基础数据表建议包含以下字段：

```sql
CREATE TABLE aim_xxx (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    -- 业务字段
    code VARCHAR(32) NOT NULL COMMENT '编码',
    name VARCHAR(64) NOT NULL COMMENT '名称',
    description VARCHAR(255) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    -- 通用字段
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    creator_id BIGINT COMMENT '创建人ID',
    updater_id BIGINT COMMENT '更新人ID',
    -- 索引
    UNIQUE INDEX uk_code (code),
    INDEX idx_status (status),
    INDEX idx_sort_order (sort_order)
) COMMENT 'XXX表' DEFAULT CHARSET = utf8mb4;
```

---

## 代码片段

### Controller 模板

参见 `snippets/ControllerTemplate.java`

### Service 模板

参见 `snippets/ServiceTemplate.java`

### Mapper XML 模板

参见 `snippets/MapperXmlTemplate.xml`

---

## 快速开始

### 1. 复制文件结构

```bash
# 复制整个功能目录作为模板
cp -r F-001-job-type-management F-xxx-your-feature
```

### 2. 替换命名

| 原命名 | 新命名 |
|--------|--------|
| JobType | YourFeature |
| jobType | yourFeature |
| job-type | your-feature |
| aim_agent_job_type | aim_your_feature |

### 3. 修改字段

根据业务需求修改实体类字段：
- 保留通用字段（id, create_time, update_time, is_deleted, creator_id, updater_id）
- 修改业务字段（code, name, description, status, sort_order）
- 添加特有业务字段

### 4. 更新校验规则

根据业务需求调整：
- 编码格式正则表达式
- 字段长度限制
- 必填字段校验

---

## 注意事项

1. **编码唯一性**: 如果不需要编码，可以移除 code 字段和唯一索引
2. **状态枚举**: 根据业务需要扩展状态值（如添加"待审核"状态）
3. **排序字段**: 如果不需要自定义排序，可以移除 sort_order 字段
4. **关联校验**: 删除校验需要根据实际关联关系调整

---

## 相关功能

| 功能 | 说明 | 关联方式 |
|------|------|----------|
| 智能员工管理 | 员工关联岗位类型 | 外键关联 |
