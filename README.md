# anko

**Buildless web application framework with backend-driven validation for business web applications**

---

## Overview

**anko** is a buildless web application framework
designed to eliminate duplicate input validation in business-oriented
web applications.

anko treats backend DTOs as the **Single Source of Truth** and
automatically propagates validation rules and UI behaviors
to the frontend without requiring any frontend build process.

---

## What “Buildless” Means

**Buildless** means that no frontend build process is required.

- No npm
- No webpack / vite / rollup
- No frontend transpiling or bundling

Backend builds (Java compilation) are still used as usual.

---

## Motivation

In many web applications, the same validation logic is implemented twice:

- Frontend (required, maxlength, pattern, etc.)
- Backend (Validator, Bean Validation, etc.)

This causes:
- Inconsistencies
- Maintenance overhead
- Missed validation updates

**anko makes duplicate validation impossible by design.**

---

## Key Concepts

### 1. Validation-first Architecture

- Validation rules are defined **only in DTOs**
- Frontend validation rules are never written manually
- DTOs define both API contracts and UI behavior

---

### 2. Backend-driven UI Validation

At server startup, a `JsServlet` is registered.
When requested, it dynamically generates a frontend JavaScript file
(`validation.js`) by extracting validation metadata from DTOs.

```
┌────────────────────────────────────┐
│          Browser                   │
│                                    │
│  HTML / CSS / Vanilla JS           │
│  ────────────────────────          │
│  Template Rendering                │
│  Lightweight Content-based Routing │
│                                    │
│  ┌───────────────────────┐         │
│  │ validation.js         │<────────┼────>localStorage (cached by hash)
│  │ (loaded via HTTP)     │         │
│  └───────────────────────┘         │
│ ┌─────────┐ ^                      │
│ │  JSON   │ │                      │
│ | (fetch) | │                      │
│ └─┬───────┘ │                      │
└───┼─────────┼──────────────────────┘
	│ POST    │  GET /validation.js
	│         │
┌───┼─────────┼────────────────────────────────────────────┐
│   │         v                                            │
│   │   ┌──────────────────────────┐                       │
│   │   │     JsServlet            │                       │
│   │   │ (validation.js endpoint) │                       │
│   │   └──────────────────────────┘                       │
│   │               ^                                      │
│   │  ┌────────────┴──────────────┐                       │
│   │<─┤ DTO + Validator           │                       │
│   │  │ (Validation Metadata)     │                       │
│   │  └───────────────────────────┘                       │
│   V                                                      │
│  ┌──────────────┐     ┌──────────────────────────────┐   │
│  │ ApiServlet   │────>│ API Layer                    │   │
│  └──────────────┘     └───────────────┬──────────────┘   │
│                                       │                  │
│                              ┌────────v────────┐         │
│                              │ Service Layer   │         │
│                              └────────┬────────┘         │
│                                       │                  │
│                              ┌────────v────────┐         │
│                              │ DAO / ORM       │         │
│                              └─────────────────┘         │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

```
Browser
└─ JSON Request
		└─ ApiServlet
			└─ API Layer
					└─ Service Layer
						└─ DAO / ORM
```

- ApiServlet is responsible only for request routing and JSON handling

- API Layer handles request-specific logic

- Service Layer contains business logic

- DAO / ORM handles persistence

```
validator.js
└─ JsServlet
		└─ DTO + Validator
