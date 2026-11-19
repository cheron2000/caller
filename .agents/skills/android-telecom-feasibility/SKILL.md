---
name: android-telecom-feasibility
description: Research and validate Android cellular call screening, default dialer, InCallService, call audio, OEM behavior, and Vivo device feasibility for Callvanta.
---

# Android Telecom Feasibility

Use this skill for platform research and device-validation planning. Cite official Android documentation and distinguish documented API guarantees from OEM/carrier observations.

## Evaluate separately
- `CallScreeningService`: screening decisions and timing requirements.
- Default dialer role and `InCallService`: in-call UI/control integration.
- Cellular call audio: capture, routing, playback, and uplink/downlink restrictions.
- SpeechRecognizer/TextToSpeech: supported speech use cases; their existence does not grant cellular-call audio access.

## Required target-device evidence
Record exact Vivo model, Android API level, Funtouch OS version, SIM/carrier context, role availability, callback behavior, and reproducible test steps.

## Research output
- Question and scope
- Official API documentation and citations
- Device/OEM observations (clearly labeled)
- Permissions and role requirements
- Constraints and unsupported assumptions
- Minimal consent-aware validation plan
- Go/no-go recommendation and confidence

Never recommend undocumented workarounds, permission bypasses, covert recording, or claims of universal AI answering. Do not call a feature feasible solely because an API with a similar name exists.
