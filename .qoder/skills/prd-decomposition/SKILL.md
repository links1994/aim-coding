---
name: prd-decomposition
description: Decompose PRD into implementable sub-requirements. Use when user says "decompose PRD", "split requirements", or needs to break down product requirements into REQ items with service归属, dependencies, and acceptance criteria.
---

# PRD Decomposition Skill

Decompose Product Requirement Documents (PRD) into implementable sub-requirements.

> **Prerequisite**: Must read `.qoder/rules/01-prd-decomposition.md` before execution to obtain specification standards.

---

## Trigger Conditions

- User command: "decompose PRD" or "split requirements"
- New Program created for decomposition type
- Need to break down PRD into REQ items

---

## Inputs

- PRD document path (e.g., `inputs/prd/ai-agent-platform-prd.md`)
- Wireframe document (optional, e.g., `inputs/prd/ai-agent-platform-wireframes.md`)
- `.qoder/rules/01-prd-decomposition.md` — decomposition specification (**must read first**)
- Current Program's STATUS.yml

---

## Outputs

- Decomposition document → `orchestrator/PROGRAMS/{program_id}/workspace/decomposition.md`
- Update STATUS.yml → mark decomposition complete

---

## Workflow

### Step 1: Read PRD and Specifications

1. Read PRD document
2. Read wireframe document (if exists)
3. **Must read** decomposition specification (01-prd-decomposition.md)
4. Check STATUS.yml

### Step 2: Analyze PRD Structure

Analyze PRD to identify:
- Functional modules
- User roles and scenarios
- Business processes
- Interface requirements

**Confirmation Checkpoint**: If PRD contains ambiguous requirements, missing business rules, or unclear scope, trigger Options-Based Inquiry to confirm with user before proceeding.

### Step 3: Service Attribution Analysis

Based on architecture standards, determine service归属 for each requirement:

| Service Type | Responsibility | Examples |
|--------------|----------------|----------|
| mall-admin | Admin facade | Management interfaces, dashboards |
| mall-app | Client facade | User interfaces, mobile APIs |
| mall-chat | Chat facade | AI dialogue, streaming APIs |
| mall-agent | Core business | Employee, quota, activation services |
| mall-user | User support | User info, level queries |

### Step 4: Generate REQ Items

For each functional point, generate REQ item:

```markdown
### REQ-XXX: [Service] Feature Name

- **Source**: PRD Section X.X, Wireframe U-XXX
- **Description**: Brief description
- **Code Location**: `repos/{service}/src/main/java/...`
- **Interface Path**: `METHOD /path`
- **Dependencies**:
  - Dependent services: xxx
  - Dependent tables: xxx
- **Acceptance Criteria**:
  - [ ] Criterion 1
  - [ ] Criterion 2
```

### Step 5: Dependency Analysis

Analyze dependencies between REQs:
- Database table dependencies
- Service call dependencies
- Frontend-backend dependencies

### Step 6: Generate Decomposition Document

Generate complete decomposition.md:

```markdown
# Decomposition Results

## Program: {program-id}

### Overview
- **Source**: PRD document
- **Modules**: X functional modules
- **REQ Count**: Y sub-requirements

## Module 1: XXX

### REQ-001: [Service] Feature
...

## Dependency Matrix

| REQ | Dependencies | Dependent Tables | Depended By |
|-----|--------------|------------------|-------------|
| REQ-001 | mall-agent | aim_xxx | REQ-002, REQ-003 |

## Development Sequence

1. REQ-XXX: Reason
2. REQ-XXX: Reason
```

---

## Return Format

```
Status: Completed
Report: orchestrator/PROGRAMS/{program_id}/workspace/decomposition.md
REQ Count: X
Modules: Y

Program Status Update:
  - phase: done
  - status: deployed

Next Steps:
  - Create implementation Programs for each REQ
```