```
- Validation metadata generation never accesses the database

- No dependency on DAO / ORM

- No side effects

# Separation of Concerns

validator.js generation is completely independent from the persistence layer.

JsServlet only depends on DTO definitions and Validators

DAO / ORM is used exclusively by API request handling

Validation metadata is extracted without accessing the database

---

# Limitations

This framework intentionally limits its scope to keep the architecture simple, explicit, and maintainable.

## Validation

- No cross-field (correlation) validation

	- Validation rules are applied per field.

	- Rules that depend on multiple fields are intentionally out of scope.

- No conditional required fields

	- Conditions such as “required only if another field has a specific value” are not supported.

- Validation rules are DTO-driven

	- Only validations defined on DTOs are propagated to the frontend.

	- Frontend-only validation rules are not supported.

## Frontend Architecture

- No frontend build process

	- Frameworks such as React, Vue, or Angular are intentionally not used.

	- ES modules, bundlers, and transpilers are not part of the design.

- Limited client-side features

	- UI updates are performed by content replacement.

	- Only the content area is dynamically replaced.

	- Global state management is intentionally not provided.

- No client-side state persistence

	- Screens are treated as stateless.

	- All authoritative state resides on the server.

## Backend Architecture

- DTO changes require server restart

	- Validation metadata (validator.js) is generated at server startup.

	- Updating DTO definitions requires rebuilding and restarting the server.

- No runtime DTO introspection via API

	- Validation metadata is served only through validator.js.

	- API endpoints do not expose validation schemas.

## Use Case Focus

- Optimized for business form-based applications

	- CRUD-oriented screens

	- Administrative tools

	- Internal systems

- Not intended for

	- Complex UI interactions

	- Highly dynamic visual applications

	- Real-time or event-driven UIs

---

# Design Philosophy Behind These Limitations
These limitations are intentional design choices, not missing features.

By restricting the feature set:

- DTOs remain the single source of truth

- Frontend logic stays minimal and predictable

- Validation logic is never duplicated

- The system remains easy to reason about and debug

---

# DTO-driven Validation Example
## Java DTO Definition

```
package com.uchicom.tracker.dto.request.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.uchicom.tracker.annotation.Form;
import jakarta.validation.constraints.NotBlank;

@Form("login")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginDto {

	@NotBlank(message = "必須です。")
	public String id;

	@NotBlank(message = "必須です。")
	public String pass;
}
```

---

## Generated validator.js
```
const validationHash = 'FFFFFFFFA2C9E569';

if (validationHash != localStorage.getItem('validationHash')) {
	localStorage.setItem('validationHash', validationHash);
	localStorage.setItem('validation', JSON.stringify({
		"login": {
			"id": {
				"required": { "message": "必須です。" },
				"pattern": {
					"regexp": ".*\\S+.*",
					"message": "必須です。"
				}
			},
			"pass": {
				"required": { "message": "必須です。" },
				"pattern": {
					"regexp": ".*\\S+.*",
					"message": "必須です。"
				}
			}
		}
	}));
}

```

---

## Mapping Rules

Java Annotation	| Generated Rule |	Description
---|---|---
@Form("login") |	"login"	| Form identifier
@NotBlank	| required | Required field
@NotBlank	| pattern: .*\\S+.*	| Must contain non-whitespace
message | message | Error message propagation

---

## How This Works

1. DTO is annotated

	- Validation rules are defined once, on the server side

2. JsServlet extracts metadata

	- Reads DTO annotations at server startup

	- Generates validator.js

3. Frontend loads validator.js

	- Cached using validationHash

	- Stored in localStorage

4. Form rendering applies rules

	- Required labels

	- Visual emphasis

	- Client-side validation

5. API validation is shared

	-	JSON → DTO → Validator

	-	Violations are returned and bound to the same form

---

## Why This Matters

- No duplicated validation logic

- No frontend validation definitions

- Single source of truth: DTO

- Consistent behavior between frontend and backend

---

# Quick Start (GitHub Codespaces)

This project is configured to run out-of-the-box using **GitHub Codespaces**.

You can start the server and verify the behavior in **about 3 minutes** without any local setup.

## Steps

1. Open the repository on GitHub

2. Click **Code** → **Create codespace on main**

3. Wait for the Codespace to start

4. Run the server:
```
mvn exec:java "-Dexec.mainClass=com.uchicom.tracker.Main"
```

5. Access the application in your browser:
```
http://localhost:8080
```
---

## What is preconfigured

- Java runtime

- Maven

- H2 database

- All dependencies

No additional installation is required.

---

## What you can verify immediately

- Server startup

- API routing via **ApiServlet**

- DTO-based validation

- Generated **validator.js** via **JsServlet**

- Frontend validation without build tools

- Validation error propagation from backend to frontend

---

## Why Codespaces is recommended

- No environment differences

- No dependency conflicts

- Ideal for quick evaluation and experimentation

- Matches the “Buildless” philosophy

## mvn
### server start
```
mvn exec:java "-Dexec.mainClass=com.uchicom.tracker.Main"
```

### format
```
mvn spotless:apply
```

### test
```
mvn verify
```

### format & clean & compile & test
```
mvn spotless:apply clean compile verify
```

## SQLスクリプト
```
java -cp ~/.m2/repository/com/h2database/h2/2.4.240/2.4.240.jar org.h2.tools.RunScript -url "jdbc:h2:./database/tracker;AUTO_COMPACT_FILL_RATE=0;CIPHER=AES" -user tracker -password "tracker tracker" -script ./database/sql/create.sql	-showResults
```
# AI Context (For AI Assistants Only)

## Framework: anko

anko is a **buildless Java web application framework** optimized for
business-oriented, form-based systems.

The framework is designed to **eliminate duplicate validation logic**
by making Java DTOs the single source of truth for:

- API contracts
- Validation rules
- Frontend validation behavior


## Core Philosophy

1. **DTOs are the single source of truth**
   - Validation
   - API request structure
   - UI validation behavior

2. **Validation is defined exactly once**
   - Only in Java DTO annotations
   - Never duplicated in frontend or service logic

3. **Frontend is intentionally lightweight**
   - No framework abstractions
   - No client-side state ownership
   - No build step


## Constraints (Hard Rules)

### Frontend
- NO React / Vue / Angular / Svelte
- NO npm / webpack / vite / rollup
- NO TypeScript / JSX
- NO syntax that requires transpilation
- NO frontend-managed application state
- NO frontend-defined validation rules
- NO client-side business logic

Allowed:
- Vanilla JavaScript only (runs as-is)
- Plain CSS only
- HTML templates

Notes:
- localStorage is used **internally by generated files only**
- Application logic must NOT depend on localStorage or sessionStorage


### Backend
- NO validation outside DTOs
- NO database access during validation metadata generation
- NO frontend-specific logic in Service / DAO layers

Allowed:
- Java + Servlet
- Bean Validation annotations on DTOs
- Clear separation of layers


## Architecture Overview

### Validation Metadata Flow

```text
DTO annotations
 → JsServlet (at server startup)
 → validator.js / enumeration.js (generated)
 → Browser (cached by hash)
 → Form rendering & client-side validation
