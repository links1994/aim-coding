---
name: knowledge-base-query
description: 知识库查询 Skill，统一查询 repowiki 中的所有内容，包括功能档案、技术规范、API文档、框架文档等
language: zh-CN
output_language: 中文
tools: Read, Grep, Glob
---

# 知识库查询 Skill

统一查询本地知识库 (repowiki) 中的所有内容，支持功能档案检索、技术规范查询、API文档查阅、框架文档搜索等。

---

## 触发条件

- 开始新功能开发前，需要查找类似功能或参考现有实现
- 需要了解某个功能的实现细节或技术决策
- 需要查询技术规范、编码规范、错误码规范等
- 需要查阅框架文档、API文档、三方文档
- 网络无法连接，需要查询离线技术文档
- 用户明确指令："查询知识库"、"搜索文档"、"查找功能" 或 "委托: 知识库查询"
- 任何不确定技术细节时，优先使用本 Skill 搜索

---

## 输入

- 查询关键词：`{keyword}` 或 `{feature-name}` 或 `{doc-topic}`
- 查询类型（可选）：`feature` | `spec` | `api` | `framework` | `guide` | `all`
- 目标路径（可选）：`.qoder/repowiki/` 下的子目录

---

## 输出

- 查询结果报告：`orchestrator/PROGRAMS/{current_program}/workspace/kb-query-result.md`
- 匹配的功能列表、文档片段、技术规范等
- 复用建议和相关参考链接

---

## 知识库结构

```
.qoder/repowiki/
├── features/              # 功能档案
│   ├── index.md          # 功能索引
│   ├── F-001-xxx/        # 具体功能档案
│   └── F-002-xxx/
├── pitfalls/             # 历史坑点档案
│   ├── index.md          # 坑点索引
│   ├── feign/            # Feign 相关坑点
│   │   ├── feign-client-duplication.md
│   │   └── facade-feign-prohibition.md
│   ├── naming/           # 命名规范坑点
│   │   └── module-based-naming.md
│   ├── architecture/     # 架构规范坑点
│   │   └── service-layer-violation.md
│   └── maven/            # Maven 依赖坑点
│       └── dependency-conflict.md
├── zh/docs/              # 中文文档
│   ├── specs/            # 技术规范
│   │   ├── 错误码规范.md
│   │   ├── 架构规范.md
│   │   └── 编码规范.md
│   ├── apis/             # API文档
│   │   ├── internal/     # 内部服务API
│   │   └── third-party/  # 第三方API
│   ├── frameworks/       # 框架文档
│   │   ├── spring-boot/
│   │   └── mybatis-plus/
│   └── guides/           # 操作指南
│       ├── deployment/
│       └── operations/
└── en/docs/              # 英文文档（结构同上）
```

---

## 查询类型

### 类型 1: 功能检索 (feature)

查询已归档的功能档案，支持复用已有功能设计。

**适用场景**:
- 新功能开发前查找类似实现
- 了解某个功能的接口设计和业务逻辑
- 参考已有功能的代码结构

**输入示例**:
```
关键词: "创建员工"
类型: feature
```

**输出示例**:
```
匹配结果:
- F-001-create-agent: 智能员工创建 (相关度: 95%)
- F-003-create-order: 订单创建 (相关度: 80%)
```

### 类型 2: 技术规范查询 (spec)

查询技术规范文档，包括错误码规范、架构规范、编码规范等。

**适用场景**:
- 生成技术规格书时参考规范
- 确定错误码格式
- 了解架构设计原则

**输入示例**:
```
关键词: "错误码"
类型: spec
```

**输出示例**:
```
匹配结果:
- 错误码规范.md: SSMMTNNN 格式定义
- 架构规范.md: 四层架构设计
```

### 类型 3: API文档查询 (api)

查询内部服务API或第三方API文档。

**适用场景**:
- 需要调用其他服务的接口
- 了解Feign接口定义
- 查阅第三方SDK用法

**输入示例**:
```
关键词: "用户服务"
类型: api
```

**输出示例**:
```
匹配结果:
- user-service-api.md: 用户服务接口定义
- agent-service-api.md: 智能员工服务接口
```

### 类型 4: 框架文档查询 (framework)

查询框架使用文档，如Spring Boot、MyBatis-Plus等。

**适用场景**:
- 开发时查询框架用法
- 了解框架配置和最佳实践
- 解决框架相关问题

**输入示例**:
```
关键词: "mybatis-plus 分页"
类型: framework
```

**输出示例**:
```
匹配结果:
- mybatis-plus/guide.md: 分页查询章节
```

### 类型 5: 历史坑点查询 (pitfall)

查询历史坑点档案，避免重复踩坑。

**适用场景**:
- 代码质量分析时识别已知陷阱
- 开发前了解常见错误模式
- 代码审查时检查潜在问题

**目标目录**: `.qoder/repowiki/pitfalls/`

**输入示例**:
```
关键词: "Feign 客户端重复创建"
类型: pitfall
目标路径: .qoder/repowiki/pitfalls/feign/
```

**输出示例**:
```
匹配结果:
- feign-client-duplication.md: Feign 客户端重复创建坑点
- facade-feign-prohibition.md: 门面服务禁止创建 feign 目录
```

### 类型 6: 综合查询 (all)

跨所有类型进行查询，返回最相关的结果。

