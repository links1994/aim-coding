# 输入文件说明

Inputs 目录存放开发任务的输入文件，主要是产品需求文档（PRD）。

---

## 目录结构

```
inputs/
├── README-INPUTS.md       # 本文件
└── prd/                   # 产品需求文档
    ├── ai-agent-platform-prd.md
    └── ai-agent-platform-wireframes.md
```

---

## PRD 文件

PRD（Product Requirements Document）是开发任务的输入源，Agent 根据 PRD 进行需求拆分和技术设计。

### PRD 格式建议

一个好的 PRD 应包含：

1. **概述**
   - 功能背景
   - 目标用户
   - 预期效果

2. **功能需求**
   - 功能列表
   - 用户故事
   - 验收标准

3. **非功能需求**
   - 性能要求
   - 安全要求
   - 兼容性要求

4. **界面设计**
   - 原型图/线框图
   - 交互说明

5. **附录**
   - 术语表
   - 参考资料

---

## 使用方式

### 方式一：直接输入

```
用户: "基于这个 PRD 实现用户系统 [粘贴 PRD 内容]"
Agent:
  → 解析 PRD 内容
  → 自动进入 Phase 1（需求拆分）
```

### 方式二：引用文件

```
用户: "基于 inputs/prd/ai-agent-platform-prd.md 进行需求拆分"
Agent:
  → 读取 PRD 文件
  → 自动进入 Phase 1
```

---

## 文件命名规范

建议按以下格式命名：

```
{系统名}-{功能模块}-prd.md
{系统名}-{功能模块}-wireframes.md
```

示例：
- `ai-agent-platform-prd.md`
- `ai-agent-platform-wireframes.md`
- `order-system-prd.md`

---

## 注意事项

1. **PRD 完整性**：PRD 越完整，需求拆分越准确
2. **及时更新**：如果需求变更，及时更新 PRD 文件
3. **版本管理**：重大变更时保留历史版本
