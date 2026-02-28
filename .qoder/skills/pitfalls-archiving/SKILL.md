---
name: pitfalls-archiving
description: Archive code pitfalls and anti-patterns to repowiki. Use when discovering recurring issues, during code review findings, or documenting lessons learned to prevent future mistakes.
---

# Pitfalls Archiving Skill

Archive code pitfalls and anti-patterns to `.qoder/repowiki/pitfalls/` for future reference and automated detection.

---

## Trigger Conditions

- Discovered recurring issue during development
- Code review findings
- User command: "archive pitfall" or "document issue"
- Need to document lessons learned

---

## Inputs

- Issue description
- Violation examples (code)
- Correct solution (code)
- Related specifications

---

## Outputs

- Pitfall archive → `.qoder/repowiki/pitfalls/{category}/{pitfall-name}.md`

---

## Archive Structure

```
.qoder/repowiki/pitfalls/
├── index.md
├── feign/
│   ├── feign-client-duplication.md
│   └── facade-feign-prohibition.md
├── naming/
│   └── module-based-naming.md
├── architecture/
│   └── service-layer-violation.md
└── maven/
    └── dependency-conflict.md
```

---

## Pitfall Document Format

```markdown
# Pitfall Title

## Problem Description

Brief description of what this pitfall is and why it's problematic.

## Violation Pattern

Code characteristics or patterns for automatic detection.

## Violation Example

```java
// Incorrect code
@FeignClient(name = "mall-agent")
public interface AgentService {
    // This creates duplicate client
}
```

## Correct Solution

```java
// Correct code
// Use shared RemoteService from mall-agent-api
@Autowired
private AgentRemoteService agentRemoteService;
```

## Related Specifications

- Architecture specification: Service layer responsibilities
- Coding specification: Feign client usage

## Archive Info

- Discovery time: 2026-02-27
- Discoverer: Qoder
- Related Program: P-2026-001-REQ-018
- Severity: High
```

---

## Workflow

### Step 1: Categorize Pitfall

Determine category:
- feign — Feign related issues
- naming — Naming convention issues
- architecture — Architecture violations
- maven — Maven/dependency issues
- performance — Performance anti-patterns
- security — Security issues

### Step 2: Generate Document

Create pitfall document following format above.

### Step 3: Update Index

Update `.qoder/repowiki/pitfalls/index.md`:

```markdown
# Pitfall Index

## Feign
- [Feign Client Duplication](./feign/feign-client-duplication.md)

## Naming
- [Module Based Naming](./naming/module-based-naming.md)
```

---

## Return Format

```
Status: Completed
Archive: .qoder/repowiki/pitfalls/{category}/{name}.md
Category: {category}
Severity: {severity}
```
