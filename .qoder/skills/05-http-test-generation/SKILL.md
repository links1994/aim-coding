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

- `artifacts/openapi.yaml` — API 定义
- `artifacts/tech-spec.md` — 技术规格书（包含数据库表结构定义）
- RESOURCE-MAP.yml 中的服务基础 URL

---

## 输出

- HTTP 测试文件 → `orchestrator/PROGRAMS/{program_id}/workspace/http-tests/`
  - `admin-apis.http` — 管理服务测试
  - `app-apis.http` — 应用服务测试
  - `inner-apis.http` — 内部服务测试
- SQL 测试数据 → `orchestrator/PROGRAMS/{program_id}/workspace/http-tests/sql/`
  - `init-data.sql` — 初始化测试数据
  - `cleanup-data.sql` — 清理测试数据
  - `fixtures/{module}/` — 按模块组织的测试数据

---

## HTTP 文件格式

```http
### 创建岗位类型
# @sql: fixtures/job-type/init.sql
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

### SQL 引用标记

在 HTTP 请求前添加 SQL 注释标记，指示该请求依赖的测试数据文件：

- `# @sql: fixtures/{module}/init.sql` — 初始化数据
- `# @sql-cleanup: fixtures/{module}/cleanup.sql` — 清理数据

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
├── inner-apis.http           # mall-agent 内部测试
└── sql/                      # SQL 测试数据
    ├── init-data.sql         # 初始化数据
    ├── cleanup-data.sql      # 清理数据
    └── fixtures/             # 模块级测试数据
        └── {module}/
            ├── init.sql
            └── cleanup.sql
```

### 步骤 5：生成 SQL 测试数据

1. 分析 API 依赖的数据库表结构（读取 tech-spec.md 中的表定义）
2. 按模块生成 INSERT 语句创建测试数据
3. 生成 DELETE 语句清理测试数据
4. 在 HTTP 请求中添加 SQL 引用标记

#### SQL 文件格式示例

**sql/fixtures/job-type/init.sql:**
```sql
-- 岗位类型测试数据
INSERT INTO aim_agent_job_type (id, code, name, description, status, create_time, update_time) 
VALUES (1, 'SALES', '销售', '产品销售岗位', 1, NOW(), NOW());

INSERT INTO aim_agent_job_type (id, code, name, description, status, create_time, update_time) 
VALUES (2, 'TECH', '技术', '技术研发岗位', 1, NOW(), NOW());
```

**sql/fixtures/job-type/cleanup.sql:**
```sql
-- 清理岗位类型测试数据
DELETE FROM aim_agent_job_type WHERE id IN (1, 2);
```

---

## 返回格式

```
状态：已完成
测试文件：
  - workspace/http-tests/admin-apis.http (X 个请求)
  - workspace/http-tests/app-apis.http (Y 个请求)
  - workspace/http-tests/inner-apis.http (Z 个请求)
SQL 数据文件：
  - workspace/http-tests/sql/init-data.sql
  - workspace/http-tests/sql/cleanup-data.sql
  - workspace/http-tests/sql/fixtures/{module}/init.sql (N 个模块)
  - workspace/http-tests/sql/fixtures/{module}/cleanup.sql (N 个模块)
```
