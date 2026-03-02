---
name: code-quality-analysis
description: Analyze code quality and identify issues. Use when code generation is complete, before code review, or when user wants to check code against project standards and best practices.
---

# Code Quality Analysis Skill

Analyze generated code for quality issues, standard compliance, design principles, design patterns, and potential improvements.

---

## Trigger Conditions

- Code generation completed
- Before code review
- User command: "analyze code quality" or "check code standards"
- Need to validate code against project standards

---

## Inputs

- Generated source code in `repos/`
- `.qoder/rules/04-coding-standards.md` — coding standards
- `.qoder/rules/05-architecture-standards.md` — architecture standards
- Pitfall archives from `.qoder/repowiki/pitfalls/`

---

## Outputs

- Quality report → `orchestrator/PROGRAMS/{program_id}/workspace/quality-report.md`
- Issue list with severity levels
- Fix suggestions

---

## Analysis Categories

### 1. Naming Conventions

Check against project naming standards:
- Class names
- Method names
- Variable names
- Package structure

### 2. Architecture Compliance

Verify layered architecture:
- Controller layer responsibilities
- Service layer usage
- Mapper/Repository patterns
- DTO/VO separation

### 3. Common Pitfalls

Query pitfall archives and check:
- Feign client issues
- Transaction boundaries
- Null pointer risks
- Performance anti-patterns

### 4. Code Smells

Identify common code smells:
- Duplicate code
- Long methods
- Large classes
- Deep nesting

### 5. 设计原则适用性检查

> **注意**：设计原则是**指导性**的，应根据实际场景判断是否适用。简单场景不需要强行遵守所有原则，过度设计反而会增加复杂度。

检查设计原则是否**适合**当前场景：

#### 5.1 SOLID Principles

##### 5.1.1 Single Responsibility Principle (SRP) - 单一职责原则
- **适用场景**: 类变得臃肿、难以维护时
- **检查点**: 一个类是否只负责一个职责
- **问题信号**: 
  - 类包含多个不相关的功能模块
  - 方法操作多个不同类型的数据
  - 类需要频繁修改的原因不止一个
- **示例**:
  ```java
  // 建议考虑拆分：同时处理订单业务逻辑和数据导出
  public class OrderService {
      public void createOrder() { ... }
      public void exportOrderToExcel() { ... }  // 可考虑分离到 OrderExportService
  }
  ```

##### 5.1.2 Open/Closed Principle (OCP) - 开闭原则
- **适用场景**: 需要频繁扩展新类型时
- **检查点**: 对扩展开放，对修改关闭
- **问题信号**:
  - 使用大量 if-else/switch 处理类型判断
  - 新增功能需要修改现有类
- **示例**:
  ```java
  // 如果类型经常新增，可考虑使用策略模式
  public void processOrder(Order order) {
      if (order.getType() == 1) { ... }
      else if (order.getType() == 2) { ... }
      // 如果类型很少变化，简单 if-else 也可以接受
  }
  ```

##### 5.1.3 Liskov Substitution Principle (LSP) - 里氏替换原则
- **适用场景**: 使用继承时
- **检查点**: 子类应该能够替换父类而不影响程序正确性
- **问题信号**:
  - 子类重写方法后抛出 UnsupportedOperationException
  - 子类前置条件比父类更严格
  - 子类后置条件比父类更宽松
- **示例**:
  ```java
  // 建议避免：Square 不是 Rectangle 的合适子类
  public class Rectangle {
      public void setWidth(int w) { ... }
      public void setHeight(int h) { ... }
  }
  public class Square extends Rectangle {
      @Override
      public void setWidth(int w) { setWidth(w); setHeight(w); }
  }
  ```

##### 5.1.4 Interface Segregation Principle (ISP) - 接口隔离原则
- **适用场景**: 接口变得臃肿时
- **检查点**: 客户端不应该依赖它不需要的接口
- **问题信号**:
  - 接口包含大量方法
  - 实现类中有空实现或抛出异常的方法
  - 一个接口服务多个不相关的客户端
