@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.alyaqdhan.riyal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import com.alyaqdhan.riyal.R
import com.alyaqdhan.riyal.ui.compose.bidiValue
import com.alyaqdhan.riyal.ui.compose.accountLabel
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.alyaqdhan.riyal.core.Money
import com.alyaqdhan.riyal.data.Account
import com.alyaqdhan.riyal.data.Categories
import com.alyaqdhan.riyal.ui.MainViewModel
import com.alyaqdhan.riyal.ui.compose.CURRENCIES
import com.alyaqdhan.riyal.ui.compose.DropdownField
import com.alyaqdhan.riyal.ui.compose.EmptyState
import com.alyaqdhan.riyal.ui.compose.Face
import com.alyaqdhan.riyal.ui.compose.FaceStyle
import com.alyaqdhan.riyal.ui.compose.HelpAction
import com.alyaqdhan.riyal.ui.compose.popIn
import com.alyaqdhan.riyal.ui.compose.pressBounce
import com.alyaqdhan.riyal.ui.theme.successColor
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

private val asOfFmt = DateTimeFormatter.ofPattern("dd MMM uuuu, h:mm a")

/**
 * The bank account manager, and the first-run confirmation the app asks for once.
 *
 * Balances here are the app's only claim about how much money exists, and they were
 * read out of SMS - a good first guess, not gospel. So the page opens by saying exactly
 * that and asking the user to vouch for the numbers before anything relies on them.
 */
/** What the page is for, behind the (i) rather than above the work. */
@Composable
fun AccountsScreen(vm: MainViewModel, onBack: () -> Unit) {
    val accounts by vm.accounts.collectAsState()
    val balances by vm.balances.collectAsState()
    val needsConfirming by vm.accountsNeedConfirming.collectAsState()
    // Save only stable identifiers and the new account's generated defaults. The editor's
    // text fields save their own draft values across locale recreation.
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var editingNew by rememberSaveable { mutableStateOf(false) }
    var newAccountColor by rememberSaveable { mutableStateOf(0) }
    var newAccountOpenedAt by rememberSaveable { mutableStateOf(0L) }
    var newAccountCurrency by rememberSaveable { mutableStateOf(vm.prefs.defaultCurrency) }
    var confirmDeleteId by rememberSaveable { mutableStateOf<String?>(null) }
    fun startAdding() {
        val blank = blankAccount(vm.prefs.defaultCurrency)
        editingId = blank.id
        editingNew = true
        newAccountColor = blank.color
        newAccountOpenedAt = blank.openingAtMillis
        newAccountCurrency = blank.currency
    }
    val editing = editingId?.let { id ->
        if (editingNew) Account(
            id = id,
            name = "",
            bankName = "",
            last4 = null,
            currency = newAccountCurrency,
            openingBalanceMinor = 0L,
            openingAtMillis = newAccountOpenedAt,
            color = newAccountColor,
            needsBalance = true,
        ) else accounts.firstOrNull { it.id == id }
    }
    val confirmDelete = accounts.firstOrNull { it.id == confirmDeleteId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.management_bank_accounts)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.management_back))
                    }
                },
                actions = { HelpAction(stringResource(R.string.management_bank_accounts), stringResource(R.string.management_accounts_help)) },
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            if (accounts.isEmpty()) {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    EmptyState(
                        style = FaceStyle.SLEEPY,
                        title = stringResource(R.string.management_no_accounts),
                        subtitle = stringResource(R.string.management_accounts_empty_hint),
                    )
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        FilledTonalButton(
                            onClick = { startAdding() },
                            modifier = Modifier.pressBounce(),
                        ) { Text(stringResource(R.string.management_add_account)) }
                    }
                }
                return@Column
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (needsConfirming) {
                    item(key = "confirm") {
                        ConfirmBanner(
                            count = accounts.size,
                            anyMissingBalance = accounts.any { it.needsBalance },
                            onConfirm = { vm.confirmAccounts() },
                        )
                    }
                }
                item(key = "total") {
                    TotalRow(accounts, balances)
                }
                items(accounts, key = { it.id }) { account ->
                    AccountCard(
                        account = account,
                        balance = balances[account.id] ?: account.openingBalanceMinor,
                        onEdit = { editingId = account.id; editingNew = false },
                    )
                }
                item(key = "add") {
                    FilledTonalButton(
                        onClick = { startAdding() },
                        modifier = Modifier.fillMaxWidth().pressBounce(),
                    ) { Text(stringResource(R.string.management_add_account)) }
                }
            }
        }
    }

    editing?.let { account ->
        AccountEditorDialog(
            account = account,
            isNew = editingNew,
            onSave = {
                vm.saveAccount(it)
                editingId = null
            },
            onDelete = {
                confirmDeleteId = account.id
                editingId = null
            },
            onDismiss = { editingId = null },
        )
    }

    confirmDelete?.let { account ->
        AlertDialog(
            onDismissRequest = { confirmDeleteId = null },
            title = { Text(stringResource(R.string.management_delete_named, bidiValue(accountLabel(account)))) },
            text = {
                Text(
                    stringResource(R.string.management_account_delete_hint)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteAccount(account.id)
                    confirmDeleteId = null
                }) { Text(stringResource(R.string.management_delete)) }
            },
            dismissButton = { TextButton(onClick = { confirmDeleteId = null }) { Text(stringResource(R.string.management_cancel)) } },
        )
    }
}

