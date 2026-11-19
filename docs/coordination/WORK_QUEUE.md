# Shared Work Queue

Only the project owner or coordinator should assign/reassign owners. Each task has exactly one accountable owner.

| ID | Task | Owner | Status | Branch / PR | Acceptance criteria |
|---|---|---|---|---|---|
| CV-001 | Review product plan and identify unresolved requirements | Manus | In review | [`agent/manus/CV-001-CV-002-validation`](https://github.com/cheron2000/caller/pull/1) | Findings documented with evidence and open questions |
| CV-002 | Review Android Telecom feasibility for Vivo Y39 | Manus | In review | [`agent/manus/CV-001-CV-002-validation`](https://github.com/cheron2000/caller/pull/1) | API/OEM constraints, sources, test plan, go/no-go recommendation |
| CV-003 | Review architecture for consistency and maintainability | Claude | Ready | — | Architecture review with risks and proposed changes |
| CV-004 | Establish cross-agent collaboration workflow | ChatGPT | Done | main | Shared instructions, seven skills, role rules, and handoff process committed |
| CV-005 | Approve MVP scope and platform feasibility | Project owner | Blocked on CV-001/CV-002/CV-003 | — | Owner records approved decisions in DECISIONS.md |

## Status vocabulary
- Ready: assigned and can begin.
- In progress: owner is actively working.
- Blocked: waiting for a dependency or owner decision.
- In review: work is submitted for independent review.
- Done: acceptance criteria met and evidence linked.
- Rejected: owner/coordinator declined the proposal with rationale.

## Assignment rule
Do not start another agent's active task. If help is needed, request a review or create a separate task with a non-overlapping scope.
