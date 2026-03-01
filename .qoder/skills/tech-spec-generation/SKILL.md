---
name: tech-spec-generation
description: Generate detailed technical specification documents with iterative refinement and bidirectional traceability. Use when user says "generate tech spec", "design SPEC", or after requirement clarification is complete to create technical design documentation.
---

# Technical Specification Generation Skill

> **Language Constraint**: All outputs from this Skill must be in Chinese, including technical specifications, OpenAPI definitions, acceptance checklists, and status reports.

Generate detailed technical specifications based on requirement decomposition and clarification results.

> **Prerequisite**: Must read `.qoder/rules/03-tech-spec-generation.md`, `.qoder/rules/04-coding-standards.md`, and `.qoder/rules/05-architecture-standards.md` before execution to obtain specification standards.

---

## Trigger Conditions

- User command: "generate technical specification" or "design SPEC"
- Current Program is Implementation type
- Requirement clarification completed (workspace/answers.md generated)

---

## Program Type

This Skill applies to **Implementation Program** (implementation type).

Example: Run this Skill in `P-2026-001-REQ-031` Program

---

## Inputs

- `orchestrator/PROGRAMS/{decomposition_program_id}/workspace/decomposition.md` — requirement decomposition document
- `orchestrator/PROGRAMS/{current_program_id}/workspace/answers.md` — requirement clarification results
- `orchestrator/PROGRAMS/{current_program_id}/workspace/decisions.md` — technical decision records
- `.qoder/rules/03-tech-spec-generation.md` — technical specification specification (**must read first**)
- `.qoder/rules/04-coding-standards.md` — coding standards (**must read first**)
- `.qoder/rules/05-architecture-standards.md` — architecture specification (**must read first**)
- Current Program's STATUS.yml — update phase status

---

## Outputs

- Technical specification → `orchestrator/PROGRAMS/{current_program_id}/workspace/tech-spec.md`
- OpenAPI definition → `orchestrator/PROGRAMS/{current_program_id}/workspace/openapi.yaml`
- Acceptance checklist → `orchestrator/PROGRAMS/{current_program_id}/workspace/checklist.md`
- Update STATUS.yml → phase changes from "technical specification" to "code generation"

---

## Workflow

### Step 1: Read Input Documents and Specifications

1. **Derive decomposition Program ID**
   - Current Program: `P-2026-001-REQ-031`
   - Decomposition Program: `P-2026-001-decomposition`

2. Read requirement decomposition document (decomposition.md)
   - Only extract parts related to current REQ

3. Read current Program's requirement clarification results (workspace/answers.md)
4. Read current Program's technical decision records (workspace/decisions.md)
5. **Must read** technical specification, coding standards, and architecture specifications
6. Check STATUS.yml to confirm in "technical specification" phase

### Step 2: Knowledge Base Query (Auto-reuse existing designs)

**Use knowledge-base-query Skill to collect existing system information:**

```
Query type: feature
Keywords: {current requirement feature keywords}

Purpose:
- Query similar function technical specifications
- Reference existing data model designs
- Reuse existing interface definition patterns
- Understand existing business rule implementations
```

**Query types and purposes:**

| Query Type | Purpose | Example |
|------------|---------|---------|
| feature | Similar function archives | Smart employee creation |
| spec | Technical specifications | Error code specification, architecture specification |
| api | Internal service APIs | user-service-api |
| schema | Database table structures | Existing user table fields |

**Query result processing**:
- If similar function has tech-spec.md → Reference its design, mark reusable parts
- If no similar function → Follow standard process for design

### Step 3: Analyze Dependencies and Context

Based on knowledge base query results, analyze:

- Tables to reuse
- Tables to add
- Reusable interface patterns
- Technical specifications to follow

### Step 4: Generate Technical Specification (Draft)

**Confirmation Checkpoint**: Before generating the draft, check for uncertain items:
- Data model field constraints not specified
- API pagination or threshold values unclear
- Technical solution choices (caching strategy, async processing, etc.)
- Third-party integration details missing

If any uncertainty exists, use Options-Based Inquiry to confirm with user.

Generate `tech-spec.md` according to specification format.

**Document structure reference**: "Technical Specification Structure Standard" chapter in `.qoder/rules/03-tech-spec-generation.md`

**Error code specification reference**: "Error Code Specification" chapter in Rule

**Reuse annotations**:
- Mark "Reference F-xxx function archive" at chapter start
- Add "Reused from F-xxx" for reused designs
- Clearly mark new/modified designs

### Step 5: User Review and Iteration

**Present technical specification to user:**

```
Technical specification generated: orchestrator/PROGRAMS/{current_program_id}/workspace/tech-spec.md

Main content preview:
├── Data Model: X tables (Y new, Z modified)
├── API Endpoints: N endpoints
├── Involved Services: mall-admin, mall-app, mall-agent
├── Reuse References: F-001, F-003 (if any)
└── Key Decisions: [List key design decisions]

Please review technical specification, if following situations exist please inform:
1. Need to supplement context (existing table structures, interface documents, etc.)
2. Design scheme needs adjustment
3. Missing necessary implementation details
```

#### 5.1 Handle User Feedback

**Scenario A: User supplements context**
- Read provided table structures/interface documents
- Update tech-spec.md
- Mark revision records

**Scenario B: User requests design scheme modification**
- Analyze change impact (use "Change Impact Analysis Matrix" in Rule)
- Determine if requirement documents need synchronous update
- Ask user for confirmation
- Update tech-spec.md and related requirement documents

**Scenario C: User requests supplement implementation details**
- Add corresponding chapters in tech-spec.md
- If involving business rule changes, determine if requirement documents need update

### Step 6: Generate Related Documents

After technical specification confirmed, generate:

1. **Acceptance Checklist** (`workspace/checklist.md`)
   - Organize acceptance items by sub-requirement

### Step 7: Establish Bidirectional Traceability

Establish requirement traceability matrix in tech-spec.md (format reference Rule).

---

## Return Format

```
Status: Completed / Needs Iteration
Report: orchestrator/PROGRAMS/{current_program_id}/workspace/tech-spec.md
Related Documents:
  - workspace/openapi.yaml
  - workspace/checklist.md

Program Status Update:
  - current_phase: code generation
  - phases.technical_specification.status: done

Output Changes:
  - Requirement document update: Yes/No
  - Updated file list: [xxx.md, yyy.md]

Decision Points:
  - Need to supplement context: Yes/No
  - Need to adjust design: Yes/No
  - Can proceed to code generation phase: Yes/No
```
