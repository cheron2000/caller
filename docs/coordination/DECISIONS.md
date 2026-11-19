# Architecture and Product Decision Log

Record approved decisions here. Proposed changes belong in a PR or a clearly marked proposal; do not rewrite accepted decisions without review.

## D-001 — Product working name
- Status: Accepted as working name
- Decision: Callvanta
- Note: Trademark and domain clearance have not been completed.

## D-002 — Initial call scope
- Status: Accepted
- Decision: Ordinary SIM/cellular calls only. WhatsApp and other VoIP integrations are deferred.

## D-003 — AI answering feasibility
- Status: Conditional / not yet proven
- Decision: Do not promise live two-way AI call answering until a documented, supported, consent-aware audio path is validated on the target Vivo device and complies with platform and legal requirements.

## D-004 — Platform boundaries
- Status: Accepted
- Decision: `CallScreeningService` is for screening decisions; default-dialer integration may require `InCallService`. Neither implies unrestricted access to both sides of cellular call audio.

## D-005 — Technology direction
- Status: Proposed
- Decision: Kotlin + Jetpack Compose Android client; optional Python + FastAPI backend; local persistence; provider-neutral AI boundary. Final versions and dependencies are to be selected during implementation planning.

## D-006 — Repository workflow
- Status: Accepted
- Decision: GitHub is the shared source of truth. Agents use isolated branches and PRs; no concurrent editing of the same branch/file.
