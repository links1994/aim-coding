---
name: http-test-generation
description: 为 API 端点生成 HTTP 测试文件。在技术规格书完成、需要创建 API 测试请求或验证接口实现时使用。
---

# HTTP 测试生成 Skill

基于技术规格书为 API 端点生成 HTTP 测试文件（HTTP Client 格式）。

---

## 触发条件

- 技术规格书完成并包含 OpenAPI 定义
- 用户指令："生成 HTTP 测试" 或 "创建 API 测试"
- 需要验证 API 实现

---

## 输入

- `workspace/openapi.yaml` — API 定义
- `workspace/tech-spec.md` — 技术规格书
- RESOURCE-MAP.yml 中的服务基础 URL

---

## 输出

- HTTP 测试文件 → `orchestrator/PROGRAMS/{program_id}/workspace/http-tests/`
  - `admin-apis.http` — 管理服务测试
  - `app-apis.http` — 应用服务测试
  - `inner-apis.http` — 内部服务测试

---

## HTTP 文件格式

```http
### 创建岗位类型
POST {{baseUrl}}/admin/api/v1/job-types
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "code": "SALES",
  "name": "销售",
  "description": "产品销售岗位"
}

### 查询岗位类型列表
GET {{baseUrl}}/admin/api/v1/job-types?pageNum=1&pageSize=20
Authorization: Bearer {{token}}
```

---

## 工作流程

### 步骤 1：读取 API 定义

1. 读取 openapi.yaml
2. 读取 tech-spec.md 获取上下文
3. 读取 RESOURCE-MAP.yml 获取基础 URL

### 步骤 2：按服务分组 API

按服务对接口端点分组：
- mall-admin API
- mall-app API
- mall-agent 内部 API

### 步骤 3：生成 HTTP 请求

为每个端点：
1. 提取方法、路径、参数
2. 生成带示例数据的请求
3. 添加环境变量（baseUrl、token）
4. 添加文档注释

### 步骤 4：创建 HTTP 文件

```
workspace/http-tests/
├── http-client.env.json      # 环境变量
├── admin-apis.http           # mall-admin 测试
├── app-apis.http             # mall-app 测试
└── inner-apis.http           # mall-agent 内部测试
```

---

## 返回格式

```
状态：已完成
测试文件：
  - workspace/http-tests/admin-apis.http (X 个请求)
  - workspace/http-tests/app-apis.http (Y 个请求)
  - workspace/http-tests/inner-apis.http (Z 个请求)
```
