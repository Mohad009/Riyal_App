---
record: profile
project: Riyal_App
workflow_version: v1.6.2
approval_mechanism: manual-signed-receipts
approval_label: manual
coordinator: Mohad009
owners: []
measure: The owner observes one small task pass checks and independent review before accepting it.
readiness: Needs discovery
required_checks: [android-build, android-lint, android-unit-tests]
permitted_assumptions: []
setup_budget_days: 2
---
# Riyal project profile

Draft setup record. No product implementation or release is authorised by this file.

## Purpose

Adopt the supplied assisted workflow in the existing Android app. Manifest comments describe on-demand SMS scanning, local records and GitHub APK updates. Users, supported banks and product success measures need owner confirmation. The setup measure above is demonstrated by the proposed M-0001 pilot.

## Owner's words

2026-09-28: "Set up the agent workflow from `C:\Users\mralh\Desktop\agent-workflow` in this existing Android project. Follow its setup procedure. Prepare the agent-owned setup files and guide me through the owner steps one at a time, using simple language."

This authorises local setup preparation and incremental owner guidance, not proof of GitHub identity or product release authority.

2026-09-28: the owner requested Arabic and confirmed that this means natural, consistent Arabic inside Riyal's UI. The request and a draft implementation plan are in `docs/specs/arabic-localization.md`. This adds a requested product outcome; it does not establish that the incomplete workflow controls or build environment are operational.

2026-09-30: the owner said, "cotniue with the agent-workflow. after you commit and stuff and upload i will check". This authorises worker-authored review commits and upload for the owner's inspection. It does not establish completed setup, approve the draft pilot, activate protections, merge a pull request or release an APK.

## Scope

Retain the existing repository; prepare profile, brief, classification, check definitions, first-pilot acceptance and tool entries. Repository migration, product changes, data migrations, releases and unattended execution are outside setup scope.

## Starting position

Remote: `https://github.com/Mohad009/Riyal_App.git`. Current branch and local origin default: `master`. Starting commit: `3b37845b02ed7a8b12f811444b724d08b5b0f92f`; initial worktree clean. `RELEASE.md` describes feature branches from `development` and releases through pull requests to `master`. These local facts do not prove GitHub protections. No root README was present. No codebase inventory was performed.

## Product expectations

Preserve app behaviour during setup. Manifest comments describe explicit SMS scanning, device-local data and update downloads; these claims are unverified implementation context. Languages, accessibility, visual direction, supported devices and journeys require discovery before related work.

## Technical context

One Gradle application module, `:app`, package `com.alyaqdhan.riyal`. Build declarations: Gradle 9.6.0; AGP 9.2.1 with built-in Kotlin; Compose compiler 2.3.10; Compose Material 3, fragments/navigation, Vico and JUnit 4. Compile SDK 37, target 36, minimum 29. Java source/target compatibility 11 does not establish the runtime JDK requirement. Read-only environment checks found Android Studio's JBR OpenJDK 21.0.10 and SDK platforms android-35/android-36.1; android-37 is absent. Installed build tools are 34.0.0, 36.1.0 and 37.0.0; AGP's default 36.0.0 is absent. The SDK cmdline-tools directory is absent. Actual build compatibility and reporter output remain unverified.

2026-09-29 verification: the owner installed platform android-37.1. The isolated baseline build resolved the exact requested `platforms;android-37.0` (revision 2), build-tools 36.0.0 and platform-tools 37.0.1 under `.cache/android-sdk`, using the owner's already accepted SDK license. JBR 21.0.10 and Gradle 9.6.0 executed successfully. No further owner SDK selection is required for these local checks.

The only declared module boundary is `:app`. A cross-module checker is not applicable to this single-module setup. Internal dependency conventions/checks are `none yet`; discover them before cross-component work and inspect them in independent review. Compilation checks Kotlin/Java types. App code/resources/manifest, Gradle files, wrapper, scripts and migration inputs are production paths. Build outputs are artifacts, never governing sources; no generated-source exemption is granted.

## Authority

The pinned workflow POLICY.md governs its schema, procedures and templates. Owner-approved project requirements, decisions and acceptance on the approved baseline govern product work. This profile and `docs/specs/workflow-pilot.md` are drafts. Existing source, manifest comments and RELEASE.md are implementation/historical context, not approval. Raw inbox input and prototypes are unapproved; status views and generated indexes are derived only.

