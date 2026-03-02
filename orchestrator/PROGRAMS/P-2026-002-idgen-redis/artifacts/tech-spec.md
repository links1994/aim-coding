# 技术规格书：基于 Redis 的分布式 ID 生成服务

## 1. 概述

### 1.1 目标
创建 mall-basic 基础服务中的分布式 ID 生成功能，基于 Redis 实现递增 ID 生成能力。

### 1.2 核心功能
- 支持多业务前缀隔离
- 支持多种日期格式组合
- 支持按时间周期重置序号
- 提供 Feign 客户端供其他服务调用
- 采用号段模式（MySQL + Redis）保证高性能和数据安全

### 1.3 技术方案：号段模式

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   业务请求   │────▶│    Redis    │◄────│   MySQL     │
│             │     │  (当前号段)  │     │  (号段分配)  │
└─────────────┘     └─────────────┘     └─────────────┘
                           │
                           ▼
                    返回当前序号
```

**工作流程：**
1. **初始化**：首次调用时自动从 MySQL 申请号段（如 1-1000），存入 Redis
2. **日常请求**：直接操作 Redis 自增，内存操作性能极高
3. **号段用尽**：从 MySQL 申请新号段，更新 Redis
4. **数据安全**：MySQL 持久化号段，Redis 闪断可从 MySQL 恢复

---

## 2. 接口设计

### 2.1 内部接口（供 Feign 调用）

**路径**: `/inner/api/v1/idgen/generate`

**方法**: POST

**请求参数** (`IdGenApiRequest`):

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| prefix | String | 是 | 业务前缀，1-3位大写字母，如 OR、EMP |
| datePattern | String | 是 | 日期格式，可选：yyyyMMdd/yyyyMMddHHmmss/yyyyMM/yyyy |

**响应参数** (`IdGenApiResponse`):

| 字段 | 类型 | 说明 |
|------|------|------|
| code | String | 生成的完整编码 |

### 2.2 编码格式

```
{prefix}{date}{sequence}

示例:
- OR20260302000001  (前缀OR + 年月日 + 6位序号)
- OR20260302153045000001 (前缀OR + 年月日时分秒 + 6位序号)
- EM202603000001    (前缀EM + 年月 + 6位序号)
- AG2026000001      (前缀AG + 年 + 6位序号)
```

---

## 3. 核心类设计

### 3.1 API 模块 (mall-basic-api)

```
repos/mall-inner-api/mall-basic-api/
├── pom.xml
└── src/main/java/com/aim/mall/basic/api/
    ├── dto/
    │   ├── request/
    │   │   └── IdGenApiRequest.java
    │   └── response/
    │       └── IdGenApiResponse.java
    └── feign/
        └── IdGenRemoteService.java
```

**IdGenApiRequest.java**:
- prefix: String, 长度 1-3，正则 `[A-Z]{1,3}`
- datePattern: String, 枚举值

**IdGenApiResponse.java**:
- code: String

**IdGenRemoteService.java**:
- FeignClient(name = "mall-basic", contextId = "idGenRemoteService")
- POST /inner/api/v1/idgen/generate

### 3.2 服务模块 (mall-basic)

```
repos/mall-basic/
├── pom.xml
└── src/main/java/com/aim/mall/basic/
    ├── MallBasicApplication.java
    ├── config/
    │   └── RedisConfig.java
    └── idgen/
        ├── controller/inner/
        │   └── IdGenInnerController.java
        ├── service/
        │   ├── IdGenService.java
        │   └── impl/
        │       └── IdGenServiceImpl.java
        ├── domain/
        │   ├── dto/
        │   │   └── IdGenRequestDTO.java
        │   └── enums/
        │       └── DatePatternEnum.java
        └── constant/
            └── IdGenConstant.java
```

**DatePatternEnum**:
- YYYY_MM_DD("yyyyMMdd")
- YYYY_MM_DD_HH_MM_SS("yyyyMMddHHmmss")
- YYYY_MM("yyyyMM")
- YYYY("yyyy")

**IdGenService**:
```java
String generateCode(String prefix, DatePatternEnum datePattern);
```

**IdGenServiceImpl**:
- 采用号段模式：MySQL 分配号段，Redis 缓存自增
- 号段步长：默认 1000
- 序号达到上限（999999）时报错，不重置
- 首次调用自动初始化 MySQL 记录

---

## 4. Redis Key 设计

### 4.1 Redis Key 格式

```
idgen:segment:{prefix}:{dateValue}

