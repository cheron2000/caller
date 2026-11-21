# Architecture and Product Decision Log

Record approved decisions here. Changes to product behavior, privacy, platform strategy, or major dependencies require explicit project-owner approval.

## D-001 — Product working name
- Status: Accepted as working name
- Decision: Callvanta
- Note: Trademark and domain clearance have not been completed.

## D-002 — Initial call scope
- Status: Accepted
- Decision: Ordinary SIM/cellular calls only. WhatsApp and other VoIP integrations are deferred.

## D-003 — AI answering feasibility
- Status: Conditional / not approved for MVP
- Decision: Do not promise live two-way AI call answering until a documented, supported, consent-aware audio path is validated on the target Vivo device and complies with platform and legal requirements.

## D-004 — Platform boundaries
- Status: Accepted
- Decision: `CallScreeningService` is for screening decisions; default-dialer integration may require `InCallService`. Neither implies unrestricted access to both sides of cellular call audio.

## D-005 — Technology direction
- Status: Accepted direction
- Decision: Kotlin + Jetpack Compose Android client, local persistence, and provider-neutral boundaries. Python + FastAPI remains optional and is excluded from the first milestone. Exact dependency versions will be selected when the Android project is created.

## D-006 — Repository workflow
- Status: Accepted
- Decision: GitHub is the source of truth. ChatGPT is the sole active AI agent for now. To reduce process overhead for this single-agent project, make focused commits directly to `main`; do not open pull requests unless the project owner asks or additional agents are reactivated. The project owner retains final authority over scope and releases.

## D-007 — First milestone call direction
- Status: Accepted
- Decision: Incoming SIM/cellular call screening only. Outgoing-call screening is not part of the first milestone.

## D-008 — Screening actions
- Status: Accepted
- Decision: The user-facing screening policy will use allow, silence, and reject actions only where supported by the Android screening contract. Do not expose a separate “block” action until its exact semantics and implementation are validated.

## D-009 — Default screening behavior
- Status: Accepted
- Decision: Allow a call when no rule matches or when policy data is unavailable. The screening callback must use a bounded, deterministic fallback and must not wait for a network or AI service.

## D-010 — Rule precedence
- Status: Accepted
- Decision: Rule evaluation must be deterministic. Use explicit priority, then most-specific match, then stable creation order as tie-breakers. The rule engine must be independently unit-testable.

## D-011 — Saved contacts
- Status: Superseded by D-024
- Decision: Saved contacts remain allowed by default when no explicit rule matches. If the user creates an exact-number or prefix rule for a saved contact, that explicit rule must be evaluated and applied after the user grants Contacts permission. Do not silently bypass a matching rule because the caller is saved.

## D-012 — Withheld and unknown caller ID
- Status: Accepted
- Decision: Treat withheld/unknown caller ID as a distinct configurable rule category. Default behavior is allow; missing caller metadata is not evidence that a call is spam.

## D-013 — Call history scope
- Status: Accepted
- Decision: The first milestone records only Callvanta-observed screening events delivered through supported callbacks. Do not describe this as complete device call history and do not request broad call-log access for this feature.

## D-014 — Default dialer
- Status: Accepted
- Decision: Defer `ROLE_DIALER`, `InCallService`, and replacement in-call UI from the first milestone.

## D-015 — Stored data and retention
- Status: Accepted
- Decision: Store only minimal observed event metadata and screening result. Do not store call audio or transcripts. Provide per-record and delete-all controls. Automatically delete screening activity after 30 days. Do not add user-configurable retention in the first release; revisit only if needed.

## D-016 — Contacts permission
- Status: Superseded by D-024
- Decision: Request the Android READ_CONTACTS runtime permission with a clear explanation. This permission is required for Android to deliver saved-contact calls to Callvanta's CallScreeningService. Use it only to enable screening callbacks and local rule matching; do not upload or persist the user's address book.

## D-017 — AI and backend
- Status: Accepted
- Decision: Exclude backend and AI processing from the first milestone. Core screening and local app functionality must work offline. Any future AI processing requires an approved input source, explicit disclosure, data minimization, and server-side credentials.

## D-018 — AI answering
- Status: Accepted for MVP
- Decision: Automated two-way AI answering over ordinary cellular calls is out of scope and NO-GO for the first milestone. Reopen only after a documented, supported, consent-aware receive-and-transmit audio path is demonstrated on the target device and receives project-owner, platform-policy, and legal review.

## D-019 — Backup and privacy
- Status: Accepted
- Decision: Do not include sensitive call metadata or screening rules in automatic cloud backup. The Android application backup setting remains disabled (`allowBackup=false`); verify data-extraction rules if backup behavior changes in a future release.

## D-020 — Target device validation
- Status: Accepted
- Decision: Before claiming Vivo Y39 compatibility, record the exact model code, Android/API level, Funtouch OS/build, region, SIM configuration, and carrier, then test actual role availability, callbacks, cold-start timing, and background behavior. No device test has yet been completed.

## D-021 — Active agent
- Status: Accepted
- Decision: ChatGPT is the sole active AI agent and owns coordination, architecture, implementation planning, coding, testing, and integration until the project owner changes the assignment. Claude and Manus are inactive; do not wait on them.

## D-022 — Direct-to-main workflow
- Status: Accepted
- Decision: ChatGPT may make focused, documented commits directly to `main` without opening PRs. This is a workflow choice for the current single-agent setup, not permission to bypass owner approval for product, privacy, legal, or release decisions.


## D-023 — Local data retention and backup
- Status: Accepted
- Decision: Retain Callvanta-observed screening activity for 30 days, then delete expired events automatically. Keep Android cloud backup disabled. This retention policy applies when event persistence is implemented; no call audio or transcripts may be stored.


## D-024 — Apply explicit rules to saved contacts
- Status: Accepted
- Decision: Saved-contact calls must be eligible for exact-number and prefix rules. Add the READ_CONTACTS manifest declaration and a user-triggered runtime permission request with clear disclosure. Without permission, clearly explain that Android will not deliver saved-contact calls; do not claim that rules can screen them. Saved contacts remain allowed by default only when no explicit rule matches. Restricted/unknown/unavailable/payphone presentations remain outside the supported screening callback contract.


## D-025 — Existing-number AI receptionist routing
- Status: Accepted
- Decision: The product direction is conditional forwarding from the user's existing SIM/mobile number to a cloud-hosted Callvanta AI receptionist. The user's existing number remains the public number. Initial forwarding conditions are unanswered, busy, and unreachable where the carrier supports them; unconditional forwarding is not the default.
- Architecture: The carrier forwards the call to a telephony provider that exposes a documented bidirectional media interface to Callvanta's backend. Android remains the configuration and call-history client; it is not the cellular audio bridge.
- Provider status: Exotel is the first provider candidate for technical validation because its Voicebot Applet documents bidirectional WebSocket media. Do not treat Exotel as selected/contracted until account eligibility, number/forwarding compatibility, feature enablement, pricing, and applicable Indian telecom requirements are confirmed.
- Safety and privacy: The AI must identify itself as an AI assistant at the start of the call, disclose AI processing/recording or transcription as applicable, obtain consent where required, minimize retained content, and provide a human/voicemail fallback where configured.
- Limitation: Carrier forwarding availability, activation method, charges, caller-ID presentation, and forwarding behavior must be verified for the user's specific operator and plan before setup instructions are issued.
