# Workflow setup — SETUP-0001

Profile, repository, workflow tag and immutable revision: docs/workflow/profile.md · Mohad009/Riyal_App · v1.6.2 · 5e4e75d31f7e51bc66904b538ef33a30ca0c0ad7
Owner authority for setup: user request on 2026-09-28 to prepare agent-owned setup files in this existing project and guide owner steps one at a time. Local preparation only; no publishing or account administration performed.
Start, two-working-day timebox (or approved alternative), agent allowance (default half a working day across the agent steps), usage per step: started 2026-09-28; two working days overall and half a working day of agent time across sessions; see accounting below.

| Control | Evidence and outcome |
|---|---|
| Source precedence and relevant project readiness | Draft profile and brief prepared; Needs discovery, no approved baseline. |
| Personal repository owner verified | User confirmed Mohad009 and retention of Mohad009/Riyal_App on 2026-09-28; owner-reported confirmation, not agent authentication. |
| Worker GitHub username verified | Owner-run explicit-token API/Git checks reported ash-dev91, Mohad009/Riyal_App, push true/admin false and a successful read-only HTTPS route on master. Actual push/protection tests remain pending. |
| Separate clones; author, Git transport and API identities | User reports separate owner/agent clones; read-only checks confirm both have local commit authors. Owner reports explicit-token worker API/Git preflight passed. Owner publishing routes and actual push/protection tests remain pending. |
| Per-route credentials, no owner fallback | Pending; no evidence of operational completion. |
| Tool entry discovery: Codex / Claude Code | Pending; no evidence of operational completion. |
| Protected requirements, decisions, acceptance, enforcement, CI, CODEOWNERS | Owner reports importing the disabled ruleset draft; CODEOWNERS is prepared locally. Publication, protected CI, check bindings and activation remain pending. |
| Plan support, admin restrictions, stale approvals, no worker bypass | User reports repository Public; current GitHub docs support rulesets and auto-merge on Free public repositories. Actual restrictions, stale-review dismissal and no-bypass settings remain pending. |
| Trusted validator revision and external trust anchor | Owner provisioned the separate trusted installation; read-only checks confirmed the pinned revision and launcher bytes matching its committed blob after LF correction. Owner-run records invocation passed; protected CI environment and approval-path test remain pending. |
| Result provenance and owner approval alternative | Pending; no evidence of operational completion. |
| Acceptance mapping and reporter | Three existing VersionTest methods mapped for M-0001; Gradle XML output and exact reporter naming unverified. |
| Required checks in the profile; each a required status check, branches up to date | Commands and proposed report names defined; protected CI wiring, real runs and GitHub requirements pending. |
| Shared project, if any: each person's worker and tools, `CODEOWNERS`, who approves what and when one is away | Not applicable: user confirmed Mohad009 is the only human approver. Single-owner CODEOWNERS prepared locally; activation pending. |
| Gate / acceptance / identity / session fixtures | Pending; no evidence of operational completion. |
| Real approval-path exercise | Pending; no evidence of operational completion. |
| Observed pilot | Pending; no evidence of operational completion. |

## Owner-controlled bootstrap
What was allowed; who performed it; when normal protections became active: agent prepared local files under the user request. No owner bootstrap, protection activation, commit, push, GitHub message or account change performed. Existing repository retained as requested; no port planned. The current clone is not yet verified as the worker clone.

2026-09-30 owner request: "cotniue with the agent-workflow. after you commit and stuff and upload i will check". Treat this as authority to commit and upload draft review branches from the verified worker route, including these prepared setup files and the Arabic change, for the owner to inspect. Keep them unmerged. This request does not establish protections, signed receipts, pilot approval, owner acceptance or release authority. Record observed publication and revision after it occurs.

