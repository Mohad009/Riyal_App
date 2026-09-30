@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalLayoutApi::class,
)

package com.alyaqdhan.riyal.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.platform.LocalConfiguration
import com.alyaqdhan.riyal.R
import com.alyaqdhan.riyal.ui.compose.bidiValue
import com.alyaqdhan.riyal.ui.compose.AppLanguageSetting
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.alyaqdhan.riyal.ui.compose.ScanSheetHost
import com.alyaqdhan.riyal.core.Verbose
import com.alyaqdhan.riyal.ui.MainViewModel
import com.alyaqdhan.riyal.ui.compose.CURRENCIES
import com.alyaqdhan.riyal.ui.compose.ToolbarSpacer
import com.alyaqdhan.riyal.ui.compose.appVersion
import com.alyaqdhan.riyal.ui.compose.plainText
import com.alyaqdhan.riyal.ui.compose.popIn
import com.alyaqdhan.riyal.ui.compose.pressBounce
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter



/**
 * Everything the scanner does is decided here, but the reason to open Settings is
 * usually to *check* something rather than change it - so the screen opens with what
 * the app has actually done, and only then offers the switches.
 *
 * Each row is one line: a name, its current value, and the control. The paragraph that
 * used to sit under every row is behind the (i) instead, because a screen where every
 * setting explains itself at rest has to be read rather than scanned. The few
 * explanations still on show are the ones describing *this* state rather than what a
 * control would do - a fresh-start floor, an allowlist that would match nothing.
 */
