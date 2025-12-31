# anko

**Buildless SPA framework with backend-driven validation for business web applications**

---

## Overview

**anko** is a buildless Single Page Application (SPA) framework
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
┌─────────────────────────────┐
│          Browser            │
│                             │
│  HTML / CSS / Vanilla JS    │
│  ────────────────────────   │
│  Template Rendering         │
│  Lightweight SPA Routing    │
│                             │
│  ┌───────────────────────┐  │
│  │ validator.js          │<─┼────>localStorage (cached by hash)
│  │ (loaded via HTTP)     │  │
│  └───────────────────────┘  │
│ ┌─────────┐ ^               │
│ │  JSON   │ │               │
│ | (fetch) | │               │
│ └─┬───────┘ │               │
└───┼─────────┼───────────────┘
    │ POST    │  GET /validator.js
    │         │
┌───┼─────────┼────────────────────────────────────────────┐
│   │         v                                            │
│   │   ┌─────────────────────────┐                        │
│   │   │     JsServlet           │                        │
│   │   │ (validator.js endpoint) │                        │
│   │   └─────────────────────────┘                        │
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
java -cp ~/.m2/repository/com/h2database/h2/2.4.240/2.4.240.jar org.h2.tools.RunScript -url "jdbc:h2:./database/tracker;AUTO_COMPACT_FILL_RATE=0;CIPHER=AES" -user tracker -password "tracker tracker" -script ./database/sql/create.sql  -showResults
```
