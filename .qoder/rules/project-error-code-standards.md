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

### 6.1 错误码枚举类命名规范

**枚举类命名格式**：`{模块名}ErrorCodeEnum`

| 服务 | 枚举类名 | 所在包 |
|------|----------|--------|
| mall-agent | `AgentErrorCodeEnum` | `com.aim.mall.agent.domain.enums` |
| mall-basic | `BasicErrorCodeEnum` | `com.aim.mall.basic.idgen.domain.enums` |
| mall-user | `UserErrorCodeEnum` | `com.aim.mall.user.domain.enums` |

**规则**：
- 必须实现 `IErrorCode` 接口
- 使用 `long` 类型存储错误码
- 按错误类型分组，添加注释分隔

### 6.2 ErrorCodeEnum 定义示例

```java
package com.aim.mall.basic.idgen.domain.enums;

import com.aim.mall.common.api.IErrorCode;
import lombok.Getter;

/**
 * 基础服务错误码枚举
 */
@Getter
public enum BasicErrorCodeEnum implements IErrorCode {

    // ==================== 客户端错误 (1) ====================
    // 参数错误 40001xxx
    PREFIX_EMPTY(40001001L, "业务前缀不能为空"),
    PREFIX_FORMAT_ERROR(40001002L, "业务前缀格式错误"),
    DATE_PATTERN_EMPTY(40001003L, "日期格式不能为空"),
    DATE_PATTERN_NOT_SUPPORT(40001004L, "日期格式不支持"),

    // ==================== 服务端错误 (2) ====================
    // Redis 错误 40002xxx
    REDIS_OPERATION_ERROR(40002001L, "Redis 操作失败"),
    SEQUENCE_EXHAUSTED(40002002L, "当日序号已用完"),
    SEGMENT_ALLOCATE_ERROR(40002003L, "MySQL 号段分配失败"),

    // 系统错误 40005xxx
    SYSTEM_ERROR(40005001L, "系统内部错误"),
    OTHER_ERROR(40005999L, "其他错误");

    private final long code;
    private final String message;

    BasicErrorCodeEnum(long code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据错误码获取枚举
     */
    public static BasicErrorCodeEnum getByCode(long code) {
        for (BasicErrorCodeEnum errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
}
```

### 6.3 命名规范

| 元素 | 命名规则 | 示例 |
|------|----------|------|
| 枚举类名 | `{模块名}ErrorCodeEnum` | `BasicErrorCodeEnum` |
| 枚举常量 | `{业务}_{错误描述}` 大写下划线分隔 | `SEQUENCE_EXHAUSTED` |
| 错误描述 | 简洁中文描述 | "当日序号已用完" |

---

## 7. 使用规范

### 7.1 异常抛出规范

**禁止硬编码错误码字符串**，必须使用错误码枚举：

```java
// ❌ 错误：硬编码错误码
return CommonResult.failed("40001004", "不支持的日期格式");
throw new RuntimeException("当日序号已用完");

// ✅ 正确：使用错误码枚举
throw new BusinessException(BasicErrorCodeEnum.DATE_PATTERN_NOT_SUPPORT,
        "不支持的日期格式: " + request.getDatePattern());
throw new BusinessException(BasicErrorCodeEnum.SEQUENCE_EXHAUSTED);
```

### 7.2 CommonResult 使用规范

| 方法 | 使用场景 | 说明 |
|------|----------|------|
| `success(T data)` | 成功返回 | 正常业务处理成功 |
| `failed(IErrorCode errorCode)` | 业务错误 | 使用错误码枚举返回错误 |
| `failed(IErrorCode errorCode, String message)` | 业务错误（自定义消息） | 使用错误码但覆盖默认消息 |
| `failed(String message)` | ❌ 已废弃 | 返回 500 错误，应改用异常抛出 |
| `failed()` | ❌ 已废弃 | 返回 500 错误，应改用异常抛出 |

**重要原则**：
- `failed(String message)` 和 `failed()` 方法已废弃，不应在业务代码中直接使用
- 业务错误应通过抛出 `BusinessException` 由全局异常处理器统一处理
- 系统错误（如数据库连接失败）应抛出 `RuntimeException`，由全局异常处理器捕获返回 500 错误

### 7.3 异常处理流程

```
业务代码抛出异常
    ↓
GlobalExceptionHandler 捕获
    ↓
根据异常类型返回 CommonResult
    ↓
    ├─ BusinessException → 返回错误码枚举定义的错误
    └─ RuntimeException/Exception → 返回 500 系统错误
```

### 7.4 代码示例

```java
// Controller 层
@PostMapping("/generate")
public CommonResult<IdGenApiResponse> generate(@RequestBody @Valid IdGenApiRequest request) {
    DatePatternEnum datePattern = DatePatternEnum.fromPattern(request.getDatePattern());
    if (datePattern == null) {
        // 抛出业务异常，由全局异常处理器处理
        throw new BusinessException(BasicErrorCodeEnum.DATE_PATTERN_NOT_SUPPORT);
    }
    
    String code = idGenService.generateCode(request.getPrefix(), datePattern);
    return CommonResult.success(response);
}

// Service 层
@Override
public String generateCode(String prefix, DatePatternEnum datePattern) {
    // ...
    if (sequence > MAX_VALUE) {
        // 抛出业务异常
        throw new BusinessException(BasicErrorCodeEnum.SEQUENCE_EXHAUSTED);
    }
    // ...
}
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
| v1.1 | 2026-03-03 | AI Agent | 新增错误码枚举类命名规范、CommonResult 使用规范、异常抛出规范 |
