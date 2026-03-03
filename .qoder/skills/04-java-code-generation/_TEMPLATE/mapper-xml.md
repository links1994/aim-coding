---
name: mapper-xml-template
description: Mapper XML 代码模板
---

# Mapper XML 模板

## 文件路径

`{service}/src/main/resources/mapper/Aim{Entity}Mapper.xml`

## 模板代码

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.aim.mall.{module}.{domain}.mapper.Aim{Entity}Mapper">

    <!-- 基础字段列表 -->
    <sql id="Base_Column_List">
        id, {field1}, {field2}, status, sort_order,
        create_time, update_time, is_deleted, creator_id, updater_id
    </sql>

    <!-- 根据{唯一字段}查询 -->
    <select id="selectBy{Field}" resultType="com.aim.mall.{module}.{domain}.domain.entity.Aim{Entity}DO">
        SELECT
        <include refid="Base_Column_List"/>
        FROM aim_{module}_{table_name}
        WHERE is_deleted = 0
        AND {field} = #{field}
        LIMIT 1
    </select>

    <!-- 分页查询列表 -->
    <select id="selectPageByKeyword" resultType="com.aim.mall.{module}.{domain}.domain.entity.Aim{Entity}DO">
        SELECT
        <include refid="Base_Column_List"/>
        FROM aim_{module}_{table_name}
        WHERE is_deleted = 0
        <if test="keyword != null and keyword != ''">
            AND ({field1} LIKE CONCAT('%', #{keyword}, '%') OR {field2} LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        ORDER BY sort_order ASC, create_time DESC
        LIMIT #{limit} OFFSET #{offset}
    </select>

    <!-- 统计总数 -->
    <select id="countByKeyword" resultType="java.lang.Long">
        SELECT COUNT(*)
        FROM aim_{module}_{table_name}
        WHERE is_deleted = 0
        <if test="keyword != null and keyword != ''">
            AND ({field1} LIKE CONCAT('%', #{keyword}, '%') OR {field2} LIKE CONCAT('%', #{keyword}, '%'))
        </if>
    </select>

</mapper>
```

## 占位符说明

| 占位符 | 说明 | 示例 |
|--------|------|------|
| `{module}` | 模块名 | `agent` |
| `{domain}` | 业务域 | `employee` |
| `{Entity}` | 实体名（大驼峰） | `JobType` |
| `{table_name}` | 表名后缀 | `job_type` |
| `{field}` | 字段名（小驼峰） | `code` |
| `{Field}` | 字段名（大驼峰） | `Code` |
| `{field1}` | 字段1名 | `code` |
| `{field2}` | 字段2名 | `name` |
