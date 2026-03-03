---
name: entity-do-template
description: DO 实体类代码模板
---

# DO 实体类模板

## 文件路径

`{service}/src/main/java/com/aim/mall/{module}/{domain}/domain/entity/Aim{Entity}DO.java`

## 模板代码

```java
package com.aim.mall.{module}.{domain}.domain.entity;

import com.aim.mall.common.domain.BaseDO;
import com.aim.mall.common.enums.DeleteStatusEnum;
import com.aim.mall.common.enums.StatusEnum;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * {业务描述}实体类
 *
 * @author AI Agent
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("aim_{module}_{table_name}")
public class Aim{Entity}DO extends BaseDO {

    private static final long serialVersionUID = -1L;

    /**
     * {字段1描述}
     */
    private {field1Type} {field1};

    /**
     * {字段2描述}
     */
    private {field2Type} {field2};

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status = StatusEnum.ENABLE.getCode();

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-否，1-是
     */
    private Integer isDeleted = DeleteStatusEnum.UNDELETE.getCode();

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 更新人ID
     */
    private Long updaterId;
}
```

## 占位符说明

| 占位符 | 说明 | 示例 |
|--------|------|------|
| `{module}` | 模块名 | `agent` |
| `{domain}` | 业务域 | `employee` |
| `{Entity}` | 实体名（大驼峰） | `JobType` |
| `{table_name}` | 表名后缀 | `job_type` |
| `{field1}` | 字段1名 | `code` |
| `{field1Type}` | 字段1类型 | `String` |
| `{field2}` | 字段2名 | `name` |
| `{field2Type}` | 字段2类型 | `String` |
