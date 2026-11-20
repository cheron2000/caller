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
- Status: Accepted
- Decision: Saved contacts are allowed by default and are not screened by default. Contact matching is deferred until a clearly explained, separately approved contacts integration and permission strategy exists.

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
- Status: Accepted direction; exact duration required before persistence ships
- Decision: Store only minimal observed event metadata and screening result. Do not store call audio or transcripts. Provide per-record and delete-all controls. Retention must be user-configurable with a conservative default; the exact default duration and backup behavior must be recorded before the persistence feature is released.

## D-016 — Contacts permission
- Status: Accepted
- Decision: Defer contacts permission and contact lookup until a separate contacts feature is approved.

## D-017 — AI and backend
- Status: Accepted
- Decision: Exclude backend and AI processing from the first milestone. Core screening and local app functionality must work offline. Any future AI processing requires an approved input source, explicit disclosure, data minimization, and server-side credentials.

## D-018 — AI answering
- Status: Accepted for MVP
- Decision: Automated two-way AI answering over ordinary cellular calls is out of scope and NO-GO for the first milestone. Reopen only after a documented, supported, consent-aware receive-and-transmit audio path is demonstrated on the target device and receives project-owner, platform-policy, and legal review.

## D-019 — Backup and privacy
- Status: Accepted direction; implementation detail required before persistence ships
- Decision: Do not include sensitive call metadata in automatic cloud backup by default. Confirm the Android backup/data-extraction configuration when the persistence implementation is designed.

## D-020 — Target device validation
- Status: Accepted
- Decision: Before claiming Vivo Y39 compatibility, record the exact model code, Android/API level, Funtouch OS/build, region, SIM configuration, and carrier, then test actual role availability, callbacks, cold-start timing, and background behavior. No device test has yet been completed.

## D-021 — Active agent
- Status: Accepted
- Decision: ChatGPT is the sole active AI agent and owns coordination, architecture, implementation planning, coding, testing, and integration until the project owner changes the assignment. Claude and Manus are inactive; do not wait on them.

## D-022 — Direct-to-main workflow
- Status: Accepted
- Decision: ChatGPT may make focused, documented commits directly to `main` without opening PRs. This is a workflow choice for the current single-agent setup, not permission to bypass owner approval for product, privacy, legal, or release decisions.