@Composable
fun SettingsScreen(
    vm: MainViewModel,
    onOpenAccounts: () -> Unit,
    onOpenCategories: () -> Unit,
    onExport: () -> Unit,
) {
    val context = LocalContext.current
    val settingsDayFmt = DateTimeFormatter.ofPattern("d MMM uuuu", LocalConfiguration.current.locales[0])
    val prefs = vm.prefs
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    val hasPerm by vm.hasSmsPermission.collectAsState()
    val knownSenders by vm.senders.collectAsState()
    val rules by vm.rules.collectAsState()
    val accounts by vm.accounts.collectAsState()
    val budgets by vm.budgets.collectAsState()
    val txns by vm.txns.collectAsState()
    val lastSummary by vm.lastSummary.collectAsState()

    var expenseKw by remember { mutableStateOf(prefs.expenseKeywords) }
    var incomeKw by remember { mutableStateOf(prefs.incomeKeywords) }
    var newExpenseKw by rememberSaveable { mutableStateOf("") }
    var newIncomeKw by rememberSaveable { mutableStateOf("") }
    var rangeMonths by rememberSaveable { mutableStateOf(prefs.scanRangeMonths) }
    var freshStart by rememberSaveable { mutableStateOf(prefs.scanSinceMillis) }
    var currency by rememberSaveable { mutableStateOf(prefs.defaultCurrency) }
    var senderFilter by rememberSaveable { mutableStateOf(prefs.senderFilterEnabled) }
    var allowlist by remember { mutableStateOf(prefs.senderAllowlist) }
    var newSender by rememberSaveable { mutableStateOf("") }
    var bankOnly by rememberSaveable { mutableStateOf(prefs.bankSendersOnly) }
    var scanOnLaunch by rememberSaveable { mutableStateOf(prefs.scanOnLaunch) }
    var smartRules by rememberSaveable { mutableStateOf(prefs.smartRules) }
    var budgetsEnabled by rememberSaveable { mutableStateOf(prefs.budgetsEnabled) }
    var autoConfirmTransfers by rememberSaveable { mutableStateOf(prefs.autoConfirmTransfers) }
    var confirmWipe by remember { mutableStateOf(false) }
    var confirmExport by remember { mutableStateOf(false) }
    var pickCurrency by remember { mutableStateOf(false) }

    val appLocale = LocalConfiguration.current.locales[0]
    val enabledText = stringResource(R.string.activity_enabled)
    val disabledText = stringResource(R.string.activity_disabled)
    val logSetting = stringResource(R.string.activity_log_setting)
    val logBudget = stringResource(R.string.activity_log_budget)
    val logOnLaunch = stringResource(R.string.activity_log_on_launch)
    val logScanRange = stringResource(R.string.activity_log_scan_range)
    val logFreshStart = stringResource(R.string.activity_log_fresh_start)
    val logExpenseRemove = stringResource(R.string.activity_log_expense_remove)
    val logExpenseAdd = stringResource(R.string.activity_log_expense_add)
    val logIncomeRemove = stringResource(R.string.activity_log_income_remove)
    val logIncomeAdd = stringResource(R.string.activity_log_income_add)
    val logKeywordsReset = stringResource(R.string.activity_log_keywords_reset)
    val logBanks = stringResource(R.string.activity_log_banks)
    val logAllowlist = stringResource(R.string.activity_log_allowlist)
    val logSenderRemove = stringResource(R.string.activity_log_sender_remove)
    val logSenderAdd = stringResource(R.string.activity_log_sender_add)
    val logSenderApprove = stringResource(R.string.activity_log_sender_approve)
    val logLearning = stringResource(R.string.activity_log_learning)
    val logTransfers = stringResource(R.string.activity_log_transfers)
    val logCopied = stringResource(R.string.activity_log_copied)
    val logCleared = stringResource(R.string.activity_log_cleared)
    val logUpdateCheck = stringResource(R.string.activity_log_update_check)
    val logNoApk = stringResource(R.string.activity_log_no_apk)
    val logDownload = stringResource(R.string.activity_log_download)
    val logVersionCurrent = stringResource(R.string.activity_log_version_current)
    val logVersionAvailable = stringResource(R.string.activity_log_version_available)
    val logUpdateUnreachable = stringResource(R.string.activity_log_update_unreachable)
    val logCurrency = stringResource(R.string.activity_log_currency)
    fun logMessage(template: String, vararg args: Any): String =
        if (args.isEmpty()) template else String.format(appLocale, template, *args)

    fun note(text: String) {
        Verbose.info(logMessage(logSetting, text))
        Verbose.flush()
    }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.activity_settings_title)) }) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatusCard(
                lastScanAt = prefs.lastScanAt,
                records = txns.size,
                accounts = accounts.size,
                messagesRead = lastSummary?.scanned,
            )

            AppLanguageSetting()

            SettingsCard(stringResource(R.string.activity_your_money)) {
                ValueLine(
                    title = stringResource(R.string.activity_default_currency),
                    value = currency,
                    detail = stringResource(R.string.activity_currency_help),
                    onClick = { pickCurrency = true },
                )
                NavLine(
                    title = stringResource(R.string.activity_bank_accounts),
                    value = if (accounts.isEmpty()) stringResource(R.string.activity_none_yet) else "${accounts.size}",
                    detail = stringResource(R.string.activity_accounts_help),
                    onClick = onOpenAccounts,
                )
                NavLine(
                    title = stringResource(R.string.activity_categories),
                    value = if (rules.isEmpty()) null else pluralStringResource(R.plurals.activity_learned_rules, rules.size, rules.size),
                    detail = stringResource(R.string.activity_categories_help),
                    onClick = onOpenCategories,
                )
                SwitchLine(
                    title = stringResource(R.string.activity_budget),
                    value = if (budgetsEnabled && budgets.isNotEmpty()) pluralStringResource(R.plurals.activity_plans, budgets.size, budgets.size) else null,
                    checked = budgetsEnabled,
                    onCheckedChange = {
                        budgetsEnabled = it
                        vm.budgetsEnabled = it
                        note(logMessage(logBudget, if (it) enabledText else disabledText))
                    },
                    detail = stringResource(R.string.activity_budget_help),
                )
            }

            SettingsCard(stringResource(R.string.activity_scanning)) {
                ActionLine(
                    title = stringResource(R.string.activity_sms_permission),
                    value = if (hasPerm) stringResource(R.string.activity_allowed) else stringResource(R.string.activity_off),
                    valueIsWarning = !hasPerm,
                    detail = stringResource(R.string.activity_permission_help),
                    actionLabel = stringResource(R.string.activity_manage),
                    onAction = {
                        context.startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null),
                            ),
                        )
                    },
                )
                SwitchLine(
                    title = stringResource(R.string.activity_scan_on_launch),
                    checked = scanOnLaunch,
                    onCheckedChange = {
                        scanOnLaunch = it
                        prefs.scanOnLaunch = it
                        note(logMessage(logOnLaunch, if (it) enabledText else disabledText))
                    },
                    detail = stringResource(R.string.activity_scan_on_launch_help),
                )

                ExpandLine(
                    title = stringResource(R.string.activity_how_far_back),
                    value = rangeLabel(rangeMonths),
                    detail = stringResource(R.string.activity_how_far_back_help),
                ) {
                    val ranges = listOf(1, 3, 6, 12, 0).map { it to rangeLabel(it) }
                    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                        ranges.forEachIndexed { index, (months, label) ->
                            SegmentedButton(
                                selected = rangeMonths == months,
                                onClick = {
                                    rangeMonths = months
                                    prefs.scanRangeMonths = months
                                    note(logMessage(logScanRange, label))
                                },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = ranges.size),
                                modifier = Modifier.weight(1f),
                                icon = {},
                            ) { Text(label, softWrap = false, maxLines = 1, style = MaterialTheme.typography.labelMedium) }
                        }
                    }
                }
                // The fresh-start floor overrides the range above, so it stays on screen
                // rather than behind a tap: it is the reason "everything" can still show
                // nothing older than the day it was chosen.
                if (freshStart > 0L) {
                    StateNote(
                        stringResource(R.string.activity_fresh_start_detail, settingsDayFmt.format(Instant.ofEpochMilli(freshStart).atZone(ZoneId.systemDefault()))),
                        action = stringResource(R.string.activity_read_older),
                        onAction = {
                            prefs.scanSinceMillis = 0L
                            freshStart = 0L
                            note(logMessage(logFreshStart))
                        },
                    )
                }

                ExpandLine(
                    title = stringResource(R.string.activity_gate_keywords),
                    value = stringResource(R.string.activity_keyword_counts, expenseKw.size, incomeKw.size),
                    detail = stringResource(R.string.activity_gate_keywords_help),
                ) {
                    Text(stringResource(R.string.activity_money_out), style = MaterialTheme.typography.labelLarge)
                    KeywordChips(expenseKw) { kw ->
                        expenseKw = expenseKw - kw
                        prefs.expenseKeywords = expenseKw
                        note(logMessage(logExpenseRemove, kw))
                    }
                    AddKeywordRow(
                        value = newExpenseKw,
                        onValueChange = { newExpenseKw = it },
                        onAdd = {
                            val kw = newExpenseKw.trim().lowercase()
                            if (kw.isNotEmpty()) {
                                expenseKw = expenseKw + kw
                                prefs.expenseKeywords = expenseKw
                                note(logMessage(logExpenseAdd, kw))
                            }
                            newExpenseKw = ""
                        },
                    )
                    Text(stringResource(R.string.activity_money_in), style = MaterialTheme.typography.labelLarge)
                    KeywordChips(incomeKw) { kw ->
                        incomeKw = incomeKw - kw
                        prefs.incomeKeywords = incomeKw
                        note(logMessage(logIncomeRemove, kw))
                    }
                    AddKeywordRow(
                        value = newIncomeKw,
                        onValueChange = { newIncomeKw = it },
                        onAdd = {
                            val kw = newIncomeKw.trim().lowercase()
                            if (kw.isNotEmpty()) {
                                incomeKw = incomeKw + kw
                                prefs.incomeKeywords = incomeKw
                                note(logMessage(logIncomeAdd, kw))
                            }
                            newIncomeKw = ""
                        },
                    )
                    TextButton(onClick = {
                        prefs.resetKeywords()
                        expenseKw = prefs.expenseKeywords
                        incomeKw = prefs.incomeKeywords
                        note(logMessage(logKeywordsReset))
                    }) { Text(stringResource(R.string.activity_reset_defaults)) }
                }

                ExpandLine(
                    title = stringResource(R.string.activity_who_is_read),
                    value = senderSummary(bankOnly, senderFilter, allowlist.size),
                    detail = stringResource(R.string.activity_who_is_read_help),
                ) {
                    SwitchLine(
                        title = stringResource(R.string.activity_bank_senders_only),
                        checked = bankOnly,
                        onCheckedChange = {
                            bankOnly = it
                            prefs.bankSendersOnly = it
                            note(logMessage(logBanks, if (it) enabledText else disabledText))
                        },
                        detail = stringResource(R.string.activity_bank_senders_help),
                    )
                    SwitchLine(
                        title = stringResource(R.string.activity_approved_only),
                        checked = senderFilter,
                        onCheckedChange = {
                            senderFilter = it
                            prefs.senderFilterEnabled = it
                            note(logMessage(logAllowlist, if (it) enabledText else disabledText))
                        },
                        detail = stringResource(R.string.activity_approved_only_help),
                    )
                    if (senderFilter || bankOnly) {
                        if (allowlist.isEmpty()) {
                            Text(
                                if (senderFilter) stringResource(R.string.activity_approved_empty)
                                else stringResource(R.string.activity_extra_approved_empty),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (senderFilter) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            KeywordChips(allowlist) { sender ->
                                allowlist = allowlist - sender
                                prefs.senderAllowlist = allowlist
                                note(logMessage(logSenderRemove, sender))
                            }
                        }
                        // Add a sender by name directly, for a bank whose ID hasn't shown up
                        // in the inbox yet (or a contact number you know is a bank).
                        AddKeywordRow(
                            value = newSender,
                            onValueChange = { newSender = it },
                            label = stringResource(R.string.activity_sender_name),
                            onAdd = {
                                val s = newSender.trim()
                                if (s.isNotEmpty() && s !in allowlist) {
                                    allowlist = allowlist + s
                                    prefs.senderAllowlist = allowlist
                                    note(logMessage(logSenderAdd, s))
                                }
                                newSender = ""
                            },
                        )
                        val suggestions = remember(knownSenders, allowlist) {
                            (knownSenders - allowlist).sorted().take(12)
                        }
                        if (suggestions.isNotEmpty()) {
                            Text(
                                stringResource(R.string.activity_sender_suggestions),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                suggestions.forEach { sender ->
                                    AssistChip(
                                        onClick = {
                                            allowlist = allowlist + sender
                                            prefs.senderAllowlist = allowlist
                                            note(logMessage(logSenderApprove, sender))
                                        },
                                        label = { Text(bidiValue(sender)) },
                                    )
                                }
                            }
                        }
                    }
                }

                // Progress belongs on the screen that has the button. This row used to
                // start a scan whose sheet only Home and Activity rendered, so the work
                // you asked for here appeared over a screen you were not looking at.
                val scan by vm.scanState.collectAsState()
                val running = scan as? MainViewModel.ScanState.Running
                ActionLine(
                    title = stringResource(R.string.activity_scan_now),
                    value = running?.let { p ->
                        if (p.total > 0) stringResource(R.string.activity_progress, p.processed, p.total) else stringResource(R.string.activity_starting)
                    },
                    detail = stringResource(R.string.activity_scan_now_help),
                    actionLabel = if (running != null) stringResource(R.string.activity_scanning) else stringResource(R.string.activity_scan_action),
                    enabled = running == null,
                    onAction = { vm.startScan() },
                )
                ActionLine(
                    title = stringResource(R.string.activity_rescan_all),
                    value = null,
                    detail = stringResource(R.string.activity_rescan_help),
                    actionLabel = stringResource(R.string.activity_rescan),
                    enabled = running == null,
                    onAction = { vm.startScan(full = true) },
                )
                if (running != null) {
                    LinearProgressIndicator(
                        progress = {
                            if (running.total > 0) running.processed.toFloat() / running.total else 0f
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    )
                }
            }

            SettingsCard(stringResource(R.string.activity_automation)) {
                SwitchLine(
                    title = stringResource(R.string.activity_learn_corrections),
                    checked = smartRules,
                    onCheckedChange = {
                        smartRules = it
                        prefs.smartRules = it
                        note(logMessage(logLearning, if (it) enabledText else disabledText))
                    },
                    detail = stringResource(R.string.activity_learn_corrections_help),
                )
                SwitchLine(
                    title = stringResource(R.string.activity_confirm_transfers),
                    checked = autoConfirmTransfers,
                    onCheckedChange = {
                        autoConfirmTransfers = it
                        vm.autoConfirmTransfers = it
                        note(logMessage(logTransfers, if (it) enabledText else disabledText))
                    },
                    detail = stringResource(R.string.activity_confirm_transfers_help),
                )
            }

            SettingsCard(stringResource(R.string.activity_your_data)) {
                ActionLine(
                    title = stringResource(R.string.activity_export_transactions),
                    // The count belongs to the moment you ask for the file, not to a row
                    // you are only reading past. It is said in the confirmation instead.
                    value = null,
                    detail = stringResource(R.string.activity_export_help),
                    actionLabel = stringResource(R.string.activity_export),
                    onAction = { confirmExport = true },
                )
                ExpandLine(
                    title = stringResource(R.string.activity_verbose_log),
                    value = stringResource(R.string.activity_verbose_value),
                    detail = stringResource(R.string.activity_verbose_help),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = {
                            scope.launch { clipboard.setClipEntry(plainText(Verbose.dump())) }
                            note(logMessage(logCopied))
                        }) { Text(stringResource(R.string.activity_copy)) }
                        TextButton(onClick = {
                            Verbose.clear()
                            Verbose.info(logMessage(logCleared))
                            Verbose.flush()
                        }) { Text(stringResource(R.string.activity_clear)) }
                    }
                }
                ValueLine(
                    title = stringResource(R.string.activity_storage_title),
                    value = stringResource(R.string.activity_storage_value),
                    detail = stringResource(R.string.activity_storage_help),
                )
            }

            SettingsCard(stringResource(R.string.activity_about)) {
                // One row, every state. Normally it is the version you are on; when
                // GitHub is offering a later one it becomes the way to get it. The
                // release notes sit behind the (i) whichever way the check came out,
                // because "what changed" is worth reading on the version you already
                // have and not only on one you are about to install.
                val updateState by vm.updateState.collectAsState()
                val version = appVersion(context)
                val known = when (val u = updateState) {
                    is MainViewModel.UpdateState.Available -> u.release
                    is MainViewModel.UpdateState.UpToDate -> u.release
                    else -> null
                }
                val offered = (updateState as? MainViewModel.UpdateState.Available)?.release
                val checking = updateState is MainViewModel.UpdateState.Checking
                val notes = known?.let { plainNotes(it.notes) }?.takeIf { it.isNotBlank() }
                // Only a check the user tapped gets an answer said out loud. The daily
                // one in the background must stay silent.
                var asked by remember { mutableStateOf(false) }
                val updateDetail = when {
                    offered != null -> stringResource(R.string.activity_update_versions, version, offered.tag) + stringResource(R.string.activity_update_download_help)
                    updateState is MainViewModel.UpdateState.UpToDate -> stringResource(R.string.activity_version_current, version)
                    updateState is MainViewModel.UpdateState.Unreachable -> stringResource(R.string.activity_update_unreachable_help)
                    else -> stringResource(R.string.activity_about_help)
                } + if (notes != null) stringResource(R.string.activity_release_notes, known!!.tag) + notes else ""
                ActionLine(
                    title = stringResource(R.string.activity_brand),
                    value = when {
                        checking -> stringResource(R.string.activity_checking_ellipsis)
                        offered != null -> stringResource(R.string.activity_update_available, offered.tag)
                        updateState is MainViewModel.UpdateState.UpToDate -> stringResource(R.string.activity_version_latest, version)
                        updateState is MainViewModel.UpdateState.Unreachable ->
                            stringResource(R.string.activity_version_unchecked, version)
                        else -> version
                    },
                    valueIsWarning = offered != null,
                    detail = updateDetail,
                    actionLabel = when {
                        checking -> stringResource(R.string.activity_checking)
                        offered != null -> stringResource(R.string.activity_download)
                        else -> stringResource(R.string.activity_check_now)
                    },
                    enabled = !checking,
                    onAction = {
                        if (offered == null) {
                            asked = true
                            note(logMessage(logUpdateCheck))
                            vm.checkForUpdate(version, force = true)
                        } else if (!offered.hasApk) {
                            note(logMessage(logNoApk, offered.tag))
                        } else if (vm.downloadUpdate()) {
                            note(logMessage(logDownload, offered.tag))
                        }
                    },
                )
                // A check the user asked for has someone waiting on it, so it says how
                // it came out instead of leaving the row to be re-read.
                LaunchedEffect(updateState) {
                    when (val u = updateState) {
                        is MainViewModel.UpdateState.UpToDate ->
                            if (asked) { asked = false; note(logMessage(logVersionCurrent, version)) }
                        is MainViewModel.UpdateState.Available ->
                            if (asked) { asked = false; note(logMessage(logVersionAvailable, u.release.tag)) }
                        is MainViewModel.UpdateState.Unreachable ->
                            if (asked) { asked = false; note(logMessage(logUpdateUnreachable)) }
                        else -> Unit
                    }
                }
            }

            // Kept away from the switches on purpose: this is the one control on the
            // screen that cannot be undone by tapping it again.
            DangerCard(onClick = { confirmWipe = true })

            ToolbarSpacer()
        }
    }

    // The scan sheet is hosted here, on the screen whose button starts a scan.
    ScanSheetHost(vm)

    if (pickCurrency) {
        PickerDialog(
            title = stringResource(R.string.activity_default_currency),
            options = CURRENCIES,
            selected = currency,
            onPick = {
                currency = it
                prefs.defaultCurrency = it
                note(logMessage(logCurrency, it))
                pickCurrency = false
            },
            onDismiss = { pickCurrency = false },
        )
    }

    if (confirmWipe) {
        AlertDialog(
            onDismissRequest = { confirmWipe = false },
            title = { Text(stringResource(R.string.activity_delete_everything_title)) },
            text = {
                Text(
                    stringResource(R.string.activity_delete_everything_detail),
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.wipeAll()
                    confirmWipe = false
                }) { Text(stringResource(R.string.activity_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmWipe = false }) { Text(stringResource(R.string.activity_cancel)) }
            },
        )
    }

    if (confirmExport) {
        AlertDialog(
            onDismissRequest = { confirmExport = false },
            title = { Text(pluralStringResource(R.plurals.activity_export_records, txns.size, txns.size)) },
            text = {
                Text(
                    if (txns.isEmpty()) {
                        stringResource(R.string.activity_export_empty_detail)
                    } else {
                        stringResource(R.string.activity_export_confirm_detail)
                    },
                )
            },
            confirmButton = {
                TextButton(
                    enabled = txns.isNotEmpty(),
                    onClick = {
                        confirmExport = false
                        onExport()
                    },
                ) { Text(stringResource(R.string.activity_export)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmExport = false }) { Text(stringResource(R.string.activity_cancel)) }
            },
        )
    }
}

