# Arabic throughout Riyal

Status: Review candidate. The owner authorised the local Arabic changes, manifest fix and testing on 2026-09-29, then asked on 2026-09-30 to commit and upload the work for inspection. Upload does not approve a merge, release, protection bypass or workflow readiness. The workflow setup remains incomplete, and the Arabic journeys have not been verified on a device.

## Owner request

2026-09-28: "I want to add arabic langauge to the phone. ensure the language is consistant with the AI and the langauge is not litral"

The owner answered "yes" when asked whether this means Arabic inside Riyal and UI rather than an AI feature. The requested outcome is natural Arabic that fits the interface and uses consistent terminology. This does not request a new AI service or changes to the phone's system language.

## User experience

Use clear Modern Standard Arabic appropriate for a personal finance app. Translate the intention of a message, not the order or individual words of the English sentence. Keep actions concise and explanations direct. Use the same term for the same concept across navigation, summaries, dialogs, help and accessibility labels.

The proposed language setting offers device language, English and العربية. Device language remains the default; an explicit app choice persists after restart. A user can return to English without changing the phone's language. English remains the complete fallback. Language changes must preserve saved financial data and preferences, and must not trigger a new SMS scan or destructive operation.

Cover onboarding, navigation, home, transactions and details, analysis, account/category management, category assignment, review, budgets, scan progress/results, settings, export confirmations, update prompts, empty/error states and accessibility descriptions. Built-in Android permission/install dialogs remain controlled by Android.

## Writing guide and shared terminology

| Meaning in this app | Arabic wording |
|---|---|
| Home | الرئيسية |
| Activity / transaction list | المعاملات |
| Analysis | التحليل |
| Settings | الإعدادات |
| Bank accounts | الحسابات البنكية |
| Account | الحساب |
| Category / categories | الفئة / الفئات |
| Income / money in, when summarizing income | الدخل |
| Expenses / money out, when summarizing spending | المصروفات |
| Transfer between accounts | تحويل بين الحسابات |
| Balance | الرصيد |
| Opening balance | الرصيد الافتتاحي |
| Budget | الميزانية |
| Spending cap | حد الإنفاق |
| Needs review | معاملات تحتاج إلى مراجعة |
| Needs a category | معاملات غير مصنفة |
| Add an account | إضافة حساب |
| Save / cancel / delete | حفظ / إلغاء / حذف |
| Got it, acknowledging an explanation | فهمت |
| Scan messages, the user action | قراءة الرسائل |
| No transactions yet | لا توجد معاملات حتى الآن |
| Reset settings to defaults | استعادة الإعدادات الافتراضية |
| Delete all app data | حذف جميع بيانات التطبيق |

These are context rules, not a global English-to-Arabic replacement dictionary. For example, "Clear" means مسح for a text field, إزالة عوامل التصفية for selected filters, and must never accidentally describe deletion of transactions. "Activity" means المعاملات on the transaction tab, not النشاط. "Cap" means حد الإنفاق, not a literal object. Distinguish a transfer from income or an expense according to the existing business rules.

A natural confirmation example: "هل تريد حذف هذا الحساب؟" followed by the actual consequences established in the current implementation. Do not invent consequences while translating. An empty category view can say "لا توجد معاملات في هذه الفئة خلال الفترة المحددة" when that is the actual filter state.

## Implementation boundaries and data invariants

- Move app-owned display copy to English string/plural resources and matching Arabic resources; compose complete localized sentences with typed placeholders. Do not concatenate English word fragments or apply the existing English-only countOf helper to Arabic.
- Use Android's app language APIs through the existing AppCompatActivity and Compose resource APIs. Declare the supported locales and use AndroidX persistence for supported Android versions below 13. Do not add a translation API or network dependency.
- Keep Arabic plural forms meaningful for zero, one, two, few, many and other; verify counts such as 0, 1, 2, 3, 11 and 100. The displayed number and selected plural rule must refer to the same count.
- Localize built-in category/type labels using stable identifiers. Never rewrite stored category IDs, parser keywords, enum values or matching rules to translated display text. Preserve user-created names and user-renamed defaults exactly.
- Preserve bank messages, merchant names, account nicknames and account identifiers. They are user/source data, not app copy. Wrap mixed-direction display values where necessary; never insert directional controls into stored SMS or exported data.
- Preserve money's minor-unit arithmetic, currency precision, totals, numeric input semantics and existing exported data contract. Translation must not change a displayed amount's value or silently reinterpret a decimal separator. Keep current financial digits initially; Arabic date/month labels may follow app locale while time zone, date boundaries and Gregorian calculations stay unchanged.
- Use start/end alignment and directional icons where navigation semantics require mirroring. Keep identifiers and amounts readable. Do not blindly mirror charts, signs, logos or data values.
- Preserve unsaved input across locale recreation where practical. A language switch must not submit a form, delete data, request a new permission, or restart a completed scan.