- **示例**:
  ```java
  // 建议拆分：RobotWorker 不需要 eat 方法
  public interface Worker {
      void work();
      void eat();  // 可考虑拆分到单独的 Eatable 接口
  }
  ```

##### 5.1.5 Dependency Inversion Principle (DIP) - 依赖倒置原则
- **适用场景**: 需要提高代码可测试性、可替换性时
- **检查点**: 依赖抽象而不是具体实现
- **问题信号**:
  - 直接 new 具体实现类
  - 高层模块直接依赖低层模块的具体类
  - 没有使用接口或抽象类
- **示例**:
  ```java
  // 建议改进：依赖接口而非具体实现
  public class OrderService {
      private MySQLOrderRepository repository = new MySQLOrderRepository();  // 可考虑依赖接口
  }
  ```

#### 5.2 Law of Demeter (LoD) - 迪米特法则 / 最小知识原则

- **适用场景**: 链式调用过多、对象耦合度高时
- **核心原则**: 一个对象应该对其他对象有最少的了解（只与直接朋友通信）
- **检查点**:
  - 是否出现链式调用（a.getB().getC().doSomething()）
  - 方法参数是否包含不需要的对象
  - 是否直接操作其他对象的内部结构

- **问题信号**:
  ```java
  // 建议改进：链式调用暴露了内部结构
  public void processOrder(Order order) {
      Customer customer = order.getCustomer();
      Address address = customer.getAddress();
      String city = address.getCity();  // 深入访问了多层对象
  }
  
  // 改进方案：封装在 Order 内部
  public void processOrder(Order order) {
      String city = order.getCustomerCity();  // Order 提供方法封装内部访问
  }
  ```

#### 5.3 DRY Principle - 不要重复自己原则

- **适用场景**: 发现重复代码时（**强烈建议遵守**）
- **核心原则**: 系统中每一处知识/逻辑都应该有单一、明确、权威的表示
- **检查点**:
  - 是否存在重复代码块（Copy-Paste 代码）
  - 相似逻辑是否提取为公共方法
  - 魔法值/字符串是否定义为常量
  - 配置信息是否分散在多处

- **问题信号**:
  ```java
  // 建议改进：重复的条件判断逻辑
  public void processOrder(Order order) {
      if (order.getStatus() == 1 && order.getAmount() > 100) { ... }
  }
  
  public void cancelOrder(Order order) {
      if (order.getStatus() == 1 && order.getAmount() > 100) { ... }  // 重复逻辑
  }
  
  // 改进方案：提取为方法
  private boolean isValidOrder(Order order) {
      return order.getStatus() == 1 && order.getAmount() > 100;
  }
  ```

#### 5.4 KISS Principle - 保持简单原则

- **适用场景**: 代码变得复杂难懂时（**强烈建议遵守**）
- **核心原则**: 保持代码简单明了，避免过度设计
- **检查点**:
  - 是否存在过度复杂的抽象层次
  - 简单问题是否使用了复杂方案
  - 是否存在不必要的间接层
  - 代码是否难以理解和维护

- **问题信号**:
  ```java
  // 建议简化：过度设计
  public interface IOrderProcessor { ... }
  public abstract class AbstractOrderProcessor implements IOrderProcessor { ... }
  public class OrderProcessorFactory { ... }
  public class OrderProcessorStrategyContext { ... }
  // 实际上只需要一个简单的 OrderService
  
  // 改进方案：简单直接
  public class OrderService {
      public void processOrder(Order order) { ... }
  }
  ```

#### 5.5 YAGNI Principle - 你不会需要它原则

- **适用场景**: 预留功能过多时（**强烈建议遵守**）
- **核心原则**: 不要实现当前不需要的功能
- **检查点**:
  - 是否存在预留但未使用的接口/方法
  - 是否实现了"未来可能用到"的功能
  - 配置项是否过度灵活
  - 是否存在过度泛化的抽象

