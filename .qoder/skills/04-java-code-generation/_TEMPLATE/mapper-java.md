---
name: mapper-java-template
description: Mapper 接口代码模板
---

# Mapper 接口模板

## 文件路径

`{service}/src/main/java/com/aim/mall/{module}/{domain}/mapper/Aim{Entity}Mapper.java`

## 模板代码

```java
package com.aim.mall.{module}.{domain}.mapper;

import com.aim.mall.{module}.{domain}.domain.entity.Aim{Entity}DO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * {业务描述} Mapper接口
 *
 * @author AI Agent
 */
@Mapper
public interface Aim{Entity}Mapper extends BaseMapper<Aim{Entity}DO> {

    /**
     * 根据{唯一字段}查询（排除已删除）
     *
     * @param {field} {字段描述}
     * @return {业务描述}实体
     */
    Aim{Entity}DO selectBy{Field}(@Param("{field}") String {field});

    /**
     * 分页查询{业务描述}列表
     *
     * @param keyword  关键词
     * @param offset   偏移量
     * @param limit    每页大小
     * @return {业务描述}列表
     */
    List<Aim{Entity}DO> selectPageByKeyword(@Param("keyword") String keyword,
                                            @Param("offset") Integer offset,
                                            @Param("limit") Integer limit);

    /**
     * 统计总数（根据关键词）
     *
     * @param keyword 关键词
     * @return 总数
     */
    Long countByKeyword(@Param("keyword") String keyword);
}
```

## 占位符说明

| 占位符 | 说明 | 示例 |
|--------|------|------|
| `{service}` | 服务名 | `mall-agent` |
| `{module}` | 模块名 | `agent` |
| `{domain}` | 业务域 | `employee` |
| `{Entity}` | 实体名（大驼峰） | `JobType` |
| `{field}` | 字段名（小驼峰） | `code` |
| `{Field}` | 字段名（大驼峰） | `Code` |