示例:
- idgen:segment:OR:20260302        (按日)
- idgen:segment:OR:20260302153045  (按秒)
- idgen:segment:EM:202603          (按月)
```

### 4.2 号段数据结构

**Redis Hash 结构：**
```
Key: idgen:segment:{prefix}:{dateValue}
Field          Value
-------        -----
current        当前序号
max            号段最大值
step           步长
```

### 4.3 自增逻辑

```java
// 1. 检查 Redis 号段是否存在
String redisKey = String.format("idgen:segment:%s:%s", prefix, dateValue);

// 2. 号段不存在或已用尽，从 MySQL 申请新号段
if (segmentExhausted(redisKey)) {
    IdGenSegment segment = allocateSegmentFromMySQL(prefix, datePattern);
    loadSegmentToRedis(redisKey, segment);
}

// 3. Redis 自增
Long sequence = redisTemplate.opsForHash().increment(redisKey, "current", 1);

// 4. 检查是否超过上限
if (sequence > MAX_SEQUENCE) {
    throw new BusinessException("40002002", "当日序号已用完");
}

// 5. 格式化序号
String seqStr = String.format("%06d", sequence);
```

---

## 5. 错误码设计

根据项目错误码规范 `SSMMTNNN`：

| 错误码 | 说明 |
|--------|------|
| 40001001 | 业务前缀不能为空 |
| 40001002 | 业务前缀格式错误，必须为1-3位大写字母 |
| 40001003 | 日期格式不能为空 |
| 40001004 | 日期格式不支持 |
| 40002001 | Redis 操作失败 |
| 40002002 | 当日序号已用完 |
| 40002003 | MySQL 号段分配失败 |

---

## 6. 模块依赖

### 6.1 mall-basic-api 依赖

```xml
<dependencies>
    <dependency>
        <groupId>com.aim.mall</groupId>
        <artifactId>mall-common</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-openfeign-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```

### 6.2 mall-basic 依赖

```xml
<dependencies>
    <dependency>
        <groupId>com.aim.mall</groupId>
        <artifactId>mall-basic-api</artifactId>
    </dependency>
    <dependency>
        <groupId>com.aim.mall</groupId>
        <artifactId>mall-common</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-bootstrap</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```

---

## 7. 配置文件

### 7.1 bootstrap.yml

```yaml
spring:
  application:
    name: mall-basic
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_SERVER:localhost:8848}
      config:
        server-addr: ${NACOS_SERVER:localhost:8848}
        file-extension: yaml

server:
  port: 8085
```

---

## 8. 数据库设计

### 8.1 表结构

```sql
CREATE TABLE aim_idgen_rule
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    prefix          VARCHAR(10)  NOT NULL COMMENT '业务前缀',
    date_pattern    VARCHAR(20)  NOT NULL COMMENT '日期格式',
    current_max_seq BIGINT       DEFAULT 0 COMMENT '当前最大序号',
    step_size       INT          DEFAULT 1000 COMMENT '号段步长',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_prefix_pattern (prefix, date_pattern)
) COMMENT 'ID生成规则表'
    DEFAULT CHARSET = utf8mb4;
```

### 8.2 数据初始化策略

**自动初始化**：
- 首次调用时自动检测 MySQL 记录是否存在
- 不存在则自动插入默认配置（step_size=1000, current_max_seq=0）
- 业务方无需手动配置

## 9. 日期格式使用建议

| 日期格式 | 单日最大 ID 数 | 适用场景 |
|----------|---------------|----------|
| `yyyyMMdd` (按日) | 999,999 | 普通业务，推荐默认使用 |
| `yyyyMMddHHmmss` (按秒) | 999,999/秒 | 高并发业务，几乎不可能用完 |
| `yyyyMM` (按月) | 999,999 | 低频次业务，谨慎使用 |
| `yyyy` (按年) | 999,999 | 极低频次业务，不建议使用 |

**建议：**
- 默认使用 `yyyyMMdd` 格式
- 预估单日 ID 需求超过 50 万的业务，强制使用 `yyyyMMddHHmmss` 格式

## 10. 验收标准

- [ ] mall-basic-api API 模块创建完成
- [ ] mall-basic 服务模块创建完成
- [ ] MySQL 表结构创建完成
- [ ] 支持通过前缀、日期格式生成编码
- [ ] 支持号段模式（MySQL + Redis）
- [ ] 首次调用自动初始化数据
- [ ] 序号达到上限时报错（不重置）
- [ ] 不同业务前缀相互隔离
- [ ] 提供 Feign 客户端供其他服务调用
- [ ] HTTP 测试文件验证通过
