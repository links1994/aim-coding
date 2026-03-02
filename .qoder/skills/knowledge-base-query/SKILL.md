---
name: knowledge-base-query
description: 查询本地知识库（repowiki）中的功能归档、技术规格书、API 文档和框架文档。在开始新功能开发、需要查找类似实现、了解现有设计或查询技术规格时使用。支持功能检索、规格查询、API 文档和陷阱规避。
---

# 知识库查询 Skill

统一查询本地知识库（repowiki）中的所有内容，支持功能归档检索、技术规格书查询、API 文档查找、框架文档搜索等。

---

## 触发条件

- 开始新功能开发前，需要查找类似功能或参考现有实现
- 需要了解某个功能的实现细节或技术决策
- 需要查询技术规格书、编码规范、错误码规范等
- 需要查阅框架文档、API 文档、第三方文档
- 网络不可用，需要查询离线技术文档
- 用户明确指令："查询知识库"、"搜索文档"、"查找功能"或"委托：知识库查询"
- 对技术细节不确定时，优先使用此 Skill 搜索

---

## 输入

- 查询关键词：`{keyword}` 或 `{feature-name}` 或 `{doc-topic}`
- 查询类型（可选）：`feature` | `spec` | `api` | `schema` | `pitfall` | `all`
- 目标路径（可选）：`.qoder/repowiki/` 下的子目录

---

## 输出

- 查询结果报告：`orchestrator/PROGRAMS/{current_program}/workspace/kb-query-result.md`
- 匹配的功能列表、文档片段、技术规格书等
- 复用建议和相关参考链接

---

## 知识库结构

```
.qoder/repowiki/
├── features/              # 功能归档
│   ├── index.md          # 功能索引
│   ├── F-001-xxx/        # 具体功能归档
│   └── F-002-xxx/
├── pitfalls/             # 历史陷阱归档
│   ├── index.md          # 陷阱索引
│   ├── feign/            # Feign 相关陷阱
│   │   ├── feign-client-duplication.md
│   │   └── facade-feign-prohibition.md
│   ├── naming/           # 命名规范陷阱
│   │   └── module-based-naming.md
│   ├── architecture/     # 架构规范陷阱
│   │   └── service-layer-violation.md
│   └── maven/            # Maven 依赖陷阱
│       └── dependency-conflict.md
├── schemas/              # 数据库表结构归档
│   ├── index.md          # 表索引
│   ├── mall-user/        # 按服务组织
│   │   ├── tb_user.md
│   │   └── _service-overview.md
│   └── mall-order/
│       ├── tb_order.md
│       └── _service-overview.md
├── apis/                 # API 文档
│   ├── index.md          # API 索引
│   ├── internal/         # 内部服务 API（Feign 接口）
│   │   ├── user-service-api.md
│   │   └── order-service-api.md
│   └── third-party/      # 第三方 API
│       ├── wechat-pay-api.md
│       └── alipay-api.md
├── zh/docs/              # 中文文档
│   ├── specs/            # 技术规格书
│   │   ├── error-code-spec.md
│   │   ├── architecture-spec.md
│   │   └── coding-spec.md
│   └── guides/           # 操作指南
│       ├── deployment/
│       └── operations/
└── en/docs/              # 英文文档（相同结构）
```

---

## 查询类型

### 类型 1：功能检索（feature）

查询已归档的功能归档，支持复用现有功能设计。

**适用场景**：
- 新功能开发前查找类似实现
- 了解某个功能的接口设计和业务逻辑
- 参考现有功能的代码结构

**输入示例**：
```
关键词："创建员工"
类型：feature
```

**输出示例**：
```
匹配结果：
- F-001-create-agent：智能员工创建（相关度：95%）
- F-003-create-order：订单创建（相关度：80%）
```

### 类型 2：技术规格书查询（spec）

查询技术规格书文档，包括错误码规范、架构规范、编码规范等。

**适用场景**：
- 生成技术规格书时参考规范
- 确定错误码格式
- 了解架构设计原则

**输入示例**：
```
关键词："错误码"
类型：spec
```

**输出示例**：
```
匹配结果：
- error-code-spec.md：SSMMTNNN 格式定义
- architecture-spec.md：四层架构设计
```

### 类型 3：API 文档查询（api）

查询内部服务 API 或第三方 API 文档。

**适用场景**：
- 需要调用其他服务的接口
- 了解 Feign 接口定义
- 查阅第三方 SDK 使用

**输入示例**：
```
关键词："用户服务"
类型：api
```

**输出示例**：
```
匹配结果：
- user-service-api.md：用户服务接口定义
- agent-service-api.md：智能员工服务接口
```

### 类型 4：历史陷阱查询（pitfall）

