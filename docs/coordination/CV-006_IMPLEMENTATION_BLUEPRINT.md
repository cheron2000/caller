# CV-006 — Android Foundation Implementation Blueprint

**Owner:** ChatGPT  
**Status:** Ready for implementation  
**Approved scope:** See D-007 through D-022 in `DECISIONS.md`.  
**Development constraint:** Phone-first workflow; use GitHub Actions for remote builds. No local Android SDK or physical device test is available in this workflow.

## 1. First deliverable

Create a single-module Android application that:
- Launches to a Compose dashboard.
- Explains Callvanta's supported scope and limitations.
- Shows call-screening role availability and grant state.
- Lets the user open the Android system flow to grant the call-screening role.
- Does not request default-dialer role, call-log permission, contacts permission, microphone, or network access in the first app-shell increment.
- Has a deterministic, pure Kotlin screening-policy component with tests before connecting it to Telecom.
- Does not persist caller data until retention and backup details in CV-007 are approved.

## 2. Build configuration baseline

Use pinned, stable tool versions rather than dynamic versions.

| Component | Initial selection | Reason |
|---|---|---|
| Android Gradle Plugin | 9.5.0 | Current stable release found in official Android release notes |
| Gradle | 9.6.0 | Minimum required by AGP 9.5 |
| JDK | 17 | AGP 9.5 documented runtime requirement |
| Kotlin | 2.4.21 | Stable Kotlin release |
| Compose BOM | 2026.09.00 | Stable Compose BOM compatible with API 36 without opting into the newer API 37.1 requirement noted for the November Compose release |
| compileSdk | 36 | Stable Android 16 API level |
| targetSdk | 36 | Align with stable API 36 for initial test build |
| minSdk | 29 | Android 10 baseline; supports modern call screening behavior and avoids legacy compatibility branches |
| Room | Deferred | No persistence implementation until retention and backup behavior are finalized |

Official references:
- AGP 9.5.0 compatibility: https://developer.android.com/build/releases/agp-9-5-0-release-notes
- AGP/Gradle compatibility: https://developer.android.com/build/releases/about-agp
- Kotlin release history: https://kotlinlang.org/releases.html
- Compose BOM setup: https://developer.android.com/develop/ui/compose/bom
- Compose November release notes: https://developer.android.com/blog/posts/jetpack-compose-november-2026-release
- Room releases: https://developer.android.com/jetpack/androidx/releases/room

Version note: The November 2026 Compose 1.13 release notes state a requirement for Kotlin 2.2+ and compileSdk 37.1. We deliberately choose the previous stable BOM/API 36 baseline for the first build to avoid depending on a newer Android platform SDK preview requirement. Re-evaluate versions when CI first runs; do not upgrade dependencies merely because a newer release exists.

## 3. Package boundaries

Keep one Gradle module initially to reduce complexity for a phone-first project.

```text
app/src/main/java/com/callvanta/ai/
  MainActivity.kt
  ui/
    CallvantaApp.kt
    dashboard/
    onboarding/
    theme/
  telecom/
    screening/
    capabilities/
  domain/
    model/
    screening/
  data/
    # No database implementation until CV-007 is resolved
  ai/
    # No runtime implementation in MVP
```

Do not create empty abstractions without a first consumer. Add packages when their first implementation lands.

## 4. First implementation slices

### Slice A — Buildable app shell
- Root Gradle settings and plugin declarations.
- Single `:app` module.
- Manifest and application entry point.
- Compose dashboard with product scope, setup status, and privacy explanation.
- GitHub Actions debug build.
- No Telecom service yet; first prove the remote build pipeline.

### Slice B — Capability/onboarding
- Read `RoleManager.ROLE_CALL_SCREENING` availability/grant state.
- Explain why the screening role is needed.
- Launch the platform role request only after a user taps the setup action.
- Handle denied, unavailable, and revoked states.
- Do not request unrelated permissions or roles.

### Slice C — Pure screening policy
- Define typed caller metadata and screening action models.
- Implement rule precedence and default-allow behavior.
- Treat saved contacts as allowed by default.
- Treat withheld/unknown numbers as a separate configurable category, default allow.
- Add JVM unit tests for matching, precedence, ties, missing metadata, disabled rules, and unavailable policy data.

### Slice D — Telecom adapter
- Add the manifest-declared `CallScreeningService`.
- Keep callback work bounded and local.
- Do not call network, AI, disk-heavy operations, or UI from the callback.
- Return the approved action within the Android deadline.
- Keep the callback path safe if rule configuration cannot be loaded.
- Add service-level tests where feasible and document manual device checks.

### Slice E — Persistence (blocked)
- Do not add Room or store call metadata until CV-007 specifies retention duration, backup exclusion, fields, and deletion semantics.

## 5. Remote build approach

- Use GitHub Actions on pushes to `main` and manual dispatch.
- Set up JDK 17.
- Install Gradle 9.6.0 through the official Gradle setup action.
- Run `gradle :app:assembleDebug` and unit tests.
- Upload the debug APK as a workflow artifact.
- Never place signing keys, API keys, or credentials in the repository.
- A debug APK is for testing only; release signing is a separate later task.

The first CI run is the authoritative compatibility check for the selected plugin/toolchain versions. If it fails, fix the compatibility issue and record the actual tested versions.

## 6. Verification gates

- CI compiles the debug APK.
- Pure JVM screening policy tests pass.
- Manifest contains only required permissions and services.
- No network permission or microphone permission is present in the first milestone.
- No `ROLE_DIALER`, `InCallService`, `READ_CALL_LOG`, or contacts permission is requested.
- Device compatibility is not claimed until a manual test is run on the actual Vivo Y39.
- Record exact build and test outputs; do not report a build as passing before workflow completion.

## 7. Explicitly deferred
- Room and call event persistence until CV-007.
- Backend and all AI provider calls.
- Speech recognition, TTS, audio recording, and transcripts.
- Default dialer and in-call UI.
- Contacts lookup.
- WhatsApp and other VoIP integrations.
- Release signing and Play Store publication.

## 8. Handoff
CV-006 defines the implementation sequence and pinned toolchain baseline. The next coding task is CV-008 — create the minimal Android app shell and GitHub Actions debug build. CV-007 remains a separate blocker for persistence only; it does not block the app shell, onboarding, or pure policy engine.