// ─────────────────────────── the state you came to check ───────────────────────────

/**
 * What the app has actually done, before anything it can be told to do. Settings is
 * where you go to find out whether the thing is working, and a screen of switches
 * answers every question except that one.
 */
@Composable
private fun StatusCard(
    lastScanAt: Long,
    records: Int,
    accounts: Int,
    messagesRead: Int?,
) {
    Card(Modifier.fillMaxWidth().popIn()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                if (lastScanAt <= 0L) stringResource(R.string.activity_no_scan) else stringResource(R.string.activity_last_scan, relativeTime(lastScanAt)),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                listOfNotNull(
                    pluralStringResource(R.plurals.activity_record_count, records, records),
                    pluralStringResource(R.plurals.activity_account_count, accounts, accounts),
                    messagesRead?.let { pluralStringResource(R.plurals.activity_messages_read, it, it) },
                ).joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** "2 hours ago", or the date once that stops being a useful way to say it. */
@Composable
private fun relativeTime(millis: Long): String {
    val minutes = (System.currentTimeMillis() - millis) / 60_000
    val locale = LocalConfiguration.current.locales[0]
    return when {
        minutes < 1 -> stringResource(R.string.activity_just_now)
        minutes < 60 -> pluralStringResource(R.plurals.activity_minutes_ago, minutes.toInt(), minutes.toInt())
        minutes < 60 * 24 -> pluralStringResource(R.plurals.activity_hours_ago, (minutes / 60).toInt(), (minutes / 60).toInt())
        minutes < 60 * 24 * 7 -> pluralStringResource(R.plurals.activity_days_ago, (minutes / (60 * 24)).toInt(), (minutes / (60 * 24)).toInt())
        else -> stringResource(R.string.activity_on_date, DateTimeFormatter.ofPattern("d MMM uuuu", locale).format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())))
    }
}

