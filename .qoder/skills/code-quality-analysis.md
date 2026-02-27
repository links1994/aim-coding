---
name: code-quality-analysis
description: 代码质量分析 Skill，对生成的代码进行质量分析和优化建议
tools: Read, Write, Grep, Glob
---

# 代码质量分析 Skill

对生成的代码进行全面的质量分析，识别问题并提供优化建议。

---

## 触发条件

- HTTP 测试生成完成
- 用户明确指令："分析代码质量" 或 "委托: 优化代码质量"
- 主 Agent 在 Phase 6 调用

---

## 输入

- 生成的 Java 源代码
- 项目架构规范（RESOURCE-MAP.yml）
- 编码规范（.qoder/rules/04-coding-standards.md）

---

## 输出

- 代码质量分析报告：`orchestrator/PROGRAMS/{program_id}/workspace/code-quality-report.md`
- 质量分析数据：`orchestrator/PROGRAMS/{program_id}/workspace/code-quality-analysis.yaml`

---

## 分析范围

### 1. 代码重复检测

- 识别 3 行以上的重复代码片段
- 检测相似逻辑的不同实现
- 发现可提取为公共方法的重复代码

### 2. 性能优化

- 识别 N+1 查询问题
- 检测不必要的循环和嵌套
- 发现可以缓存的数据查询
- 识别内存泄漏风险点

### 3. SQL 性能专项

- 识别在循环中执行单条 CRUD 操作
- 检测可优化的查询语句
- 发现可使用批量操作的场景

### 4. 设计模式应用

- 识别适合应用设计模式的场景
- 检查现有设计模式的正确性
- 发现违反设计原则的代码

### 5. DDD 原则检查

- 验证领域模型的纯净性
- 检查聚合根的正确性
- 识别跨领域边界的不当调用

### 6. 代码规范检查

检查代码是否符合项目定义的编码规范：

#### 6.1 知识库查询（历史坑点检测）

**核心机制**: 在代码规范检查前，先查询 repowiki 中的历史坑点档案，识别已知的常见陷阱。

**查询流程**:
```
1. 识别待检查代码的特征（如：创建 Feign 客户端、命名模式等）
2. 调用 knowledge-base-query Skill 查询 repowiki
3. 目标目录: `.qoder/repowiki/pitfalls/`
4. 匹配已知坑点模式
5. 输出针对性检查建议
```

**查询类别与目标路径**:

| 检查场景 | 查询关键词 | 目标目录 |
|---------|-----------|---------|
| Feign 客户端创建 | `Feign,RemoteService,existence,check,reuse` | `.qoder/repowiki/pitfalls/feign/` |
| 命名规范 | `naming convention,Request,Response,DTO` | `.qoder/repowiki/pitfalls/naming/` |
| 架构分层 | `facade,feign,inter-module call` | `.qoder/repowiki/pitfalls/architecture/` |
| Maven 依赖 | `Maven,dependencies,import resolution` | `.qoder/repowiki/pitfalls/maven/` |

**调用 knowledge-base-query Skill 示例**:
```yaml
# 当检测到代码中创建了新的 Feign 客户端时
skill_invocation:
  skill: "knowledge-base-query"
  parameters:
    keywords: "Feign,RemoteService,existence,check,reuse"
    type: "pitfall"
    target_path: ".qoder/repowiki/pitfalls/feign/"
    
# 预期返回的坑点档案
expected_results:
  - file: "feign-client-duplication.md"
    title: "Feign 客户端重复创建"
    pattern: "在门面服务中创建 Feign 客户端而非使用 API 模块的 RemoteService"
    solution: "删除门面服务中的 Feign 客户端，注入 API 模块的 RemoteService 使用"
```

**基于知识库的检查项**:

- [ ] **Feign 客户端重复创建检查**
  - 查询 repowiki: `.qoder/repowiki/pitfalls/feign/feign-client-duplication.md`
  - 检查: 是否已存在对应的 RemoteService
  - 违规示例: 在 mall-admin 中创建 `JobTypeFeignClient` 而非使用 `AgentRemoteService`

- [ ] **命名规范一致性检查**
  - 查询 repowiki: `.qoder/repowiki/pitfalls/naming/module-based-naming.md`
  - 检查: Feign 客户端是否遵循 `{Module}RemoteService` 格式
  - 违规示例: `JobTypeFeignClient` 应改为 `AgentRemoteService`

- [ ] **架构规范合规性检查**
  - 查询 repowiki: `.qoder/repowiki/pitfalls/architecture/facade-feign-prohibition.md`
  - 检查: 门面服务是否违规创建 `feign/` 目录
  - 违规示例: `mall-admin/src/main/java/com/aim/mall/admin/feign/` 目录存在

