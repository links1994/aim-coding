---
name: knowledge-base-query
description: Query local knowledge base (repowiki) for feature archives, technical specifications, API docs, and framework docs. Use when starting new feature development, need to find similar implementations, understand existing designs, or query technical specifications. Supports feature retrieval, spec lookup, API docs, and pitfall avoidance.
---

# Knowledge Base Query Skill

Unified query of all content in local knowledge base (repowiki), supporting feature archive retrieval, technical specification queries, API document lookup, framework document search, etc.

---

## Trigger Conditions

- Before starting new feature development, need to find similar functions or reference existing implementations
- Need to understand implementation details or technical decisions of a function
- Need to query technical specifications, coding standards, error code specifications, etc.
- Need to consult framework docs, API docs, third-party docs
- Network unavailable, need to query offline technical documents
- User explicit command: "query knowledge base", "search documents", "find function" or "delegate: knowledge base query"
- When uncertain about technical details, prioritize using this Skill to search

---

## Inputs

- Query keywords: `{keyword}` or `{feature-name}` or `{doc-topic}`
- Query type (optional): `feature` | `spec` | `api` | `schema` | `pitfall` | `all`
- Target path (optional): Subdirectory under `.qoder/repowiki/`

---

## Outputs

- Query result report: `orchestrator/PROGRAMS/{current_program}/workspace/kb-query-result.md`
- Matched function lists, document fragments, technical specifications, etc.
- Reuse suggestions and related reference links

---

## Knowledge Base Structure

```
.qoder/repowiki/
├── features/              # Feature archives
│   ├── index.md          # Feature index
│   ├── F-001-xxx/        # Specific feature archives
│   └── F-002-xxx/
├── pitfalls/             # Historical pitfall archives
│   ├── index.md          # Pitfall index
│   ├── feign/            # Feign related pitfalls
│   │   ├── feign-client-duplication.md
│   │   └── facade-feign-prohibition.md
│   ├── naming/           # Naming convention pitfalls
│   │   └── module-based-naming.md
│   ├── architecture/     # Architecture specification pitfalls
│   │   └── service-layer-violation.md
│   └── maven/            # Maven dependency pitfalls
│       └── dependency-conflict.md
├── schemas/              # Database table structure archives
│   ├── index.md          # Table index
│   ├── mall-user/        # Organized by service
│   │   ├── tb_user.md
│   │   └── _service-overview.md
│   └── mall-order/
│       ├── tb_order.md
│       └── _service-overview.md
├── apis/                 # API documents
│   ├── index.md          # API index
│   ├── internal/         # Internal service APIs (Feign interfaces)
│   │   ├── user-service-api.md
│   │   └── order-service-api.md
│   └── third-party/      # Third-party APIs
│       ├── wechat-pay-api.md
│       └── alipay-api.md
├── zh/docs/              # Chinese documents
│   ├── specs/            # Technical specifications
│   │   ├── error-code-spec.md
│   │   ├── architecture-spec.md
│   │   └── coding-spec.md
│   └── guides/           # Operation guides
│       ├── deployment/
│       └── operations/
└── en/docs/              # English documents (same structure)
```

---

## Query Types

### Type 1: Feature Retrieval (feature)

Query archived feature archives, support reusing existing function designs.

**Applicable Scenarios**:
- Find similar implementations before new feature development
- Understand interface design and business logic of a function
- Reference code structure of existing functions

**Input Example**:
```
Keywords: "create employee"
Type: feature
```

**Output Example**:
```
Match Results:
- F-001-create-agent: Smart employee creation (relevance: 95%)
- F-003-create-order: Order creation (relevance: 80%)
```

### Type 2: Technical Specification Query (spec)

Query technical specification documents, including error code specifications, architecture specifications, coding specifications, etc.

**Applicable Scenarios**:
- Reference specifications when generating technical specifications
- Determine error code formats
- Understand architecture design principles

**Input Example**:
```
Keywords: "error code"
Type: spec
```

**Output Example**:
```
Match Results:
- error-code-spec.md: SSMMTNNN format definition
- architecture-spec.md: Four-layer architecture design
```

### Type 3: API Document Query (api)

Query internal service APIs or third-party API documents.

**Applicable Scenarios**:
- Need to call interfaces of other services
- Understand Feign interface definitions
- Consult third-party SDK usage

**Input Example**:
```
Keywords: "user service"
Type: api
```

**Output Example**:
```
Match Results:
- user-service-api.md: User service interface definitions
- agent-service-api.md: Smart employee service interface
```

### Type 4: Historical Pitfall Query (pitfall)