- **问题信号**:
  ```java
  // 建议移除：预留了大量不需要的功能
  public class OrderService {
      public void createOrder() { ... }
      public void createOrderAsync() { ... }  // 目前不需要异步
      public void createOrderBatch() { ... }  // 目前不需要批量
      public void createOrderWithWorkflow() { ... }  // 目前不需要工作流
  }
  ```

#### 5.6 Composition Over Inheritance - 组合优于继承

- **适用场景**: 考虑使用继承时
- **核心原则**: 优先使用组合来复用代码，而非继承
- **检查点**:
  - 继承层次是否过深（超过3层）
  - 子类是否只使用了父类的部分功能
  - 是否存在"is-a"关系的滥用
  - 继承是否导致了紧耦合

- **问题信号**:
  ```java
  // 建议改进：Stack 不是 Vector
  public class Stack extends Vector { ... }  // Stack 应该组合 Vector
  
  // 改进方案：使用组合
  public class Stack {
      private Vector elements = new Vector();  // 组合
      public void push(Object e) { elements.add(e); }
      public Object pop() { ... }
  }
  ```

#### 5.7 Program to Interface - 面向接口编程

- **适用场景**: 需要提高代码灵活性时
- **核心原则**: 依赖抽象接口而非具体实现
- **检查点**:
  - 变量声明是否使用具体类而非接口
  - 方法参数是否使用具体类
  - 返回值是否暴露了内部实现
  - 集合类型是否使用具体实现类声明

- **问题信号**:
  ```java
  // 建议改进：依赖具体实现
  public class OrderService {
      private ArrayList<Order> orders = new ArrayList<>();  // 建议使用 List
      
      public HashMap<String, Order> getOrderMap() { ... }  // 建议返回 Map
  }
  
  // 改进方案：面向接口编程
  public class OrderService {
      private List<Order> orders = new ArrayList<>();
      
      public Map<String, Order> getOrderMap() { ... }
  }
  ```

#### 5.8 Fail Fast - 快速失败原则

- **适用场景**: 需要提高代码健壮性时（**强烈建议遵守**）
- **核心原则**: 问题应该尽早暴露，不要隐藏错误
- **检查点**:
  - 参数校验是否在入口处完成
  - 空指针是否被静默处理
  - 异常是否被吞掉或延迟抛出
  - 无效状态是否被允许继续传播

- **问题信号**:
  ```java
  // 建议改进：延迟发现问题
  public void processOrder(Order order) {
      // 没有前置校验
      saveToDatabase(order);  // 可能在数据库层才报错
  }
  
  // 改进方案：尽早校验
  public void processOrder(Order order) {
      if (order == null) throw new IllegalArgumentException("Order cannot be null");
      if (order.getItems().isEmpty()) throw new ValidationException("Order must have items");
      saveToDatabase(order);
  }
  ```

#### 5.9 Separation of Concerns (SoC) - 关注点分离原则

- **核心原则**: 将程序分解为不同的部分，每个部分处理单独的关注点
- **检查点**:
  - 业务逻辑是否与数据访问混合
  - 表现层是否包含业务逻辑
  - 横切关注点（日志、事务、安全）是否侵入业务代码
  - 配置文件是否包含业务逻辑

- **问题信号**:
  ```java
  // ❌ 违反SoC: 混合了多个关注点
  public class OrderController {
      @PostMapping("/orders")
      public Response createOrder(@RequestBody OrderRequest request) {
          // 1. 参数校验
          // 2. 业务逻辑
          // 3. 数据库操作
          // 4. 发送通知
          // 5. 记录日志
          // 6. 返回响应
      }
  }
  
  // ✅ 正确做法: 关注点分离
  public class OrderController {
      @PostMapping("/orders")
      public Response createOrder(@Valid @RequestBody OrderRequest request) {
          return orderService.createOrder(request);  // 只负责协调
      }
  }
  ```