查询历史陷阱归档，避免重复犯错。

**适用场景**：
- 代码质量分析时识别已知陷阱
- 开发前了解常见错误模式
- 代码审查时检查潜在问题

**目标目录**：`.qoder/repowiki/pitfalls/`

**输入示例**：
```
关键词："Feign 客户端重复创建"
类型：pitfall
目标路径：.qoder/repowiki/pitfalls/feign/
```

**输出示例**：
```
匹配结果：
- feign-client-duplication.md：Feign 客户端重复创建陷阱
- facade-feign-prohibition.md：门面服务禁止创建 feign 目录
```

### 类型 5：综合查询（all）

跨所有类型查询，返回最相关的结果。

**适用场景**：
- 不确定内容属于哪个类别
- 需要全面了解某个主题
- 初始调研阶段

### 类型 6：数据库表结构查询（schema）

查询数据库表结构归档，支持遗留项目改造时了解现有表设计。

**适用场景**：
- 遗留项目改造需要了解现有表结构
- 新功能开发需要在旧表基础上扩展字段
- 避免字段命名冲突
- 了解表关系和索引设计

**目标目录**：`.qoder/repowiki/schemas/`

**输入示例**：
```
关键词："tb_user"
类型：schema
目标路径：.qoder/repowiki/schemas/mall-user/
```

**输出示例**：
```
匹配结果：
- tb_user.md：用户主表结构，包含 15 个字段，3 个索引
- tb_user_role.md：用户角色关联表
- tb_user_address.md：用户地址表

字段信息：
- id：BIGINT，主键，自增
- username：VARCHAR(64)，唯一索引
- phone：VARCHAR(20)，敏感字段，AES 加密
```

---

## 工作流程

### 步骤 1：解析查询意图

分析用户输入，确定查询类型和目标：

```
输入："查询错误码规范"
分析：
  - 关键词：错误码规范
  - 类型：spec（技术规格书）
  - 目标文件：.qoder/repowiki/docs/specs/error-code-spec.md
```

```
输入："有没有订单创建功能？"
分析：
  - 关键词：创建订单
  - 类型：feature（功能检索）
  - 目标索引：.qoder/repowiki/features/index.md
```

### 步骤 2：执行查询

根据查询类型执行相应的搜索：

#### 功能检索流程

```
1. 读取 .qoder/repowiki/features/index.md
2. 匹配功能名称、描述、标签
3. 按相关度排序
4. 读取匹配的功能归档文件
```

#### 文档查询流程

```
1. 确定查询范围（specs/ | apis/ | guides/）
2. 遍历目录中的所有文档
3. 匹配文件名和内容
4. 提取最相关的片段
```

#### 历史陷阱查询流程

```
1. 确定查询范围：.qoder/repowiki/pitfalls/{category}/
2. 遍历陷阱归档文件
3. 匹配陷阱模式和关键词
4. 提取陷阱描述、违规示例、解决方案
5. 返回给代码质量分析 Skill 进行检查
```

**陷阱归档格式**：
```markdown
# 陷阱标题

## 问题描述
简要描述这个陷阱是什么

## 违规模式
用于自动检测的代码特征或模式

## 违规示例
错误代码示例

## 正确方案
正确代码示例

## 相关规范
- 架构规范：xxx
- 编码规范：xxx

## 归档信息
- 发现时间：2026-02-27
- 发现者：Qoder
- 相关 Program：P-2026-001-REQ-018
```

#### 数据库表结构查询流程

```
1. 确定查询范围：.qoder/repowiki/schemas/{service}/
2. 遍历表结构归档文件
3. 匹配表名、字段名、注释
4. 提取表基本信息、字段列表、索引信息
5. 返回给代码生成 Skill 进行遗留表改造
```

**表结构归档格式**：
```markdown
---
table_name: tb_user
description: 用户主表，存储用户基本信息
database: mall_user
service: mall-user
---

# tb_user

## 基本信息
...

## 字段列表
| 字段名 | 数据类型 | 约束 | 说明 |
|------------|-----------|------------|---------|
| id | BIGINT | 主键 | 主键 ID |
| username | VARCHAR(64) | 唯一 | 用户名 |

## 索引信息
...

## 外键关系
...
```

### 步骤 3：生成查询报告

```markdown
# 知识库查询结果

## 查询信息

- 查询关键词：{keywords}
- 查询类型：{type}
- 查询时间：{timestamp}
- 匹配数量：{count}

## 匹配结果

### 结果 1：[标题]

**类型**：feature | spec | api | framework | guide
**来源**：{file-path}
**相关度**：{score}%

**内容摘要**：
{提取的内容}

**复用建议**：
{根据类型提供复用建议}

---

### 结果 2：[标题]

...

## 查询总结

- 找到 {count} 个相关结果
- 建议优先查看：[相关度最高的结果]
- 下一步行动建议：[行动建议]
```

