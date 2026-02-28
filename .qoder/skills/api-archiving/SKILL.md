---
name: api-archiving
description: Archive API definitions to repowiki. Use when API design is complete, for service interface documentation, or when other services need to reference API contracts.
---

# API Archiving Skill

Archive API definitions to `.qoder/repowiki/apis/` for service interface documentation and reference.

---

## Trigger Conditions

- API design completed
- Need to document service interfaces
- Other services need to reference API contracts
- User command: "archive API"

---

## Inputs

- OpenAPI definition or interface code
- Service information
- API documentation

---

## Outputs

- API archive → `.qoder/repowiki/apis/{type}/{service-name}.md`

---

## Archive Structure

```
.qoder/repowiki/apis/
├── index.md
├── internal/               # Internal service APIs
│   ├── user-service-api.md
│   └── agent-service-api.md
└── third-party/            # Third-party APIs
    ├── wechat-pay-api.md
    └── alipay-api.md
```

---

## API Document Format

```markdown
---
service: mall-user
api_type: internal
description: User service internal APIs for Feign calls
version: 1.0.0
created_at: 2026-02-28
---

# User Service API

## Overview

Base URL: `http://mall-user/inner/api/v1`

## Endpoints

### Get User Detail

```
GET /user/detail?userId={userId}
```

**Parameters**:
| Name | Type | Required | Description |
|------|------|----------|-------------|
| userId | Long | Yes | User ID |

**Response**:
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "john",
    "level": "A"
  }
}
```

### Get User Level

```
GET /user/level?userId={userId}
```

...

## Feign Client

```java
@FeignClient(name = "mall-user")
public interface UserRemoteService {
    @GetMapping("/inner/api/v1/user/detail")
    CommonResult<UserApiResponse> getUserDetail(@RequestParam("userId") Long userId);
}
```

## Error Codes

| Code | Description |
|------|-------------|
| 2001001 | User not found |
| 2001002 | Invalid user ID |
```

---

## Workflow

### Step 1: Extract API Info

From OpenAPI or code:
- Endpoints
- Parameters
- Responses
- Error codes

### Step 2: Determine Type

- internal — Service internal APIs (Feign)
- third-party — Third-party APIs

### Step 3: Create Archive

Generate API document following format above.

### Step 4: Update Index

Update `.qoder/repowiki/apis/index.md`.

---

## Return Format

```
Status: Completed
Archive: .qoder/repowiki/apis/{type}/{service}.md
Type: {internal|third-party}
Endpoints: X
```