### 6. 设计模式适用性检查

> **注意**：设计模式是**建议性**的，应根据实际场景判断是否适用。简单场景不需要强行使用设计模式，过度设计反而会增加复杂度。

检查是否**适合**使用设计模式，以及是否存在可以考虑使用设计模式的场景：

#### 6.1 创建型模式

| 模式 | 适用场景 | 检查点 |
|------|----------|--------|
| **Factory Method** | 需要创建多种类型的对象，但类型在运行时确定 | 是否存在大量 if-else 创建不同对象 |
| **Abstract Factory** | 需要创建相关的产品族 | 是否存在多组相关的对象创建 |
| **Builder** | 复杂对象的创建，参数较多 | 构造函数参数是否超过4个 |
| **Singleton** | 全局唯一实例（谨慎使用） | 配置类、工具类是否重复实例化 |
| **Prototype** | 创建成本高的对象需要复制 | 是否存在深度拷贝需求 |

#### 6.2 结构型模式

| 模式 | 适用场景 | 检查点 |
|------|----------|--------|
| **Adapter** | 接口不兼容需要转换 | 是否存在接口适配的胶水代码 |
| **Bridge** | 抽象和实现需要独立变化 | 类层次结构是否过于复杂 |
| **Composite** | 树形结构，统一处理整体和部分 | 是否存在树形数据结构的递归处理 |
| **Decorator** | 动态添加职责 | 是否使用继承扩展功能 |
| **Facade** | 简化复杂子系统的接口 | 复杂业务调用是否缺少统一入口 |
| **Flyweight** | 大量细粒度对象共享 | 是否存在大量重复对象 |
| **Proxy** | 控制访问、延迟加载 | 是否需要权限控制或缓存 |

#### 6.3 行为型模式

| 模式 | 适用场景 | 检查点 |
|------|----------|--------|
| **Strategy** | 多种算法可互换 | 是否存在大量 if-else 处理不同策略 |
| **Template Method** | 算法骨架固定，步骤可变 | 是否存在重复的算法结构 |
| **Observer** | 一对多依赖，状态变化通知 | 是否需要事件通知机制 |
| **Command** | 请求参数化、队列化 | 是否需要撤销/重做功能 |
| **Iterator** | 遍历聚合对象 | 自定义集合是否提供遍历方式 |
| **State** | 状态决定行为 | 是否存在复杂的状态转换逻辑 |
| **Chain of Responsibility** | 多个对象有机会处理请求 | 是否需要职责链处理请求 |

#### 6.4 设计模式使用建议

**当发现以下代码特征时，可以**考虑**使用对应的设计模式（视场景复杂度而定）：**

```java
// 可以考虑使用 Strategy 模式（如果策略较多且经常变化）
public void calculatePrice(Order order, String customerType) {
    if (customerType.equals("VIP")) { ... }
    else if (customerType.equals("NORMAL")) { ... }
    else if (customerType.equals("EMPLOYEE")) { ... }
}
// 注：如果只有 2-3 种简单策略，if-else 可能更直观

// 可以考虑使用 Builder 模式（如果参数多且部分可选）
public Order(String customerName, String address, String phone, 
             LocalDateTime createTime, BigDecimal amount, 
             List<Item> items, String remark) { ... }  // 参数过多
// 注：如果参数都是必填的，可考虑使用 DTO 或工厂方法

// 可以考虑使用 Factory 模式（如果创建逻辑复杂）
public PaymentProcessor createProcessor(String type) {
    if (type.equals("ALIPAY")) return new AlipayProcessor();
    if (type.equals("WECHAT")) return new WechatProcessor();
    return null;
}
// 注：简单场景下，简单的工厂方法或依赖注入可能更合适

// 可以考虑使用 Template Method 模式（如果算法骨架确实重复）
public void processOrder(Order order) {
    validate(order);      // 固定步骤
    // 不同实现
    save(order);          // 固定步骤
}

public void processRefund(Refund refund) {
    validate(refund);     // 相同的固定步骤
    // 不同实现
    save(refund);         // 相同的固定步骤
}
// 注：如果只有 2 个方法有相似结构，提取公共方法可能更简单
```