#### 6.2 Feign 客户端规范

- **命名规范**: Feign 客户端必须使用 `{Module}RemoteService` 命名格式
- **位置规范**: Feign 客户端必须定义在 `mall-{module}-api` 模块中
- **禁止重复**: 门面服务（mall-admin/mall-app/mall-chat）禁止创建 `feign/` 目录
- **引用规范**: 门面服务必须引用 API 模块中的 RemoteService，禁止自行创建

**检查示例**:
```java
// ✅ 正确: 使用 API 模块中的 RemoteService
@Service
@RequiredArgsConstructor
public class JobTypeAdminService {
    private final AgentRemoteService agentRemoteService;  // 来自 mall-agent-api
}

// ❌ 错误: 门面服务中创建 Feign 客户端
@FeignClient(name = "mall-agent", path = "/inner/api/v1/job-types")
public interface JobTypeFeignClient { ... }  // 禁止在 mall-admin 中创建
```

#### 6.3 命名规范检查

- **Controller 层**: 请求参数以 `Request` 结尾，返回以 `Response` 或 `VO` 结尾
- **Service 层**: 分页查询以 `PageQuery` 结尾，列表查询以 `ListQuery` 结尾，返回以 `DTO` 结尾
- **API 模块**: 远程请求以 `ApiRequest` 结尾，远程响应以 `ApiResponse` 结尾

#### 6.4 架构规范检查

- **分层架构**: 遵循 接口层 → 应用层 → 领域层 → 基础设施层
- **服务职责**: QueryService/ManageService/DomainService 职责划分清晰
- **调用关系**: 门面服务禁止相互调用，应用服务禁止调用门面服务

---

## 质量评估标准

| 指标 | 目标值 | 检测方法 |
|------|--------|----------|
| 圈复杂度 | ≤ 10 | 分析每个方法的圈复杂度 |
| 方法长度 | ≤ 50 行 | 统计每个方法的代码行数 |
| 类职责单一性 | - | 分析类的方法数量和功能相关性 |
| 依赖关系合理性 | - | 分析类之间的依赖关系是否符合分层架构 |

---

## 报告格式

### YAML 格式报告

```yaml
code_quality_analysis:
  req_id: "REQ-{timestamp}-{uuid}"
  feature_id: feature_001
  feature_name: "功能点名称"

  issues:
    - issue_type: duplication
      severity: high
      location: "类名.方法名:行号"
      description: "重复代码描述"
      suggestion: "改进建议"

    - issue_type: performance
      severity: high
      location: "类名.方法名:行号"
      description: "性能问题描述"
      solution: "优化方案"

  quality_metrics:
    - metric_name: "圈复杂度"
      current_value: 15
      target_value: 10
      improvement_needed: true

  knowledge_base_query:
    queries_executed:
      - query: "Feign Client existence check reuse api module"
        keywords: "Feign,RemoteService,existence,check,reuse"
        category: "common_pitfalls_experience"
        memories_found: 2
        relevant_rules:
          - "Feign Client Existence Check Rule"
          - "Facade service feign directory prohibition"
      - query: "Feign naming module RemoteService"
        keywords: "Feign,naming,module name,RemoteService"
        category: "development_code_specification"
        memories_found: 1
        relevant_rules:
          - "Feign Client Naming: Module-Based"
    applied_checks:
      - check: "Feign duplication check"
        source: "knowledge_base"
        status: passed
      - check: "Feign naming convention"
        source: "knowledge_base"
        status: passed

  code_specification_check:
    - category: "知识库历史坑点"
      source: "knowledge_base_query"
      checks:
        - item: "Feign 客户端重复创建"
          rule: "Feign Client Existence Check Rule"
          status: compliant
          violations: []
        - item: "命名规范一致性"
          rule: "Feign Client Naming: Module-Based"
          status: compliant
          violations: []
        - item: "架构规范合规性"
          rule: "Facade service feign directory prohibition"
          status: compliant
          violations: []
    - category: "Feign客户端规范"
      checks:
        - item: "命名格式 {Module}RemoteService"
          status: compliant
          violations: []
        - item: "位置在 API 模块中"
          status: compliant
          violations: []
        - item: "门面服务无 feign/ 目录"
          status: compliant
          violations: []
    - category: "命名规范"
      checks:
        - item: "Controller 参数 Request/Response 后缀"
          status: compliant
          violations: []
        - item: "Service 参数 Query/DTO 后缀"
          status: compliant
          violations: []
    - category: "架构规范"
      checks:
        - item: "分层架构遵循"
          status: compliant
          violations: []
        - item: "服务调用关系正确"
          status: compliant
          violations: []

  best_practices_checklist:
    - practice: "SOLID原则遵循"
      status: compliant
      violations: []
```

