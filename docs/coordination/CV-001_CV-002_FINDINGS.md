# CV-001 and CV-002 — Product requirements and Vivo Y39 feasibility findings

**Owner:** Manus

**Scope:** Review of the product plan and official Android Telecom feasibility for ordinary cellular/SIM calls on the Vivo Y39. This is a documentation-only review; no application code or device testing was performed.

## Findings from the repository

The accepted baseline is an Android assistant for ordinary SIM calls. WhatsApp and other VoIP calls are deferred. The first foundation includes onboarding, caller context where available, user-controlled screening, local call records, and privacy controls. Live two-way AI answering remains conditional on proving a supported audio path. The repository is still in its documentation-only stage, and no app source or Gradle project exists. These are existing project decisions, not new approval from this review (`README.md`, `docs/PROJECT_PLAN.md`, `docs/coordination/DECISIONS.md`).

The plan leaves several MVP requirements open. It does not define which call outcomes the user may select (allow, silence, reject/block), how user rules are authored or prioritized, or what the app should show when a number is withheld, unavailable, or already in contacts. The Android screening callback is documented for numbers not in the user's contacts; it should not be treated as a general event feed for every call. If the product needs complete call awareness or a replacement in-call interface, that is a distinct default-dialer scope with substantially more UI and role requirements. The plan also needs to say whether local “call history” means only Callvanta-observed screening events or a complete device call history, and to define what fields are retained, for how long, and how deletion works.

Before implementation, the owner should approve concrete acceptance criteria for: the intended call categories and default behavior; caller data sources and missing-data handling; whether default-dialer replacement is in MVP or deferred; local record fields, retention, and deletion; AI input/output and remote processing disclosure; supported country/carrier assumptions; and measurable reliability, offline, and screening-latency targets. Consent and applicable law remain deployment-specific questions, not resolved by Android APIs.

## Official Android evidence

**Call screening:** `CallScreeningService` is selected by the user through the call-screening role. Telecom provides call details for new incoming and outgoing calls, and the app returns a screening disposition. For incoming calls, it must call `respondToCall()` within five seconds after binding; the phone does not start ringing until the response or timeout. Android documents screening and caller ID as metadata/disposition functions, not an interactive voice conversation. Android 10/API 29 and later can provide screening and caller ID for numbers outside the address book without requiring `READ_CALL_LOG` for those functions. The callback contract and this narrower scope are documented in Android's API reference and screening guide. [1] [2]

**Default dialer:** Becoming the default phone app is a separate user-granted `ROLE_DIALER` role. Android's dialer guide requires handling `ACTION_DIAL` with a dial-pad UI and implementing `InCallService` with incoming- and ongoing-call UIs. `InCallService` manages call state and controls; the cited contract does not grant a raw cellular call audio stream. The role must be checked and requested on-device; documentation cannot establish whether Callvanta's eventual package qualifies or whether the Vivo implementation behaves correctly. [3] [9]

**Cellular-call audio:** Android's audio-input guidance says that during a voice call an ordinary app cannot capture call audio through the ordinary microphone capture path. It describes capture by an accessibility service as a special case and capture by a privileged pre-installed app holding `CAPTURE_AUDIO_OUTPUT`. The `VOICE_CALL`, `VOICE_UPLINK`, and `VOICE_DOWNLINK` audio sources explicitly require `CAPTURE_AUDIO_OUTPUT`, which Android reserves for system components and does not make available to third-party applications. This rules out relying on those public audio sources in a normal third-party Callvanta app. [4] [5]

The accessibility-service exception is not a general Callvanta entitlement or evidence of an end-to-end, policy-compliant two-way AI path. In particular, the cited API documentation does not establish that TTS output can be routed into a cellular call's uplink, that all target devices expose the same behavior, or that using accessibility for this product purpose would meet Android distribution policy or user expectations. Do not treat it as a workaround. Android's `SpeechRecognizer` requires `RECORD_AUDIO` and may stream audio to remote servers; its existence does not give the app access to call audio. `TextToSpeech` synthesizes supplied text but does not capture call audio or establish an in-call transmission path. [6] [7]

