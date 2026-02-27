---
name: spec-archiving
description: 技术规范归档 Skill，将技术规范、编码标准、架构规范等归档到 repowiki/docs/specs，便于代码迭代时查询
language: zh-CN
output_language: 中文
tools: Read, Write, Grep, Glob
---

# 技术规范归档 Skill

将技术规范、编码标准、架构规范、接口规范等归档到 `repowiki/docs/specs/` 目录，建立可查询的技术规范知识库，支持代码迭代和规范一致性检查。

---

## 触发条件

- 新建或更新技术规范文档
- 用户明确指令："归档规范" 或 "记录规范"
- 代码质量分析发现规范缺失
- 架构决策确定新的规范标准

---

## 输入

- 规范标题
- 规范类型
- 规范内容
- 适用范围
- 版本信息
- 相关规则引用

---

## 输出

- 规范文档：`repowiki/docs/specs/{spec-name}.md`
- 更新索引：`repowiki/docs/specs/index.md`
- 模板文件（如需要）：`repowiki/docs/specs/_TEMPLATE/{template-name}.md`

---

## 规范分类

| 分类 | 目录 | 说明 |
|------|------|------|
| 错误码规范 | `specs/` | 错误码定义、格式、分配策略 |
| 架构规范 | `specs/` | 分层架构、服务职责、调用关系 |
| 编码规范 | `specs/` | 命名规范、代码风格、注释规范 |
| 接口规范 | `specs/` | API 设计、路径规范、参数规范 |
| 数据库规范 | `specs/` | 表设计、字段命名、索引规范 |
| 安全规范 | `specs/` | 安全编码、权限控制、数据保护 |

---

## 归档流程

### Step 1: 分析规范信息

提取规范的关键信息：

```yaml
spec_analysis:
  title: "Feign 客户端命名规范"
  category: "架构规范"
  version: "v1.0"
  scope: "所有微服务模块"
  description: "定义 Feign 客户端的命名格式、位置、使用方式"
  key_points:
    - "命名格式: {Module}RemoteService"
    - "位置: mall-{module}-api 模块"
    - "门面服务禁止创建 feign/ 目录"
  related_rules:
    - ".qoder/rules/05-architecture-standards.md"
  context:
    created_by: "Qoder"
    created_at: "2026-02-27"
    updated_at: "2026-02-27"
    related_programs:
      - "P-2026-001-REQ-018"
```

### Step 2: 生成规范文档

使用模板创建 Markdown 格式的规范文档：

```markdown
---
spec_type: 架构规范
version: v1.0
scope: 所有微服务模块
status: active
created_at: 2026-02-27
updated_at: 2026-02-27
author: Qoder
---

# Feign 客户端命名规范

## 概述

定义 Feign 客户端的命名格式、存放位置、使用方式，确保跨服务调用接口的一致性和可维护性。

## 规范内容

### 1. 命名格式

Feign 客户端必须使用 `{Module}RemoteService` 命名格式：

- ✅ `AgentRemoteService` - 对应 mall-agent 模块
- ✅ `UserRemoteService` - 对应 mall-user 模块
- ❌ `JobTypeFeignClient` - 错误：使用了业务名而非模块名
- ❌ `AgentFeignClient` - 错误：使用了 FeignClient 后缀

### 2. 存放位置

Feign 客户端必须定义在 API 模块中：

```
repos/mall-inner-api/mall-{module}-api/src/main/java/com/aim/mall/{module}/api/feign/{Module}RemoteService.java
```

示例：
- `mall-inner-api/mall-agent-api/.../feign/AgentRemoteService.java`
- `mall-inner-api/mall-user-api/.../feign/UserRemoteService.java`

### 3. 门面服务限制

**门面服务（mall-admin/mall-app/mall-chat）禁止创建 feign/ 目录**。

门面服务应当：
1. 在 pom.xml 中引用 API 模块依赖
2. 直接注入使用 API 模块中的 RemoteService

### 4. 使用示例

#### API 模块定义

```java
// mall-agent-api/src/main/java/com/aim/mall/agent/api/feign/AgentRemoteService.java
@FeignClient(name = "mall-agent", path = "/inner/api/v1")
public interface AgentRemoteService {
    @GetMapping("/agents/{id}")
    CommonResult<AgentApiResponse> getAgentById(@PathVariable("id") Long id);
}
```

#### 门面服务使用

```java
// mall-admin/src/main/java/com/aim/mall/admin/service/AgentAdminService.java
@Service
@RequiredArgsConstructor
public class AgentAdminService {
    private final AgentRemoteService agentRemoteService;
    