### Markdown 报告

```markdown
# 代码质量分析报告

## 概览

| 指标 | 当前值 | 目标值 | 状态 |
|------|--------|--------|------|
| 圈复杂度 | 15 | 10 | 需改进 |
| 方法平均长度 | 45 | 50 | 达标 |
| 重复代码率 | 5% | 5% | 达标 |

## 问题列表

### 高优先级

1. **重复代码** - AgentService.createAgent 和 updateAgent
   - 建议：提取公共方法

2. **代码规范违规** - 门面服务中创建了 Feign 客户端
   - 位置: `mall-admin/src/main/java/com/aim/mall/admin/feign/JobTypeFeignClient.java`
   - 问题: 门面服务禁止创建 feign/ 目录，应使用 mall-agent-api 中的 AgentRemoteService
   - 建议：删除 JobTypeFeignClient，注入 AgentRemoteService 使用

### 中优先级

3. **性能问题** - 循环中单条插入
   - 建议：使用批量插入

## 知识库查询结果

| 查询主题 | 关键词 | 记忆类别 | 找到规则数 | 应用检查数 |
|---------|--------|---------|-----------|-----------|
| Feign 客户端检查 | `Feign,RemoteService,existence,check,reuse` | common_pitfalls_experience | 2 | 3 |
| 命名规范检查 | `naming convention,Request,Response,DTO` | development_code_specification | 1 | 2 |

### 应用的历史规则

1. **Feign Client Existence Check Rule** (common_pitfalls_experience)
   - 规则: 创建 Feign 客户端前必须检查是否已存在 RemoteService
   - 应用检查: Feign 客户端重复创建检查

2. **Facade service feign directory prohibition** (development_code_specification)
   - 规则: 门面服务禁止创建 feign/ 目录
   - 应用检查: 门面服务架构合规性检查

3. **Feign Client Naming: Module-Based** (development_code_specification)
   - 规则: Feign 客户端必须使用 `{Module}RemoteService` 命名
   - 应用检查: Feign 命名规范一致性检查

## 代码规范检查详情

| 规范类别 | 检查项 | 来源 | 状态 | 违规数 |
|---------|--------|------|------|--------|
| 知识库历史坑点 | Feign 客户端重复创建 | knowledge_base | ✅ 通过 | 0 |
| 知识库历史坑点 | 命名规范一致性 | knowledge_base | ✅ 通过 | 0 |
| 知识库历史坑点 | 架构规范合规性 | knowledge_base | ❌ 失败 | 1 |
| Feign客户端规范 | 命名格式 {Module}RemoteService | architecture_standard | ✅ 通过 | 0 |
| Feign客户端规范 | 位置在 API 模块中 | architecture_standard | ❌ 失败 | 1 |
| Feign客户端规范 | 门面服务无 feign/ 目录 | architecture_standard | ❌ 失败 | 1 |
| 命名规范 | Controller 参数后缀 | naming_standard | ✅ 通过 | 0 |
| 命名规范 | Service 参数后缀 | naming_standard | ✅ 通过 | 0 |
| 架构规范 | 分层架构遵循 | architecture_standard | ✅ 通过 | 0 |

## 优化建议

...
```

---

## 检查清单

### 必需检查项

- [ ] 代码重复检测
- [ ] 性能问题识别
- [ ] SQL 性能专项检查
- [ ] 设计模式应用检查
- [ ] DDD 原则检查
- [ ] **知识库查询（历史坑点）**
  - [ ] 查询 Feign 相关历史经验
  - [ ] 查询命名规范相关规则
  - [ ] 查询架构规范相关规则
- [ ] **代码规范检查**
  - [ ] 基于知识库的历史坑点检查
  - [ ] Feign 客户端规范（命名、位置、禁止重复）
  - [ ] 命名规范（Request/Response/Query/DTO 后缀）
  - [ ] 架构规范（分层、服务职责、调用关系）
- [ ] 圈复杂度评估
- [ ] 方法长度评估

### 报告要求

- [ ] 生成 YAML 格式报告
- [ ] 包含量化指标
- [ ] 提供优化建议
- [ ] 包含实施计划

---

## 返回格式

```
状态：已完成
报告：orchestrator/PROGRAMS/{program_id}/workspace/code-quality-analysis.yaml
产出：N 个优化建议（含代码修改）
决策点：需要人工确认高风险重构吗？
```