**设计模式使用原则**：
1. **简单优先**：能用简单方案解决的，不要引入设计模式
2. **按需使用**：当代码确实出现扩展性、维护性问题时，再考虑使用
3. **避免过度设计**：不要为了使用设计模式而使用设计模式
4. **考虑团队熟悉度**：选择团队熟悉且易于维护的方案

---

## Workflow

### Step 1: Read Standards and Pitfalls

1. Read coding standards
2. Read architecture standards
3. Query pitfall archives using knowledge-base-query Skill

### Step 2: Scan Source Code

Read generated source files:
- Controllers
- Services
- Mappers
- DTOs/Entities

### Step 3: Execute Checks

For each file, run quality checks:
1. Naming convention check
2. Architecture pattern check
3. Pitfall pattern matching
4. Code smell detection
5. **Design Principles check**（根据场景判断是否适用）
   - SOLID Principles (SRP, OCP, LSP, ISP, DIP)
   - Law of Demeter (LoD)
   - DRY Principle（强烈建议）
   - KISS Principle（强烈建议）
   - YAGNI Principle（强烈建议）
   - Composition Over Inheritance
   - Program to Interface
   - Fail Fast（强烈建议）
   - Separation of Concerns (SoC)
6. **Design patterns check**（根据场景判断是否适用）
   - Identify pattern opportunities
   - Verify appropriate pattern usage
   - Check for anti-patterns

### Step 4: Generate Report

```markdown
# Code Quality Report

## Summary

| Category | Issues | Critical | Warning | Info |
|----------|--------|----------|---------|------|
| Naming | 2 | 0 | 1 | 1 |
| Architecture | 1 | 0 | 1 | 0 |
| Pitfalls | 0 | 0 | 0 | 0 |
| Design Principles | 5 | 0 | 3 | 2 |
| Design Patterns | 1 | 0 | 0 | 1 |

## Detailed Issues

### Issue 1: Naming Convention
- **File**: XxxController.java
- **Line**: 45
- **Severity**: Warning
- **Description**: Method name should start with lowercase
- **Suggestion**: Rename to `getJobTypeList`

### Issue 2: Design Principle - SRP Violation
- **File**: OrderService.java
- **Line**: 25-80
- **Severity**: Warning
- **Description**: Class has multiple responsibilities: order processing and Excel export
- **Suggestion**: Extract Excel export logic to OrderExportService

### Issue 3: Design Principle - DRY Violation
- **File**: OrderValidator.java
- **Line**: 30-45, 60-75
- **Severity**: Warning
- **Description**: Duplicate validation logic in multiple methods
- **Suggestion**: Extract common validation to a private method

### Issue 4: Design Pattern Opportunity
- **File**: PaymentService.java
- **Line**: 40-60
- **Severity**: Info
- **Description**: Multiple if-else for payment type handling
- **Suggestion**: Consider using Strategy pattern for different payment processors

## Recommendations

1. Fix naming issues
2. Review architecture compliance
3. Refactor classes violating SRP
4. Consider applying appropriate design patterns

---

## Return Format

```
Status: Completed / Issues Found
Report: workspace/quality-report.md
Issues: X total (Y critical, Z warning, W info)
  - Naming: X issues
  - Architecture: X issues
  - Pitfalls: X issues
  - Design Principles: X issues (SOLID, DRY, KISS, etc.)
  - Design Patterns: X issues

Recommendations:
- Fix critical issues before review
- Address warnings in next iteration
- 评估设计原则的适用性，简单场景不需要强行遵守
- 设计模式按需使用，避免过度设计
```
