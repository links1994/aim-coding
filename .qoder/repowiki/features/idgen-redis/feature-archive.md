# 功能归档：基于 Redis 的分布式 ID 生成服务

## 功能概述

基于 Redis 实现分布式递增 ID 生成服务，支持多业务前缀隔离、多种日期格式组合和按时间周期重置序号。

---

## 核心功能

1. **分布式 ID 生成**: 基于号段模式（MySQL + Redis）实现高性能 ID 生成
2. **多业务前缀隔离**: 不同业务前缀使用独立的序号序列
3. **多种日期格式**: 支持 yyyyMMdd、yyyyMMddHHmmss、yyyyMM、yyyy
4. **时间周期重置**: 按日期自动重置序号
5. **数据自动初始化**: 首次调用自动创建 MySQL 记录
6. **序号上限保护**: 达到 999999 时报错，避免重复编码

---

## 接口清单

### 内部接口（Feign 调用）

| 接口 | 路径 | 方法 | 说明 |
|------|------|------|------|
| 生成编码 | `/inner/api/v1/idgen/generate` | POST | 生成分布式 ID 编码 |

### Feign 客户端

```java
@FeignClient(name = "mall-basic", contextId = "idGenRemoteService")
public interface IdGenRemoteService {
    @PostMapping("/inner/api/v1/idgen/generate")
    CommonResult<IdGenApiResponse> generate(@RequestBody @Valid IdGenApiRequest request);
}
```

---

## 核心类

### API 模块 (mall-basic-api)

| 类 | 路径 | 说明 |
|----|------|------|
| IdGenApiRequest | `api/dto/request/` | ID 生成请求 |
| IdGenApiResponse | `api/dto/response/` | ID 生成响应 |
| IdGenRemoteService | `api/feign/` | Feign 客户端 |

### 服务模块 (mall-basic)

| 类 | 路径 | 说明 |
|----|------|------|
| IdGenInnerController | `idgen/controller/inner/` | 内部接口控制器 |
| IdGenService | `idgen/service/` | ID 生成服务接口 |
| IdGenServiceImpl | `idgen/service/impl/` | ID 生成服务实现（号段模式） |
| DatePatternEnum | `idgen/domain/enums/` | 日期格式枚举 |
| IdGenConstant | `idgen/constant/` | 常量定义 |
| AimIdGenRuleDO | `idgen/domain/entity/` | ID 生成规则实体 |
| AimIdGenRuleMapper | `idgen/mapper/` | ID 生成规则 Mapper |

### 数据库表

| 表名 | 说明 |
|------|------|
| aim_idgen_rule | ID 生成规则表（号段分配）|

---

## 编码格式

```
{prefix}{date}{sequence}

示例:
- OR20260302000001  (前缀OR + 年月日 + 6位序号)
- OR20260302153045000001 (前缀OR + 年月日时分秒 + 6位序号)
- EM202603000001    (前缀EM + 年月 + 6位序号)
- AG2026000001      (前缀AG + 年 + 6位序号)
```

---

## 技术架构：号段模式

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
1. **初始化**：首次调用自动从 MySQL 申请号段（默认 1000 个），存入 Redis
2. **日常请求**：直接操作 Redis 自增，内存操作性能极高
3. **号段用尽**：从 MySQL 申请新号段，更新 Redis
4. **数据安全**：MySQL 持久化号段，Redis 闪断可从 MySQL 恢复

## Redis Key 设计

```
idgen:segment:{prefix}:{datePattern}:{dateValue}

示例:
- idgen:segment:OR:yyyyMMdd:20260302        (按日)
- idgen:segment:OR:yyyyMMddHHmmss:20260302153045  (按秒)
- idgen:segment:EM:yyyyMM:202603            (按月)
```

**Hash 结构：**
- `current`: 当前序号
- `max`: 号段最大值
- `step`: 步长

---

## 使用方式

### 1. 引入依赖

```xml
<dependency>
    <groupId>com.aim.mall</groupId>
    <artifactId>mall-basic-api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 注入 Feign 客户端

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final IdGenRemoteService idGenRemoteService;
}
```

### 3. 调用生成编码

```java
IdGenApiRequest request = new IdGenApiRequest();
request.setPrefix("OR");  // 订单前缀
request.setDatePattern("yyyyMMdd");

CommonResult<IdGenApiResponse> result = idGenRemoteService.generate(request);
String orderCode = result.getData().getCode();  // OR20260302000001
```

---

## 设计决策

### 1. Redis Key 设计
- 格式: `idgen:{prefix}:{dateValue}`
- 使用冒号分隔，便于 Redis 管理和监控
- 日期值嵌入 Key 实现自动周期隔离

### 2. 序号溢出处理
- 超过 999999 时重置为 1
- 记录 WARN 日志提醒
- 注意：极端高并发可能产生重复编码

### 3. 日期格式扩展
- 使用枚举定义支持的格式
- 新增格式只需扩展枚举和添加 formatter

---

## 错误码

| 错误码 | 说明 |
|--------|------|
| 40001004 | 不支持的日期格式 |

---

## 相关文件

- 技术规格: `orchestrator/PROGRAMS/P-2026-002-idgen-redis/artifacts/tech-spec.md`
- 质量报告: `orchestrator/PROGRAMS/P-2026-002-idgen-redis/artifacts/quality-report.md`
- HTTP 测试: `orchestrator/PROGRAMS/P-2026-002-idgen-redis/http-tests/IdGenInnerController.http`
