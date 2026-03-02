# P-2026-002-idgen-redis: 基于 Redis 的分布式 ID 生成服务

## 目标

创建 mall-basic 基础服务中的分布式 ID 生成功能，基于 Redis 实现递增 ID 生成能力，支持多业务前缀隔离、多种日期格式组合和按时间周期重置序号。

## 背景

项目中多个模块需要生成业务编码（如订单号、员工编号等），需要一个统一的基础服务来提供分布式 ID 生成能力，确保：
- 全局唯一性
- 高并发性能
- 支持多种编码格式
- 按时间周期自动重置序号

## 方案

### 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                    mall-basic 服务                           │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐  │
│  │  ID生成服务  │    │  Redis服务  │    │  API模块         │  │
│  │  IdGenService│◄───│  RedisTemplate│───│  mall-basic-api │  │
│  └─────────────┘    └─────────────┘    └─────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  其他服务通过引入 mall-basic-api 调用 ID 生成接口              │
│  - mall-admin                                               │
│  - mall-app                                                 │
│  - mall-agent                                               │
└─────────────────────────────────────────────────────────────┘
```

### 编码格式

```
格式: {前缀}{日期}{自增序号}

示例:
- OR20260302000001  (前缀OR + 年月日 + 6位序号)
- OR202603000001    (前缀OR + 年月 + 6位序号)
- OR260302000001    (前缀OR + 年月日 + 6位序号)

组成部分:
1. 前缀: 1-2位字母，如 OR、EM、AG
2. 日期: 支持多种格式
   - yyyyMMdd    (年月日)
   - yyyyMM      (年月)
   - yyyy        (年)
   - yyyyMMddHHmmss (年月日时分秒)
3. 自增序号: 6位数字，不足补零
```

### 关键设计决策

1. **Redis Key 设计**
   - 格式: `idgen:{prefix}:{datePattern}:{dateValue}`
   - 示例: `idgen:OR:yyyyMMdd:20260302`
   - 使用 Redis INCR 命令实现原子自增

2. **时间周期重置**
   - 通过 Key 中嵌入日期实现自动隔离
   - 新周期自动生成新的 Key，序号从 1 开始

3. **多前缀隔离**
   - 不同业务前缀使用独立的 Redis Key
   - 互不影响，可独立配置

4. **服务模块划分**
   - `mall-basic`: 服务实现，提供 HTTP 接口
   - `mall-basic-api`: API 模块，供其他服务通过 Feign 调用

### 涉及模块

1. **新建模块**
   - `repos/mall-basic/` - 基础配置服务
   - `repos/mall-inner-api/mall-basic-api/` - API 定义模块

2. **需要更新的文件**
   - `repos/mall-inner-api/pom.xml` - 添加 mall-basic-api 子模块
   - `orchestrator/ALWAYS/RESOURCE-MAP.yml` - 添加新服务信息

## 涉及文件

### 新建文件

```
repos/mall-basic/
├── pom.xml
├── src/main/java/com/aim/mall/basic/
│   ├── MallBasicApplication.java
│   ├── config/
│   │   └── RedisConfig.java
│   └── idgen/
│       ├── controller/inner/
│       │   └── IdGenInnerController.java
│       ├── service/
│       │   ├── IdGenService.java
│       │   └── impl/
│       │       └── IdGenServiceImpl.java
│       ├── domain/
│       │   ├── dto/
│       │   │   ├── IdGenRequestDTO.java
│       │   │   └── IdGenResultDTO.java
│       │   └── enums/
│       │       └── DatePatternEnum.java
│       └── constant/
│           └── IdGenConstant.java
└── src/main/resources/
    ├── bootstrap.yml
    ├── bootstrap-dev.yml
    └── bootstrap-prod.yml

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

### 修改文件

- `repos/mall-inner-api/pom.xml` - 添加 mall-basic-api 子模块
- `orchestrator/ALWAYS/RESOURCE-MAP.yml` - 添加 mall-basic 服务配置

## 验收标准

- [ ] mall-basic 服务模块创建完成
- [ ] mall-basic-api API 模块创建完成
- [ ] 支持通过前缀、日期格式、序号位数生成编码
- [ ] 支持按时间周期自动重置序号
- [ ] 不同业务前缀相互隔离
- [ ] 提供 Feign 客户端供其他服务调用
- [ ] HTTP 测试文件验证通过
