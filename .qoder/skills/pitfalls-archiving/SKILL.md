---
name: pitfalls-archiving
description: 归档代码陷阱和反模式到 repowiki。在发现重复问题、代码审查发现问题或记录经验教训以防止未来错误时使用。
---

# 代码陷阱归档 Skill

将代码陷阱和反模式归档到 `.qoder/repowiki/pitfalls/`，用于未来参考和自动检测。

---

## 触发条件

- 开发过程中发现重复问题
- 代码审查发现
- 用户指令："归档陷阱" 或 "记录问题"
- 需要记录经验教训

---

## 输入

- 问题描述
- 违规示例（代码）
- 正确解决方案（代码）
- 相关规范

---

## 输出

- 陷阱归档 → `.qoder/repowiki/pitfalls/{category}/{pitfall-name}.md`

---

## 归档结构

```
.qoder/repowiki/pitfalls/
├── index.md
├── feign/
│   ├── feign-client-duplication.md
│   └── facade-feign-prohibition.md
├── naming/
│   └── module-based-naming.md
├── architecture/
│   └── service-layer-violation.md
└── maven/
    └── dependency-conflict.md
```

---

## 陷阱文档格式

```markdown
# 陷阱标题

## 问题描述

简要描述这个陷阱是什么以及为什么有问题。

## 违规模式

用于自动检测的代码特征或模式。

## 违规示例

```java
// 错误代码
@FeignClient(name = "mall-agent")
public interface AgentService {
    // 这会创建重复的客户端
}
```

## 正确方案

```java
// 正确代码
// 使用 mall-agent-api 中的共享 RemoteService
@Autowired
private AgentRemoteService agentRemoteService;
```

## 相关规范

- 架构规范：服务层职责
- 编码规范：Feign 客户端使用

## 归档信息

- 发现时间：2026-02-27
- 发现者：Qoder
- 相关 Program：P-2026-001-REQ-018
- 严重程度：高
```

---

## 工作流程

### 步骤 1：分类陷阱

确定类别：
- feign — Feign 相关问题
- naming — 命名规范问题
- architecture — 架构违规
- maven — Maven/依赖问题
- performance — 性能反模式
- security — 安全问题

### 步骤 2：生成文档

按照上述格式创建陷阱文档。

### 步骤 3：更新索引

更新 `.qoder/repowiki/pitfalls/index.md`：

```markdown
# 陷阱索引

## Feign
- [Feign 客户端重复创建](./feign/feign-client-duplication.md)

## 命名
- [基于模块的命名](./naming/module-based-naming.md)
```

---

## 返回格式

```
状态：已完成
归档：.qoder/repowiki/pitfalls/{category}/{name}.md
类别：{category}
严重程度：{severity}
```
