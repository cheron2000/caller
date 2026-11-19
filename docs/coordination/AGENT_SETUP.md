# Connecting the agents to Callvanta

## GitHub access

Connect the GitHub account to each agent and grant access only to `cheron2000/caller` where possible.

- ChatGPT: use the already-connected GitHub integration.
- Claude: connect the repository through Claude's GitHub integration or supported GitHub MCP connector.
- Manus: use Manus Connectors → GitHub → Connect, then authorize the `caller` repository.

The GitHub connector gives repository access; it does not automatically guarantee that every skill is loaded.

## Shared repository instructions

All agents must read `AGENTS.md` and the coordination documents before taking work.

## Skills

Canonical skill files are under `.agents/skills/<skill-name>/SKILL.md`. Claude-compatible copies are under `.claude/skills/<skill-name>/SKILL.md`.

### Claude
Use the repository's `.claude/skills/` entries in a Claude Code workflow. If using Claude mobile/app rather than Claude Code, ask it to read `AGENTS.md` and the relevant `.agents/skills/` files through its GitHub connector; automatic skill discovery depends on the Claude product and integration.

### Manus Android app
1. Connect GitHub in Manus and authorize the `caller` repository.
2. Select `cheron2000/caller` in a Manus task.
3. Ask Manus to read `AGENTS.md`, `docs/coordination/WORK_QUEUE.md`, and the relevant `.agents/skills/*/SKILL.md` files before acting.
4. For persistent Manus Skill installation, use Manus Skills → Add → Import from GitHub if the repository is eligible for import. Manus's documented GitHub skill-import flow currently describes importing a public GitHub repository; do not make the private project public solely for this. The built-in GitHub connector can still let Manus read the private repository if authorized.

## First Manus task

Start with task `CV-001` or `CV-002` from the work queue. Ask Manus to work read-only, cite official sources, and write its findings to a new branch and PR only after the project owner approves the task.

## Safety

Do not paste personal access tokens into prompts or commit them. Do not allow multiple agents to work on the same branch or edit the same file concurrently.
