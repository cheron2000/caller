---
name: agent-coordination
description: Coordinate ChatGPT, Claude, and Manus through shared task ownership, isolated Git branches, pull requests, and explicit handoffs in the Callvanta repository.
---

# Agent Coordination Protocol

Read `AGENTS.md` and `docs/coordination/WORK_QUEUE.md` before starting.

## Roles
- ChatGPT: coordinator, task decomposition, decision tracking, integration review.
- Claude: architecture and assigned implementation work.
- Manus: independent research, feasibility checks, verification, and assigned execution.
- Roles can be reassigned by the project owner.

## Workflow
1. Select a task assigned to you. Do not take another agent's active task.
2. Confirm scope, acceptance criteria, and dependencies.
3. Create branch `agent/<agent>/<task-id>-<short-description>` from current `main`.
4. Work only on that branch; do not edit another agent's active branch.
5. Commit focused changes and open a PR to `main`.
6. Include task ID, summary, changed files, verification evidence, risks, and next steps.
7. Update the work queue and append a handoff entry.
8. Wait for review and owner approval before merge.

## Conflict avoidance
- One accountable owner per task.
- Never have two agents edit the same file concurrently.
- If another agent's work is needed, request a review or create a non-overlapping task.
- Rebase/merge only after checking current branch state; never force-push without explicit authorization.

## Truthfulness
Never claim a build, test, device check, or external action succeeded without running it and recording evidence. Report blockers instead of guessing.
