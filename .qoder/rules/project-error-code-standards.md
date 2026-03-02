---
trigger: model_decision
description: 项目错误码生成规范 - 定义 AIM 电商平台错误码格式、系统/模块映射、错误类型及生成规则
---

# 项目错误码生成规范

## 1. 错误码格式

错误码遵循 **`SSMMTNNN`** 格式，共 8 位数字：

| 位置 | 段 | 长度 | 含义 | 说明 |
|------|-----|------|------|------|
| 1-2 | SS | 2位 | 系统代码 | 标识服务所属系统 |
| 3-4 | MM | 2位 | 模块代码 | 标识业务模块 |
| 5 | T | 1位 | 错误类型 | 区分错误类别 |
| 6-8 | NNN | 3位 | 序号 | 顺序号 (001-999) |

**示例**：`40031001` = Agent系统(40) + Employee模块(03) + 客户端错误(1) + 序号(001)

---

## 2. 系统代码映射 (SS)

| 系统代码 | 系统名称 | 对应服务 | 说明 |
|----------|----------|----------|------|
| 20 | Common | mall-common | 通用/公共错误 |
| 30 | Admin | mall-admin | 管理后台门面服务 |
| 31 | App | mall-app | 客户端门面服务 |
| 32 | Chat | mall-chat | AI对话门面服务 |
| 40 | Agent | mall-agent | AI智能体员工服务 |
| 50 | User | mall-user | 用户支撑服务 |

---

## 3. 模块代码映射 (MM)

### 3.1 通用模块 (Common - 20)

| 模块代码 | 模块名称 | 说明 |
|----------|----------|------|
| 00 | System | 系统级通用错误 |
| 01 | Auth | 认证授权模块 |
| 02 | Param | 参数校验模块 |
| 03 | File | 文件处理模块 |

### 3.2 Agent 系统模块 (40)

| 模块代码 | 模块名称 | 业务域 | 说明 |
|----------|----------|--------|------|
| 01 | Employee | employee | 智能员工管理 |
| 02 | JobType | employee | 岗位类型管理 |
| 03 | Schedule | schedule | 排班管理 |
| 04 | Performance | performance | 绩效管理 |
| 05 | Skill | skill | 技能管理 |
| 06 | Training | training | 培训管理 |

### 3.3 User 系统模块 (50)

| 模块代码 | 模块名称 | 业务域 | 说明 |
|----------|----------|--------|------|
| 01 | Profile | user | 用户资料 |
| 02 | Account | user | 账户管理 |
| 03 | Permission | permission | 权限管理 |

### 3.4 Admin/App/Chat 门面模块

门面服务错误码直接使用被调用服务的错误码，不单独定义业务模块错误码。

---

## 4. 错误类型 (T)

| 类型代码 | 类型名称 | HTTP状态 | 使用场景 | 示例 |
|----------|----------|----------|----------|------|
| 0 | Success | 200 | 成功响应 | 正常业务处理成功 |
| 1 | Client Error | 200 | 客户端错误 | 参数校验失败、非法请求 |
| 2 | Server Error | 200 | 服务端错误 | 系统异常、数据库错误 |
| 3 | Business Error | 200 | 业务错误 | 业务规则违反、状态不允许 |

**注意**：所有错误响应 HTTP 状态码均为 200，错误类型通过响应体中的 `code` 字段区分。

---

## 5. 错误码生成规则

### 5.1 生成步骤

1. **确定系统代码 (SS)**：根据服务类型选择对应系统代码
2. **确定模块代码 (MM)**：根据业务功能选择对应模块代码
3. **确定错误类型 (T)**：根据错误性质选择类型代码
4. **分配序号 (NNN)**：在模块内按顺序分配，从 001 开始

### 5.2 序号分配规则

| 序号范围 | 用途 | 说明 |
|----------|------|------|
| 001-099 | 通用错误 | 模块级通用错误码 |
| 100-199 | 参数错误 | 参数校验相关错误 |
| 200-299 | 业务错误 | 业务规则相关错误 |
| 300-399 | 数据错误 | 数据不存在、重复等 |
| 400-499 | 状态错误 | 状态不允许、冲突等 |
| 500-599 | 权限错误 | 权限不足、未授权等 |
| 900-999 | 系统错误 | 系统级异常 |

### 5.3 错误码示例

**Agent 系统 - Employee 模块 (4003)**：