Query historical pitfall archives to avoid repeating mistakes.

**Applicable Scenarios**:
- Identify known traps during code quality analysis
- Understand common error patterns before development
- Check potential issues during code review

**Target Directory**: `.qoder/repowiki/pitfalls/`

**Input Example**:
```
Keywords: "Feign client duplicate creation"
Type: pitfall
Target path: .qoder/repowiki/pitfalls/feign/
```

**Output Example**:
```
Match Results:
- feign-client-duplication.md: Feign client duplicate creation pitfall
- facade-feign-prohibition.md: Facade service prohibited from creating feign directory
```

### Type 5: Comprehensive Query (all)

Query across all types, return most relevant results.

**Applicable Scenarios**:
- Uncertain which category content belongs to
- Need comprehensive understanding of a topic
- Initial research phase

### Type 6: Database Table Structure Query (schema)

Query database table structure archives, support understanding existing table designs during legacy project transformation.

**Applicable Scenarios**:
- Legacy project transformation needs to understand existing table structures
- New feature development needs to extend fields based on old tables
- Avoid field naming conflicts
- Understand table relationships and index designs

**Target Directory**: `.qoder/repowiki/schemas/`

**Input Example**:
```
Keywords: "tb_user"
Type: schema
Target path: .qoder/repowiki/schemas/mall-user/
```

**Output Example**:
```
Match Results:
- tb_user.md: User main table structure, contains 15 fields, 3 indexes
- tb_user_role.md: User role association table
- tb_user_address.md: User address table

Field Information:
- id: BIGINT, PK, auto-increment
- username: VARCHAR(64), unique index
- phone: VARCHAR(20), sensitive field, AES encrypted
```

---

## Workflow

### Step 1: Parse Query Intent

Analyze user input, determine query type and target:

```
Input: "Query error code specification"
Analysis:
  - Keywords: error code specification
  - Type: spec (technical specification)
  - Target file: .qoder/repowiki/docs/specs/error-code-spec.md
```

```
Input: "Is there an order creation function?"
Analysis:
  - Keywords: create order
  - Type: feature (feature retrieval)
  - Target index: .qoder/repowiki/features/index.md
```

### Step 2: Execute Query

Execute corresponding searches based on query type:

#### Feature Retrieval Process

```
1. Read .qoder/repowiki/features/index.md
2. Match function names, descriptions, tags
3. Sort by relevance
4. Read matched function archive files
```

#### Document Query Process

```
1. Determine query range (specs/ | apis/ | guides/)
2. Traverse all documents in directory
3. Match filenames and content
4. Extract most relevant fragments
```

#### Historical Pitfall Query Process

```
1. Determine query range: .qoder/repowiki/pitfalls/{category}/
2. Traverse pitfall archive files
3. Match pitfall patterns and keywords
4. Extract pitfall descriptions, violation examples, solutions
5. Return to code quality analysis Skill for checking
```

**Pitfall Archive Format**:
```markdown
# Pitfall Title

## Problem Description
Briefly describe what this pitfall is

## Violation Pattern
Code characteristics or patterns for automatic detection

## Violation Example
Incorrect code example

## Correct Solution
Correct code example

## Related Specifications
- Architecture specification: xxx
- Coding specification: xxx

## Archive Info
- Discovery time: 2026-02-27
- Discoverer: Qoder
- Related Program: P-2026-001-REQ-018
```

#### Database Table Structure Query Process

```
1. Determine query range: .qoder/repowiki/schemas/{service}/
2. Traverse table structure archive files
3. Match table names, field names, comments
4. Extract table basic info, field lists, index info
5. Return to code generation Skill for legacy table transformation
```

**Table Structure Archive Format**:
```markdown
---
table_name: tb_user
description: User main table, stores user basic information
database: mall_user
service: mall-user
---

# tb_user

## Basic Info
...

## Field List
| Field Name | Data Type | Constraint | Comment |
|------------|-----------|------------|---------|
| id | BIGINT | PK | Primary key ID |
| username | VARCHAR(64) | UK | Username |

## Index Info
...

## Foreign Key Relations
...
```

### Step 3: Generate Query Report

```markdown
# Knowledge Base Query Results

## Query Info

- Query keywords: {keywords}
- Query type: {type}
- Query time: {timestamp}
- Match count: {count}

## Match Results

### Result 1: [Title]

**Type**: feature | spec | api | framework | guide
**Source**: {file-path}
**Relevance**: {score}%

**Content Summary**:
{extracted content}

**Reuse Suggestion**:
{based on type, provide reuse suggestions}

---

### Result 2: [Title]

...

## Query Summary

- Found {count} related results
- Suggest priority view: [highest relevance result]
- Next step action suggestion: [action suggestions]
```