    public AgentResponse getAgent(Long id) {
        return convertToResponse(agentRemoteService.getAgentById(id).getData());
    }
}
```

## 适用范围

- 所有新建的 Feign 客户端
- 所有门面服务对应用服务的调用

## 相关规范

- [架构规范](./架构规范.md)
- [编码规范](./编码规范.md)

## 版本历史

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0 | 2026-02-27 | Qoder | 初始版本 |

## 附件

- 规范维护责任人：架构组
- 审核周期：每季度
```

### Step 3: 更新规范索引

更新 `repowiki/docs/specs/index.md`：

```markdown
# 技术规范索引

## 规范列表

### 架构规范

- [Feign 客户端命名规范](./feign-client-naming-spec.md) - v1.0 - 2026-02-27
- [分层架构规范](./architecture-layer-spec.md) - v1.0 - 2026-02-01

### 编码规范

- [Java 编码规范](./java-coding-spec.md) - v1.0 - 2026-02-01

### 错误码规范

- [错误码规范](./错误码规范.md) - v1.0 - 2026-02-04

## 按时间索引

| 日期 | 规范 | 类型 | 版本 |
|------|------|------|------|
| 2026-02-27 | Feign 客户端命名规范 | 架构规范 | v1.0 |

## 规范模板

- [规范文档模板](./_TEMPLATE/spec-template.md)
```

---

## 模板文件

规范模板存放在 `repowiki/docs/specs/_TEMPLATE/` 目录：

### spec-template.md

通用规范文档模板，包含规范文档的标准结构。

### spec-type-templates/

按规范类型分类的模板：
- `architecture-spec-template.md` - 架构规范模板
- `coding-spec-template.md` - 编码规范模板
- `api-spec-template.md` - 接口规范模板
- `database-spec-template.md` - 数据库规范模板

---

## 与代码质量分析的协作

```
代码质量分析 Skill
    ↓ 发现规范缺失或违规
    ↓ 查询 specs/ 目录
知识库查询 Skill
    ↓ 返回匹配的规范文档
    ↓ 提供检查依据
代码质量分析 Skill
    ↓ 基于规范进行检查
    ↓ 输出违规报告
    ↓ 建议更新规范（如为新场景）
规范归档 Skill
    ↓ 创建或更新规范文档
    ↓ 更新索引
```

---

## 使用示例

### 示例 1: 归档新的 Feign 命名规范

```
用户: "归档 Feign 客户端命名规范"

Agent:
  → 分析规范信息
  → 使用模板创建文档
  → 文件: repowiki/docs/specs/feign-client-naming-spec.md
  → 更新索引: repowiki/docs/specs/index.md
  → 返回: 已归档规范，后续代码质量分析将引用此规范
```

### 示例 2: 从代码质量分析触发规范更新

```
代码质量分析:
  → 发现新类型的违规
  → 查询 specs/ 目录无匹配规范
  → 输出: "发现新规范场景，建议归档规范"

用户: "为这个场景创建规范"

Agent:
  → 调用 spec-archiving Skill
  → 基于违规案例创建规范文档
  → 更新索引
```

---

## 返回格式

```
状态：已归档
文档路径：repowiki/docs/specs/{spec-name}.md
索引更新：repowiki/docs/specs/index.md

归档信息：
- 标题: {title}
- 类型: {category}
- 版本: {version}
- 后续代码质量分析将引用此规范
```

---

## 相关 Skill

- **知识库查询 Skill**: `.qoder/skills/knowledge-base-query.md`
- **代码质量分析 Skill**: `.qoder/skills/code-quality-analysis.md`
- **坑点归档 Skill**: `.qoder/skills/pitfalls-archiving.md`