The user confirmed Mohad009 as their personal owner account, Mohad009/Riyal_App as the repository to retain, and that they are the only human approver. ash-dev91 is the separate worker, not an approving owner. Owner-run API/Git preflight reported the expected worker/repository, push true/admin false, and a successful read-only route. The draft task names ash-dev91 as its intended implementer without authorising work. On 2026-09-28 the owner explicitly replied "approve" to the single-owner review arrangement: Mohad009 approves app, requirements and workflow changes; changes only to task/feedback records may merge automatically after all required checks pass. This is the approved deviation prescribed by setup step 5. Root CODEOWNERS is prepared with Mohad009 owning every path except workflow tasks and feedback. Activation, authentic checks and behavioral verification remain pending; this approval does not approve the rest of the draft profile, pilot, bootstrap publication or an approval-mode switch. No delegated product authority. Reserved deployment, incident, maintenance, upgrade and release decisions follow the pinned workflow defaults.

## Delivery

2026-09-29 bounded owner exception: the owner explicitly authorised local Arabic UI implementation, the existing manifest lint repair and local testing while workflow setup remains unfinished. Follow docs/specs/arabic-localization.md for scope and preservation rules. This permits local code and verification without claiming that normal readiness has passed; publishing, merging and release remain outside this exception.

M-0001: observe a bounded version-regression verification task through review, integration and acceptance; requires completed setup controls and owner-approved scope.

Prepared exact CI report names and commands in `.github/workflows/android-checks.yml`, not yet published or observed on GitHub:

| Required report | Windows command | Purpose |
|---|---|---|
| android-build | `./gradlew.bat :app:assembleDebug` | Compile/type-check and package debug app |
| android-lint | `./gradlew.bat :app:lintDebug` | Android lint |
| android-unit-tests | `./gradlew.bat :app:testDebugUnitTest` | JUnit regressions |

Linux CI uses `bash ./gradlew` with the same tasks to avoid reliance on an executable bit. The prepared candidate-test workflow uses a read-only token, no persisted checkout credentials, no owner/signing secrets, separate matrix jobs and pinned actions. It records the assembled candidate revision, Java/Gradle versions and raw reports. It is separate from the still-unprepared protected `wf ci` gate. Owner bootstrap must establish protected CI, confirm actual report names and provenance, and require up-to-date branches. The status-issue workflow is not an integration check. No separate type command is needed because compilation covers it. No component-boundary tool exists yet; see Technical context.

Capture actual JUnit XML from `app/build/test-results/testDebugUnitTest/`, lint reports from `app/build/reports/`, test counts, mapped names, exact candidate SHA and JDK/SDK/Gradle environment on the task pull request/issue. The workflow's Node reporter is not a Gradle adapter. On 2026-09-29, checks on a clean archive of `3b37845b02ed7a8b12f811444b724d08b5b0f92f` passed assembleDebug and all 199 JVM tests in 16 suites (zero failures/errors/skips). All three pilot mappings matched exactly one passing JUnit testcase. Lint failed with one existing PermissionImpliesUnsupportedChromeOsHardware error at AndroidManifest.xml:7, plus 38 warnings and 3 hints. The SMS permission lacks an explicit optional telephony feature declaration. This existing failure is recorded, not suppressed or fixed by setup. Raw reports and summary are outside the checkout at `C:\Users\mralh\AppData\Local\Temp\riyal-baseline-e7c3ec5d9aa744248928f1867478c187`; no task PR exists yet. Independent review uses a fresh context and canonical sources. Owner approval and authentic integration checks precede acceptance. Signing keys stay outside candidate CI.

## Quality expectations

| Expectation | Acceptance condition | Evidence | Role | Required stage |
|---|---|---|---|---|
| Version comparison | AC-PILOT-01 through AC-PILOT-03 | Mapped JUnit tests and semantic review | Agent/reviewer | verify |
| Workflow pilot | AC-PILOT-04 | Observed review, integration and acceptance | Owner/agent | accept |
| Android health | All required reports pass on assembled candidate | Authentic checks and raw reports | Agent/owner | integrate |
| Data/signing preservation | No migration or release in pilot | Scope review | Reviewer | accept |