```


### Request Handling Flow

```text
Browser (JSON)
 → ApiServlet
   → API Layer
     → Service Layer
       → DAO / ORM
```


## JsServlet Responsibilities

```text
- Extract validation metadata from DTO annotations
- Generate frontend JavaScript dynamically:
  - validator.js (validation rules)
  - enumeration.js (enum propagation)
- Serve generated JavaScript via HTTP
- Apply hash-based cache control
```

JsServlet MUST:
- Depend only on DTOs and Validators
- Have NO dependency on Service / DAO / Database
- Produce deterministic output


## ApiServlet Responsibilities

```text
- Accept JSON requests
- Bind JSON to DTOs
- Execute Bean Validation
- Route validated DTOs to API Layer
- Return validation violations as structured JSON
```

ApiServlet does NOT:
- Define validation rules
- Contain business logic
- Access persistence directly


## DTO Definition Rules

Example:

```java
@Form("formId")   // unique identifier
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExampleDto {
    @NotBlank(message = "必須です。")
    public String field;
}
```

Rules:
- Validation annotations define ALL validation behavior
- @Form value is used as the frontend form identifier
- DTOs are request-scoped and stateless


## Generated validator.js Behavior

- Generated by JsServlet at server startup
- Contains validation metadata extracted from DTOs
- Uses validationHash to detect changes
- Cached on the client side automatically

Application JavaScript MUST NOT:
- Manually write validation rules
- Modify validation metadata
- Depend on storage implementation details


## Frontend Form Rules

- Forms reference validation metadata by @Form ID
- Required indicators and validation messages are auto-applied
- Client-side validation mirrors backend validation
- Backend validation always runs, even if frontend validation is bypassed


## Validation Guarantees

```text
- Frontend validation can be bypassed
- Backend validation always applies
- Validation errors are returned as JSON
- Errors are bound back to the same form
- No divergence between frontend and backend validation
```


## Change Management

- DTO changes require rebuild and server restart
- validationHash invalidates cached metadata automatically


## Supported Validation Annotations

- @NotBlank
- @Size
- @Email
- @Pattern
- Other standard Bean Validation annotations (field-level only)


## Explicit Non-Goals

- Cross-field (correlation) validation
- Conditional required fields
- Frontend state management
- Complex UI interactions
- Realtime or event-driven UI


## Target Use Cases

- Business forms
- Administrative dashboards
- CRUD-centric internal systems
- Validation-heavy workflows


## Mental Model for AI Assistants

If a behavior is not defined on a DTO, it does not exist.
If validation appears outside a DTO, it is a design error.
If frontend code grows complex, the design is wrong.
