# CV-003 — Architecture Review

**Owner:** ChatGPT (reassigned by project owner after the other agents became unavailable)  
**Status:** Review completed; awaiting project-owner review  
**Scope:** Consistency, feasibility, maintainability, privacy, and implementation readiness of the current Callvanta architecture.  
**Review basis:** Current `main` documents plus the Manus CV-001/CV-002 report on `agent/manus/CV-001-CV-002-validation`.  
**Limitations:** Documentation review only. No Android source code, Gradle project, backend, build, or Vivo Y39 device test exists in the reviewed repository state.

## Executive assessment

The current architecture has the right high-level separation (UI, Telecom adapter, domain, data, and optional AI/backend) and correctly treats cellular call audio as restricted. It is suitable as a direction, but it is not yet implementation-ready. The most important missing pieces are precise product behavior, a narrow data contract for observed call events, a deterministic screening policy contract, and a concrete Android service lifecycle/latency design.

**Recommendation:** Keep a single Android application module for the first implementation, with package-level boundaries. Build a local-first screening and event-recording foundation only after the owner approves the open product decisions. Do not introduce a remote AI dependency into the screening path. Keep backend and AI work out of the first implementation milestone.

## Findings

### Critical — Product behavior is underspecified

**Affected documents:** `PROJECT_PLAN.md`, `ARCHITECTURE.md`, `DECISIONS.md`

The plan does not define exact screening actions, default behavior, rule matching, priority/conflict resolution, treatment of withheld numbers, or what happens when caller metadata is absent. These choices affect user safety and call behavior and cannot be guessed during implementation.

**Recommendation:**
- Define a small explicit action enum aligned to the Android screening contract (for example, allow, silence, reject; only expose block where the platform contract and user expectation are clear).
- Define deterministic rule precedence and a safe default.
- Define behavior for private/unknown numbers and contacts.
- Keep rule evaluation pure and unit-testable in the domain layer.
- Record owner approval before wiring actions to Telecom APIs.

**Owner decision required:** Yes. See `OWNER_DECISIONS_REQUIRED.md`.

### Critical — “Call history” could imply data the app cannot observe

**Affected documents:** `PROJECT_PLAN.md`, `ARCHITECTURE.md`

The proposed `CallRecord` can be misread as a complete device call-log record. A screening service callback is not a universal call-history feed. The app must not promise every call is recorded unless a separate supported mechanism and permission strategy are approved and validated.

**Recommendation:**
- Name the initial collection “Callvanta activity” or “observed call events”.
- Store only events delivered through supported Callvanta callbacks.
- Label unavailable fields as unknown; do not infer duration or call outcome.
- Defer complete device call-log access and `READ_CALL_LOG` unless a separately justified feature requires it.
- Provide delete-all and per-record deletion behavior before persistence ships.

**Owner decision required:** Yes.

### High — Screening deadline and persistence need an explicit design

**Affected documents:** `ARCHITECTURE.md`, `ANDROID_PLATFORM_CONSTRAINTS.md`

The architecture correctly says screening must be local and bounded, but it does not define cold-start behavior, database availability, exception handling, or a fallback when rule storage cannot be read. A database write must not delay the Telecom response.

**Recommendation:**
- Keep the callback path limited to metadata normalization, a bounded in-memory policy evaluation, and the required response.
- Do not make network calls, invoke an LLM, wait for UI state, or perform expensive database work before responding.
- Load a validated, compact rules snapshot outside the critical callback where lifecycle permits; define a safe fallback if unavailable.
- Respond first within the platform deadline; persist an event asynchronously/best-effort after the decision, with an explicit failure strategy.
- Measure callback-to-response time, including cold process start, on the target device. Treat the five-second platform deadline as a hard upper bound, not a performance target.

**Owner decision required:** No for the engineering pattern; yes for the user-visible fallback action.

### High — Default dialer scope is not decided

**Affected documents:** `PROJECT_PLAN.md`, `ARCHITECTURE.md`, `DECISIONS.md`

The architecture mentions a future `InCallService` path but does not distinguish a screening-only app from a default-dialer replacement. These are materially different product scopes, UI responsibilities, testing obligations, and role prompts.

**Recommendation:** Defer default-dialer replacement from the first milestone. Build the narrow screening-role foundation first. Revisit a default-dialer experience only after the owner explicitly approves the expanded scope and a device test plan is ready.

**Owner decision required:** Yes.

### High — Data model is too broad and retention is undefined

**Affected documents:** `ARCHITECTURE.md`

The proposed `CallRecord`, `CallerProfile`, and `CallInsight` entities are conceptual only. The fields, data provenance, retention, encryption, backup behavior, and deletion semantics are unspecified. Caller numbers and notes are personal data.

**Recommendation:**
- Define a minimal observed-event model before implementing Room.
- Separate raw platform callback data, normalized event data, and AI-derived data.
- Include provenance/source and nullable availability for every non-guaranteed field.
- Avoid storing raw audio or transcripts in the MVP.
- Decide retention and deletion semantics before persistence is released.
- Review Android backup/export behavior so sensitive call metadata is not unintentionally copied to cloud backups.
- Keep logs free of phone numbers, notes, transcripts, tokens, and other call content.

**Owner decision required:** Yes for retention and backup behavior.

### High — Permission and role strategy should be least-privilege

**Affected documents:** `PROJECT_PLAN.md`, `ARCHITECTURE.md`

