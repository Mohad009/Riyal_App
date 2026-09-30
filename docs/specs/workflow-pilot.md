# Workflow adoption brief and proposed pilot

Status: draft; owner approval required.

Prepare the existing Riyal Android project for assisted work, then observe one bounded task through authorisation, checks, independent review, integration and acceptance. Retain this repository. No product behaviour changes during setup.

The proposed pilot verifies three existing VersionTest cases. After setup, the owner approves a concrete test/documentation improvement if justified; do not invent a production edit to exercise the workflow.

## First-milestone acceptance

- AC-PILOT-01: v1.10 is newer than 1.9; v1.9 is not newer than 1.10.
- AC-PILOT-02: the same release is not offered as an update, including a debug suffix.
- AC-PILOT-03: invalid version tags are ignored without exceptions or false update offers.
- AC-PILOT-04: the owner observes task authorisation, authentic checks, independent review, protected integration and explicit acceptance. Record technical and workflow outcomes separately using the pinned templates/pilot.md on the task pull request/issue.

The automated cases map to existing test methods; execution is unverified. Confirm exact reporter names against actual Gradle JUnit XML before treating mappings as verified.

## Limits and stop conditions

Proposed allowance: one task, two agent hours and one owner demonstration; confirm before authorisation. No production edits, signing, releases, real SMS or financial data. Stop for missing authority, wrong identity, failed checks, unverified protection or exhausted limits. Required repairs become separately scoped work. No release authority.
