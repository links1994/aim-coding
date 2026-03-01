---
title: 项目文档索引
description: AIM Coding 项目技术文档和规范的完整索引
version: 1.0.0
updated_at: 2026-03-02
---

# 项目文档索引

本文档提供 AIM Coding 项目所有技术文档和规范的索引。

---

## 规范文档 (Specs)

| 文档 | 路径 | 描述 | 状态 |
|------|------|------|------|
| [CommonResult 响应格式规范](../specs/common-result-spec.md) | `specs/common-result-spec.md` | 定义统一响应对象 CommonResult 的结构和使用 | ✅ Active |
| [操作人ID传递规范](../specs/operator-id-spec.md) | `specs/operator-id-spec.md` | 定义门面服务和应用服务中获取操作人ID的标准方式 | ✅ Active |

---

## 编码规范 (Rules)

| 文档 | 路径 | 描述 |
|------|------|------|
| [Java 编码规范](../../.qoder/rules/04-coding-standards.md) | `.qoder/rules/04-coding-standards.md` | Java 代码编写标准，包含命名、分层、异常处理等规范 |
| [架构规范](../../.qoder/rules/05-architecture-standards.md) | `.qoder/rules/05-architecture-standards.md` | 系统架构设计规范 |

---

## 快速参考

### 操作人ID获取

```java
// 门面服务
@RequestHeader(AuthConstant.USER_TOKEN_HEADER) String user
Long userId = UserInfoUtil.getUserInfo(user).getId();

// 应用服务
// 通过 ApiRequest.getOperatorId() 获取
```

### 响应格式

```java
// 成功响应
return CommonResult.success(data);
return CommonResult.pageSuccess(items, totalCount);

// 失败响应
return CommonResult.failed(errorCode);
```

---

## 文档维护

- **维护责任人**: 技术团队
- **更新频率**: 随规范变更同步更新
- **反馈渠道**: 技术讨论群

---

## 最近更新

| 日期 | 文档 | 变更内容 |
|------|------|----------|
| 2026-03-02 | operator-id-spec.md | 新增操作人ID传递规范 |
| 2026-03-02 | common-result-spec.md | 初始版本 |