The documents mention permissions and roles generically. They should distinguish the call-screening role from the default-dialer role and avoid broad call-log or contacts permissions unless an approved feature truly needs them.

**Recommendation:**
- Request the screening role only when the user enables screening.
- Do not request `ROLE_DIALER` in the initial milestone if default-dialer functionality is deferred.
- Do not request `READ_CALL_LOG` for a feature that only uses the screening callback.
- Treat contacts integration as optional and separately consented; define the exact API and permission before implementation.
- Provide a capability screen showing granted, denied, unavailable, and revoked states.

**Owner decision required:** Yes for contacts integration and default-dialer inclusion.

### Medium — The initial module structure should avoid premature modularization

**Affected documents:** `PROJECT_STRUCTURE.md`, `ARCHITECTURE.md`

The conceptual layers are useful, but a multi-module Gradle architecture would add build complexity before the domain is stable and while development is phone-first.

**Recommendation:** Start with one `:app` module and package boundaries:
- `ui/` — Compose screens and UI state.
- `telecom/` — Android Telecom service and platform adapters.
- `domain/` — pure Kotlin models, policy engine, and use cases.
- `data/` — Room entities/DAO/repositories and settings storage.
- `ai/` — interfaces only until an approved AI feature exists.

Extract Gradle modules only when dependency isolation, build time, or team ownership justifies it.

### Medium — Backend and AI are premature for the first milestone

**Affected documents:** `ARCHITECTURE.md`, `PROJECT_PLAN.md`

A Python/FastAPI service and provider-neutral AI abstraction are reasonable future options, but neither is needed for local screening, local preferences, or locally observed events. Adding a backend now would increase security, deployment, network, and privacy work without enabling the first milestone.

**Recommendation:**
- Keep the backend optional and out of the first milestone.
- Do not create AI API keys or provider integrations until a specific, lawful input source and user-facing AI feature are approved.
- When introduced, use typed request/response schemas, authentication, rate limits, data minimization, timeouts, and server-side secrets.
- Never route screening decisions through the backend.

### Medium — Test strategy needs concrete layers and acceptance criteria

**Affected documents:** `PROJECT_PLAN.md`, `ARCHITECTURE.md`

The documents call for tests but do not define the first testable units or device test matrix.

**Recommendation:**
1. Pure JVM unit tests for screening-rule matching, precedence, and defaults.
2. Repository tests for persistence, retention, and deletion.
3. Android instrumentation tests for service wiring and role/capability states where feasible.
4. Manual device validation on the actual Vivo Y39 for role grant/revocation, callback delivery, cold start, dual SIM, reboot, background restrictions, and response latency.
5. Synthetic phone numbers and test contacts only.
6. Record test environment details and distinguish automated tests from manual device checks.

## Recommended implementation sequence

1. **Owner decisions:** Approve screening actions/defaults, observed-event scope, default-dialer deferral, contacts behavior, retention, and privacy/backup behavior.
2. **Project skeleton:** Create the minimal Kotlin/Compose Android project and build configuration only after scope approval.
3. **Capability/onboarding UI:** Explain the screening role and show actual role state; no unnecessary permissions.
4. **Pure domain policy:** Implement rule models, deterministic matching, precedence, safe defaults, and unit tests.
5. **Telecom adapter:** Implement the screening service with a bounded response path and explicit error fallback.
6. **Local event persistence:** Add Room only for the approved minimal observed-event fields, with deletion/retention behavior.
7. **Device validation:** Build remotely, install on the Vivo Y39, and record actual results.
8. **Later evaluation:** Decide whether default dialer, contacts, backend, or AI insights are justified. Keep two-way cellular AI answering gated.

## Proposed target boundaries

```text
app/src/main/java/com/callvanta/ai/
  ui/
    dashboard/
    onboarding/
    settings/
    activity/
  telecom/
    screening/
    capabilities/
  domain/
    model/
    screening/
    repository/
    usecase/
  data/
    local/
    repository/
    preferences/
  ai/
    contract/       # interfaces only until approved
```

This is a package proposal, not a requirement to create all directories immediately.

## Acceptance review

| Area | Assessment | Notes |
|---|---|---|
| Layer separation | Pass with refinements | Existing conceptual boundaries are sound |
| Android Telecom feasibility | Pass with strict limits | Screening is viable in principle; target-device behavior untested |
| Cellular audio assumptions | Pass | Correctly gated; must remain NO-GO absent evidence |
| Product requirements | Needs owner decisions | Screening behavior and record scope incomplete |
| Data/privacy design | Needs owner decisions | Retention, fields, deletion, backup need definition |
| Maintainability | Pass with simplification | Start single-module; avoid premature backend/modularization |
| Testability | Needs detail | Add pure policy tests and device validation matrix |
| Implementation readiness | Not yet ready | Owner decisions and device facts remain outstanding |

## Handoff

- **Task:** CV-003 — Architecture review.
- **Owner:** ChatGPT.
- **Branch:** `agent/chatgpt/CV-003-architecture-review`.
- **Files:** This report, updated work queue, updated agent operating instructions, owner decision checklist.
- **Verification:** Read current repository plan, architecture, platform constraints, decision log, and the Manus CV-001/CV-002 report from its branch. Documentation review only.
- **Not performed:** No Android build, code test, emulator run, or Vivo device test.
- **Next action:** Project owner reviews `OWNER_DECISIONS_REQUIRED.md` and approves or changes the proposed defaults before implementation begins.
