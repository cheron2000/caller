# Shared Agent Skills

Canonical repository skills live in `.agents/skills/` using the portable Agent Skills format.

Each skill has a `SKILL.md` with YAML frontmatter (`name` and `description`) and actionable instructions. Agents should load the skill relevant to their task and follow `AGENTS.md`.

## Skill set
- `callvanta-context`: project context and product constraints.
- `agent-coordination`: task ownership, branches, PRs, handoffs.
- `android-telecom-feasibility`: Android call APIs and audio feasibility.
- `android-engineering`: Android client architecture and quality rules.
- `backend-engineering`: FastAPI and provider boundary rules.
- `security-privacy`: secrets, call data, consent, threat boundaries.
- `review-testing`: independent review and evidence-based testing.

These are shared instructions, not executable application code.
