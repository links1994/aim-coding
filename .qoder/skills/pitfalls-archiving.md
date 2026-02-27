---
name: pitfalls-archiving
description: 历史坑点归档 Skill，将开发过程中遇到的问题和陷阱归档到 repowiki，供后续代码质量分析查询
tools: Read, Write, Grep, Glob
---

# 历史坑点归档 Skill

将开发过程中踩过的坑、遇到的问题、违反规范的案例归档到 repowiki/pitfalls/ 目录，建立可查询的历史坑点知识库。

---

## 触发条件

- 发现并修复了代码规范问题
- 用户明确指令："归档这个坑点" 或 "记录这个问题"
- 代码质量分析发现违规并修复后
- 开发过程中遇到值得记录的问题

---

## 输入

- 坑点标题
- 问题描述
- 违规代码示例
- 正确解决方案
- 相关规范引用
- 发现上下文（Program ID、时间等）

---

## 输出

- 坑点档案文件：`.qoder/repowiki/pitfalls/{category}/{pitfall-name}.md`
- 更新坑点索引：`.qoder/repowiki/pitfalls/index.md`

---

## 坑点分类

| 分类 | 目录 | 说明 |
|------|------|------|
| Feign 相关 | `pitfalls/feign/` | Feign 客户端创建、使用相关问题 |
| 命名规范 | `pitfalls/naming/` | 命名不规范、命名冲突等问题 |
| 架构规范 | `pitfalls/architecture/` | 分层架构、服务职责等问题 |
| Maven 依赖 | `pitfalls/maven/` | 依赖冲突、版本问题等 |
| 数据库 | `pitfalls/database/` | SQL 性能、索引问题等 |
| 代码质量 | `pitfalls/code-quality/` | 重复代码、性能问题等 |

---

## 归档流程

### Step 1: 分析坑点信息

提取坑点的关键信息：

```yaml
pitfall_analysis:
  title: "Feign 客户端重复创建"
  category: "feign"  # 确定分类
  severity: "high"   # high | medium | low
  description: "在门面服务中创建 Feign 客户端而非使用 API 模块的 RemoteService"
  pattern: "@FeignClient in facade service (mall-admin/mall-app/mall-chat)"
  violation_example: |
    @FeignClient(name = "mall-agent", path = "/inner/api/v1/job-types")
    public interface JobTypeFeignClient { ... }
  correct_example: |
    @Service
    @RequiredArgsConstructor
    public class JobTypeAdminService {
        private final AgentRemoteService agentRemoteService;
    }
  related_rules:
    - "Feign Client Naming: Module-Based"
    - "Facade service feign directory prohibition"
  context:
    program: "P-2026-001-REQ-018"
    discovered_by: "Qoder"
    discovered_at: "2026-02-27"
    related_files:
      - "repos/mall-admin/src/main/java/com/aim/mall/admin/feign/JobTypeFeignClient.java"
      - "repos/mall-admin/src/main/java/com/aim/mall/admin/controller/JobTypeAdminController.java"
```

### Step 2: 生成坑点档案

创建 Markdown 格式的坑点档案：

```markdown
# Feign 客户端重复创建

## 基本信息

- **分类**: Feign 相关
- **严重程度**: 高
- **发现时间**: 2026-02-27
- **发现人**: Qoder
- **相关 Program**: P-2026-001-REQ-018

## 问题描述

在门面服务（mall-admin/mall-app/mall-chat）中创建 Feign 客户端，而不是使用 API 模块（mall-agent-api）中已定义的 RemoteService。

这违反了架构规范：
1. 门面服务禁止创建 `feign/` 目录
2. Feign 客户端应当统一在 API 模块中定义
3. 命名应当遵循 `{Module}RemoteService` 格式

## 违规模式

检测特征：
- 文件路径包含 `mall-admin/src/main/java/com/aim/mall/admin/feign/`
- 使用了 `@FeignClient` 注解
- 类名以 `FeignClient` 结尾而非 `RemoteService`

## 违规示例

### 错误代码

```java
// 文件: mall-admin/src/main/java/com/aim/mall/admin/feign/JobTypeFeignClient.java
@FeignClient(name = "mall-agent", path = "/inner/api/v1/job-types")
public interface JobTypeFeignClient {
    @PostMapping("/list")
    CommonResult<PageData<JobTypeApiResponse>> pageList(@RequestBody JobTypePageApiRequest request);
    // ...
}
```

### 错误用法

```java
@Service
@RequiredArgsConstructor
public class JobTypeAdminService {
    private final JobTypeFeignClient jobTypeFeignClient;  // 错误：使用了门面服务中定义的 Feign 客户端
}
```

## 正确方案

### 方案 1: 使用 API 模块的 RemoteService（推荐）

```java
// 引用 mall-agent-api 中的 AgentRemoteService
@Service
@RequiredArgsConstructor
public class JobTypeAdminService {
    private final AgentRemoteService agentRemoteService;  // 正确：使用 API 模块的 RemoteService
}
```

### 修改步骤

1. 删除门面服务中的 `feign/` 目录
2. 在 pom.xml 中添加 API 模块依赖（如果尚未添加）
3. 注入 API 模块的 RemoteService 替代自定义 Feign 客户端
4. 更新 Controller 中的方法调用

## 相关规范

- [架构规范 - Feign 客户端命名](.qoder/rules/05-architecture-standards.md#51-feign-客户端定义规范)
- [架构规范 - 门面服务禁止 feign 目录](.qoder/rules/05-architecture-standards.md#66-对象归属总结)
- [编码规范 - Feign Client Naming: Module-Based](memory://development_code_specification)

## 预防措施

1. **代码审查**: 审查时检查门面服务是否创建了 feign/ 目录
2. **知识库查询**: 代码质量分析时查询本坑点档案
3. **架构评审**: 新功能设计时确认 Feign 客户端位置

## 参考链接

- 修复 Commit: `abc1234`
- 相关 PR: #123
- 代码质量分析报告: `orchestrator/PROGRAMS/P-2026-001-REQ-018/workspace/code-quality-report.md`
```

