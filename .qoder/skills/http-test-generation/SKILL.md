---
name: http-test-generation
description: Generate HTTP test files for API endpoints. Use when technical specification is complete, need to create test requests for APIs, or validating interface implementations.
---

# HTTP Test Generation Skill

Generate HTTP test files (HTTP Client format) for API endpoints based on technical specifications.

---

## Trigger Conditions

- Technical specification completed with OpenAPI definition
- User command: "generate HTTP tests" or "create API tests"
- Need to validate API implementations

---

## Inputs

- `workspace/openapi.yaml` — API definition
- `workspace/tech-spec.md` — technical specification
- Service base URLs from RESOURCE-MAP.yml

---

## Outputs

- HTTP test files → `orchestrator/PROGRAMS/{program_id}/workspace/http-tests/`
  - `admin-apis.http` — Admin service tests
  - `app-apis.http` — App service tests
  - `inner-apis.http` — Internal service tests

---

## HTTP File Format

```http
### Create Job Type
POST {{baseUrl}}/admin/api/v1/job-types
Content-Type: application/json
Authorization: Bearer {{token}}

{
  "code": "SALES",
  "name": "Sales",
  "description": "Product sales position"
}

### List Job Types
GET {{baseUrl}}/admin/api/v1/job-types?pageNum=1&pageSize=20
Authorization: Bearer {{token}}
```

---

## Workflow

### Step 1: Read API Definitions

1. Read openapi.yaml
2. Read tech-spec.md for context
3. Read RESOURCE-MAP.yml for base URLs

### Step 2: Group APIs by Service

Group endpoints by service:
- mall-admin APIs
- mall-app APIs
- mall-agent inner APIs

### Step 3: Generate HTTP Requests

For each endpoint:
1. Extract method, path, parameters
2. Generate request with sample data
3. Add environment variables (baseUrl, token)
4. Add documentation comments

### Step 4: Create HTTP Files

```
workspace/http-tests/
├── http-client.env.json      # Environment variables
├── admin-apis.http           # mall-admin tests
├── app-apis.http             # mall-app tests
└── inner-apis.http           # mall-agent inner tests
```

---

## Return Format

```
Status: Completed
Test Files:
  - workspace/http-tests/admin-apis.http (X requests)
  - workspace/http-tests/app-apis.http (Y requests)
  - workspace/http-tests/inner-apis.http (Z requests)
```
