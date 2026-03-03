---
name: unit-test-generation
description: 为 Java 代码生成单元测试。在代码生成完成、需要补充单元测试覆盖率或用户明确请求生成单元测试时使用。此步骤为可选流程，在功能归档前执行。
---

# 单元测试生成 Skill

为已生成的 Java 代码创建单元测试，确保核心业务逻辑有适当的测试覆盖。

> **执行方式**：此步骤为可选流程，由主 Agent 根据项目需求决定是否执行。如 Sub-Agent 可用，可委托 `code-generator` Agent 执行；否则使用本 Skill 执行。

---

## 触发条件

- 代码生成完成（Phase 4 结束）
- 用户明确指令："生成单元测试" 或 "补充测试覆盖"
- 功能归档前，主 Agent 判断需要单元测试
- 项目规范要求核心逻辑必须有单元测试覆盖

---

## 输入

- `orchestrator/PROGRAMS/{program_id}/artifacts/tech-spec.md` — 技术规格书
- `orchestrator/PROGRAMS/{program_id}/artifacts/code-generation-report.md` — 代码生成报告
- 已生成的 Java 源代码（Service 层、工具类等需要测试的类）
- `.qoder/rules/common-coding-standards.md` — 通用编码规范

---

## 输出

- 单元测试类 → `{service}/src/test/java/` 对应包路径下
- 测试资源文件 → `{service}/src/test/resources/`（如需要）
- 单元测试报告 `orchestrator/PROGRAMS/{program_id}/artifacts/unit-test-report.md`

---

## 测试范围决策

### 需要单元测试的类

| 类型 | 优先级 | 说明 |
|------|--------|------|
| XxxManageService | 高 | 核心业务逻辑，包含增删改操作 |
| XxxQueryService | 中 | 查询逻辑，特别是复杂查询条件组装 |
| XxxApplicationService | 中 | 应用层编排逻辑 |
| 工具类/工具方法 | 高 | 纯函数，输入输出明确 |
| 枚举/常量类 | 低 | 通常不需要测试 |
| Controller | 可选 | 优先使用 HTTP 测试覆盖 |
| Mapper | 可选 | 优先使用集成测试覆盖 |

### 不需要单元测试的类

- DO/Entity 类（仅包含字段和 getter/setter）
- DTO/Request/Response 类（仅数据传输）
- 配置类（@Configuration）
- 简单的数据转换方法

---

## 测试框架与依赖

### 标准依赖（Spring Boot 项目）

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
```

### 测试框架选择

| 场景 | 框架 | 说明 |
|------|------|------|
| 纯 Java 逻辑 | JUnit 5 + Mockito | 标准单元测试 |
| 需要 Spring 上下文 | @SpringBootTest | 集成测试场景 |
| 数据库操作 | @DataJpaTest / @MybatisPlusTest | 数据层测试 |

---

## 工作流程

### 步骤 1：分析待测试代码

1. 读取代码生成报告，确定生成了哪些服务
2. 分析每个服务的 Service 层代码
3. 识别需要测试的核心方法
4. 确定测试优先级

### 步骤 2：确定测试策略

根据方法类型选择测试策略：

| 方法类型 | 测试策略 | Mock 需求 |
|----------|----------|-----------|
| 纯业务逻辑 | 直接测试，验证输出 | 依赖的 Service/Mapper |
| 数据库操作 | 验证 Mapper 调用 | Mapper 层 |
| 远程调用 | 验证 Feign 调用 | Feign 客户端 |
| 复杂条件分支 | 分支覆盖测试 | 相关依赖 |

### 步骤 3：生成单元测试

#### Service 层测试模板

```java
package com.aim.mall.{module}.{domain}.service;

import com.aim.mall.{module}.{domain}.domain.entity.Aim{Xxx}DO;
import com.aim.mall.{module}.{domain}.mapper.Aim{Xxx}Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * {业务描述}管理服务测试
 *
 * @author AI Agent
 */
@ExtendWith(MockitoExtension.class)
class {Xxx}ManageServiceTest {

    @Mock
    private Aim{Xxx}Mapper aim{Xxx}Mapper;

    @Mock
    private Aim{Xxx}Service aim{Xxx}Service;

    @InjectMocks
    private {Xxx}ManageService {xxx}ManageService;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
    }

    @Test
    void create{XXX}_WithValidData_ShouldReturnId() {
        // Given
        {Xxx}CreateDTO dto = createValidDTO();
        when(aim{Xxx}Mapper.insert(any(Aim{Xxx}DO.class))).thenReturn(1);

        // When
        Long result = {xxx}ManageService.create{Xxx}(dto);

        // Then
        assertThat(result).isNotNull();
        verify(aim{Xxx}Mapper).insert(any(Aim{Xxx}DO.class));
    }

    @Test
    void create{XXX}_WithDuplicateName_ShouldThrowException() {
        // Given
        {Xxx}CreateDTO dto = createDTOWithDuplicateName();
        when(aim{Xxx}Service.existsByName(dto.getName())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> {xxx}ManageService.create{Xxx}(dto))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("名称已存在");
    }

    @Test
    void update{XXX}_WithValidData_ShouldReturnTrue() {
        // Given
        Long id = 1L;
        {Xxx}UpdateDTO dto = createValidUpdateDTO();
        when(aim{Xxx}Mapper.updateById(any(Aim{Xxx}DO.class))).thenReturn(1);

        // When
        boolean result = {xxx}ManageService.update{Xxx}(id, dto);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void delete{XXX}_WithExistingId_ShouldReturnTrue() {
        // Given
        Long id = 1L;
        when(aim{Xxx}Mapper.updateById(any(Aim{Xxx}DO.class))).thenReturn(1);

        // When
        boolean result = {xxx}ManageService.delete{Xxx}(id);

        // Then
        assertThat(result).isTrue();
        verify(aim{Xxx}Mapper).updateById(argThat(entity -> 
            entity.getId().equals(id) && entity.getIsDeleted() == 1
        ));
    }

    // 辅助方法：创建测试数据
    private {Xxx}CreateDTO createValidDTO() {
        {Xxx}CreateDTO dto = new {Xxx}CreateDTO();
        dto.setName("测试名称");
        dto.setCode("TEST001");
        return dto;
    }
}
```

#### QueryService 测试模板

```java
package com.aim.mall.{module}.{domain}.service;