2026-09-30 publication attempt: the worker route authenticated as ash-dev91 with push permission and no admin permission; a fresh worker-authenticated fetch confirmed `master` at `3b37845b02ed7a8b12f811444b724d08b5b0f92f`. The complete local bootstrap commit is `4765f6377deae8ef8481e3ce7f975cf3cf7237b7` on `codex/workflow-bootstrap`. GitHub rejected that push because the worker OAuth credential lacks `workflow` scope to create `.github/workflows/android-checks.yml`. No remote bootstrap branch was created. This review branch omits both `.github/workflows/` files so the owner can inspect the other prepared setup material; it is not an operational bootstrap. Publish the workflow files through an owner-approved credential with the required scope, or have the owner publish them from the separate owner clone after review. No alternate account or protection bypass was used.

## Supported scope and limitations
Execution mode: assisted. Runner not adopted. Optional navigation tools deferred.
Remaining controls, owner, required-before stage and evidence needed: owner checklist below and profile Unknowns. Local preparation is not operational adoption. Status publication is prepared for master; manual mode remains in both config and profile.

## Owner steps (procedures/setup.md; the agent does not attempt, retry or wait on these)

- [ ] 2. Verify the owner and worker GitHub usernames; separate human and agent clones; follow `identity.md`
- [ ] 5. Branch protection or rulesets, `CODEOWNERS`, required status checks (`wf ci` now; each entry of the profile's `required_checks` when approving the profile change that defines it), branches up to date before merging, stale-review dismissal, no worker bypass; a ruleset with code-owner review, 0 required approvals and approval of the most recent push off, `CODEOWNERS` `* @OWNER` then `/docs/workflow/tasks/` and `/docs/workflow/feedback/` unowned, and *Allow auto-merge* on, so a records-only pull request merges after its checks (`setup.md` step 5); record when protections became active; fill `approval.approver` and `approval.agent_identity` in `docs/workflow/config.json`
- [ ] Pin the *Project status* issue once `.github/workflows/wf-status.yml` has opened it on the trusted branch (or run that workflow from the Actions tab)
- [ ] 6. External launcher pinned to `5e4e75d31f7e51bc66904b538ef33a30ca0c0ad7` (`WF_VALIDATOR_REPO`, `WF_VALIDATOR_REV`) outside the agent clone. Approval mode is `manual` until steps 5 and 9 pass, and nothing is approved in it; then switch `approval.label` (config) and `approval_label` (profile) to `enforced` in one code-owner-reviewed pull request, or provision the owner public key and sign receipts (`approval-evidence.md`)
- [ ] 9. Real approval-path test in a disposable branch or repository, including what bringing an approved branch up to date does
- [ ] 10. Central reporting config and private runtime directory (`operations.md`), if adopted
- [ ] 11. Observe the pilot milestone

## Agent steps (one session at a time; post progress here and stop)

- [x] 1. Fill `docs/workflow/profile.md` from the owner's input and `docs/workflow/inbox/`; leave *Unknowns* honest, each with its required-before stage
- [x] 4. Add the stack-specific path classifications the defaults miss (`docs/workflow/config.json`)
- [ ] 7. The profile's `required_checks` (readiness fails while it is empty); acceptance IDs and mappings for the first milestone only
- [ ] 8. Fixtures and tool-discovery tests, in their own session

Existing repository: do not inventory the codebase; classification is a config edit; keep the first milestone small enough to be the pilot; porting into a fresh repository is an owner option to record here.

Read for setup: `procedures/setup.md`, `identity.md`, `templates/profile.md`, `templates/setup.md` and `config.default.json` in `.cache/agent-workflow/`. Consult `POLICY.md` and `SCHEMA.md` only when a rule is unclear.

## Current session accounting - 2026-09-28

| Step | Result | Agent wall time | Usage |
|---|---|---|---|
| 1 | Draft profile and pilot brief prepared with staged unknowns | unknown | unknown |
| 3 | Official wf-adopt scaffold completed; immutable tag resolved to commit shown above | unknown | unknown |
| 4 | Android paths added; default protections retained; reviewer config and acceptance map protected; master selected consistently | unknown | unknown |
| 7 | Proposed check names/commands, pilot acceptance and mappings prepared; actual Android runs, reporter capture and CI names remain pending | unknown | unknown |
| 8 | Not attempted; explicitly reserved for a separate session | not run | not used |

Cumulative agent wall time/usage: unknown; no extension granted or implied. Resume with the remaining default allowance; obtain an explicit allowance before work that could exceed it. The scaffold first failed its Git lookup under the sandbox, then completed after execution approval; no owner credential fallback was used.

Local record-schema validation passed (one draft milestone, one draft task). Representative Android and enforcement path classifications passed; the status view correctly reports Needs discovery and an unauthorised Draft pilot. Tracked diff whitespace check passed. This is feedback only, not readiness, approval, fixture verification or proof of protected CI. Android build/lint/tests and live tool discovery have not run. Raw evidence/handoffs belong on the eventual task pull request/issue; none exists or has a verified worker publishing route yet. An untracked .vscode directory appeared during the session and was left untouched.

## Owner identity progress

Step 2a completed by owner confirmation on 2026-09-28: Mohad009 is the personal owner account; retain Mohad009/Riyal_App. Recorded in profile, config approver and draft milestone. Approval remains manual; no account access or publishing performed. Agent time/usage for recording this confirmation: unknown/unknown.

Step 2b: user supplied ash-dev91 as the intended separate worker account. Recorded in config, profile and draft task; no authentication or permission verification performed. Agent time/usage for this update: unknown/unknown.

Step 2c: user reported that the collaborator invitation to ash-dev91 was sent. Invitation acceptance and actual access remain unverified. Agent time/usage for recording this update: unknown/unknown.

Step 2d: user reported accepting the collaborator invitation as ash-dev91. This records owner-reported acceptance only; the agent has not authenticated or tested access. Agent time/usage for recording this update: unknown/unknown.

Step 2e: user reported completing creation of C:\Users\mralh\Desktop\Riyal_App-owner. Keep C:\Users\mralh\Desktop\Riyal_App for agent work and the uncommitted setup files. This confirms the reported clone arrangement, not credential isolation. Agent time/usage for recording this update: unknown/unknown.

Step 2f: user supplied the GitHub noreply commit address `335093604+ash-dev91@users.noreply.github.com` for ash-dev91. Commit author configuration is not yet performed or verified. Agent time/usage for recording this update: unknown/unknown.

Step 2g: user reported setting the worker commit author. Agent read-only verification confirmed repository-local user.name `ash-dev91` and user.email `335093604+ash-dev91@users.noreply.github.com`. This verifies commit metadata only, not GitHub authentication. Agent time/usage for this update: unknown/unknown.

Step 2h: user reported GitHub CLI installed. Read-only agent checks found `C:\Program Files\GitHub CLI\gh.exe` version 2.101.0 (2026-09-15). Its login/environment help was inspected; representative worker authentication and transport testing remain pending. The existing agent process PATH does not yet resolve gh, so use its absolute path. Agent time/usage: unknown/unknown.

Step 2i: user reported completing the separate worker sign-in. The expected account is ash-dev91, with per-process CLI configuration at `%LOCALAPPDATA%\Riyal-Agent\gh`. Actual authenticated account and credential storage have not yet been verified. The agent has not read or printed credentials. Agent time/usage: unknown/unknown.

Step 2j: user reported auth status showing `ash-dev91 (keyring)` for the separate worker configuration. This confirms owner-observed CLI identity/storage; the agent has not loaded the credential. Agent time/usage: unknown/unknown.

Step 2k: user supplied the owner-run API result: Account ash-dev91; Repository Mohad009/Riyal_App; Push permission true; Admin permission false; read-only API check passed. This establishes the reported worker identity and API permission metadata, not an actual push or protection enforcement. No credential was shared with the agent. Agent time/usage: unknown/unknown.

Step 2l: user supplied the --git result: ash-dev91; Mohad009/Riyal_App; push true/admin false; route explicit-token HTTPS Git and gh; branch master; read-only Git check passed. This records an owner-observed preflight, not actual push/protection enforcement or strong credential isolation. Agent time/usage: unknown/unknown.

Step 2m: user confirmed the owner clone's effective commit name/email belong to Mohad009. A read-only agent check found neither field saved in the owner clone's local config. The private email was not printed or recorded. Save the confirmed values locally next. Agent time/usage: unknown/unknown.

Step 2n: user reported saving the owner author locally. Read-only agent checks confirmed both local user.name and user.email are present in Riyal_App-owner; values were not printed. Both clones now have separate local author settings. Agent time/usage: unknown/unknown.

Step 2o: user confirmed only Mohad009 approves project changes. The agent prepared root CODEOWNERS using the supplied single-owner pattern: all paths owned by Mohad009, followed by unowned workflow tasks/feedback exceptions. This local file does not establish protections or approval of the proposed deviation. Agent time/usage: unknown/unknown.

Step 5a: user reported Mohad009/Riyal_App is Public. Current official GitHub documentation confirms rulesets and auto-merge are available for Free public repositories; no plan upgrade is required for those features. No visibility change was requested or performed. Actual settings and plan-dependent controls must still be verified. Agent time/usage: unknown/unknown.

Step 5b: user reported enabling Allow auto-merge in repository settings. This is owner-reported configuration; no live records-only merge has been tested. The proposed single-owner arrangement still needs the remaining rules and a real approval-path exercise. Agent time/usage: unknown/unknown.

Step 5c: user reported no existing rulesets. Classic branch-protection rules have not yet been inspected. No rules were modified. Agent time/usage: unknown/unknown.

Step 5d: user reported no classic branch-protection rules. Combined with step 5c, neither type of existing protection was reported. No rule was removed or weakened. Agent time/usage: unknown/unknown.

Step 5e: user reported importing the supplied ruleset draft as instructed. Its prepared enforcement state is Disabled; activation and behavioral verification remain pending. This does not approve the single-owner deviation or complete step 5. Agent time/usage: unknown/unknown.

Step 6a: user reported creating C:\Users\mralh\Desktop\agent-workflow-trusted and checking out the supplied immutable commit. Read-only agent checks confirmed HEAD `5e4e75d31f7e51bc66904b538ef33a30ca0c0ad7`, a clean checkout and bin/wf present. The launcher has CRLF line endings, which Bash cannot execute correctly. Git for Windows bash.exe is available at C:\Program Files\Git\bin\bash.exe. Launcher execution, environment controls and authoritative CI remain unverified. Agent time/usage: unknown/unknown.

Step 6b: user reported restoring bin/wf with LF line endings. Read-only checks confirmed no CRLF, unchanged pinned HEAD and identical raw/committed blob hashes (`7c009e47dccb0f671979323ad80ac06fe9b28fe4`). The inherited core.autocrlf setting remains true; the default status reported bin/wf modified while diff summary/numstat showed no source differences. Future checkout operations must retain LF for this launcher. No launcher execution or authoritative CI has been verified. Agent time/usage: unknown/unknown.

Step 6c: user supplied the external launcher records result: ok true, errors empty, one milestone, one task, zero decisions and zero feedback. This records a successful owner-observed invocation with the supplied pinned environment and explicit app path. It verifies record schemas, not readiness, approval, immutable environment controls, candidate isolation or authoritative CI. Manual mode remains unchanged. Agent time/usage: unknown/unknown.

Step 5f: user explicitly replied "approve" to the proposed review behavior: Mohad009 approves app, requirements and workflow changes; task/feedback-only pull requests may merge automatically after all required checks pass. Recorded as an approved deviation in the profile Authority. The ruleset remains Disabled. This approval does not approve the draft pilot, bootstrap publication, complete profile or enforced-mode transition. Agent time/usage: unknown/unknown.

Step 7 continuation: prepared .github/workflows/android-checks.yml with the exact three proposed report names, assembled-candidate checkout, Temurin 21, API 37/default build-tools 36.0.0 installation and report artifacts. Candidate tests have read-only contents permission, no persisted Git credential and no owner/signing secrets supplied. Official release metadata resolved checkout v7.0.1 (`3d3c42e5aac5ba805825da76410c181273ba90b1`), setup-java v6.0.1 (`de7274f081f381c8f8158605e0321c36c376e2e6`) and upload-artifact v7.0.1 (`043fb46d1a93c77aae656e7c1c64a875d1fc6a0a`). Official documentation inspected; representative action execution and dependency/script inspection before operational adoption remain pending. This is candidate-test CI only, not the authoritative wf ci gate. Local record validation and tracked diff whitespace check passed. No YAML parser/actionlint was available, so no parsed-YAML or Actions validation is claimed. No Android commands have run. Agent time/usage: unknown/unknown.

Read-only environment check: Android Studio JBR OpenJDK 21.0.10 is installed. The SDK at C:\Users\mralh\AppData\Local\Android\Sdk has platforms android-35/android-36.1, no android-37; build tools 34.0.0/36.1.0/37.0.0, no AGP-default 36.0.0; no cmdline-tools directory. Owner installation is needed before local Android verification. No SDK writes or licenses accepted by the agent.

## SDK installation and Android baseline results - 2026-09-29

Earlier Arabic feature discovery: the owner confirmed natural Arabic inside Riyal's UI. Draft requirements, shared terminology, data invariants, proposed journeys and verification are in docs/specs/arabic-localization.md. Task-focused inspection found English literals throughout the UI and an English-only plural helper; no app implementation was changed. At that stage API 37 was missing; the results below supersede that environment limitation. Local records validation passed; the derived status still reports Needs discovery and incomplete setup. This neither completes the pilot nor waives readiness. Discovery time/usage: unknown/unknown.

The owner reported installation; read-only inspection found android-37.1. The agent ran step 7 checks on a clean archive of 3b37845b02ed7a8b12f811444b724d08b5b0f92f in an external temporary directory, without local.properties. Separate Gradle/Android user directories and SDK storage were used, with known owner/worker token and signing environment variables removed. This is practical separation, not proof of OS credential isolation.

The initial sandbox attempt failed with AccessDeniedException in the installed Gradle library. The approved elevated rerun completed in 3m54s. Gradle used the owner's already accepted SDK license and downloaded platform 37.0 revision 2, build-tools 36.0.0 and platform-tools 37.0.1 into .cache/android-sdk. The prepared Android CI package name was corrected to platforms;android-37.0 based on the installed package metadata.

Observed results: assembleDebug passed; testDebugUnitTest passed with 199 tests in 16 suites and zero failures/errors/skips. Each of the three acceptance mappings matched one passing testcase in VersionTest. lintDebug failed with one pre-existing PermissionImpliesUnsupportedChromeOsHardware error at AndroidManifest.xml:7, 38 warnings and 3 hints. No app source change or lint suppression was made. External evidence is at C:\Users\mralh\AppData\Local\Temp\riyal-baseline-e7c3ec5d9aa744248928f1867478c187, including summary.json and original JUnit/lint reports. Actual GitHub check runs/provenance, trusted wf ci, fixtures/discovery, approvals and pilot remain incomplete. Total agent time/usage: unknown/unknown; elevated Gradle wall time observed: 3m54s.

## Owner decision - local Arabic draft while setup remains incomplete

On 2026-09-29 the owner explicitly answered yes to starting Arabic changes, the manifest fix and local testing while setup is unfinished. Record this bounded exception in the profile and Arabic plan. It permits local implementation and verification only; it does not approve a merge, publication, release, protection bypass or a switch to enforced mode. Resume normal setup after this local work; retain manual mode and the disabled ruleset.

## Next agent session

Resume from this record. Incorporate owner-confirmed facts without claiming independent worker verification. Finish step 7 execution/report capture when a suitable test environment and CI bootstrap are available. In a session separate from the profile-writing session, run step 8 gate/acceptance/identity/session fixtures and fresh Codex and Claude Code discovery tests. Record tool versions, exact prompts, transcript/results externally; a blocked task must produce no production edits. Claude Code availability is unverified. Do not replace live discovery with reading adapters.

Before owner step 5 can complete, prepare the concrete protected CI/check configuration for review under the owner's bootstrap arrangement. Before step 11, the owner must approve the concrete pilot task and limits. Current task/milestone remain Draft.