@Composable
private fun ConfirmBanner(count: Int, anyMissingBalance: Boolean, onConfirm: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth().popIn(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Face(mood = 0.4f, style = FaceStyle.CONFUSED, modifier = Modifier.size(48.dp))
                Column {
                    Text(
                        stringResource(R.string.management_confirm_accounts_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    // One sentence, and only the one this state needs: the banner used
                    // to say the same thing three ways before the button that ends it.
                    Text(
                        if (anyMissingBalance) {
                            pluralStringResource(R.plurals.management_accounts_confirm_missing, count, count)
                        } else {
                            pluralStringResource(R.plurals.management_accounts_confirm_hint, count, count)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth().pressBounce(),
            ) { Text(stringResource(R.string.management_confirm_accounts)) }
        }
    }
}

@Composable
private fun TotalRow(accounts: List<Account>, balances: Map<String, Long>) {
    // Only same-currency accounts can be added up; mixing them would invent an exchange
    // rate the app has no business guessing.
    val live = accounts.filter { !it.archived }
    val byCurrency = live.groupBy { it.currency }
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            stringResource(R.string.management_total),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        byCurrency.forEach { (currency, group) ->
            val total = group.sumOf { balances[it.id] ?: it.openingBalanceMinor }
            Text(
                bidiValue(Money.format(total, currency)),
                style = MaterialTheme.typography.headlineSmall,
                color = if (total < 0) MaterialTheme.colorScheme.error else successColor(),
            )
        }
    }
}

@Composable
private fun AccountCard(
    account: Account,
    balance: Long,
    onEdit: () -> Unit,
) {
    // Two lines: which account, and what is in it. Everything else the card used to
    // recite - the opening figure, the senders it reads, Edit and Delete - is in the
    // editor this row opens, which is where it can actually be changed.
    Surface(
        onClick = onEdit,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier
            .fillMaxWidth()
            .popIn()
            .pressBounce(0.98f),
    ) {
        Row(
            Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        Color(if (account.color != 0) account.color else Categories.colorFor("other"))
                    ),
            )
            Column(Modifier.weight(1f)) {
                Text(bidiValue(accountLabel(account)), style = MaterialTheme.typography.titleMedium)
                if (account.needsBalance) {
                    Text(
                        stringResource(R.string.management_account_no_balance),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
            Text(
                bidiValue(Money.format(balance, account.currency)),
                style = MaterialTheme.typography.titleMedium,
                color = if (balance < 0) MaterialTheme.colorScheme.error else successColor(),
            )
        }
    }
}

@Composable
private fun AccountEditorDialog(
    account: Account,
    isNew: Boolean,
    onSave: (Account) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    var name by rememberSaveable(account.id) { mutableStateOf(account.name) }
    var bankName by rememberSaveable(account.id) { mutableStateOf(account.bankName) }
    var last4 by rememberSaveable(account.id) { mutableStateOf(account.last4.orEmpty()) }
    var currency by rememberSaveable(account.id) { mutableStateOf(account.currency) }
    var senders by rememberSaveable(account.id) { mutableStateOf(account.senderIds.joinToString(", ")) }
    var archived by rememberSaveable(account.id) { mutableStateOf(account.archived) }
    var balance by rememberSaveable(account.id) {
        mutableStateOf(
            if (account.openingBalanceMinor == 0L && account.needsBalance) ""
            else Money.toMajor(account.openingBalanceMinor, account.currency).toPlainString()
        )
    }
    val parsedBalance = balance.trim().replace(",", "").toBigDecimalOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) stringResource(R.string.management_add_account_title) else stringResource(R.string.management_edit_account)) },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.management_nickname)) },
                    placeholder = { Text(accountLabel(account.copy(name = "", bankName = bankName, last4 = last4.ifBlank { null }))) },
                    supportingText = {
                        Text(stringResource(R.string.management_nickname_hint))
                    },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text(stringResource(R.string.management_bank)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = balance,
                    onValueChange = { balance = it },
                    label = { Text(if (isNew) stringResource(R.string.management_balance_now) else stringResource(R.string.management_opening_balance)) },
                    suffix = { Text(currency) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = TextStyle(textDirection = TextDirection.Ltr),
                )
                Text(
                    if (isNew) {
                        stringResource(R.string.management_opening_new_hint)
                    } else {
                        stringResource(
                            R.string.management_opening_date_hint,
                            asOfFmt.withLocale(LocalConfiguration.current.locales[0]).format(
                                Instant.ofEpochMilli(account.openingAtMillis).atZone(ZoneId.systemDefault())
                            ),
                        )
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                DropdownField(
                    label = stringResource(R.string.management_currency),
                    value = currency,
                    options = CURRENCIES,
                    display = { it },
                    onSelect = { currency = it },
                )
                OutlinedTextField(
                    value = last4,
                    onValueChange = { last4 = it.filter(Char::isDigit).take(6) },
                    label = { Text(stringResource(R.string.management_account_digits)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                OutlinedTextField(
                    value = senders,
                    onValueChange = { senders = it },
                    label = { Text(stringResource(R.string.management_sms_senders)) },
                    placeholder = { Text(stringResource(R.string.management_sender_example)) },
                    supportingText = {
                        Text(stringResource(R.string.management_senders_hint))
                    },
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Switch(checked = archived, onCheckedChange = { archived = it })
                    Text(stringResource(R.string.management_closed_account), style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank() || bankName.isNotBlank() || last4.isNotBlank(),
                onClick = {
                    val minor = parsedBalance?.let { Money.toMinor(it, currency) } ?: 0L
                    onSave(
                        account.copy(
                            name = name.trim(),
                            bankName = bankName.trim(),
                            last4 = last4.trim().ifBlank { null },
                            currency = currency,
                            openingBalanceMinor = minor,
                            // A hand-entered figure is true as of now, so the opening
                            // moment moves with it and older records stop double-counting.
                            openingAtMillis = if (minor != account.openingBalanceMinor) {
                                System.currentTimeMillis()
                            } else {
                                account.openingAtMillis
                            },
                            senderIds = senders.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet(),
                            archived = archived,
                            needsBalance = false,
                        )
                    )
                },
            ) { Text(stringResource(R.string.management_save)) }
        },
        dismissButton = {
            Row {
                if (!isNew) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                    ) { Text(stringResource(R.string.management_delete)) }
                }
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.management_cancel)) }
            }
        },
    )
}

private fun blankAccount(currency: String) = Account(
    id = Account.ID_PREFIX + UUID.randomUUID().toString().take(8),
    name = "",
    bankName = "",
    last4 = null,
    currency = currency,
    openingBalanceMinor = 0L,
    openingAtMillis = System.currentTimeMillis(),
    color = Categories.PALETTE.random(),
    needsBalance = true,
)