import com.aim.mall.common.api.CommonResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * {业务描述}查询服务测试
 *
 * @author AI Agent
 */
@ExtendWith(MockitoExtension.class)
class {Xxx}QueryServiceTest {

    @Mock
    private Aim{Xxx}Service aim{Xxx}Service;

    @InjectMocks
    private {Xxx}QueryService {xxx}QueryService;

    @Test
    void getById_WithExistingId_ShouldReturnData() {
        // Given
        Long id = 1L;
        Aim{Xxx}DO mockData = createMockDO(id);
        when(aim{Xxx}Service.getById(id)).thenReturn(mockData);

        // When
        CommonResult<{Xxx}Response> result = {xxx}QueryService.getById(id);

        // Then
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getId()).isEqualTo(id);
    }

    @Test
    void getById_WithNonExistingId_ShouldReturnNull() {
        // Given
        Long id = 999L;
        when(aim{Xxx}Service.getById(id)).thenReturn(null);

        // When
        CommonResult<{Xxx}Response> result = {xxx}QueryService.getById(id);

        // Then
        assertThat(result.getData()).isNull();
    }

    @Test
    void pageQuery_WithKeyword_ShouldReturnFilteredResults() {
        // Given
        {Xxx}PageQuery query = new {Xxx}PageQuery();
        query.setKeyword("测试");
        query.setPageNum(1);
        query.setPageSize(10);

        Page<Aim{Xxx}DO> mockPage = createMockPage();
        when(aim{Xxx}Service.page(any(Page.class), any(QueryWrapper.class)))
            .thenReturn(mockPage);

        // When
        CommonResult<CommonResult.PageData<{Xxx}Response>> result = 
            {xxx}QueryService.page{Xxx}(query);

        // Then
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getList()).isNotEmpty();
    }
}
```

### 步骤 4：生成测试报告

生成 `orchestrator/PROGRAMS/{program_id}/artifacts/unit-test-report.md`：

```markdown
# 单元测试报告

## 测试概览

| 服务 | 测试类数 | 测试方法数 | 覆盖率 |
|------|----------|------------|--------|
| mall-agent | 3 | 12 | 75% |
| mall-user | 2 | 8 | 80% |

## 测试类清单

### mall-agent

| 测试类 | 被测类 | 测试方法数 | 说明 |
|--------|--------|------------|------|
| JobTypeManageServiceTest | JobTypeManageService | 5 | 增删改操作测试 |
| JobTypeQueryServiceTest | JobTypeQueryService | 4 | 查询操作测试 |
| EmployeeManageServiceTest | EmployeeManageService | 3 | 核心创建逻辑测试 |

## 测试场景覆盖

### 正常场景

- [x] 创建记录 - 验证数据正确保存
- [x] 更新记录 - 验证数据正确更新
- [x] 删除记录 - 验证软删除逻辑
- [x] 查询单条 - 验证数据正确返回
- [x] 分页查询 - 验证分页参数处理

### 异常场景

- [x] 重复名称创建 - 验证异常抛出
- [x] 更新不存在记录 - 验证处理逻辑
- [x] 删除不存在记录 - 验证处理逻辑
- [x] 参数校验失败 - 验证校验逻辑

## 未覆盖场景（如有）

| 场景 | 原因 | 建议 |
|------|------|------|
| 复杂关联查询 | 需要数据库环境 | 建议集成测试覆盖 |
| 分布式事务 | 需要完整上下文 | 建议端到端测试覆盖 |

## 运行测试

```bash
# 运行所有单元测试
mvn test

# 运行特定服务测试
cd repos/mall-agent
mvn test

# 生成覆盖率报告
mvn jacoco:report
```

## 下一步

1. [可选] 补充集成测试
2. [可选] 运行测试验证
3. 继续功能归档流程
```

---

## 测试规范

### 命名规范

| 元素 | 命名规则 | 示例 |
|------|----------|------|
| 测试类 | `{被测类名}Test` | `JobTypeManageServiceTest` |
| 测试方法 | `{被测方法}_{场景}_{预期结果}` | `create_WithValidData_ShouldReturnId` |
| 测试数据方法 | `create{描述}DTO/DO` | `createValidDTO()` |

### 代码规范

1. **Given-When-Then 结构**：每个测试方法遵循三段式结构
2. **独立测试**：每个测试方法独立，不依赖执行顺序
3. **清晰断言**：使用 AssertJ 进行链式断言，提高可读性
4. **适当 Mock**：只 Mock 外部依赖，不 Mock 被测类内部逻辑

### 禁止事项

- ❌ 禁止测试私有方法（通过反射）
- ❌ 禁止测试 getter/setter
- ❌ 禁止在单元测试中访问真实数据库
- ❌ 禁止测试方法之间相互依赖

---

## 返回格式

```
状态：已完成/部分完成/跳过

单元测试报告：orchestrator/PROGRAMS/{program_id}/artifacts/unit-test-report.md
输出：
  - {service}/src/test/java/...（测试类路径）

测试统计：
  - 服务数：N
  - 测试类数：M
  - 测试方法数：K

下一步建议：
  - 如测试通过 → 继续功能归档
  - 如测试失败 → 修复代码或调整测试
```
