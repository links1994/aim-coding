---
name: requirement-clarification
description: Clarify requirements interactively using ReAct pattern. Use when user says "clarify REQ-xxx", "confirm requirement REQ-xxx", or needs to clarify business rules before technical design. Supports question-by-question confirmation with real-time documentation updates.
---

# Requirement Clarification Skill

> **Language Constraint**: All outputs from this Skill must be in Chinese, including questions, options, documentation content, and status reports.

Use ReAct (Reasoning + Acting) design pattern for interactive requirement clarification with users.

> **Prerequisite**: Must read `.qoder/rules/02-requirement-clarification.md` before execution to obtain specification standards.

---

## Trigger Conditions

- User command: "clarify REQ-xxx" or "confirm requirement REQ-xxx"
- Current Program is Implementation type (not decomposition type)
- Need to reference decomposition document from decomposition Program

---

## Program Type

This Skill applies to **Implementation Program** (implementation type), not Decomposition Program.

Implementation Program naming convention: `{parent-ID}-REQ-xxx`

Examples:
- Decomposition Program: `P-2026-001-decomposition`
- Implementation Program: `P-2026-001-REQ-031` (this Skill runs in such Programs)

---

## Inputs

- Target requirement ID (e.g., REQ-031)
- `orchestrator/PROGRAMS/{decomposition_program_id}/workspace/decomposition.md` — requirement decomposition document
- `.qoder/rules/02-requirement-clarification.md` — requirement clarification specification (**must read first**)
- Current Program's STATUS.yml — update phase status

---

## Outputs

- Question list → `orchestrator/PROGRAMS/{current_program_id}/workspace/questions.md`
- Confirmation results → `orchestrator/PROGRAMS/{current_program_id}/workspace/answers.md`
- Technical decisions → `orchestrator/PROGRAMS/{current_program_id}/workspace/decisions.md`
- Update STATUS.yml → phase changes from "requirement clarification" to "technical specification"

---

## ReAct Workflow

```
┌─────────────────────────────────────────────────────────────┐
│                      ReAct Loop                             │
├─────────────────────────────────────────────────────────────┤
│  1. Reason: Analyze problem, generate options               │
│      ↓                                                      │
│  2. Action: Call ask_user_question to get user choice       │
│      ↓                                                      │
│  3. Observe: Record user answer, update memory state        │
│      ↓                                                      │
│  4. Update: Real-time update questions.md and answers.md    │
│      ↓                                                      │
│  5. Next: Next question or finish                           │
└─────────────────────────────────────────────────────────────┘
```

---

## Workflow

### Step 1: Initialization and Knowledge Base Query

1. **Read Inputs**
   - Read decomposition.md to locate target requirement
   - **Must read** clarification specification (02-requirement-clarification.md)

2. **Knowledge Base Query (Auto-reuse existing decisions)**

   **Use knowledge-base-query Skill to query**:
   ```
   Query type: feature
   Keywords: {current requirement feature keywords}
   
   Purpose:
   - Query similar function clarification question lists
   - Reference existing technical decisions
   - Avoid re-clarifying confirmed business rules
   ```
   
   **Query result processing**:
   - If similar function has decisions.md → Pre-fill technical decisions, reduce clarification questions
   - If no similar function → Follow standard process to generate clarification questions

3. **Check Existing Status**
   - Check if `workspace/questions.md` already exists
   - Check if `workspace/answers.md` already exists
   - Determine current clarification progress
   - Check current Program STATUS.yml to confirm in "requirement clarification" phase

### Step 2: ReAct Loop - Question-by-Question Clarification

**For each unclarified question, execute the following loop:**

#### 2.1 Reason - Analyze Problem

Analyze current question's business background, generate 2-4 clarification options:

- Options should cover common business scenarios
- Provide recommended default option (marked "recommended")
- Must include an "Other (custom)" option

**Zero Assumption Principle**: Do not infer answers based on "common practices" or "typical implementations". Every uncertainty must be confirmed through Options-Based Inquiry.

#### 2.2 Action - Ask User

Use `ask_user_question` tool, ask one question at a time.

#### 2.3 Observe - Record Answer

Record result based on user choice:
- If preset option selected: Directly record option value
- If "Other" selected: Continue asking for specific requirements

#### 2.4 Update - Real-time Document Update

**Real-time update `workspace/questions.md` and `workspace/answers.md`**

Document format reference: "Output Format Specification" chapter in `.qoder/rules/02-requirement-clarification.md`

#### 2.5 Next - Determine Continue or Finish

- If more unclarified questions exist → Continue to next question
- If all questions clarified → Generate decision records, finish

### Step 3: Generate Technical Decision Records

After all questions clarified:

1. Generate `workspace/decisions.md` (format reference ADR specification in Rule)
2. Update STATUS.yml:
   ```yaml
   current_phase: technical specification
   phases:
     - name: requirement clarification
       status: done
     - name: technical specification
       status: pending
   ```

---

## Return Format

```
Status: Completed / In Progress / Needs Further Clarification
Reports:
  - workspace/questions.md
  - workspace/answers.md
  - workspace/decisions.md
Progress: X/Y questions confirmed

Program Status Update:
  - current_phase: technical specification
  - phases.requirement_clarification.status: done

Decision Points:
  - All high-priority questions confirmed: Yes/No
  - Can proceed to technical specification phase: Yes/No
```