---

## Usage Scenarios

### Scenario 1: Pre-development Feature Retrieval

```
User: "I want to implement job type management function, is there something similar?"

Agent:
  → Call knowledge-base-query Skill
  → Type: feature
  → Keywords: "job type management"
  → Return matched function archives
  → Suggest referencing most relevant function design
```

### Scenario 2: Technical Specification Query

```
User: "How should error codes be defined?"

Agent:
  → Call knowledge-base-query Skill
  → Type: spec
  → Keywords: "error code specification"
  → Return error code specification document
  → Extract SSMMTNNN format requirements
```

### Scenario 3: API Interface Query

```
User: "What Feign interfaces does user service have?"

Agent:
  → Call knowledge-base-query Skill
  → Type: api
  → Keywords: "user service"
  → Return API documents and interface lists
```

### Scenario 4: Framework Usage Query

```
User: "How to implement mybatis-plus pagination query?"

Agent:
  → Call knowledge-base-query Skill
  → Type: framework
  → Keywords: "mybatis-plus pagination"
  → Return framework documents and example code
```

### Scenario 5: Uncertain Content Comprehensive Query

```
User: "How to design job type error codes?"

Agent:
  → Call knowledge-base-query Skill
  → Type: all
  → Keywords: "job type error code"
  → Search across all types
  → Return function archives + error code specifications + related APIs
```

### Scenario 6: Legacy Project Transformation Table Structure Query

```
User: "Need to add new fields to user table, what's the existing table structure?"

Agent:
  → Call knowledge-base-query Skill
  → Type: schema
  → Keywords: "tb_user"
  → Target path: .qoder/repowiki/schemas/mall-user/
  → Return tb_user table structure archive
  → Extract field list: existing 15 fields, avoid naming conflicts
  → Extract index info: assess performance impact of new fields
  → Extract foreign key relations: understand table dependencies
  → Generate new code based on archive
```

### Scenario 7: New Feature Depends on Old Table

```
User: "New feature needs to use order table, query order table structure"

Agent:
  → Call knowledge-base-query Skill
  → Type: schema
  → Keywords: "tb_order"
  → Return table structure archive
  → Understand available fields and field meanings
  → Confirm related tables (e.g., tb_order_item)
  → Generate new code based on existing design
```

---

## Collaboration with Other Skills

### Collaboration with Requirement Clarification

```
Requirement Clarification Phase:
  → Query similar function clarification records
  → Reference existing decision records
  → Avoid repeated questions
```

### Collaboration with Technical Specification Generation

```
When generating technical specifications:
  → Query error code specifications
  → Query architecture specifications
  → Reference similar function interface designs
```

### Collaboration with Code Generation

```
Code generation phase:
  → Query function archives as templates
  → Query old table structures (schema) - transformation projects
  → Query API documents to understand call methods (Feign interfaces, third-party APIs)
```

### Collaboration with Database Archiving

```
Legacy table transformation scenarios:
  → Query table structure archives (schema)
  → Get field lists, avoid naming conflicts
  → Understand index situations, assess performance impact
  → Confirm foreign key relations, avoid breaking constraints
  → Generate new code based on archives

New feature depends on old table:
  → Query table structure to understand available fields
  → Query related tables to understand data relationships
  → Generate code based on existing design
```

---

## Return Format

```
Status: Completed
Report: orchestrator/PROGRAMS/{program_id}/workspace/kb-query-result.md
Match Count: {count}

High Priority Results:
1. [Type] {title} (Relevance: {score}%)
   Path: {path}
   Summary: {brief}

Medium Priority Results:
...

Suggestions:
- To view full content, please specify result number
- To further filter, please provide more specific keywords
```

---

## Related Documents

- **Feature Archiving Skill**: `.qoder/skills/feature-archiving/skill.md`
- **Pitfall Archiving Skill**: `.qoder/skills/pitfalls-archiving/skill.md`
- **Specification Archiving Skill**: `.qoder/skills/spec-archiving/skill.md`
- **Database Archiving Skill**: `.qoder/skills/database-schema-archiving/skill.md`
- **API Archiving Skill**: `.qoder/skills/api-archiving/skill.md`
- **Requirement Clarification Skill**: `.qoder/skills/requirement-clarification/skill.md`
- **Technical Specification Generation Skill**: `.qoder/skills/tech-spec-generation/skill.md`
- **Code Generation Skill**: `.qoder/skills/java-code-generation/skill.md`
