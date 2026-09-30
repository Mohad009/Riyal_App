# Proposed owner bootstrap and protection settings

Draft preparation only. Nothing here establishes owner approval, protection activation or operational readiness. The agent prepares files; the owner performs GitHub/account/credential steps.

Repository: Mohad009/Riyal_App, reported Public. Trusted branch: master. Sole approving owner: Mohad009. Worker: ash-dev91, reported collaborator push permission without admin access. Current Allow auto-merge setting: owner reports enabled.

## Before activation

The owner reported no pre-existing rulesets or classic branch protections, then imported `ruleset-draft.json` as the disabled draft. Its check names are proposed and not yet bound to producing applications; verify and bind them before activation. CODEOWNERS currently exists only in the local setup files. The owner provisioned C:\Users\mralh\Desktop\agent-workflow-trusted at 5e4e75d31f7e51bc66904b538ef33a30ca0c0ad7; read-only checks confirmed the pin and canonical launcher bytes after LF correction. The owner-run external launcher records test passed. This is not a protected CI entry point. The owner reviews the setup files and establishes a bounded bootstrap route. Protected CI must be concrete and reviewable before normal protections can be declared active.

Record what initial publication/bootstrap the owner allows, who performs it, and the revision/time at which normal controls become active in setup.md. Approval stays manual until verified protections and the disposable approval-path test permit the reviewed switch to enforced. Do not treat an incomplete bootstrap check as authoritative wf ci. The pinned validator correctly rejects governing/enforcement changes in manual mode without signed evidence; bootstrap must explicitly address this before the enforced-mode transition, rather than silently disabling or relabelling the gate.

## Single-owner ruleset from the supplied setup procedure

| Setting | Proposed value |
|---|---|
| Branch target | master |
| Normal enforcement | Active after approved bootstrap and check provisioning |
| Bypass list | Empty, including administrators and worker |
| Require pull request | On |
| Required approvals | 0, with code-owner review required |
| Code-owner review | On; root CODEOWNERS assigns all paths to Mohad009 except workflow tasks and feedback |
| Dismiss stale approvals after new commits | On |
| Approval of most recent reviewable push | Off, as prescribed for a single owner |
| Required status checks | wf ci, android-build, android-lint, android-unit-tests; confirm actual names and producing application |
| Require branch up to date | On |
| Allow auto-merge | Owner reports On |

On 2026-09-28 the owner explicitly approved this review arrangement; it is recorded as the profile's single-owner deviation. Code-owner review protects app/governing/enforcement changes; records-only tasks/feedback may merge after their checks. Activation and real behavior remain unverified. This approval does not authorise bootstrap publication, pilot implementation or an enforced-mode switch.

## Outstanding agent preparation

Complete concrete protected CI/check provisioning and Android reporter capture. Validate the pinned external launcher route separately from candidate tests; candidate tests receive no owner credentials. Run fixtures and fresh-tool instruction discovery in the required separate session. Keep check provenance and exact candidate binding explicit.

## Owner validation after provisioning

Use an authorised disposable branch/repository to show worker direct push and unapproved owned changes are rejected, records-only changes can merge after checks, owner approval permits owned changes, stale approvals become invalid and updating an approved branch reruns the required checks. Record exact resulting revisions. Do not test bypasses destructively on the production branch.
