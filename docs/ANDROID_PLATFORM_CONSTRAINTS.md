# Android Platform Constraints and Feasibility

This document records constraints that must shape NOVA AI's design. Validate exact behavior against the Android API level and Vivo Funtouch OS version on the target device.

## 1. Call screening is not call answering

`CallScreeningService` is designed to let an app evaluate incoming/outgoing call details and respond with a screening decision. Android imposes a short response deadline (commonly documented as five seconds). The callback is not a mechanism for conducting an interactive AI conversation with the caller.

Design consequence:
- Keep screening local, fast, and deterministic.
- Do not call an LLM in the screening callback.
- Do not present screening as an answering service.

## 2. Default dialer and in-call experience

A more complete in-call UI generally requires the app to qualify for and be selected as the user's default dialer, including an `InCallService` implementation and required manifest declarations.

Design consequence:
- Treat default-dialer selection as an explicit user setup step.
- Preserve a clear explanation of what changing the default dialer means.
- Test call controls, incoming UI, outgoing calls, and OEM behavior on the actual device.
- Do not assume a screening-service app automatically gains dialer privileges.

## 3. Cellular call audio access

Ordinary third-party Android apps do not have unrestricted access to both sides of cellular-call audio. Audio capture, routing, source availability, and playback behavior depend on Android version, permissions, role, hardware, OEM implementation, and carrier behavior.

Design consequence:
- AI two-way voice answering is a research gate.
- Do not design the MVP around undocumented audio capture workarounds.
- Do not claim universal compatibility.
- Verify feasibility on the Vivo Y39 with a minimal, consent-based technical test before committing to the feature.

## 4. Speech APIs

Android speech recognition and text-to-speech APIs can support user-facing speech features in appropriate contexts. Their existence does not grant access to cellular-call audio.

Design consequence:
- Use speech APIs only with audio the app is legitimately allowed to receive.
- Keep speech recognition and synthesis separate from telecom event handling.
- Provide unavailable/offline fallbacks.

## 5. Privacy and consent

Call content, caller identity, transcripts, and summaries can be sensitive personal data.

Requirements:
- Explain what is collected, why, where it is processed, and how long it is retained.
- Obtain consent where required by applicable law and platform policy.
- Never silently record calls.
- Do not upload call content without clear disclosure and an appropriate legal basis.
- Provide user controls for deletion and retention.

## 6. Device validation checklist

Before committing to AI answering, record:
- Exact Android version and API level.
- Exact Funtouch OS version.
- Whether default-dialer role can be requested and granted.
- Whether required Telecom callbacks are delivered reliably.
- Whether the target device exposes any documented, policy-compliant call-audio path for the intended use case.
- Whether carrier/SIM configuration changes observed behavior.
- Battery, background execution, and notification behavior.
- Results across incoming, outgoing, rejected, answered, and missed calls.

## 7. Go/no-go rule

Proceed with automated two-way AI call conversation only if a documented, supported, consent-aware audio path works on the target device and complies with Android and applicable legal requirements. Otherwise, ship supported screening, caller context, notes, and post-call assistance without claiming live AI answering.
