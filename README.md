# NOVA AI — Android Call Assistant

NOVA AI is a planned Android application for assisting with incoming cellular (SIM) calls. The initial release focuses on caller context, user-controlled call screening, call records, and a foundation for AI-assisted call handling where Android and device capabilities permit it.

> **Current repository state:** planning and architecture scaffold only. No application source code, Gradle configuration, backend implementation, or build workflow has been added yet.

## Product goals

- Detect and present incoming cellular calls using supported Android telecom APIs.
- Provide caller identification/context where available and permitted.
- Let the user configure screening preferences and review call outcomes.
- Build a call history and, where permitted, caller-purpose notes, transcripts, and summaries.
- Investigate AI voice answering as a later capability, subject to Android/OEM/carrier restrictions.
- Keep core call-related UI and records useful even when the network or AI backend is unavailable.

## Important platform constraint

Android does not grant ordinary apps unrestricted access to both sides of cellular-call audio. `CallScreeningService` is intended for screening decisions and has a strict response deadline; it is not an AI conversation interface. A full in-call experience generally requires the app to be selected as the default dialer and to implement `InCallService`. Even then, audio access and routing remain restricted and device-dependent.

Therefore, automatic two-way AI conversations must be treated as a feasibility milestone, not a guaranteed MVP feature. Do not advertise or implement silent call recording. Any recording, transcription, or AI processing must be transparent, consent-aware, and compliant with applicable law and platform policy.

## Documentation

- [Project plan](docs/PROJECT_PLAN.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Android platform constraints](docs/ANDROID_PLATFORM_CONSTRAINTS.md)
- [Implementation roadmap](docs/ROADMAP.md)
- [Proposed repository structure](docs/PROJECT_STRUCTURE.md)

## Proposed technology direction

- Android client: Kotlin, Jetpack Compose, Android Telecom APIs, Room
- On-device speech capabilities: Android SpeechRecognizer and TextToSpeech, subject to supported use cases
- Optional AI service: Python and FastAPI
- AI provider: provider-agnostic integration; credentials remain server-side
- Build and delivery: GitHub Actions, after the Android project and Gradle build configuration are introduced

## Non-goals for the first milestone

- WhatsApp call answering or interception
- Guaranteed AI answering on every Android device
- Bypassing Android permissions, telecom restrictions, or user consent
- Shipping API secrets inside the APK

## Status

This repository currently contains planning documents and empty structural placeholders only. Implementation should begin only after the platform feasibility decisions and MVP acceptance criteria are reviewed.
