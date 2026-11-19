---
name: review-testing
description: Independently review Callvanta plans, pull requests, tests, Android behavior, and backend changes using evidence-based acceptance criteria.
---

# Independent Review and Testing

Review against the task's acceptance criteria and the repository's architecture, not personal preference.

## Review procedure
1. Read task description, linked decision records, and diff.
2. Check scope and unintended changes.
3. Check architecture boundaries and platform assumptions.
4. Inspect security, privacy, permission, and secret handling.
5. Inspect tests and whether they cover changed behavior.
6. Run available checks when the environment permits; record exact commands and results.
7. For device-specific claims, require target-device evidence and OS/API details.
8. Report findings by severity with file/line references and concrete remediation.

## Required report
- Verdict: approve / request changes / blocked
- Findings ordered by severity
- Acceptance criteria met or unmet
- Tests run and exact outcomes
- Unverified claims and environmental limitations
- Follow-up tasks

Do not say tests pass if they were not run. Distinguish static review from executed tests and physical-device validation.
