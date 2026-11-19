---
name: android-engineering
description: Apply Callvanta's Android client architecture, Kotlin and Jetpack Compose conventions, telecom boundaries, persistence, and test requirements during implementation.
---

# Android Engineering Rules

Before implementation, confirm the project owner has approved moving beyond the documentation-only stage.

## Architecture
- Kotlin Android client with Jetpack Compose presentation.
- Keep UI, telecom adapters, domain logic, persistence, and AI/network boundaries separate.
- Android Telecom APIs belong behind adapters; domain logic must not depend on Compose.
- Use local persistence for user-controlled records and settings; define retention and deletion behavior.
- Detect unsupported platform capabilities and gate features explicitly.

## Telecom
- Screening callbacks must be fast, deterministic, and independent of remote AI.
- Treat default-dialer and `InCallService` work as a separately approved milestone.
- Never assume access to both sides of cellular-call audio.

## Quality
- Explain each permission and role request.
- Handle permission denial, unavailable metadata, process recreation, offline state, and OEM differences.
- Add unit tests for domain policies and integration tests where feasible.
- Document exact device/API versions used for manual validation.
- Do not add dependencies or permissions without a rationale and review.