**适用场景**:
- 不确定内容属于哪个分类
- 需要全面了解某个主题
- 初步调研阶段

---

## 工作流程

### Step 1: 解析查询意图

分析用户输入，确定查询类型和目标：

```
输入: "查询错误码规范"
分析:
  - 关键词: 错误码规范
  - 类型: spec (技术规范)
  - 目标文件: .qoder/repowiki/docs/specs/错误码规范.md
```

```
输入: "有没有创建订单的功能"
分析:
  - 关键词: 创建订单
  - 类型: feature (功能检索)
  - 目标索引: .qoder/repowiki/features/index.md
```

### Step 2: 执行查询

根据查询类型执行相应的搜索：

#### 功能检索流程

```
1. 读取 .qoder/repowiki/features/index.md
2. 匹配功能名称、描述、标签
3. 按相关度排序
4. 读取匹配功能的档案文件
```

#### 文档查询流程

```
1. 确定查询范围 (specs/ | apis/ | frameworks/ | guides/)
2. 遍历目录下的所有文档
3. 匹配文件名和内容
4. 提取最相关的片段
```

#### 历史坑点查询流程

```
1. 确定查询范围: .qoder/repowiki/pitfalls/{category}/
2. 遍历坑点档案文件
3. 匹配坑点模式(pattern)和关键词
4. 提取坑点描述、违规示例、解决方案
5. 返回给代码质量分析 Skill 用于检查
```

**坑点档案格式**:
```markdown
# 坑点标题

## 问题描述
简要描述这个坑点是什么

## 违规模式
代码特征或模式，用于自动检测

## 违规示例
错误的代码示例

## 正确方案
正确的代码示例

## 相关规范
- 架构规范: xxx
- 编码规范: xxx

## 归档信息
- 发现时间: 2026-02-27
- 发现人: Qoder
- 相关 Program: P-2026-001-REQ-018
```

### Step 3: 生成查询报告

```markdown
# 知识库查询结果

## 查询信息

- 查询关键词: {keywords}
- 查询类型: {type}
- 查询时间: {timestamp}
- 匹配数量: {count}

## 匹配结果

### 结果 1: [标题]

**类型**: feature | spec | api | framework | guide
**来源**: {file-path}
**相关度**: {score}%

**内容摘要**:
{extracted content}

**复用建议**:
{based on type, provide reuse suggestions}

---

### 结果 2: [标题]

...

## 查询总结

- 共找到 {count} 个相关结果
- 建议优先查看: [最高相关度结果]
- 下一步行动建议: [action suggestions]
```

---

## 使用场景

### 场景 1: 开发前功能检索

```
用户: "我要实现岗位管理功能，有类似的吗？"

Agent:
  → 调用 knowledge-base-query Skill
  → 类型: feature
  → 关键词: "岗位管理"
  → 返回匹配的功能档案
  → 建议参考最相关的功能设计
```

### 场景 2: 技术规范查询

```
用户: "错误码应当如何定义？"

Agent:
  → 调用 knowledge-base-query Skill
  → 类型: spec
  → 关键词: "错误码规范"
  → 返回错误码规范文档
  → 提取 SSMMTNNN 格式要求
```

### 场景 3: API接口查询

```
用户: "用户服务有哪些Feign接口？"

Agent:
  → 调用 knowledge-base-query Skill
  → 类型: api
  → 关键词: "用户服务"
  → 返回API文档和接口列表
```

### 场景 4: 框架用法查询

```
用户: "mybatis-plus分页查询如何实现？"

Agent:
  → 调用 knowledge-base-query Skill
  → 类型: framework
  → 关键词: "mybatis-plus 分页"
  → 返回框架文档和示例代码
```

### 场景 5: 不确定内容时的综合查询

```
用户: "岗位类型错误码如何设计？"

Agent:
  → 调用 knowledge-base-query Skill
  → 类型: all
  → 关键词: "岗位类型 错误码"
  → 跨所有类型搜索
  → 返回功能档案 + 错误码规范 + 相关API
```

---

## 与其他 Skill 的协作

### 与需求澄清协作

```
需求澄清阶段:
  → 查询类似功能的澄清记录
  → 参考已有的决策记录
  → 避免重复提问
```

### 与技术规格生成协作

```
生成技术规格时:
  → 查询错误码规范
  → 查询架构规范
  → 参考类似功能的接口设计
```

### 与代码生成协作

```
代码生成阶段:
  → 查询功能档案作为模板
  → 查询框架文档获取用法
  → 查询API文档了解调用方式
```

---

## 返回格式

```
状态：已完成
报告：orchestrator/PROGRAMS/{program_id}/workspace/kb-query-result.md
匹配数量：{count}

高优先级结果:
1. [类型] {title} (相关度: {score}%)
   路径: {path}
   摘要: {brief}

中优先级结果:
...

建议:
- 如需查看完整内容，请指定结果编号
- 如需进一步筛选，请提供更具体的关键词
```

---

## 相关文档

- **功能归档 Skill**: `.qoder/skills/feature-archiving.md`
- **需求澄清 Skill**: `.qoder/skills/requirement-clarification.md`
- **技术规格生成 Skill**: `.qoder/skills/tech-spec-generation.md`
- **代码生成 Skill**: `.qoder/skills/java-code-generation.md`