| 错误码 | 类型 | 描述 | 使用场景 |
|--------|------|------|----------|
| 40031001 | Client | 参数错误 | 通用参数校验失败 |
| 40031002 | Client | 员工姓名不能为空 | name 字段为空 |
| 40031003 | Client | 手机号格式错误 | phone 格式不正确 |
| 40032001 | Business | 员工已存在 | 手机号已注册 |
| 40032002 | Business | 员工不存在 | 查询的员工 ID 不存在 |
| 40032003 | Business | 员工已被禁用 | 操作已禁用员工 |
| 40033001 | Data | 员工信息不存在 | 根据 ID 查询无记录 |
| 40034001 | State | 员工状态不允许 | 当前状态不能执行该操作 |

---

## 6. 错误码定义规范

### 6.1 ErrorCodeEnum 定义

```java
@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {
    
    // ========== 通用错误 (2000) ==========
    SUCCESS(20000000, "success"),
    PARAM_ERROR(20001001, "参数错误"),
    SYSTEM_ERROR(20002901, "系统繁忙，请稍后重试"),
    
    // ========== Agent - Employee (4003) ==========
    EMPLOYEE_PARAM_ERROR(40031001, "参数错误"),
    EMPLOYEE_NAME_EMPTY(40031002, "员工姓名不能为空"),
    EMPLOYEE_PHONE_INVALID(40031003, "手机号格式错误"),
    EMPLOYEE_ALREADY_EXISTS(40032001, "员工已存在"),
    EMPLOYEE_NOT_FOUND(40032002, "员工不存在"),
    EMPLOYEE_DISABLED(40032003, "员工已被禁用"),
    
    // ========== Agent - JobType (4004) ==========
    JOB_TYPE_PARAM_ERROR(40041001, "参数错误"),
    JOB_TYPE_NAME_EMPTY(40041002, "岗位类型名称不能为空"),
    JOB_TYPE_CODE_EMPTY(40041003, "岗位类型编码不能为空"),
    JOB_TYPE_CODE_INVALID(40041004, "岗位类型编码格式错误"),
    JOB_TYPE_ALREADY_EXISTS(40042001, "岗位类型已存在"),
    JOB_TYPE_NOT_FOUND(40042002, "岗位类型不存在"),
    JOB_TYPE_HAS_EMPLOYEES(40042003, "岗位类型有关联员工，无法删除"),
    
    // ... 其他错误码
    ;
    
    private final int code;
    private final String message;
}
```

### 6.2 命名规范

| 元素 | 命名规则 | 示例 |
|------|----------|------|
| 枚举名 | `{模块}_{错误描述}` 大写下划线分隔 | `EMPLOYEE_NOT_FOUND` |
| 错误描述 | 简洁中文描述 | "员工不存在" |
| 常量名 | 模块前缀 + 错误类型 | `EMPLOYEE_`, `JOB_TYPE_` |

---

## 7. 使用规范

### 7.1 异常抛出

```java
// 参数校验异常
throw new MethodArgumentValidationException(ErrorCodeEnum.EMPLOYEE_NAME_EMPTY);

// 业务异常
throw new BusinessException(ErrorCodeEnum.EMPLOYEE_NOT_FOUND);

// 带动态参数的业务异常
throw new BusinessException(ErrorCodeEnum.JOB_TYPE_HAS_EMPLOYEES, 
    String.format("当前有 %d 名员工关联", employeeCount));
```

### 7.2 错误码分配流程

1. 开发前根据功能模块确定系统代码和模块代码
2. 根据错误类型选择类型代码
3. 在对应序号范围内分配下一个可用序号
4. 在 ErrorCodeEnum 中定义新的错误码常量
5. 在技术规格书中记录错误码定义

---

## 8. 相关规范引用

| 规范 | 路径 | 说明 |
|------|------|------|
| 通用编码规范 | `.qoder/rules/common-coding-standards.md` | 异常处理规范 |
| 通用架构规范 | `.qoder/rules/common-architecture-standards.md` | 分层架构规范 |
| 项目命名规范 | `.qoder/rules/project-naming-standards.md` | 服务命名、模块划分 |
| 项目响应格式规范 | `.qoder/rules/project-common-result-standards.md` | CommonResult 使用规范 |

---

## 9. 版本历史

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0 | 2026-03-02 | AI Agent | 初始版本，定义错误码生成规范 |
