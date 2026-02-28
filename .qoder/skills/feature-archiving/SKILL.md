---
name: feature-archiving
description: Archive completed features to repowiki for future reuse. Use when feature implementation is complete, needs to be documented for reference, or when creating reusable feature templates.
---

# Feature Archiving Skill

Archive completed features to `.qoder/repowiki/features/` for future development reference and reuse.

---

## Trigger Conditions

- Feature implementation completed
- User command: "archive feature" or "delegate: archive feature"
- Standard workflow final task in implementation Program

---

## Inputs

- Implementation Program workspace:
  - `tech-spec.md` — technical specification
  - `answers.md` — requirement clarification results
  - Source code in `repos/`
- Current Program ID and REQ ID

---

## Outputs

- Feature archive → `.qoder/repowiki/features/F-{seq}-{name}/`
  - `feature-archive.md` — feature description, interface list, core classes, design decisions
  - `reuse-guide.md` — how to reuse this feature
  - `snippets/` — reusable code snippets

---

## Archive Structure

```
.qoder/repowiki/features/
├── index.md                    # Feature index
├── _TEMPLATE/                  # Archive template
│   └── feature-archive.md
├── F-001-create-agent/         # Specific feature archive
│   ├── feature-archive.md
│   ├── reuse-guide.md
│   └── snippets/
│       ├── Controller.java
│       └── Service.java
└── F-002-job-type/
    └── ...
```

---

## Workflow

### Step 1: Read Source Documents

1. Read tech-spec.md
2. Read answers.md
3. Read key source files
4. Check existing feature index

### Step 2: Generate Archive ID

Generate next feature ID: `F-{sequence:000}-{feature-name}`

Example: `F-001-create-agent`

### Step 3: Create Archive Directory

```bash
mkdir -p .qoder/repowiki/features/F-xxx-{name}/snippets
```

### Step 4: Generate feature-archive.md

```markdown
---
feature_id: F-001
feature_name: Smart Employee Creation
program: P-2026-001-REQ-031
description: Create smart employee with validation and quota check
service: mall-agent
created_at: 2026-02-28
tags: [employee, creation, validation]
---

# F-001: Smart Employee Creation

## Overview

Brief description of the feature.

## Interfaces

| Interface | Path | Method | Description |
|-----------|------|--------|-------------|
| Create | /inner/api/v1/ai-employee/create | POST | Create smart employee |

## Core Classes

| Class | Type | Description |
|-------|------|-------------|
| AiEmployeeService | Service | Business logic |
| AimEmployeeDO | Entity | Database entity |

## Design Decisions

### Decision 1: XXX
- **Context**: Why this decision
- **Decision**: What was decided
- **Rationale**: Why this approach

## Reuse Guide

See [reuse-guide.md](./reuse-guide.md)
```

### Step 5: Generate reuse-guide.md

```markdown
# Reuse Guide: Smart Employee Creation

## When to Reuse

- Creating similar employee/agent entities
- Need validation + quota check pattern

## Key Components

1. **Validation Pattern**: See snippets/ValidationPattern.java
2. **Quota Check**: See snippets/QuotaCheck.java

## Adaptation Points

| Original | Adapt To | Notes |
|----------|----------|-------|
| AiEmployee | XxxEntity | Change entity name |
| employeeQuota | xxxQuota | Change quota field |

## Code Snippets

See `snippets/` directory.
```

### Step 6: Extract Code Snippets

Extract reusable code to `snippets/`:
- Controller pattern
- Service pattern
- Validation pattern
- Feign client pattern

### Step 7: Update Feature Index

Update `.qoder/repowiki/features/index.md`:

```markdown
# Feature Index

| ID | Name | Service | Tags | Program |
|----|------|---------|------|---------|
| F-001 | Smart Employee Creation | mall-agent | employee,creation | P-2026-001-REQ-031 |
```

---

## Return Format

```
Status: Completed
Archive: .qoder/repowiki/features/F-{id}-{name}/
Files:
  - feature-archive.md
  - reuse-guide.md
  - snippets/

Index Updated: .qoder/repowiki/features/index.md
```
