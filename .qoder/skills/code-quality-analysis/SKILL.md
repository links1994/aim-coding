---
name: code-quality-analysis
description: Analyze code quality and identify issues. Use when code generation is complete, before code review, or when user wants to check code against project standards and best practices.
---

# Code Quality Analysis Skill

Analyze generated code for quality issues, standard compliance, and potential improvements.

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

### Step 4: Generate Report

```markdown
# Code Quality Report

## Summary

| Category | Issues | Critical | Warning | Info |
|----------|--------|----------|---------|------|
| Naming | 2 | 0 | 1 | 1 |
| Architecture | 1 | 0 | 1 | 0 |
| Pitfalls | 0 | 0 | 0 | 0 |

## Detailed Issues

### Issue 1: Naming Convention
- **File**: XxxController.java
- **Line**: 45
- **Severity**: Warning
- **Description**: Method name should start with lowercase
- **Suggestion**: Rename to `getJobTypeList`

## Recommendations

1. Fix naming issues
2. Review architecture compliance
```

---

## Return Format

```
Status: Completed / Issues Found
Report: workspace/quality-report.md
Issues: X total (Y critical, Z warning, W info)

Recommendations:
- Fix critical issues before review
- Address warnings in next iteration
```
