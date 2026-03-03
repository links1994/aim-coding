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
  - 按 Controller 类名命名，如 `JobTypeInnerController.http`、`AgentAdminController.http`
  - 每个文件包含该 Controller 的所有 API 测试
- SQL 测试数据 → `orchestrator/PROGRAMS/{program_id}/workspace/http-tests/sql/`
  - `init-schema.sql` — 表结构定义 + 初始化数据（合并为一个文件）

---

## HTTP 文件格式

在 HTTP 文件顶部定义环境变量，使用 `@` 语法：

```http
# 环境变量定义
@baseUrl = http://localhost:8080
@token = your-token-here

### 创建岗位类型
# @sql: fixtures/job-type/init.sql
POST {{baseUrl}}/inner/api/v1/job-types/create
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "code": "SALES",
  "name": "销售",
  "description": "产品销售岗位"
}

### 查询岗位类型列表
POST {{baseUrl}}/inner/api/v1/job-types/list
Content-Type: application/json

{
  "pageNum": 1,
  "pageSize": 10
}
```

### SQL 引用标记

在 HTTP 请求前添加 SQL 注释标记，指示该请求依赖的测试数据文件：

- `# @sql: sql/init-schema.sql` — 表结构定义和初始化数据

---

## 工作流程

### 步骤 1：读取 API 定义

1. 读取 openapi.yaml
2. 读取 tech-spec.md 获取上下文
3. 读取 RESOURCE-MAP.yml 获取基础 URL

### 步骤 2：按 Controller 分组 API

读取技术规格书中的 Controller 定义，按 Controller 类名分组：
- 每个 Controller 生成一个独立的 `.http` 文件
- 文件名格式：`{ControllerName}.http`
- 示例：`JobTypeInnerController.http`、`AgentAdminController.http`

### 步骤 3：生成 HTTP 请求

为每个端点：
1. 提取方法、路径、参数
2. 生成带示例数据的请求
3. 添加环境变量（baseUrl、token）
4. 添加文档注释

### 步骤 4：创建 HTTP 文件

每个 HTTP 文件顶部定义该 Controller 所需的环境变量：

```
workspace/http-tests/
├── JobTypeInnerController.http       # JobTypeInnerController 的 API 测试
├── AgentAdminController.http         # AgentAdminController 的 API 测试
└── sql/                              # SQL 测试数据
    └── init-schema.sql               # 表结构定义 + 初始化数据
```

### 步骤 5：生成 SQL 测试数据

1. 分析 API 依赖的数据库表结构（读取 tech-spec.md 中的表定义）
2. 生成 `init-schema.sql` 文件，包含：
   - CREATE TABLE 语句（表结构定义）
   - INSERT 语句（初始化数据）
3. 在 HTTP 请求中添加 SQL 引用标记

#### SQL 文件格式示例

**sql/init-schema.sql:**
```sql
-- ============================================
-- 表结构定义
-- ============================================

CREATE TABLE IF NOT EXISTS aim_agent_job_type
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name        VARCHAR(100) NOT NULL COMMENT '岗位类型名称',
    code        VARCHAR(50)  NOT NULL COMMENT '岗位类型编码',
    description VARCHAR(500) COMMENT '岗位类型描述',
    status      TINYINT      DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    sort_order  INT          DEFAULT 0 COMMENT '排序号',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted  TINYINT      DEFAULT 0 COMMENT '逻辑删除标记：0-未删除 1-已删除',
    creator_id  BIGINT COMMENT '创建人ID',
    updater_id  BIGINT COMMENT '更新人ID',
    UNIQUE KEY uk_code (code)
) COMMENT '岗位类型表' DEFAULT CHARSET = utf8mb4;

-- ============================================
-- 初始化数据
-- ============================================

INSERT INTO aim_agent_job_type (id, code, name, description, status, sort_order, create_time, update_time)
VALUES (1, 'SALES', '销售', '产品销售岗位', 1, 1, NOW(), NOW());

INSERT INTO aim_agent_job_type (id, code, name, description, status, sort_order, create_time, update_time)
VALUES (2, 'TECH', '技术', '技术研发岗位', 1, 2, NOW(), NOW());
```

---

## 使用场景

### 场景 1：接口开发完成自测

```
用户："接口开发完了，帮我生成测试文件"

Agent：
  → 读取 openapi.yaml 获取接口定义
  → 按 Controller 分组生成 HTTP 测试
  → 生成 SQL 初始化数据
  → 输出到 http-tests/ 目录
```

### 场景 2：联调前准备

```
用户："需要给前端提供接口测试示例"

Agent：
  → 生成完整的 HTTP 测试文件
  → 包含请求参数示例
  → 包含环境变量配置
  → 方便前端直接导入使用
```

### 场景 3：回归测试

```
用户："修改了接口，需要验证是否影响现有功能"

Agent：
  → 读取现有接口定义
  → 生成覆盖所有端点的测试
  → 包含正向和异常场景
  → 支持快速回归验证
```

---

## 返回格式

```
状态：已完成
测试文件：
  - workspace/http-tests/JobTypeInnerController.http (X 个请求)
  - workspace/http-tests/AgentAdminController.http (Y 个请求)
  - workspace/http-tests/... (按实际 Controller 数量)
SQL 数据文件：
  - workspace/http-tests/sql/init-schema.sql (表结构 + 初始化数据)
```