@Composable
private fun rangeLabel(months: Int): String = when (months) {
    0 -> stringResource(R.string.activity_everything)
    12 -> stringResource(R.string.activity_one_year)
    else -> pluralStringResource(R.plurals.activity_months_short, months, months)
}

@Composable
private fun senderSummary(bankOnly: Boolean, allowlistOn: Boolean, approved: Int): String = when {
    allowlistOn && approved == 0 -> stringResource(R.string.activity_no_senders)
    allowlistOn -> pluralStringResource(R.plurals.activity_approved_senders, approved, approved)
    bankOnly && approved > 0 -> pluralStringResource(R.plurals.activity_banks_extra, approved, approved)
    bankOnly -> stringResource(R.string.activity_banks_only)
    else -> stringResource(R.string.activity_any_sender)
}

// ─────────────────────────── one line per row ───────────────────────────

/**
 * The shape every row shares: a name, what it is currently set to, and the control
 * that changes it - on one line, so the screen can be read down the left edge for a
 * name and down the right edge for a value.
 *
 * [detail] is the paragraph that used to sit under the row at rest. It is one tap away
 * instead, because an explanation is read once and a setting is looked up many times.
 */
@Composable
private fun SettingLine(
    title: String,
    value: String? = null,
    detail: String? = null,
    valueIsWarning: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailing: @Composable RowScope.() -> Unit,
) {
    var showDetail by remember { mutableStateOf(false) }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            // One height for every row, whatever its control: a switch is taller than a
            // line of text, and the ragged rhythm that produced is most of what made
            // the screen read as a wall rather than a list.
            .heightIn(min = 52.dp)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        if (detail != null) {
            Icon(
                Icons.Filled.Info,
                contentDescription = stringResource(R.string.activity_about_setting, title),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showDetail = true },
            )
        }
        Spacer(Modifier.weight(1f))
        if (value != null) {
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                color = if (valueIsWarning) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        trailing()
    }

    if (showDetail && detail != null) {
        AlertDialog(
            onDismissRequest = { showDetail = false },
            title = { Text(title) },
            // Scrollable, because one of these is now a release body written by whoever
            // wrote it. Without this the dialog clips at the bottom of the screen with
            // no way to reach the rest - and the part it was cutting off was the
            // warning about the signing key, which is the one paragraph that has to be
            // read before tapping Download.
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) { Text(detail) }
            },
            confirmButton = { TextButton(onClick = { showDetail = false }) { Text(stringResource(R.string.activity_got_it)) } },
        )
    }
}

