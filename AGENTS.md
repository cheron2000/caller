# Callvanta Agent Operating Instructions

These instructions apply to every AI agent working in this repository.

## Source of truth
- GitHub repository `cheron2000/caller` is the canonical project workspace.
- Read `README.md`, `docs/PROJECT_PLAN.md`, `docs/ARCHITECTURE.md`, `docs/ANDROID_PLATFORM_CONSTRAINTS.md`, `docs/ROADMAP.md`, and `docs/coordination/DECISIONS.md` before proposing changes.
- Do not silently change approved product scope or architecture. Changes affecting product behavior, privacy, platform strategy, or major dependencies require project-owner approval.

## Active project staffing
- **ChatGPT is the sole active AI agent and coordinator/integrator** until the project owner explicitly changes the assignment.
- ChatGPT owns planning, architecture, documentation, implementation, testing, review, and integration.
- Claude and Manus are inactive for now; do not wait for their responses or assign them blocking dependencies.
- The project owner retains final authority over product scope, sensitive behavior, and releases.

## Workflow
1. Read the latest `docs/coordination/WORK_QUEUE.md` and relevant decisions before each task.
2. Keep changes focused and make direct commits to `main` for the current single-agent workflow. Do not open PRs unless the project owner asks or additional agents are reactivated.
3. Never claim tests, builds, device behavior, or external checks passed unless they were actually run and evidence is available.
4. Update the work queue and decision log when task state or approved scope changes.
5. If a task is blocked by a product, privacy, or legal choice, record the options and recommendation and ask the owner; do not silently choose.
6. Do not discard earlier agent work. Review it, preserve useful findings, and record its provenance.

## Engineering and safety rules
- Follow the architecture and platform constraints in `docs/`.
- Do not assume unrestricted access to cellular call audio. Automated two-way AI answering remains out of scope for the MVP.
- Never implement covert call recording or undisclosed transmission of call content.
- Keep provider/API secrets out of source code, commits, APKs, logs, and issue descriptions.
- Minimize personal call data and use synthetic test data.
- Do not add dependencies, permissions, background services, or broad Android roles without explaining why.
- Keep UI, telecom adapters, domain logic, persistence, and backend concerns separated.
- Add tests for behavior changes and document device-specific limitations.
- Keep screening local, deterministic, bounded, and independent of network/LLM availability.
- If evidence contradicts the architecture, report it and propose a decision rather than quietly changing direction.

## Current approved first milestone
- Incoming SIM/cellular calls only.
- Allow, silence, and reject only where supported by Android's screening contract.
- Allow by default when no rule matches or policy data is unavailable.
- Saved contacts allowed by default; contacts integration deferred.
- Withheld/unknown caller ID has a distinct configurable rule, default allow.
- Record Callvanta-observed screening events only, not complete device call history.
- Defer default-dialer role and in-call UI.
- No call audio, transcripts, covert recording, AI answering, backend, or AI processing in the first milestone.
- Minimal local metadata, per-record/delete-all controls; exact retention duration and backup configuration must be decided before persistence ships.

## Required task discipline
Every non-trivial task needs an ID, scope, acceptance criteria, and verification plan. Keep work small, document limitations, and report exact files changed and verification performed. The project owner makes final release decisions.

## Documentation-only stage
The owner has now approved the initial MVP scope. Implementation may begin according to the work queue. Do not expand scope beyond the decisions recorded in `docs/coordination/DECISIONS.md` without asking the owner.