### Step 3: 更新坑点索引

更新 `.qoder/repowiki/pitfalls/index.md`：

```markdown
# 历史坑点索引

## 按分类索引

### Feign 相关

- [Feign 客户端重复创建](./feign/feign-client-duplication.md) - 2026-02-27
- [门面服务创建 feign 目录](./feign/facade-feign-prohibition.md) - 2026-02-27

### 命名规范

- [模块命名不规范](./naming/module-based-naming.md) - 2026-02-27

### 架构规范

- [服务层职责混淆](./architecture/service-layer-violation.md) - 2026-02-27

## 按时间索引

| 时间 | 坑点 | 分类 | 严重程度 |
|------|------|------|----------|
| 2026-02-27 | Feign 客户端重复创建 | Feign | 高 |
| 2026-02-27 | 门面服务创建 feign 目录 | Feign | 高 |

## 按严重程度索引

### 高

- [Feign 客户端重复创建](./feign/feign-client-duplication.md)
- [门面服务创建 feign 目录](./feign/facade-feign-prohibition.md)

### 中

### 低
```

---

## 与代码质量分析的协作

```
代码质量分析 Skill
    ↓ 发现潜在问题
    ↓ 查询 pitfalls/ 目录
知识库查询 Skill
    ↓ 返回匹配的坑点档案
    ↓ 提供检查建议
代码质量分析 Skill
    ↓ 基于坑点档案进行检查
    ↓ 输出违规报告
    ↓ 修复问题
    ↓ 触发归档（如为新坑点）
坑点归档 Skill
    ↓ 创建坑点档案
    ↓ 更新索引
```

---

## 使用示例

### 示例 1: 归档 Feign 重复创建问题

```
用户: "归档刚才修复的 Feign 客户端问题"

Agent:
  → 分析已修复的问题
  → 提取关键信息:
      - 问题: 在 mall-admin 中创建了 JobTypeFeignClient
      - 正确方案: 使用 AgentRemoteService
      - 相关文件: JobTypeFeignClient.java, JobTypeAdminController.java
  → 创建档案: .qoder/repowiki/pitfalls/feign/feign-client-duplication.md
  → 更新索引: .qoder/repowiki/pitfalls/index.md
  → 返回: 已归档坑点，后续代码质量分析将自动检查
```

### 示例 2: 从代码质量分析触发归档

```
代码质量分析:
  → 发现新类型的违规
  → 检查 pitfalls/ 目录无匹配档案
  → 输出: "发现新坑点类型，建议归档"

用户: "归档这个新坑点"

Agent:
  → 调用 pitfalls-archiving Skill
  → 创建新坑点档案
  → 更新索引
```

---

## 返回格式

```
状态：已归档
档案路径：.qoder/repowiki/pitfalls/{category}/{pitfall-name}.md
索引更新：.qoder/repowiki/pitfalls/index.md

归档信息：
- 标题: {title}
- 分类: {category}
- 严重程度: {severity}
- 后续代码质量分析将自动检查此坑点
```

---

## 相关 Skill

- **知识库查询 Skill**: `.qoder/skills/knowledge-base-query.md`
- **代码质量分析 Skill**: `.qoder/skills/code-quality-analysis.md`
- **功能归档 Skill**: `.qoder/skills/feature-archiving.md`
