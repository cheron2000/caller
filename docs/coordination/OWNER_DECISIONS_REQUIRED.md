# Callvanta — Owner Decisions Required Before Implementation

This document converts the open requirements identified in CV-001/CV-002 and CV-003 into concrete choices. The “recommended default” column is a proposal from the coordinator, not an approved product decision.

| ID | Decision | Recommended default | Why it is recommended | Owner response |
|---|---|---|---|---|
| O-001 | First milestone call scope | Incoming SIM calls only | Keeps the first Telecom path narrow and testable | Pending |
| O-002 | Screening actions | Allow, silence, reject; expose only actions supported by the API contract | Avoids implying unsupported blocking semantics | Pending |
| O-003 | Default screening behavior | Allow when no rule matches or policy data is unavailable | Avoids unexpectedly suppressing calls; confirm product intent | Pending |
| O-004 | Rule precedence | Explicit priority, then most-specific match, then stable creation order | Makes outcomes deterministic | Pending |
| O-005 | Saved contacts | Do not screen contacts by default; make contact matching a separately enabled option | Reduces permission and false-positive risk | Pending |
| O-006 | Withheld/unknown caller ID | Apply a dedicated user-configurable rule; default allow | Avoids treating missing metadata as proof of spam | Pending |
| O-007 | Call history | Callvanta-observed screening events only; do not promise full device call history | Screening callbacks are not a complete call-log feed | Pending |
| O-008 | Default dialer | Defer ROLE_DIALER and InCallService from the first milestone | Significantly expands scope and validation requirements | Pending |
| O-009 | Stored data | Minimal metadata and screening result; no audio or transcript | Minimizes sensitive data collection | Pending |
| O-010 | Retention | User-configurable retention with a conservative default, plus delete-one and delete-all | Gives users control; exact duration needs owner choice | Pending |
| O-011 | Contacts permission | Defer until the user enables a clearly described contacts feature | Least-privilege approach | Pending |
| O-012 | AI/backend | Exclude from first milestone; local screening must work offline | No AI input source or live call audio path is approved | Pending |
| O-013 | AI answering | NO-GO for ordinary cellular calls until supported two-way audio path and policy/legal review are demonstrated | Android public API constraints | Pending |
| O-014 | Backup | Exclude sensitive call metadata from automatic cloud backup unless explicitly approved | Prevents unintended secondary copies | Pending |
| O-015 | Target device validation | Confirm exact Vivo model code, Android/API level, Funtouch build, SIM and carrier before compatibility claims | Regional/product specifications do not prove the actual unit's state | Pending |

## Proposed first-milestone acceptance criteria

- User can see whether the call-screening role is available, granted, denied, or revoked.
- User can enable/disable screening with an explicit explanation.
- Incoming call screening uses only metadata delivered by the supported callback.
- Rule evaluation is deterministic, local, bounded, and independently unit-tested.
- No network request or LLM invocation occurs in the screening callback.
- The app records only Callvanta-observed events and labels missing fields as unavailable.
- User can delete individual records and all Callvanta records.
- The app makes no claim of full call history or live AI answering.
- Actual target-device results are documented separately from API-level expectations.

## Approval record

No owner decisions have been approved by this document. The project owner must confirm or amend the recommended defaults, then the coordinator will record accepted decisions in `DECISIONS.md`.
