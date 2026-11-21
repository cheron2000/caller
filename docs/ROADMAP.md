# Callvanta — Implementation Roadmap

The roadmap delivers supported functionality without depending on an unverified cellular-audio capability.

## Phase 0 — Product and platform validation
- [x] Review product requirements and unresolved decisions.
- [x] Review Android Telecom constraints and document Vivo Y39 validation gaps.
- [x] Approve initial MVP scope and record decisions.
- [ ] Confirm exact target device model, Android API level, Funtouch OS build, region, SIM configuration, and carrier.
- [ ] Run the physical-device role/callback validation after an installable build exists.

**Exit condition:** approved scope and documented platform feasibility. Product scope is approved; physical-device validation remains outstanding.

## Phase 1 — Android foundation
- [x] Define the implementation blueprint and initial toolchain.
- [ ] Create the Android Gradle project and app module.
- [ ] Establish Kotlin and Jetpack Compose conventions.
- [ ] Add app navigation and dashboard.
- [ ] Add onboarding and capability status.
- [ ] Define pure domain models and screening policy.
- [ ] Add unit tests for policy behavior.
- [ ] Add a remote debug build workflow and APK artifact.
- [ ] Add local persistence only after retention and backup decisions are finalized.

**Exit condition:** installable app shell with a successful remote build and no unsupported call-handling claims.

## Phase 2 — Cellular call screening
- [ ] Integrate supported call-screening service.
- [ ] Add local screening preferences and deterministic rules.
- [ ] Handle missing/unknown caller metadata.
- [ ] Persist observed screening events only after retention and deletion rules are approved.
- [ ] Validate service timing and lifecycle on Vivo Y39.
- [ ] Test role denial/revocation, reboot, and background behavior.

**Exit condition:** reliable screening within Android's contract, demonstrated on the target device.

## Phase 3 — Deferred platform evaluation
- [ ] Reconsider default-dialer integration only if product owner changes scope.
- [ ] Reconsider contacts integration only after permission and data-use review.
- [ ] Do not pursue live two-way cellular AI without documented supported audio path and policy/legal review.

## Phase 4 — Deferred AI/backend track
- [ ] Reassess a provider-neutral AI service contract only when an approved input source and user-facing feature exist.
- [ ] If approved, implement backend authentication, rate limits, validation, data minimization, and server-side secrets.
- [ ] Add permitted text summarization only from data Callvanta is allowed to process.

## Phase 5 — Hardening and release
- [ ] Security and privacy review.
- [ ] Accessibility and usability review.
- [ ] Device compatibility testing.
- [ ] Battery and performance checks.
- [ ] Release signing and secret management.
- [ ] User-facing limitations and privacy documentation.
- [ ] Versioned release and rollback plan.

## Explicit non-goals for the first milestone
- WhatsApp and other VoIP integrations.
- Default dialer replacement and custom in-call UI.
- Automated AI answering over ordinary cellular calls.
- Call audio recording, transcription, or transcript storage.
- Backend/LLM dependency for call screening.
- Full device call-history replacement.


## Phase 3 — Conditional-forwarding AI receptionist (new active direction)

- [ ] Confirm user's mobile carrier and whether conditional forwarding to an external Indian number is supported on their plan.
- [ ] Validate a cloud telephony provider account and destination number suitable for forwarded calls.
- [ ] Run a provider media proof-of-concept using documented bidirectional audio streaming; Exotel Voicebot Applet is the first candidate, subject to account enablement.
- [ ] Define the provider-neutral telephony adapter and call-session lifecycle.
- [ ] Build a Python + FastAPI voice gateway with authenticated WebSocket media handling.
- [ ] Integrate streaming speech-to-text, conversation orchestration, LLM, and streaming text-to-speech.
- [ ] Add AI identity/privacy disclosure, consent handling where required, caller-requested exit, escalation, and failure fallback.
- [ ] Add Android service configuration, readiness status, call metadata, and user-controlled deletion.
- [ ] Test end-to-end using a test number before enabling conditional forwarding on the user's personal number.
- [ ] Document carrier-specific activation/deactivation and charges only after the user's operator is confirmed.

**Exit condition:** a real forwarded call to a test number is answered by the AI, supports two-way conversation with acceptable latency, handles failure safely, and passes privacy/security review. No personal-number forwarding until the owner explicitly approves the live test.



### Airtel carrier notes (validated at planning level)

- Carrier identified by project owner: Airtel India.
- Airtel's public guidance describes conditional forwarding for busy, unanswered, and unreachable states and shows management through Airtel Thanks / call settings.
- Use the app/settings route rather than blindly dialling forwarding codes. The Department of Telecommunications ordered suspension of USSD-based call forwarding from 15 April 2024 until further notice; the order specifically cites the commonly misused `*401#` facility. Confirm current availability and charges with Airtel for the subscriber's exact plan.
- No forwarding is to be activated yet. First obtain an Exotel test destination and complete the voice pipeline; then validate forwarding to that test number only.