## Vivo Y39 facts and limits

Vivo India's official Y39 5G specification page lists Android 15 and Funtouch OS 15 for that regional product listing. This is evidence about the listed model/market, not the exact phone in hand: the project's target unit, model code, region, installed updates, SIM/carrier configuration, and current OS have not been inspected. Vivo also cautions that actual behavior can vary with software version and model. Confirm the device's About phone screen and build details before recording a device-specific result. [8]

No Vivo-specific Telecom callback, default-dialer, battery-management, or call-audio test was run. Android documentation provides API contracts, not an OEM compatibility guarantee. Y39 support for screening or dialer role availability therefore remains unverified on the target unit.

## Minimal validation plan

1. On the actual target phone, record the marketed and model-code identifiers, Android version/API level, Funtouch OS/build number, region, SIM count, and carrier. Do not infer these values from the product page alone.
2. For screening, build only after scope approval. Verify the call-screening role is available and can be requested with the user's consent. Use controlled test numbers and synthetic contacts. Exercise incoming calls from an unknown number, a saved contact, withheld/unavailable caller ID, and relevant outgoing calls; record callback delivery, metadata actually present, and disposition. Measure callback-to-response time, including cold process start, and ensure the response is under five seconds without network/LLM work.
3. Test role denial/revocation, reboot, background operation, dual-SIM selection where applicable, and repeated calls. Keep logs free of real caller content and secrets. Confirm records and deletion behavior against the approved data inventory.
4. If a default-dialer UI is in scope, separately test `ROLE_DIALER` eligibility and user grant, `ACTION_DIAL`, incoming and ongoing UI, answer/reject/hold/audio routing controls, emergency-call behavior, and fallback/revocation on this device.
5. For live AI answering, first identify a documented, third-party-supported audio path that covers the required receive and transmit directions and the intended distribution policy. The cited public `VOICE_CALL`/uplink/downlink APIs do not meet that bar. Do not try hidden APIs, permission bypasses, covert recording, or accessibility as a capture workaround. If no approved public path is available, close the gate as no-go rather than treating a microphone or TTS demo as proof.

## Recommendation

**GO, conditionally, for a narrow MVP foundation:** onboarding and capability status, caller metadata where the documented callback provides it, user-configured local screening that responds within five seconds, and clearly scoped local records/privacy controls. Keep the screening logic local, bounded, and deterministic. Make no claim that Callvanta observes or records every call unless a separate supported mechanism is validated and approved.

**NO-GO for automated two-way AI answering over ordinary cellular calls in the third-party Android MVP, with high confidence for the documented public audio-source path.** Android's API documentation denies ordinary third-party access to the cellular-call sources needed for direct two-way audio; the Vivo Y39's exact behavior has not been tested, but an OEM-specific observation would not itself override Android permission or distribution constraints. Reopen only if a documented, supported, consent-aware path for both receive and transmit is demonstrated on the actual device and receives project-owner, policy, and legal review. This is a platform/API assessment, not a device test or legal opinion.

## References

[1]: https://developer.android.com/reference/android/telecom/CallScreeningService "CallScreeningService API reference"
[2]: https://developer.android.com/develop/connectivity/telecom/dialer-app/screen-calls "Screen calls"
[3]: https://developer.android.com/develop/connectivity/telecom/dialer-app "Build a default phone application"
[4]: https://developer.android.com/media/platform/sharing-audio-input "Sharing audio input"
[5]: https://developer.android.com/reference/android/media/MediaRecorder.AudioSource "MediaRecorder.AudioSource API reference"
[6]: https://developer.android.com/reference/android/speech/SpeechRecognizer "SpeechRecognizer API reference"
[7]: https://developer.android.com/reference/android/speech/tts/TextToSpeech "TextToSpeech API reference"
[8]: https://www.vivo.com/in/products/param/y39-5g "vivo Y39 5G India specifications"
[9]: https://developer.android.com/reference/android/app/role/RoleManager "RoleManager API reference"
