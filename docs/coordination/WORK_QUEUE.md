# Shared Work Queue

The project owner retains final authority over product scope and merges. ChatGPT is currently the sole active AI agent and owns the coordinator/integrator role.

| ID | Task | Owner | Status | Branch / PR | Acceptance criteria |
|---|---|---|---|---|---|
| CV-001 | Review product plan and identify unresolved requirements | ChatGPT | In review | [Manus findings PR #1](https://github.com/cheron2000/caller/pull/1) | Findings independently reviewed; owner requirements listed in `OWNER_DECISIONS_REQUIRED.md` |
| CV-002 | Review Android Telecom feasibility for Vivo Y39 | ChatGPT | In review | [Manus findings PR #1](https://github.com/cheron2000/caller/pull/1) | Android constraints and device validation gaps reviewed; no device test claimed |
| CV-003 | Review architecture for consistency and maintainability | ChatGPT | In review | `agent/chatgpt/CV-003-architecture-review` (PR pending) | Architecture report completed with findings, priorities, and implementation sequence |
| CV-004 | Establish cross-agent collaboration workflow | ChatGPT | Done | main | Shared instructions and seven skills committed |
| CV-005 | Confirm MVP product and privacy decisions | Project owner | Blocked on owner responses | `OWNER_DECISIONS_REQUIRED.md` | Owner confirms/amends decisions; coordinator records accepted decisions in `DECISIONS.md` |
| CV-006 | Prepare implementation-ready Android foundation plan | ChatGPT | Blocked on CV-005 | — | Approved scope, module/package plan, build plan, test plan, and device validation checklist |

## Status vocabulary
- Ready: assigned and can begin.
- In progress: owner is actively working.
- Blocked: waiting for a dependency or owner decision.
- In review: deliverable exists and awaits owner/coordinator integration or merge.
- Done: acceptance criteria met and evidence linked.
- Rejected: owner/coordinator declined the proposal with rationale.

## Current handoff
- CV-001/CV-002 findings were produced by Manus on its branch and independently reviewed by ChatGPT. PR #1 remains unmerged.
- CV-003 architecture review was completed by ChatGPT on `agent/chatgpt/CV-003-architecture-review`; PR creation is the next repository action.
- The project remains documentation-only. No app source code, Gradle configuration, backend implementation, or build workflow is authorized yet.
- CV-005 requires project-owner choices. Recommendations are proposals, not accepted decisions.

## Assignment rule
Only one active agent should edit a given branch or file at a time. The project owner may reassign tasks and approve implementation.
