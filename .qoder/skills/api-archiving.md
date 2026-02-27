---
name: api-archiving
description: API文档归档 Skill，将内部服务Feign接口和第三方API归档保存，支持跨服务调用时查询
language: zh-CN
output_language: 中文
tools: Read, Write, Grep, Glob
---

# API文档归档 Skill

将内部服务 Feign 接口和第三方 API 进行结构化归档，生成可查询的 API 文档，支持跨服务调用时快速了解接口定义。

---

## 触发条件

- 新服务开发完成，需要归档 Feign 接口
- 新增第三方 API 对接
- API 接口变更后需要更新文档
- 用户明确指令："归档API" 或 "查询API"
- 代码生成时需要了解依赖服务的接口定义

---

## 输入

- Feign 接口源代码（Java 接口文件）
- 第三方 API 文档（OpenAPI/Swagger JSON、PDF、网页等）
- 接口所属服务/模块信息
- 归档上下文（Program ID、操作人等）

---

## 输出

- API 文档：`.qoder/repowiki/apis/internal/{service}-api.md`（内部服务）
- API 文档：`.qoder/repowiki/apis/third-party/{provider}-api.md`（第三方）
- API 索引更新：`.qoder/repowiki/apis/index.md`

---

## API分类

| 分类 | 目录 | 说明 |
|------|------|------|
| 内部服务API | `apis/internal/` | 微服务间 Feign 调用接口 |
| 第三方API | `apis/third-party/` | 外部服务对接接口（支付、短信等）|

---

## 归档流程

### Step 1: 提取 API 信息

#### 内部服务 API（Feign 接口）

从 Java 源代码提取接口定义：

```yaml
api_extraction:
  service: "mall-user"
  interface_name: "UserRemoteService"
  extraction_method: "SOURCE_CODE"
  source: "repos/mall-inner-api/mall-user-api/.../feign/UserRemoteService.java"
```

**提取内容**：
- 接口基本信息（接口名、服务名、路径前缀）
- 方法清单（方法名、HTTP方法、路径、参数、返回值）
- 请求/响应 DTO 定义
- 错误码说明

#### 第三方 API

从文档提取接口定义：

```yaml
api_extraction:
  provider: "wechat-pay"
  api_type: "REST"
  extraction_method: "OPENAPI_JSON"
  source: "docs/wechat-pay-api-v3.json"
```

### Step 2: 分析接口依赖

分析接口间的调用关系：

```yaml
dependency_analysis:
  consumers:  # 调用本接口的服务
    - mall-admin
    - mall-app
  dependencies:  # 本接口依赖的其他接口
    - mall-auth (token验证)
```

### Step 3: 生成 API 文档

读取模板：`.qoder/repowiki/apis/_TEMPLATE/api-template.md`

根据提取的信息填充模板：

**内部服务 API 路径**：`.qoder/repowiki/apis/internal/{service}-api.md`

**第三方 API 路径**：`.qoder/repowiki/apis/third-party/{provider}-api.md`

### Step 4: 更新 API 索引

在 `.qoder/repowiki/apis/index.md` 中添加条目：

```markdown
## 内部服务API

| 服务 | 接口文档 | 接口数量 | 状态 |
|------|----------|----------|------|
| mall-user | [user-service-api](./internal/user-service-api.md) | 15 | 活跃 |
| mall-order | [order-service-api](./internal/order-service-api.md) | 20 | 活跃 |

## 第三方API

| 提供商 | 接口文档 | 接口数量 | 状态 |
|--------|----------|----------|------|
| 微信支付 | [wechat-pay-api](./third-party/wechat-pay-api.md) | 8 | 活跃 |
| 支付宝 | [alipay-api](./third-party/alipay-api.md) | 6 | 活跃 |
```

---

## 目录结构

```
.qoder/repowiki/apis/
├── index.md                      # API索引
├── _TEMPLATE/
│   └── api-template.md           # API文档模板
├── internal/                     # 内部服务API
│   ├── user-service-api.md
│   ├── order-service-api.md
│   └── agent-service-api.md
└── third-party/                  # 第三方API
    ├── wechat-pay-api.md
    └── alipay-api.md
```

---

## 与代码生成的协作

### 跨服务调用场景

```
用户: "需要在 mall-admin 中调用用户服务"

Agent:
  → 调用 api-archiving Skill 查询 UserRemoteService
  → 读取 .qoder/repowiki/apis/internal/user-service-api.md
  → 获取接口方法列表、参数定义、返回值
  → 调用 java-code-generation Skill 生成调用代码
```

### 第三方对接场景

```
用户: "需要接入微信支付"

Agent:
  → 查询 wechat-pay-api.md 文档
  → 了解统一下单、查询订单、退款等接口
  → 获取请求参数、签名算法、回调处理
  → 生成对接代码
```

---

## 使用示例

### 示例 1: 归档内部服务 Feign 接口

```
用户: "归档 mall-user 服务的 Feign 接口"

Agent:
  → 扫描 mall-user-api 模块的 feign/ 目录
  → 提取 UserRemoteService 接口定义
  → 解析方法、参数、返回值
  → 生成文档: .qoder/repowiki/apis/internal/user-service-api.md
  → 更新索引: .qoder/repowiki/apis/index.md
  → 返回: 已归档 15 个接口
```

### 示例 2: 归档第三方 API

```
用户: "归档微信支付 API 文档"

Agent:
  → 读取微信支付 OpenAPI 定义
  → 提取接口列表、参数、返回值
  → 生成文档: .qoder/repowiki/apis/third-party/wechat-pay-api.md
  → 更新索引
```

### 示例 3: 基于 API 文档生成代码

```
用户: "基于用户服务 API 生成调用代码"

Agent:
  → 读取 user-service-api.md
  → 提取 Feign 接口定义
  → 在 mall-admin 中生成 Service 层调用代码
```

---

## 返回格式

```
状态：已归档
服务：{service-name}
文档路径：.qoder/repowiki/apis/{internal|third-party}/{name}-api.md
索引更新：.qoder/repowiki/apis/index.md

归档信息：
- 接口数量: {count}
- 方法数量: {count}
- 依赖服务: {service1}, {service2}
- 后续代码生成可引用此文档
```

---

## 相关 Skill

- **知识库查询 Skill**: `.qoder/skills/knowledge-base-query.md`（用于检索 API 文档）
- **代码生成 Skill**: `.qoder/skills/java-code-generation.md`（基于 API 文档生成调用代码）
- **技术规格生成 Skill**: `.qoder/skills/tech-spec-generation.md`（设计时参考 API 文档）