---

## 使用场景

### 场景 1：开发前功能检索

```
用户："我要实现岗位类型管理功能，有没有类似的？"

Agent：
  → 调用 knowledge-base-query Skill
  → 类型：feature
  → 关键词："岗位类型管理"
  → 返回匹配的功能归档
  → 建议参考最相关的功能设计
```

### 场景 2：技术规格书查询

```
用户："错误码应该怎么定义？"

Agent：
  → 调用 knowledge-base-query Skill
  → 类型：spec
  → 关键词："错误码规范"
  → 返回错误码规范文档
  → 提取 SSMMTNNN 格式要求
```

### 场景 3：API 接口查询

```
用户："用户服务有哪些 Feign 接口？"

Agent：
  → 调用 knowledge-base-query Skill
  → 类型：api
  → 关键词："用户服务"
  → 返回 API 文档和接口列表
```

### 场景 4：框架使用查询

```
用户："mybatis-plus 分页查询怎么实现？"

Agent：
  → 调用 knowledge-base-query Skill
  → 类型：framework
  → 关键词："mybatis-plus 分页"
  → 返回框架文档和示例代码
```

### 场景 5：不确定内容综合查询

```
用户："岗位类型的错误码怎么设计？"

Agent：
  → 调用 knowledge-base-query Skill
  → 类型：all
  → 关键词："岗位类型 错误码"
  → 跨所有类型搜索
  → 返回功能归档 + 错误码规范 + 相关 API
```

### 场景 6：遗留项目改造表结构查询

```
用户："需要在用户表加新字段，现有表结构是什么？"

Agent：
  → 调用 knowledge-base-query Skill
  → 类型：schema
  → 关键词："tb_user"
  → 目标路径：.qoder/repowiki/schemas/mall-user/
  → 返回 tb_user 表结构归档
  → 提取字段列表：现有 15 个字段，避免命名冲突
  → 提取索引信息：评估新字段性能影响
  → 提取外键关系：了解表依赖关系
  → 基于归档生成新代码
```

### 场景 7：新功能依赖旧表

```
用户："新功能需要使用订单表，查询订单表结构"

Agent：
  → 调用 knowledge-base-query Skill
  → 类型：schema
  → 关键词："tb_order"
  → 返回表结构归档
  → 了解可用字段和字段含义
  → 确认关联表（如 tb_order_item）
  → 基于现有设计生成新代码
```

---

## 与其他 Skill 的协作

### 与需求澄清协作

```
需求澄清阶段：
  → 查询类似功能的澄清记录
  → 参考现有决策记录
  → 避免重复提问
```

### 与技术规格书生成协作

```
生成技术规格书时：
  → 查询错误码规范
  → 查询架构规范
  → 参考类似功能的接口设计
```

### 与代码生成协作

```
代码生成阶段：
  → 查询功能归档作为模板
  → 查询旧表结构（schema）- 改造项目
  → 查询 API 文档了解调用方式（Feign 接口、第三方 API）
```

### 与数据库归档协作

```
遗留表改造场景：
  → 查询表结构归档（schema）
  → 获取字段列表，避免命名冲突
  → 了解索引情况，评估性能影响
  → 确认外键关系，避免破坏约束
  → 基于归档生成新代码

新功能依赖旧表：
  → 查询表结构了解可用字段
  → 查询关联表了解数据关系
  → 基于现有设计生成代码
```

---

## 返回格式

```
状态：已完成
报告：orchestrator/PROGRAMS/{program_id}/workspace/kb-query-result.md
匹配数量：{count}

高优先级结果：
1. [类型] {title}（相关度：{score}%）
   路径：{path}
   摘要：{brief}

中优先级结果：
...

建议：
- 查看完整内容请指定结果编号
- 进一步筛选请提供更具体的关键词
```

---

## 相关文档

- **功能归档 Skill**：`.qoder/skills/07-feature-archiving/SKILL.md`
- **陷阱归档 Skill**：`.qoder/skills/pitfalls-archiving/SKILL.md`
- **规格书归档 Skill**：`.qoder/skills/spec-archiving/SKILL.md`
- **数据库归档 Skill**：`.qoder/skills/database-schema-archiving/SKILL.md`
- **API 归档 Skill**：`.qoder/skills/api-archiving/SKILL.md`
- **需求澄清 Skill**：`.qoder/skills/02-requirement-clarification/SKILL.md`
- **技术规格书生成 Skill**：`.qoder/skills/03-tech-spec-generation/SKILL.md`
- **代码生成 Skill**：`.qoder/skills/04-java-code-generation/SKILL.md`
