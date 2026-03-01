---
name: spec-archiving
description: 归档技术规格书到 repowiki。在技术规格书定稿、需要标准文档或团队需要引用项目约定时使用。
---

# 规格书归档 Skill

将技术规格书归档到 `.qoder/repowiki/docs/`，用于标准文档和团队参考。

---

## 触发条件

- 技术规格书定稿
- 需要记录项目标准
- 团队需要引用约定
- 用户指令："归档规格书"

---

## 输入

- 规格书文档
- 规则文件
- 约定定义

---

## 输出

- 规格书归档 → `.qoder/repowiki/docs/{lang}/specs/{spec-name}.md`

---

## 归档结构

```
.qoder/repowiki/docs/
├── zh/                     # 中文文档
│   ├── specs/              # 技术规格书
│   │   ├── error-code-spec.md
│   │   ├── architecture-spec.md
│   │   └── coding-spec.md
│   └── guides/             # 操作指南
│       ├── deployment/
│       └── operations/
└── en/                     # 英文文档
    └── ...
```

---

## 规格书文档格式

```markdown
---
spec_name: 错误码规范
version: 1.0.0
description: 标准错误码格式和使用
created_at: 2026-02-28
---

# 错误码规范

## 概述

本文档定义项目的标准错误码格式。

## 格式

错误码遵循格式：`SSMMTNNN`

| 段 | 含义 | 说明 |
|---------|---------|-------------|
| SS | 系统 | 20=通用，30=管理，40=智能体 |
| MM | 模块 | 01=用户，02=订单，03=员工 |
| T | 类型 | 0=成功，1=客户端错误，2=服务端错误，3=业务错误 |
| NNN | 序号 | 顺序编号 (001-999) |

## 示例

| 错误码 | 含义 |
|------|---------|
| 2000000 | 成功 |
| 2001001 | 用户不存在 |
| 4003001 | 员工已存在 |

## 使用

```java
// 返回成功
return CommonResult.success(data);

// 返回错误
return CommonResult.error(2001001, "用户不存在");
```
```

---

## 工作流程

### 步骤 1：读取源文件

读取规格书源文件。

### 步骤 2：确定语言

- zh — 中文
- en — 英文

### 步骤 3：创建归档

按照上述格式生成规格书文档。

### 步骤 4：更新索引

更新 `.qoder/repowiki/docs/index.md`。

---

## 归档示例

### 示例 1：操作人 ID 传递规范

**来源**：用户关于操作人 ID 传递约定的需求

**归档位置**：`.qoder/repowiki/specs/operator-id-spec.md`

**关键内容**：
- 门面服务：`@RequestHeader(AuthConstant.USER_TOKEN_HEADER) String user` + `UserInfoUtil.getUserInfo(user).getId()`
- 应用服务：通过方法参数接收（如 ApiRequest 中的 `operatorId`）
- 门面服务和应用服务之间职责清晰分离

**相关更新**：
- 添加章节到 `.qoder/rules/04-coding-standards.md`
- 在相关规格书文档中引用

---

## 返回格式

```
状态：已完成
归档：.qoder/repowiki/docs/{lang}/specs/{name}.md
语言：{zh|en}
版本：X.Y.Z
```