No product model behaviour was declared in the inspected manifests; model evaluation is outside this pilot.

## Operations

RELEASE.md documents APK releases, previous signing incompatibility and schema changes that can erase categorisation. These risks require task-specific discovery before release/storage work. Historical keystore paths are unverified on this Windows machine. Never use personal SMS, real financial records or signing keys in candidate checks. Support owner, monitoring, recovery, rollback, incident contact and hotfix window remain unknown before operational work. No deployment is part of the pilot.

## Execution

Assisted only; no runner adopted. Setup: two working days overall and half a working day of agent wall time across sessions unless the owner records another allowance. Unavailable time/usage is unknown, not zero. No paid services authorised. Node 22.20.0 available. Git Bash was not found on PATH. Local feedback commands on Windows: `node .cache/agent-workflow/validator/cli.js --repo . records` and `status`; these are not authoritative gates.

The user reports the separate owner clone at `C:\Users\mralh\Desktop\Riyal_App-owner`; the original `C:\Users\mralh\Desktop\Riyal_App` is designated for agent work. Read-only verification confirmed the worker clone's local commit author ash-dev91 and `335093604+ash-dev91@users.noreply.github.com`. GitHub CLI 2.101.0 is installed at `C:\Program Files\GitHub CLI\gh.exe`; its login/environment help was inspected. The owner reports worker keyring sign-in and successful explicit-token API/Git preflight using `%LOCALAPPDATA%\Riyal-Agent\gh` outside the project. Owner-clone author configuration/routes and actual worker push/protection testing remain pending. Separate clones/configuration alone do not prove credential isolation. No global account switching. Fresh sessions supply independent review/discovery; supported harness versions and actual discovery remain unverified until step 8.

Owner-run `scripts/check-worker-access.mjs --git` reported ash-dev91, Mohad009/Riyal_App, push true/admin false, explicit-token HTTPS Git and gh, branch master, and a successful read-only Git check. Public anonymous reads can succeed, so this does not prove an authenticated push or protected integration. The user confirmed the owner clone's effective name/email belong to Mohad009 and saved them locally; read-only checks confirm both local author fields are present. No private owner email was recorded. Single/shared ownership, owner publishing-route verification, actual push and protection tests remain pending. See `docs/workflow/toolbox.md` for candidate tool status.

Pilot tests use synthetic version strings on the JVM, without app login or real device data. Later device journeys require a dedicated test device/emulator, synthetic messages, authorised effects and reset instructions. Store only external secret-store references if needed, never credentials.

## Workflow configuration

Cazy00/agent-workflow v1.6.2, commit `5e4e75d31f7e51bc66904b538ef33a30ca0c0ad7`, installed from the supplied checkout. Trusted branch `master` matches local metadata/release documentation; owner verification pending. Manual mode has no provisioned receipts, so nothing is approved. No external tools added; optional navigation deferred.

The owner reports the repository is Public and Allow auto-merge is enabled. GitHub currently supports rulesets and auto-merge on Free public repositories; actual protection activation, check provenance, no-bypass settings and records-only merging remain unverified. Proposed bootstrap and settings are in `docs/workflow/bootstrap.md`. Project status publisher is prepared for master, with live operation/pinning pending; its issue will be public. Central reporting to Cazy00/agent-workflow is not adopted: public data boundary, worker, external config, private runtime, tools, costs and cadence await owner step 10. No central reporting is authorised during setup.

## Unknowns

| Unknown | Required before |
|---|---|
| Owner publishing-route verification and actual worker write/rejection tests | Publishing; bootstrap and approval-path test |
| Actual protections, check provenance, unavailable controls and external validator | Integration; steps 5/6/9 |
| JDK/SDK, actual CI names, reporter output and baseline results | Pilot verification; step 7 completion |
| Codex/Claude discovery and gate/identity/session fixtures | Supported operation; separate step 8 session |
| Concrete pilot scope and limits | Pilot implementation |
| Users, languages, accessibility, component contracts | Changes to affected journeys |
| Data schema and recovery | Storage changes |
| Signing continuity, release/support/rollback authority | Release/deployment |
| Reporting boundary and runtime | Central reporting |
