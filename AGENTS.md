# Callvanta Agent Operating Instructions

These instructions apply to every AI agent working in this repository.

## Source of truth
- GitHub repository `cheron2000/caller` is the canonical project workspace.
- Read `README.md`, `docs/PROJECT_PLAN.md`, `docs/ARCHITECTURE.md`, `docs/ANDROID_PLATFORM_CONSTRAINTS.md`, and `docs/ROADMAP.md` before proposing changes.
- Do not silently change approved product scope or architecture. Propose changes with rationale and wait for owner approval when they affect product behavior, privacy, platform strategy, or major dependencies.

## Active project staffing
- **ChatGPT is the sole active AI agent and coordinator/integrator** until the project owner explicitly reactivates or assigns another agent.
- ChatGPT owns planning, architecture, documentation, implementation, testing, review, and integration tasks assigned in the work queue.
- Claude and Manus are inactive for now; do not wait for their responses or assign them blocking dependencies.
- The project owner retains final authority over product scope, sensitive behavior, and merge/release decisions.
- These assignments can be changed by the project owner at any time.

## Coordination rules
1. Every non-trivial task must have an ID, one accountable owner, a scope, acceptance criteria, and a verification plan.
2. Before editing, read the latest `docs/coordination/WORK_QUEUE.md` and relevant decisions.
3. Never have two agents edit the same branch or file concurrently.
4. Work on a dedicated branch named `agent/chatgpt/<task-id>-<short-description>` unless the owner specifies otherwise.
5. Commit focused changes and open a pull request to `main`; do not push directly to `main` unless the project owner explicitly asks.
6. In the PR description, include task ID, summary, files changed, tests/verification, risks, and follow-up work.
7. Update the work queue with status, branch, PR link, and handoff notes.
8. The project owner makes final merge/release decisions.
9. If a task is blocked by a product, privacy, or legal choice, record the options and recommendation and ask the owner; do not silently choose.
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
- Exact next action needed from the project owner

## Documentation-only stage
Until the project owner approves implementation, do not add application source code, Gradle files, backend implementation, or build workflows. Documentation, plans, architecture, skills, and coordination metadata are allowed.
