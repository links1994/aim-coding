# Program: P-2026-001-ai-agent-platform

## 基本信息

| 属性 | 值 |
|------|-----|
| **Program ID** | P-2026-001 |
| **名称** | AI智能体代理平台 |
| **来源** | PRD: ai-agent-platform-prd.md |
| **创建时间** | 2026-02-26 |

## 目标

建设AI智能体代理平台，将非标的"AI能力"转化为标准化的"岗位SOP"，允许用户申请成为"智能员工"，通过AI对话能力完成商品销售和客户咨询服务，并按销售业绩获取佣金收益。

## 需求范围

### 本次迭代包含

1. **APP用户管理端** - 智能员工申请、解锁、管理
2. **APP用户使用端** - AI导购对话、评分举报
3. **智能体代理平台-后台** - 岗位/名额/审核/管理/内容审核
4. **智能对话引擎** - 话术匹配、知识库检索、LLM生成

### 后续迭代

- 商城主页-客服入口
- 更多岗位类型

## 核心业务流程

```
申请员工 → 运营审核 → 邀请解锁 → 生成智能体 → 员工上线 → AI对话服务 → 用户下单 → 佣金结算T+7
```

## 涉及服务

- **mall-admin**: 管理后台门面服务
- **mall-app**: 客户端门面服务  
- **mall-chat**: AI对话门面服务
- **mall-agent**: 智能员工核心业务服务
- **mall-user**: 用户支撑服务

## 关键数据表

- `aim_employee` - 智能员工表
- `aim_job_type` - 岗位类型表
- `aim_employee_script` - 话术表
- `aim_knowledge_file` - 知识库文件表
- `aim_conversation` - 对话记录表
- `aim_style_config` - 人设风格配置表
- `aim_agent_product` - 代理商品配置表
- `aim_category_mapping` - 类目映射表
- `aim_quota_config` - 名额配置表
- `aim_activation_record` - 激活记录表

## 文档索引

- PRD: `inputs/prd/ai-agent-platform-prd.md`
- 线框图: `inputs/prd/ai-agent-platform-wireframes.md`
- 需求拆分: `workspace/decomposition.md`
