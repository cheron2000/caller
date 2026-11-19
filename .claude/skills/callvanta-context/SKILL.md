---
name: callvanta-context
description: Load Callvanta's product vision, MVP scope, approved architecture, and Android platform constraints before making project decisions or changes.
---

# Callvanta Project Context

Before acting, read `AGENTS.md`, root `README.md`, `docs/PROJECT_PLAN.md`, `docs/ARCHITECTURE.md`, `docs/ANDROID_PLATFORM_CONSTRAINTS.md`, `docs/ROADMAP.md`, and `docs/coordination/DECISIONS.md`.

## Product baseline
- Callvanta is an Android assistant for ordinary cellular/SIM calls.
- First product foundation: onboarding, caller context where available, user-controlled screening, call history, privacy controls.
- WhatsApp and other VoIP apps are deferred.
- Live two-way AI answering is conditional on a verified supported audio path on the target device.
- Initial device for validation: Vivo Y39; verify exact OS/API version rather than assuming it.

## Non-negotiable constraints
- Screening is not answering. `CallScreeningService` has a short response deadline and must not wait for remote AI.
- A full in-call experience may require default-dialer role and `InCallService`.
- Neither API grants unrestricted access to both sides of cellular audio.
- No covert recording or undisclosed transmission of call content.
- No API keys in the Android app, source control, logs, or APK.
- Keep core call UI and local records useful without a backend.

## Output
When reporting, separate:
1. Facts from repository documentation.
2. External evidence.
3. Assumptions or proposals.
4. Decisions that require project-owner approval.
