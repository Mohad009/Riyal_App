@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.alyaqdhan.riyal.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import com.alyaqdhan.riyal.R
import com.alyaqdhan.riyal.ui.compose.bidiValue
import com.alyaqdhan.riyal.ui.compose.accountLabel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alyaqdhan.riyal.core.Money
import com.alyaqdhan.riyal.data.Account
import com.alyaqdhan.riyal.data.Direction
import com.alyaqdhan.riyal.data.MsgTemplate
import com.alyaqdhan.riyal.data.ReviewItem
import com.alyaqdhan.riyal.data.TransferProposal
import com.alyaqdhan.riyal.data.TxnType
import com.alyaqdhan.riyal.ui.MainViewModel
import com.alyaqdhan.riyal.ui.compose.EmptyState
import com.alyaqdhan.riyal.ui.compose.Face
import com.alyaqdhan.riyal.ui.compose.FaceStyle
import com.alyaqdhan.riyal.ui.compose.HelpAction
import com.alyaqdhan.riyal.ui.compose.ManualTxnDialog
import com.alyaqdhan.riyal.ui.compose.SummaryPill
import com.alyaqdhan.riyal.ui.theme.successColor
import com.alyaqdhan.riyal.ui.compose.pressBounce
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

private val reviewDateFmt = DateTimeFormatter.ofPattern("dd MMM uuuu, h:mm a")

/**
 * Inner page (opened from the Home stringResource(R.string.management_needs_review) section): messages that matched the
 * keywords but could not be read automatically. Nothing was recorded for them, the
 * user decides what each one was, or dismisses it. With "Remember" checked the choice
 * teaches the app: dismissing hides similar messages too (restorable below), recording
 * marks that kind of message as wanted.
 */
/** What the page is for, behind the (i) rather than above the work. */
/** The item the manual dialog is open for, and what is already decided about it. */
private data class Resolving(
    val item: ReviewItem,
    val learnSimilar: Boolean,
    /** Set when the user already answered "money out" or "money in" on the card. */
    val type: TxnType? = null,
)

