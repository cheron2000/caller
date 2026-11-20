# Callvanta Android application

The first Android application shell is implemented with Kotlin and Jetpack Compose.

## Current increment
- Single Android application module.
- Compose launch screen describing the approved MVP scope.
- Minimal manifest with no call, contacts, microphone, or network permissions.
- GitHub Actions debug build and JVM unit-test workflow.

## Not implemented yet
- Call-screening role onboarding.
- `CallScreeningService`.
- Screening rules and preferences.
- Local event persistence (blocked until retention and backup behavior are finalized).
- Backend, AI, call audio, or recording.

See [the implementation blueprint](../docs/coordination/CV-006_IMPLEMENTATION_BLUEPRINT.md) and [the work queue](../docs/coordination/WORK_QUEUE.md).
