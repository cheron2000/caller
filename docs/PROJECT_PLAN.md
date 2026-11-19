# NOVA AI — Project Plan

## 1. Vision

Build an Android call-assistant application that helps the device owner understand and manage incoming cellular calls, with AI-assisted caller interactions as a later capability when supported by Android, the device manufacturer, and applicable carrier behavior.

## 2. Problem statement

Users receive calls from unknown numbers and often need to interrupt what they are doing to understand who is calling and why. NOVA AI aims to provide caller context, configurable screening, and concise call records. More advanced voice interaction is a research track because Android tightly controls cellular-call audio.

## 3. Target platform and operating assumptions

- Initial device for validation: Vivo Y39.
- Development environment: phone-first workflow; source control and cloud builds through GitHub.
- Android client direction: Kotlin and Jetpack Compose.
- Initial call scope: ordinary SIM/cellular calls only.
- WhatsApp and other VoIP apps are explicitly deferred.

The exact Android version, Vivo Funtouch OS version, default-dialer behavior, and device-specific telecom capabilities must be verified on the target phone before implementation decisions are finalized.

## 4. MVP scope

### Included in the first product foundation

1. App shell and dashboard.
2. Permission and capability onboarding.
3. Incoming-call detection using supported Android APIs.
4. Caller information display using data legitimately available to the app.
5. Call screening preferences and outcomes, within the supported `CallScreeningService` contract.
6. Local call records and settings.
7. Clear privacy disclosures and controls.

### Candidate capabilities after feasibility validation

- Default-dialer experience using `InCallService`.
- User-triggered or supported call notes.
- Speech-to-text and text-to-speech for permitted audio paths.
- AI-generated caller-purpose extraction and call summaries.
- Automated AI voice conversation, only if a supported and policy-compliant audio path is demonstrated on the target device.

### Explicitly deferred

- WhatsApp call answering, interception, or automation.
- Other third-party VoIP call handling.
- Cross-device synchronization.
- Claims of universal AI call answering.

## 5. Functional requirements

- The app must explain why each permission is needed before requesting it.
- The user must be able to enable or disable screening features.
- Screening decisions must meet Android's service response requirements.
- The app must gracefully handle unavailable caller information.
- Call history must distinguish confirmed data from inferred or AI-generated information.
- AI features must disclose when information is sent to a remote service.
- Users must be able to review and delete locally stored app records.
- The app must not secretly record or transmit calls.

## 6. Non-functional requirements

- Reliability: basic screens and locally stored records should remain usable offline.
- Privacy: collect the minimum data required; protect stored data; avoid logging call content or secrets.
- Security: keep AI provider credentials on the backend, never in the APK or repository.
- Performance: call-screening work must remain within Android's response deadline.
- Compatibility: detect unavailable APIs and OEM limitations rather than assuming support.
- Maintainability: separate UI, telecom integration, domain logic, persistence, and remote AI services.

## 7. User experience outline

1. First launch: explain product scope, limitations, privacy, and permissions.
2. Setup: guide the user through only the permissions and roles needed for the selected feature.
3. Dashboard: show readiness, recent calls, and screening status.
4. Incoming call: use Android-supported system call surfaces and roles.
5. Call details: show number/contact data, screening result, notes, and any available summary.
6. Settings: manage privacy, screening rules, AI service configuration, and data retention.

## 8. Success criteria for the initial milestone

- App launches on the target phone once a buildable Android project exists.
- Permission and role states are displayed accurately.
- Incoming SIM-call events are handled through supported APIs.
- Screening behavior is tested against Android's timing and service requirements.
- Call records persist locally and can be reviewed/deleted.
- No unsupported call-audio access is claimed.
- Automated two-way AI answering has a written feasibility result before it is placed in a release scope.

## 9. Risks and mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Restricted cellular call audio | AI cannot hear/respond to both parties | Run a device/API feasibility spike before designing the conversation engine |
| OEM-specific behavior | Features differ on Vivo/Funtouch OS | Test on the actual target device and document results |
| Telecom service deadline | Screening may fail if processing is slow | Keep screening logic local, bounded, and deterministic |
| Permission/role friction | Setup abandonment | Explain each permission and provide clear recovery guidance |
| Privacy/legal requirements | Recording/transcription may be unlawful or disallowed | Require transparency, consent where required, and user controls |
| API key exposure | Backend compromise and abuse | Store provider keys only in server-side environment secrets |

## 10. Product decision

The project must not equate call screening with answering a call. These are separate capabilities with different Android APIs and constraints. MVP planning should proceed with screening and call records while AI voice answering remains conditional on a successful feasibility test.
