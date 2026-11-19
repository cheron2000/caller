# NOVA AI — Implementation Roadmap

The roadmap is staged so that the project can deliver useful supported functionality without depending on an unverified cellular-audio capability.

## Phase 0 — Product and platform validation

- [ ] Confirm target device model, Android API level, and Funtouch OS version.
- [ ] Review Android Telecom role and service requirements.
- [ ] Verify whether the app can be selected as default dialer on the target device.
- [ ] Write a privacy/data-flow inventory.
- [ ] Define MVP acceptance tests.
- [ ] Record a go/no-go decision for live AI call audio.

**Exit condition:** approved scope and a documented platform feasibility report.

## Phase 1 — Android foundation

- [ ] Create the Android Gradle project and app module.
- [ ] Establish Kotlin and Jetpack Compose conventions.
- [ ] Add app navigation and dashboard.
- [ ] Add onboarding, permission explanations, and capability status.
- [ ] Define domain models and repository interfaces.
- [ ] Set up local persistence.
- [ ] Add basic tests and a cloud build workflow.

**Exit condition:** installable app shell with no unsupported call handling claims.

## Phase 2 — Cellular call awareness and screening

- [ ] Integrate supported call-screening service.
- [ ] Add local screening preferences.
- [ ] Handle missing/unknown caller metadata.
- [ ] Persist screening outcomes and call records where permitted.
- [ ] Validate service timing and lifecycle on Vivo Y39.
- [ ] Test permission denial, role changes, reboot, and background behavior.

**Exit condition:** reliable, tested call screening within Android's contract.

## Phase 3 — Default dialer evaluation

- [ ] Implement the required default-dialer role flow if product scope requires it.
- [ ] Evaluate `InCallService` lifecycle and call controls.
- [ ] Build a compliant in-call UI.
- [ ] Test incoming and outgoing call scenarios on the target device.
- [ ] Document OEM-specific limitations.

**Exit condition:** stable in-call integration, or a documented decision to omit it.

## Phase 4 — AI and speech features (conditional)

- [ ] Establish a provider-neutral AI service contract.
- [ ] Build optional backend authentication and request validation.
- [ ] Configure provider credentials only in backend secrets.
- [ ] Add permitted text summarization and caller-purpose extraction.
- [ ] Add speech recognition/synthesis only for supported audio sources.
- [ ] Add transparent disclosure, consent, and deletion controls.
- [ ] Revisit live two-way conversation only if Phase 0 feasibility criteria are met.

**Exit condition:** tested AI assistance that uses only permitted inputs and has safe failure behavior.

## Phase 5 — Hardening and release

- [ ] Security and privacy review.
- [ ] Accessibility and usability review.
- [ ] Device compatibility testing.
- [ ] Battery and performance checks.
- [ ] Release signing and secret management.
- [ ] User-facing limitations and privacy documentation.
- [ ] Versioned release and rollback plan.

## Deferred track — VoIP applications

WhatsApp and other third-party VoIP integrations are out of the initial scope. Reassess separately after the cellular MVP is stable, using only supported integration points and without claiming call interception or answering capabilities that the platform does not expose.
