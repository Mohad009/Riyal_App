# Riyal v1.6.2 release preparation

Status: Draft candidate preparation. This is not approval to tag, sign, publish or distribute an APK.

## Owner choices and source

On 2026-09-30 the owner asked to prepare a release after merging the Arabic and workflow review pull requests, chose `v1.6.2`, chose `Mohad009/Riyal_App` as the GitHub release repository, and reported that the existing release signing keystore is unavailable. The freshly fetched `master` baseline is `4d0780d` (both pull requests merged). GitHub showed no releases or tags in this repository at preparation time.

The candidate changes `versionName` from `1.6.1` to `1.6.2` and `versionCode` from 5 to 6. The app's update lookup previously targeted `Alyaqdhans/Riyal`; it now targets the chosen release repository. A future GitHub release must use tag `v1.6.2`, attach an `.apk` asset and carry this candidate's notes, or the new app's update check will not offer the intended download. Already-installed versions still check `Alyaqdhans/Riyal`, so publishing only to `Mohad009/Riyal_App` will not alert those users. Their update path needs an explicit distribution plan if the original signing key is recovered.

## Release boundary

The feature carried forward from the merged app code is the Arabic interface, with device, English and Arabic language choices. Built-in labels are localized for display while stored financial identifiers and user-entered names remain unchanged. The optional telephony feature declaration also fixes the earlier ChromeOS lint error. `Store.SCHEMA_VERSION` remains 2; this candidate introduces no storage migration. Actual persistence across an upgrade has not been tested on a device.

The existing release certificate is required for an update to the installed `com.alyaqdhan.riyal` app. The owner reports its keystore unavailable, and `local.properties` is absent here; therefore **do not sign with a new key, publish a release APK, or tell users to uninstall the app**. Android backup is disabled in the manifest, and the CSV export is not a full restore path for saved categorization and preferences. The historical certificate information in `RELEASE.md` is not proof that the key is available or that a new APK matches it. Recovery of the original key, verification of its full certificate fingerprint against a known installed or previously distributed APK, and an install-over test that preserves synthetic data are release gates. If recovery is impossible, distribution and data-migration choices require a separate owner decision.

The pinned agent-workflow setup remains incomplete (manual approval mode, no authenticated required CI check, draft pilot and no accepted Arabic journey). Local preparation and a draft review pull request do not establish an accepted release candidate. The release gate also needs owner acceptance of Arabic/English journeys on a dedicated device, including RTL, enlarged text, TalkBack, permission handling and app-language persistence.

## Checks to attach to the review pull request

- Build the release variant without signing credentials and verify its package, version name and version code. An unsigned APK is inspection-only and must not be offered to users.
- Run the debug build, lint and all JVM tests on the exact candidate. Record test count and failures.
- Verify the update comparison sees `v1.6.2` as newer than installed `1.6.1` and that the release lookup resolves the chosen repository.
- When the original key is available, build and verify a signed release APK, compare its full certificate fingerprint to the prior distributed APK, and install it over a prior version on a dedicated device with synthetic data. Check saved categories, accounts, preferences and transactions after the upgrade.
- Complete workflow readiness, independent review, owner acceptance and `wf lifecycle --stage release` before publishing.

## Draft public release notes

**Riyal v1.6.2**

### العربية

- أصبحت واجهة ريال متاحة بالعربية، بما في ذلك المعاملات والتحليل والميزانيات وإدارة الحسابات والفئات والإعدادات.
- يمكنك اختيار لغة الجهاز أو الإنجليزية أو العربية من إعدادات التطبيق.
- حسّنا عرض النصوص من اليمين إلى اليسار مع إبقاء أسماء الحسابات والفئات التي أدخلتها كما هي.
- أصبح التطبيق قابلاً للتثبيت على الأجهزة التي لا تحتوي على عتاد اتصال خلوي، مع بقاء إذن الرسائل مطلوباً عند قراءة رسائل البنك.

### English

- Added Arabic across Riyal's main screens and dialogs, including navigation, transactions, analysis, budgets, account and category management, scan results and settings.
- Added an in-app choice to follow the device language or use English or Arabic.
- Improved right-to-left labels and mixed-language display without changing stored category identifiers or user-entered names.
- Improved device compatibility by making telephony hardware optional in the app manifest.

These notes are a draft. Confirm the signed update and device journeys before publication.