## Planned journeys

The complete feature includes every journey below; completing the first does not complete Arabic support.

1. Choose Arabic, review balances and transactions, open/filter a transaction, restart the app, and return to English. Includes locale plumbing, shared controls and the primary navigation. This is the first proposed end-to-end milestone.
2. Manage accounts, categories and budgets in Arabic, including add/edit/cancel/delete flows, empty states and mixed-language user names. Depends on the localized display/identifier contract from the first journey.
3. Read and review synthetic bank messages, assign a category, inspect analysis, change settings, and use export/update prompts in Arabic. Includes onboarding, permission explanations, progress, errors and final app-wide language/layout review.

Prepare numbered milestone/task records after checking IDs against fresh trusted history and open pull requests. Preserve the existing draft workflow pilot until its scope is explicitly changed through the workflow; this request does not silently mark it complete or replace it.

## Verification and acceptance

The candidate declares `<uses-feature android:name="android.hardware.telephony" android:required="false" />` in AndroidManifest.xml, as the observed lint error recommends. It keeps READ_SMS and existing runtime permission handling. This makes phone hardware optional in package metadata; verify that the existing no-permission/no-message flows remain usable before accepting the change. The lint finding is fixed rather than suppressed.

Baseline verification completed on 2026-09-29 using a clean archive of 3b37845b02ed7a8b12f811444b724d08b5b0f92f: debug build passed; 199 JVM tests passed; lint found one existing manifest error (PermissionImpliesUnsupportedChromeOsHardware), 38 warnings and 3 hints. SDK Platform 37.0 and the required build tools were resolved in the isolated build environment. The candidate adds the optional telephony feature declaration and passes debug build, lint and 204 JVM tests. English/Arabic resource names and format placeholders have been checked for parity. No emulator or dedicated device journey has yet been verified for this task.

Automated coverage should verify English/Arabic resource coverage and format placeholders, Arabic count behavior, locale persistence/fallback, and that translating built-in labels preserves identifiers and user-entered names. Record a meaningful failing result before implementation and passing result after it. Run :app:assembleDebug, :app:lintDebug and :app:testDebugUnitTest after each coherent change.

UI verification must exercise the changed journeys in Arabic and English, at a narrow phone width and enlarged font size, with synthetic financial data. Check direction, navigation, clipping, dialogs, TalkBack descriptions, Arabic/Latin names, signed three-decimal OMR values, restart and Android's app-language setting. Use an emulator/dedicated test app; installing a test must not overwrite the user's financial app or data. Resource tests alone cannot establish natural wording or correct rendering.

Independent review should assess semantic Arabic, consistent terms, destructive-action wording, data preservation, test fidelity and the screenshots/journey results. Owner acceptance covers actual Arabic journeys after checks and review; no release is requested here.

## Discovery findings and dependencies

The manifest already declares supportsRtl=true. MainActivity extends AppCompatActivity. The default strings.xml currently contains only the app name, while Compose screens and navigation labels contain English literals. Bits.kt contains an English noun-plus-s plural helper. Several date formatters and Money.kt formatting need deliberate locale review; they must not be mechanically translated. No AI response integration was identified in the task-focused search, and AI behavior is outside this request.

The adopted workflow is still manual with no trusted baseline receipts; protections, authoritative CI and the real approval-path exercise are incomplete. Resolving SDK installation alone does not complete those controls. Production implementation must pass the workflow's readiness step after its required setup and baseline verification.

Technical references: [Android app languages](https://developer.android.com/guide/topics/resources/app-languages), [language and RTL support](https://developer.android.com/training/basics/supporting-devices/languages). These guide implementation mechanisms, not product authority.
