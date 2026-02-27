---
name: http-test-generation
description: HTTP 测试生成 Skill，为 Controller 生成 HTTP 测试文件
tools: Read, Write, Grep, Glob
---

# HTTP 测试生成 Skill

为 Controller 生成 HTTP 测试文件（.http 格式）。

---

## 触发条件

- 代码生成完成
- 用户明确指令："生成 HTTP 测试" 或 "委托: 生成 HTTP 测试"
- 主 Agent 在 Phase 5 调用

---

## 输入

- 生成的 Controller 代码
- API 设计文档（OpenAPI 格式）
- 项目配置（RESOURCE-MAP.yml）

---

## 输出

- HTTP 测试文件：`{Controller目录}/http/{controller小写}-api.http`
- 测试报告：`orchestrator/PROGRAMS/{program_id}/workspace/http-test-report.md`

---

## 文件组织

```
{服务}/src/main/java/com/aim/mall/{模块}/controller/
├── AgentController.java
└── http/
    └── agent-api.http          # HTTP 测试文件
```

---

## 生成规范

### 环境配置

```http
### 环境变量配置
@baseUrl = http://localhost:8080
@contentType = application/json
```

### 正常场景测试

```http
### 创建智能员工 - 正常场景
# @name create_agent_success
POST {{baseUrl}}/api/v1/agent/create
Content-Type: {{contentType}}

{
  "name": "测试智能员工",
  "description": "这是一个测试用的智能员工",
  "creatorId": 1001
}

### 验证创建成功
# 检查响应状态码为200，业务状态码为0，返回ID大于0
```

### 异常场景测试

```http
### 创建智能员工 - 参数校验异常
# @name create_agent_validation_error
POST {{baseUrl}}/api/v1/agent/create
Content-Type: {{contentType}}

{
  "name": "",                    # 必填字段为空
  "description": "缺少必填的name字段",
  "creatorId": 1001
}

### 验证参数校验失败
# 检查响应状态码为400，业务状态码为PARAM_ERROR
```

### 特殊头部处理

```http
### 需要用户信息的请求
POST {{baseUrl}}/api/v1/agent/create
Content-Type: {{contentType}}
user: {"id": 1, "device_id": "device_001", "user_name": "test_user"}

{
  "name": "测试智能员工",
  "description": "带用户信息的请求",
  "creatorId": 1001
}
```

---

## 检查清单

### 必需测试场景

- [ ] 每个 Controller 都有对应的 HTTP 测试文件
- [ ] 至少包含一个正常场景测试
- [ ] 至少包含一个异常场景测试（参数校验）
- [ ] 文件路径符合规范：`./http/{controller小写}-api.http`
- [ ] 包含环境变量配置（@baseUrl, @contentType）
- [ ] 使用正确的 @name 标记测试用例

### 格式规范

- [ ] HTTP 文件语法正确
- [ ] 请求方法和路径正确
- [ ] Content-Type 设置正确
- [ ] JSON 格式正确
- [ ] 变量引用格式正确（{{variable}}）

---

## 返回格式

```
状态：已完成
报告：orchestrator/PROGRAMS/{program_id}/workspace/http-test-report.md
产出：N 个 HTTP 测试文件（列出文件路径）
决策点：无
```
