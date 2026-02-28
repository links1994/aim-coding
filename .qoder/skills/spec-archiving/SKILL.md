---
name: spec-archiving
description: Archive technical specifications to repowiki. Use when technical specifications are finalized, for standards documentation, or when teams need to reference project conventions.
---

# Specification Archiving Skill

Archive technical specifications to `.qoder/repowiki/docs/` for standards documentation and team reference.

---

## Trigger Conditions

- Technical specifications finalized
- Need to document project standards
- Teams need to reference conventions
- User command: "archive specification"

---

## Inputs

- Specification documents
- Rule files
- Convention definitions

---

## Outputs

- Spec archive → `.qoder/repowiki/docs/{lang}/specs/{spec-name}.md`

---

## Archive Structure

```
.qoder/repowiki/docs/
├── zh/                     # Chinese documents
│   ├── specs/              # Technical specifications
│   │   ├── error-code-spec.md
│   │   ├── architecture-spec.md
│   │   └── coding-spec.md
│   └── guides/             # Operation guides
│       ├── deployment/
│       └── operations/
└── en/                     # English documents
    └── ...
```

---

## Spec Document Format

```markdown
---
spec_name: Error Code Specification
version: 1.0.0
description: Standard error code format and usage
created_at: 2026-02-28
---

# Error Code Specification

## Overview

This document defines the standard error code format for the project.

## Format

Error codes follow the format: `SSMMTNNN`

| Segment | Meaning | Description |
|---------|---------|-------------|
| SS | System | 20=Common, 30=Admin, 40=Agent |
| MM | Module | 01=User, 02=Order, 03=Employee |
| T | Type | 0=Success, 1=Client Error, 2=Server Error, 3=Business Error |
| NNN | Number | Sequential number (001-999) |

## Examples

| Code | Meaning |
|------|---------|
| 2000000 | Success |
| 2001001 | User not found |
| 4003001 | Employee already exists |

## Usage

```java
// Return success
return CommonResult.success(data);

// Return error
return CommonResult.error(2001001, "User not found");
```
```

---

## Workflow

### Step 1: Read Source

Read specification source files.

### Step 2: Determine Language

- zh — Chinese
- en — English

### Step 3: Create Archive

Generate spec document following format above.

### Step 4: Update Index

Update `.qoder/repowiki/docs/index.md`.

---

## Return Format

```
Status: Completed
Archive: .qoder/repowiki/docs/{lang}/specs/{name}.md
Language: {zh|en}
Version: X.Y.Z
```
