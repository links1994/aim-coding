---
name: database-schema-archiving
description: Archive database table structures to repowiki. Use when table design is complete, for legacy project documentation, or when other features need to reference existing table structures.
---

# Database Schema Archiving Skill

Archive database table structures to `.qoder/repowiki/schemas/` for reference and reuse.

---

## Trigger Conditions

- Table design completed
- Need to document existing tables for legacy projects
- Other features need to reference table structures
- User command: "archive table schema"

---

## Inputs

- Table DDL or Entity class
- Table documentation
- Service归属 information

---

## Outputs

- Schema archive → `.qoder/repowiki/schemas/{service}/{table-name}.md`

---

## Archive Structure

```
.qoder/repowiki/schemas/
├── index.md
├── mall-user/
│   ├── _service-overview.md
│   ├── tb_user.md
│   └── tb_user_role.md
└── mall-order/
    ├── _service-overview.md
    └── tb_order.md
```

---

## Schema Document Format

```markdown
---
table_name: tb_user
description: User main table, stores user basic information
database: mall_user
service: mall-user
engine: InnoDB
charset: utf8mb4
created_at: 2026-02-28
---

# tb_user

## Basic Info

| Attribute | Value |
|-----------|-------|
| Table Name | tb_user |
| Description | User main table |
| Service | mall-user |
| Database | mall_user |

## Field List

| Field Name | Data Type | Nullable | Default | Constraint | Comment |
|------------|-----------|----------|---------|------------|---------|
| id | BIGINT | NO | AUTO_INCREMENT | PK | Primary key ID |
| username | VARCHAR(64) | NO | - | UK | Username |
| phone | VARCHAR(20) | YES | NULL | - | Phone number, AES encrypted |
| status | TINYINT | NO | 1 | - | Status: 1-active, 0-inactive |
| create_time | DATETIME | NO | CURRENT_TIMESTAMP | - | Create time |
| update_time | DATETIME | NO | CURRENT_TIMESTAMP | ON UPDATE | Update time |

## Index Info

| Index Name | Type | Fields | Comment |
|------------|------|--------|---------|
| PRIMARY | Primary | id | - |
| uk_username | Unique | username | Username unique |
| idx_phone | Normal | phone | Phone lookup |

## Foreign Keys

| Name | Field | Ref Table | Ref Field | On Delete |
|------|-------|-----------|-----------|-----------|
| fk_user_role | role_id | tb_role | id | CASCADE |

## DDL

```sql
CREATE TABLE `tb_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL,
  ...
) ENGINE=InnoDB;
```
```

---

## Workflow

### Step 1: Extract Table Info

From DDL or Entity:
- Table name
- Field definitions
- Index definitions
- Foreign keys

### Step 2: Determine Service

Identify which service owns this table.

### Step 3: Create Archive

Generate schema document following format above.

### Step 4: Update Index

Update service overview and main index.

---

## Return Format

```
Status: Completed
Archive: .qoder/repowiki/schemas/{service}/{table}.md
Service: {service}
Fields: X
Indexes: Y
```