@Composable
fun ReviewScreen(vm: MainViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val mergedMessage = stringResource(R.string.management_merged_transfer)
    val keptMessage = stringResource(R.string.management_kept_records)
    val dismissedFuture = stringResource(R.string.management_dismissed_future)
    val dismissedMessage = stringResource(R.string.management_message_dismissed)
    val restoredMessage = stringResource(R.string.management_restored_review)
    val undoLabel = stringResource(R.string.management_undo)
    val reviews by vm.reviews.collectAsState()
    val transfers by vm.pendingTransfers.collectAsState()
    val allTransfers by vm.transfers.collectAsState()
    val autoConfirmOn by vm.autoConfirmOn.collectAsState()
    val accounts by vm.accounts.collectAsState()
    val categoryUse by vm.categoryUse.collectAsState()
    // Confirmed pairs never reach this queue, so the page says so rather than looking
    // as if the app found nothing.
    val autoConfirmed = remember(allTransfers) {
        allTransfers.count { it.state == TransferProposal.STATE_ACCEPTED }
    }
    val autoNote = if (autoConfirmOn && autoConfirmed > 0) {
        pluralStringResource(R.plurals.management_auto_transfers_hint, autoConfirmed, autoConfirmed)
    } else {
        null
    }
    val pending = remember(reviews) { reviews.filter { it.state == ReviewItem.STATE_PENDING } }
    val dismissed = remember(reviews) { reviews.filter { it.state == ReviewItem.STATE_DISMISSED } }
    // What the manual dialog is finishing. A direction-only item arrives with its
    // amount and the answer already given, so the dialog opens on the category rather
    // than on an empty amount field.
    // Save the pending item's id and the user's answer, not the non-saveable ReviewItem.
    // After recreation the item is read from the same review state as the list.
    var resolvingId by rememberSaveable { mutableStateOf<String?>(null) }
    var resolvingLearnSimilar by rememberSaveable { mutableStateOf(false) }
    var resolvingTypeName by rememberSaveable { mutableStateOf<String?>(null) }
    val resolving = reviews.firstOrNull { it.id == resolvingId && it.state == ReviewItem.STATE_PENDING }
        ?.let { item ->
            Resolving(item, resolvingLearnSimilar,
                resolvingTypeName?.let { name -> TxnType.values().firstOrNull { it.name == name } })
        }
    var showDismissed by rememberSaveable { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.management_needs_review)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.management_back))
                    }
                },
                actions = { HelpAction(stringResource(R.string.management_needs_review), stringResource(R.string.management_review_help)) },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            if (pending.isEmpty() && dismissed.isEmpty() && transfers.isEmpty()) {
                EmptyState(
                    style = FaceStyle.NORMAL,
                    mood = 0.9f,
                    title = stringResource(R.string.management_all_clear),
                    subtitle = stringResource(R.string.management_review_empty_hint) +
                        (autoNote?.let { "\n\n$it" } ?: ""),
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (transfers.isNotEmpty()) {
                        item(key = "transfer-header") {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    pluralStringResource(R.plurals.management_transfers_to_confirm, transfers.size, transfers.size),
                                    style = MaterialTheme.typography.titleSmall,
                                )
                                Text(
                                    stringResource(R.string.management_transfer_explanation),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        items(transfers, key = { "t-" + it.id }) { proposal ->
                            TransferCard(
                                proposal = proposal,
                                accounts = accounts,
                                onAccept = {
                                    vm.acceptTransfer(proposal)
                                    scope.launch { snackbar.showSnackbar(mergedMessage) }
                                },
                                onReject = {
                                    vm.rejectTransfer(proposal)
                                    scope.launch { snackbar.showSnackbar(keptMessage) }
                                },
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }
                    autoNote?.let { note ->
                        item(key = "auto-transfers") {
                            Text(
                                note,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = if (transfers.isEmpty()) 0.dp else 12.dp),
                            )
                        }
                    }
                    item(key = "intro") {
                        Text(
                            when {
                                pending.isEmpty() && transfers.isEmpty() -> stringResource(R.string.management_nothing_review)
                                pending.isEmpty() -> stringResource(R.string.management_only_transfers)
                                else -> stringResource(R.string.management_nothing_recorded)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = if (transfers.isEmpty()) 0.dp else 12.dp),
                        )
                    }
                    items(pending, key = { it.id }) { item ->
                        ReviewCard(
                            item = item,
                            rememberDefault = vm.prefs.smartRules,
                            onResolve = { learn ->
                                resolvingId = item.id
                                resolvingLearnSimilar = learn
                                resolvingTypeName = null
                            },
                            onDecide = { direction, word ->
                                if (word != null) vm.learnKeyword(word, direction)
                                resolvingId = item.id
                                resolvingLearnSimilar = false
                                resolvingTypeName = if (direction == Direction.EXPENSE) TxnType.EXPENSE.name
                                else TxnType.INCOME.name
                            },
                            onDismiss = { alsoSimilar ->
                                val similar = if (alsoSimilar) {
                                    val t = MsgTemplate.of(item.sender, item.body)
                                    pending.count { it.id != item.id && MsgTemplate.of(it.sender, it.body) == t }
                                } else 0
                                vm.dismissReview(item, alsoSimilar)
                                scope.launch {
                                    val result = snackbar.showSnackbar(
                                        message = when {
                                            similar > 0 -> context.resources.getQuantityString(R.plurals.management_dismissed_similar, similar, similar)
                                            alsoSimilar -> dismissedFuture
                                            else -> dismissedMessage
                                        },
                                        actionLabel = undoLabel,
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        vm.restoreReview(item)
                                    }
                                }
                            },
                            modifier = Modifier.animateItem(),
                        )
                    }
                    if (dismissed.isNotEmpty()) {
                        item(key = "dismissed-header") {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(pluralStringResource(R.plurals.management_dismissed_count, dismissed.size, dismissed.size), style = MaterialTheme.typography.titleSmall)
                                TextButton(onClick = { showDismissed = !showDismissed }) {
                                    Text(if (showDismissed) stringResource(R.string.management_hide) else stringResource(R.string.management_show))
                                }
                            }
                        }
                        if (showDismissed) {
                            item(key = "dismissed-hint") {
                                Text(
                                    stringResource(R.string.management_dismissed_hint),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            items(dismissed, key = { "d-" + it.id }) { item ->
                                DismissedCard(
                                    item = item,
                                    onRestore = {
                                        vm.restoreReview(item)
                                        scope.launch { snackbar.showSnackbar(restoredMessage) }
                                    },
                                    modifier = Modifier.animateItem(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    resolving?.let { open ->
        val item = open.item
        ManualTxnDialog(
            title = if (open.type != null) stringResource(R.string.management_which_category) else stringResource(R.string.management_what_was_this),
            atMillis = item.atMillis,
            defaultCurrency = vm.prefs.defaultCurrency,
            accounts = accounts,
            categoryUse = categoryUse,
            initialAmountMinor = item.amountMinor,
            initialCurrency = item.currency,
            initialType = open.type,
            onSave = { amountMinor, currency, type, merchant, categoryId, from, to ->
                vm.resolveReview(
                    item, amountMinor, currency, type, merchant, categoryId,
                    fromAccountId = from, toAccountId = to, learnSimilar = open.learnSimilar,
                )
                resolvingId = null
            },
            onDismiss = { resolvingId = null },
        )
    }
}

/**
 * One nominated transfer, shown as the two messages it came from so the user can see
 * exactly what would be merged. Deliberately a two-button decision with no default:
 * accepting wrongly quietly erases a real expense and a real income from every total,
 * so it is never something the app does on its own.
 */
@Composable
private fun TransferCard(
    proposal: TransferProposal,
    accounts: List<Account>,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val unassignedAccount = stringResource(R.string.management_unassigned_account)
    val fromAccount = accounts.firstOrNull { it.id == proposal.fromAccountId }
    val toAccount = accounts.firstOrNull { it.id == proposal.toAccountId }
    val fromName = if (fromAccount != null) accountLabel(fromAccount) else unassignedAccount
    val toName = if (toAccount != null) accountLabel(toAccount) else unassignedAccount

    Card(
        modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("⇄", style = MaterialTheme.typography.headlineSmall)
                Column(Modifier.weight(1f)) {
                    Text(
                        Money.format(proposal.amountMinor, proposal.currency),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    Text(
                        reviewDateFmt.withLocale(LocalConfiguration.current.locales[0]).format(
                            Instant.ofEpochMilli(proposal.atMillis).atZone(ZoneId.systemDefault())
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    stringResource(R.string.management_transfer_left, bidiValue(Money.format(proposal.amountMinor, proposal.currency)), bidiValue(fromName)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
                Text(
                    stringResource(R.string.management_transfer_arrived, bidiValue(Money.format(proposal.amountMinor, proposal.currency)), bidiValue(toName)),
                    style = MaterialTheme.typography.bodySmall,
                    color = successColor(),
                )
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                TextButton(onClick = onReject) { Text(stringResource(R.string.management_keep_both)) }
                Button(
                    onClick = onAccept,
                    shapes = ButtonDefaults.shapes(),
                    modifier = Modifier.pressBounce(),
                ) { Text(stringResource(R.string.management_confirm_transfer)) }
            }
        }
    }
}

@Composable
private fun ReviewCard(
    item: ReviewItem,
    rememberDefault: Boolean,
    onResolve: (learnSimilar: Boolean) -> Unit,
    onDecide: (direction: Direction, learnWord: String?) -> Unit,
    onDismiss: (alsoSimilar: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Face(
                    mood = if (item.directionOnly) 0.2f else -0.2f,
                    style = FaceStyle.CONFUSED,
                    modifier = Modifier.size(44.dp),
                )
                Column(Modifier.weight(1f)) {
                    if (item.directionOnly) {
                        Text(
                            bidiValue(Money.format(item.amountMinor!!, item.currency ?: "")),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            bidiValue(item.sender) + " · " + reviewDateFmt.withLocale(LocalConfiguration.current.locales[0]).format(
                                Instant.ofEpochMilli(item.atMillis).atZone(ZoneId.systemDefault())
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Text(bidiValue(item.sender), style = MaterialTheme.typography.titleSmall)
                        Text(
                            reviewDateFmt.withLocale(LocalConfiguration.current.locales[0]).format(Instant.ofEpochMilli(item.atMillis).atZone(ZoneId.systemDefault())),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            SummaryPill(
                stringResource(when {
                    item.reason == "no amount found" -> R.string.management_reason_no_amount
                    item.reason == "only balance-like amounts found" -> R.string.management_reason_balance_only
                    item.reason == "amount is zero" -> R.string.management_reason_zero
                    item.reason == "the amount is clear, the direction is not" -> R.string.management_reason_direction
                    item.reason.startsWith("could not parse amount") -> R.string.management_reason_parse
                    else -> R.string.management_reason_other
                }),
                if (item.directionOnly) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.errorContainer,
                if (item.directionOnly) MaterialTheme.colorScheme.onSecondaryContainer
                else MaterialTheme.colorScheme.onErrorContainer,
            )
            var expanded by remember { mutableStateOf(false) }
            Text(
                item.body,
                style = MaterialTheme.typography.bodySmall,
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { expanded = !expanded },
            )
            // The word is offered, never taken. A gate keyword decides what the app
            // reads at all, so adopting one on the evidence of a single message is the
            // user's call and starts unticked.
            var learnWord by remember { mutableStateOf<String?>(null) }
            if (item.directionOnly && item.suggestedWords.isNotEmpty()) {
                Text(
                    stringResource(R.string.management_learn_similar_messages),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.suggestedWords.take(2).forEach { word ->
                        FilterChip(
                            selected = learnWord == word,
                            onClick = { learnWord = if (learnWord == word) null else word },
                            label = { Text("\"$word\"") },
                        )
                    }
                }
            }
            var rememberChoice by remember { mutableStateOf(rememberDefault) }
            if (!item.directionOnly) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { rememberChoice = !rememberChoice },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(checked = rememberChoice, onCheckedChange = { rememberChoice = it })
                    Text(
                        stringResource(R.string.management_remember_similar),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            // One question, two answers. Everything else about the record is already
            // known, so this is the whole decision.
            if (item.directionOnly) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilledTonalButton(
                        onClick = { onDecide(Direction.EXPENSE, learnWord) },
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier.weight(1f).pressBounce(),
                    ) { Text(stringResource(R.string.management_money_out)) }
                    FilledTonalButton(
                        onClick = { onDecide(Direction.INCOME, learnWord) },
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier.weight(1f).pressBounce(),
                    ) { Text(stringResource(R.string.management_money_in)) }
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                TextButton(onClick = { onDismiss(rememberChoice) }) { Text(stringResource(R.string.management_dismiss)) }
                if (!item.directionOnly) {
                    FilledTonalButton(
                        onClick = { onResolve(rememberChoice) },
                        shapes = ButtonDefaults.shapes(),
                        modifier = Modifier.pressBounce(),
                    ) {
                        Text(stringResource(R.string.management_add_manually))
                    }
                }
            }
        }
    }
}

@Composable
private fun DismissedCard(
    item: ReviewItem,
    onRestore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(bidiValue(item.sender), style = MaterialTheme.typography.titleSmall)
                Text(
                    reviewDateFmt.withLocale(LocalConfiguration.current.locales[0]).format(Instant.ofEpochMilli(item.atMillis).atZone(ZoneId.systemDefault())),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    item.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            TextButton(onClick = onRestore) { Text(stringResource(R.string.management_restore)) }
        }
    }
}