@Composable
private fun SwitchLine(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    detail: String,
    value: String? = null,
) {
    SettingLine(title = title, value = value, detail = detail) {
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

/** A row whose value is chosen elsewhere - in a dialog, or nowhere at all. */
@Composable
private fun ValueLine(
    title: String,
    value: String,
    detail: String? = null,
    onClick: (() -> Unit)? = null,
) {
    SettingLine(title = title, value = value, detail = detail, onClick = onClick) {
        if (onClick != null) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

/** A row that leads to another page. */
@Composable
private fun NavLine(
    title: String,
    value: String? = null,
    detail: String? = null,
    onClick: () -> Unit,
) {
    SettingLine(title = title, value = value, detail = detail, onClick = onClick) {
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
    }
}

/** A row whose control does something once, rather than holding a value. */
@Composable
private fun ActionLine(
    title: String,
    value: String?,
    detail: String,
    actionLabel: String,
    onAction: () -> Unit,
    valueIsWarning: Boolean = false,
    enabled: Boolean = true,
) {
    SettingLine(title = title, value = value, detail = detail, valueIsWarning = valueIsWarning) {
        TextButton(
            onClick = onAction,
            enabled = enabled,
            modifier = Modifier.pressBounce(),
        ) { Text(actionLabel) }
    }
}

/**
 * A row that opens into its own controls, for the settings that genuinely need more
 * than a switch - keywords, senders, the range. The screen stays a list of names and
 * values until one of them is actually being changed.
 */
@Composable
private fun ExpandLine(
    title: String,
    value: String,
    detail: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Column {
        SettingLine(
            title = title,
            value = value,
            detail = detail,
            onClick = { open = !open },
        ) {
            Icon(
                if (open) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (open) stringResource(R.string.activity_collapse_setting, title) else stringResource(R.string.activity_expand_setting, title),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
        AnimatedVisibility(
            visible = open,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column(
                Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = content,
            )
        }
    }
}

/**
 * A line that has to be on screen because it describes the state the app is in, not
 * what a control would do - the one kind of explanation worth the vertical space.
 */
@Composable
private fun StateNote(text: String, action: String, onAction: () -> Unit) {
    Column(Modifier.padding(bottom = 4.dp)) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        TextButton(onClick = onAction) { Text(action) }
    }
}

/**
 * The one irreversible control, in its own container at the end of the screen. Sitting
 * in a card of switches, it read as one more of them.
 */
@Composable
private fun DangerCard(onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().popIn(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        ),
    ) {
        Row(
            Modifier
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.activity_delete_data), style = MaterialTheme.typography.titleSmall)
                Text(
                    stringResource(R.string.activity_delete_data_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

/** One value out of a short list, without a dropdown anchored to a row. */
@Composable
private fun PickerDialog(
    title: String,
    options: List<String>,
    selected: String,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                options.forEach { option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onPick(option) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            option,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (option == selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.activity_cancel)) } },
    )
}

@Composable
private fun SettingsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth().popIn()) {
        Column(
            Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            content()
        }
    }
}

@Composable
private fun KeywordChips(items: Set<String>, onRemove: (String) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { item ->
            InputChip(
                selected = false,
                onClick = {},
                label = { Text(bidiValue(item)) },
                trailingIcon = {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = stringResource(R.string.activity_remove_item, bidiValue(item)),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onRemove(item) },
                    )
                },
            )
        }
    }
}

@Composable
private fun AddKeywordRow(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: () -> Unit,
    label: String = stringResource(R.string.activity_add_keyword),
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        FilledTonalButton(onClick = onAdd, enabled = value.isNotBlank()) { Text(stringResource(R.string.activity_add)) }
    }
}

/**
 * A GitHub release body as something to read in a dialog.
 *
 * The body is markdown, and this dialog is a Text: "**Upgrading clears your data.**"
 * arrives with its asterisks showing and "### Accounts" with its hashes. Nothing here
 * renders markdown, so the markers are taken off rather than displayed. Emphasis is
 * lost, which is a smaller cost than a screen of punctuation.
 */
private fun plainNotes(body: String): String = body.trim().lines().joinToString("\n") { line ->
    line.trimEnd()
        .replace(headingMark, "")
        .replace(boldMark, "")
        .replace(bulletMark, "• ")
}

private val headingMark = Regex("^#{1,6}\\s*")
private val bulletMark = Regex("^\\s*[-*]\\s+")
private val boldMark = Regex("\\*\\*|__")
