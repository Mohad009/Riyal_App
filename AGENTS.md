# Riyal_App — agent guide

Read this first; it is a map. The workflow that governs work here is `Cazy00/agent-workflow` at the revision pinned in `docs/workflow/config.json`, installed at `.cache/agent-workflow/` (`scripts/wf records` fetches it when missing). Start with its `.agents/skills/workflow/SKILL.md`, then only the procedure for your next action.

| Need | Go to |
|---|---|
| What the project is, who decides what, quality bar, delivery | `docs/workflow/profile.md` |
| Milestones, tasks, decisions | `docs/workflow/milestones/`, `docs/workflow/tasks/`, `docs/workflow/decisions/`; only records on the approved baseline count |
| Raw owner input awaiting triage (no authority) | `docs/workflow/inbox/` |
| Acceptance definitions and test mappings | `docs/workflow/acceptance.json`, `tests/acceptance-map.json` |
| Setup progress, owner checklist and agent steps | `docs/workflow/setup.md` |
| Progress at a glance (derived; never edit it) | the pinned *Project status* issue on GitHub, or `scripts/wf status` |
| Procedures, templates, policy | `.cache/agent-workflow/procedures/`, `templates/`, `POLICY.md` |

Commands: `scripts/wf records | readiness | paths | ci | acceptance | lifecycle | session | status` (usage in the installation's `QUICKSTART.md`). Run evidence, reviews and handoffs go on the task's pull request or issue, never into this repository. One pull request per task, never per step. When the repository allows auto-merge, turn it on as you open the pull request (`gh pr merge --auto` with the merge method the repository allows); bring a branch that falls behind up to date (`gh pr update-branch`); after a settings change, rerun a check so auto-merge re-evaluates. Never approve, merge by hand, sign or bypass a rule yourself; never switch to the owner's account.
