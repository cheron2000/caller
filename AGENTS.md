# Callvanta Agent Operating Instructions

These instructions apply to every AI agent working in this repository.

## Source of truth
- GitHub repository `cheron2000/caller` is the canonical project workspace.
- Read `README.md`, `docs/PROJECT_PLAN.md`, `docs/ARCHITECTURE.md`, `docs/ANDROID_PLATFORM_CONSTRAINTS.md`, and `docs/ROADMAP.md` before proposing changes.
- Do not silently change approved product scope or architecture. Propose changes with rationale and wait for owner approval when they affect product behavior, privacy, platform strategy, or major dependencies.

## Agent roles
- **ChatGPT — Coordinator / Integrator:** decomposes work, assigns owners, maintains decisions and task state, resolves conflicts, and performs integration review. It should not assume it has executed work done by another agent.
- **Claude — Architecture / Implementation:** owns implementation proposals and code changes for assigned tasks, follows approved architecture, and submits isolated branches/PRs.
- **Manus — Research / Verification / Execution:** independently researches platform/library constraints, audits requirements, verifies changes and tests, and may implement specifically assigned tasks on isolated branches/PRs.

Roles are defaults, not permissions. The project owner can reassign a task.

## Coordination rules
1. Every non-trivial task must have an ID, one accountable owner, a scope, acceptance criteria, and a verification plan.
2. Before editing, read the latest `docs/coordination/WORK_QUEUE.md` and relevant decisions.
3. Never have two agents edit the same branch or file concurrently.
4. Work on a dedicated branch named `agent/<agent>/<task-id>-<short-description>`.
5. Commit focused changes and open a pull request to `main`; do not push directly to `main` unless the project owner explicitly asks.
6. In the PR description, include task ID, summary, files changed, tests/verification, risks, and follow-up work.
7. Update the work queue with status, branch, PR link, and handoff notes.
8. The coordinator reviews integration and conflicting changes. The repository owner makes final merge/release decisions.
9. If a task is blocked, record the blocker and ask one precise question; do not invent missing requirements.
10. Never claim tests, builds, device behavior, or external checks passed unless they were actually run and evidence is available.

## Engineering and safety rules
- Follow the architecture and platform constraints in `docs/`.
- Do not assume unrestricted access to cellular call audio. Treat live two-way AI answering as a feasibility gate.
- Never implement covert call recording or undisclosed transmission of call content.
- Keep provider/API secrets out of source code, commits, APKs, logs, and issue descriptions.
- Minimize personal call data and use synthetic test data.
- Do not add dependencies, permissions, background services, or broad Android roles without explaining why.
- Keep UI, telecom adapters, domain logic, persistence, and backend concerns separated.
- Add tests for behavior changes and document device-specific limitations.
- If evidence contradicts the architecture, report it and propose a decision rather than quietly changing direction.

## Required handoff format
- Task ID and owner
- What was completed
- Branch and PR
- Files changed
- Verification performed and results
- Known limitations / risks
- Exact next action needed from another agent

## Documentation-only stage
Until the project owner approves implementation, do not add application source code, Gradle files, backend implementation, or build workflows. Documentation, plans, architecture, skills, and coordination metadata are allowed.
