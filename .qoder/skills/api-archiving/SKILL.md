---
name: api-archiving
description: 归档 API 定义到 repowiki。在 API 设计完成、需要服务接口文档或其他服务需要引用 API 契约时使用。
---

# API 归档 Skill

将 API 定义归档到 `.qoder/repowiki/apis/`，用于服务接口文档和引用参考。

---

## 触发条件

- API 设计完成
- 需要记录服务接口
- 其他服务需要引用 API 契约
- 用户指令："归档 API"

---

## 输入

- OpenAPI 定义或接口代码
- 服务信息
- API 文档

---

## 输出

- API 归档 → `.qoder/repowiki/apis/{type}/{service-name}.md`

---

## 归档结构

```
.qoder/repowiki/apis/
├── index.md
├── internal/               # 内部服务 API
│   ├── user-service-api.md
│   └── agent-service-api.md
└── third-party/            # 第三方 API
    ├── wechat-pay-api.md
    └── alipay-api.md
```

---

## API 文档格式

```markdown
---
service: mall-user
api_type: internal
description: 用户服务内部 API，用于 Feign 调用
version: 1.0.0
created_at: 2026-02-28
---

# 用户服务 API

## 概述

基础 URL: `http://mall-user/inner/api/v1`

## 接口列表

### 获取用户详情

```
GET /user/detail?userId={userId}
```

**参数**:
| 名称 | 类型 | 必填 | 说明 |
|------|------|----------|-------------|
| userId | Long | 是 | 用户 ID |

**响应**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "john",
    "level": "A"
  }
}
```

### 获取用户等级

```
GET /user/level?userId={userId}
```

...

## Feign 客户端

```java
@FeignClient(name = "mall-user")
public interface UserRemoteService {
    @GetMapping("/inner/api/v1/user/detail")
    CommonResult<UserApiResponse> getUserDetail(@RequestParam("userId") Long userId);
}
```

## 错误码

| 错误码 | 说明 |
|------|-------------|
| 2001001 | 用户不存在 |
| 2001002 | 用户 ID 无效 |
```

---

## 工作流程

### 步骤 1：提取 API 信息

从 OpenAPI 或代码中提取：
- 接口端点
- 参数
- 响应
- 错误码

### 步骤 2：确定类型

- internal — 服务内部 API（Feign）
- third-party — 第三方 API

### 步骤 3：创建归档

按照上述格式生成 API 文档。

### 步骤 4：更新索引

更新 `.qoder/repowiki/apis/index.md`。

---

## 返回格式

```
状态：已完成
归档：.qoder/repowiki/apis/{type}/{service}.md
类型：{internal|third-party}
接口数：X
```
