@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalLayoutApi::class,
)

package com.alyaqdhan.riyal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import com.alyaqdhan.riyal.R
import com.alyaqdhan.riyal.ui.compose.bidiValue
import com.alyaqdhan.riyal.ui.compose.categoryLabel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.alyaqdhan.riyal.core.Money
import com.alyaqdhan.riyal.data.Categories
import com.alyaqdhan.riyal.data.Category
import com.alyaqdhan.riyal.data.Stats
import com.alyaqdhan.riyal.data.TxnType
import com.alyaqdhan.riyal.data.UserRule
import com.alyaqdhan.riyal.ui.MainViewModel
import com.alyaqdhan.riyal.ui.compose.HelpAction
import com.alyaqdhan.riyal.ui.compose.CategoryBadge
import com.alyaqdhan.riyal.ui.compose.CategoryChips
import com.alyaqdhan.riyal.ui.compose.CategoryVisuals
import com.alyaqdhan.riyal.ui.compose.PeriodBar
import com.alyaqdhan.riyal.ui.compose.SectionTitle
import com.alyaqdhan.riyal.ui.compose.TimeSlice
import com.alyaqdhan.riyal.ui.compose.popIn
import com.alyaqdhan.riyal.ui.compose.pressBounce
import com.alyaqdhan.riyal.ui.compose.rememberCategoryOrder
import com.alyaqdhan.riyal.ui.theme.successColor
import kotlin.math.roundToInt
import java.time.YearMonth

/**
 * Every category, split into what you spend and what you earn, each showing what it
 * actually cost or brought in over the chosen period. Tapping one opens its records.
 *
 * This is also where custom categories are created and edited - it used to be a block
 * buried in Settings, which is a strange place to manage something you look at daily.
 */
/** What the page is for, behind the (i) rather than above the work. */
@Composable
fun CategoriesScreen(
    vm: MainViewModel,
    onBack: () -> Unit,
    onOpenCategory: (String, TimeSlice) -> Unit,
) {
    val txns by vm.txns.collectAsState()
    // Read so a create/rename/delete recomposes the list built from the registry.
    val custom by vm.categories.collectAsState()
    val rules by vm.rules.collectAsState()
    val askEachTime by vm.askEachTime.collectAsState()
    val categoryUse by vm.categoryUse.collectAsState()
    val currency = remember(txns) { Stats.primaryCurrency(txns, vm.prefs.defaultCurrency) }
    var slice by rememberSaveable(stateSaver = listSaver<TimeSlice, Any>(
        save = { listOf(it.start, it.endExclusive, it.label, it.month?.toString().orEmpty()) },
        restore = {
            TimeSlice(
                start = it[0] as Long,
                endExclusive = it[1] as Long,
                label = it[2] as String,
                month = (it[3] as String).takeIf(String::isNotEmpty)?.let(YearMonth::parse),
            )
        },
    )) { mutableStateOf(TimeSlice.thisMonth()) }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var newCategoryColor by rememberSaveable { mutableStateOf(0) }
    var showEmpty by rememberSaveable { mutableStateOf(false) }
    var confirmDeleteId by rememberSaveable { mutableStateOf<String?>(null) }
    var addingKeyword by rememberSaveable { mutableStateOf(false) }
    val editing = editingId?.let { id ->
        if (id.isEmpty()) Category(id = "", name = "", color = newCategoryColor)
        else custom.firstOrNull { it.id == id }
    }
    val confirmDelete = custom.firstOrNull { it.id == confirmDeleteId }

    val expenses = remember(txns, slice, currency, custom) {
        Stats.breakdownIn(txns, slice.start, slice.endExclusive, currency, type = TxnType.EXPENSE)
    }
    val incomes = remember(txns, slice, currency, custom) {
        Stats.breakdownIn(txns, slice.start, slice.endExclusive, currency, type = TxnType.INCOME)
    }
    val counts = remember(txns, slice, custom) {
        txns.filter { slice.contains(it.atMillis) }.groupingBy { it.categoryId }.eachCount()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.management_categories)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.management_back))
                    }
                },
                actions = { HelpAction(stringResource(R.string.management_categories), stringResource(R.string.management_categories_help)) },
            )
        },
    ) { padding ->
        // Counted outside the list: it decides whether the toggle is worth a row at all.
        val hidden = remember(counts) {
            (Categories.forType(TxnType.EXPENSE) + Categories.forType(TxnType.INCOME))
                .count { (counts[it.id] ?: 0) == 0 }
        }
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            item(key = "period") {
                PeriodBar(slice = slice, onChange = { slice = it }, txns = txns)
            }

            item(key = "expense-title") { SectionTitle(stringResource(R.string.management_spending)) }
            val expenseCats = shown(Categories.forType(TxnType.EXPENSE), counts, showEmpty)
            items(expenseCats, key = { "e-" + it.id }) { cat ->
                val row = expenses.firstOrNull { it.categoryId == cat.id }
                CategoryRow(
                    category = cat,
                    amountMinor = row?.amountMinor ?: 0L,
                    fraction = row?.fraction ?: 0f,
                    count = counts[cat.id] ?: 0,
                    currency = currency,
                    income = false,
                    onClick = { onOpenCategory(cat.id, slice) },
                    onEdit = if (cat.custom) ({ editingId = cat.id }) else null,
                )
            }

            item(key = "income-title") { SectionTitle(stringResource(R.string.management_income)) }
            val incomeCats = shown(Categories.forType(TxnType.INCOME), counts, showEmpty)
            items(incomeCats, key = { "i-" + it.id }) { cat ->
                val row = incomes.firstOrNull { it.categoryId == cat.id }
                CategoryRow(
                    category = cat,
                    amountMinor = row?.amountMinor ?: 0L,
                    fraction = row?.fraction ?: 0f,
                    count = counts[cat.id] ?: 0,
                    currency = currency,
                    income = true,
                    onClick = { onOpenCategory(cat.id, slice) },
                    onEdit = if (cat.custom) ({ editingId = cat.id }) else null,
                )
            }

            // The categories with nothing in them are still worth having - they are what
            // a record gets filed into next - but this screen is for reading a period,
            // and eight rows of "nothing in this period · OMR 0.000" is most of what was
            // on it. They are one tap away instead.
            if (hidden > 0 || showEmpty) {
                item(key = "show-empty") {
                    TextButton(
                        onClick = { showEmpty = !showEmpty },
                        modifier = Modifier.padding(top = 4.dp),
                    ) {
                        Text(
                            if (showEmpty) stringResource(R.string.management_hide_empty_categories)
                            else pluralStringResource(R.plurals.management_show_empty_categories, hidden, hidden)
                        )
                    }
                }
            }

            item(key = "transfers") {
                val moved = Stats.transferTotalIn(txns, slice.start, slice.endExclusive, currency)
                if (moved > 0) {
                    Text(
                        stringResource(R.string.management_transfers_no_category, Money.format(moved, currency)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
            }

            item(key = "add") {
                FilledTonalButton(
                    onClick = {
                        newCategoryColor = Categories.PALETTE.random()
                        editingId = ""
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp).pressBounce(),
                ) { Text(stringResource(R.string.management_add_category)) }
            }

            // What the app has been taught, and the only place it can be untaught. A
            // rule re-files past records as well as future ones, which is too much to
            // do invisibly: it should be possible to read back every name that was
            // learned and take any of them away.
            item(key = "learned-title") { SectionTitle(stringResource(R.string.management_learned_title)) }
            item(key = "learned-intro") {
                Text(
                    if (rules.isEmpty() && askEachTime.isEmpty()) {
                        stringResource(R.string.management_learned_empty)
                    } else {
                        stringResource(R.string.management_learned_hint)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            item(key = "learned-add") {
                FilledTonalButton(
                    onClick = { addingKeyword = true },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).pressBounce(),
                ) { Text(stringResource(R.string.management_add_keyword)) }
            }
            val sortedRules = rules.sortedBy { it.pattern }
            items(sortedRules, key = { "rule-" + it.pattern + "-" + it.categoryId }) { rule ->
                LearnedRow(
                    name = rule.pattern,
                    detail = stringResource(R.string.management_filed_as, bidiValue(categoryLabel(Categories.byId(rule.categoryId)))),
                    categoryId = rule.categoryId,
                    actionLabel = stringResource(R.string.management_forget_rule),
                    onAction = { vm.removeRule(rule.pattern) },
                )
            }
            if (askEachTime.isNotEmpty()) {
                item(key = "asked-title") {
                    Text(
                        stringResource(R.string.management_ask_every_time_title),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 10.dp, bottom = 2.dp),
                    )
                }
                items(askEachTime.sorted(), key = { "ask-$it" }) { name ->
                    LearnedRow(
                        name = name,
                        detail = stringResource(R.string.management_never_saved_rule),
                        categoryId = null,
                        actionLabel = stringResource(R.string.management_remove),
                        onAction = { vm.setAskEachTime(name, false) },
                    )
                }
            }
        }
    }

    if (addingKeyword) {
        KeywordRuleDialog(
            askEachTime = askEachTime,
            categoryUse = categoryUse,
            onSave = { pattern, categoryId ->
                vm.addRule(pattern, categoryId)
                addingKeyword = false
            },
            onDismiss = { addingKeyword = false },
        )
    }

    editing?.let { cat ->
        CategoryEditorDialog(
            category = cat,
            onSave = { name, income, color, icon ->
                if (cat.id.isBlank()) vm.addCategory(name, income, color, icon)
                else vm.updateCategory(cat.id, name, color, icon)
                editingId = null
            },
            onDelete = if (cat.id.isNotBlank()) ({
                confirmDeleteId = cat.id
                editingId = null
            }) else null,
            onDismiss = { editingId = null },
        )
    }

    confirmDelete?.let { cat ->
        AlertDialog(
            onDismissRequest = { confirmDeleteId = null },
            title = { Text(stringResource(R.string.management_delete_named, bidiValue(categoryLabel(cat)))) },
            text = {
                Text(
                    stringResource(
                        R.string.management_category_delete_hint,
                        bidiValue(categoryLabel(Categories.byId(
                            if (cat.income) Categories.DEFAULT_INCOME else Categories.DEFAULT_EXPENSE
                        ))),
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteCategory(cat.id)
                    confirmDeleteId = null
                }) { Text(stringResource(R.string.management_delete)) }
            },
            dismissButton = { TextButton(onClick = { confirmDeleteId = null }) { Text(stringResource(R.string.management_cancel)) } },
        )
    }
}

@Composable
private fun CategoryRow(
    category: Category,
    amountMinor: Long,
    fraction: Float,
    count: Int,
    currency: String,
    income: Boolean,
    onClick: () -> Unit,
    onEdit: (() -> Unit)?,
) {
    val used = amountMinor > 0
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth().popIn().pressBounce(0.98f),
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CategoryBadge(category.id, size = 40.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        bidiValue(categoryLabel(category)),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f),
                    )
                    if (onEdit != null) {
                        TextButton(onClick = onEdit) { Text(stringResource(R.string.management_edit)) }
                    }
                }
                Text(
                    if (count == 0) stringResource(R.string.management_no_period_transactions)
                    else pluralStringResource(
                        if (income) R.plurals.management_category_income_count else R.plurals.management_category_expense_count,
                        count, count, (fraction * 100).roundToInt(),
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (used) {
                    LinearProgressIndicator(
                        progress = { fraction.coerceIn(0f, 1f) },
                        color = Color(Categories.colorFor(category.id)),
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape),
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    bidiValue(Money.format(amountMinor, currency)),
                    style = MaterialTheme.typography.titleSmall,
                    color = when {
                        !used -> MaterialTheme.colorScheme.onSurfaceVariant
                        income -> successColor()
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.management_open_category, bidiValue(categoryLabel(category))),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

/**
 * One thing the app has learned: the name, what it does, and how to undo it. Used for
 * both halves of the list, because "a rule" and "a name to ask about" are the same kind
 * of thing to the reader - something remembered about a counterparty.
 */
@Composable
private fun LearnedRow(
    name: String,
    detail: String,
    categoryId: String?,
    actionLabel: String,
    onAction: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth().popIn(),
    ) {
        Row(
            Modifier.padding(start = 14.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (categoryId != null) CategoryBadge(categoryId, size = 32.dp)
            Column(Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.bodyMedium)
                Text(
                    detail,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TextButton(onClick = onAction) { Text(actionLabel) }
        }
    }
}

/**
 * Teaches a keyword by hand, rather than waiting to correct a record and leaving
 * "Always" on. It writes exactly the same [UserRule] that switch does, so there is one
 * mechanism and one list, not two.
 *
 * The rule is scoped to one side of the ledger on purpose: the same counterparty can
 * pay you and be paid, and a category chosen for one direction must not file the other.
 */
@Composable
private fun KeywordRuleDialog(
    askEachTime: Set<String>,
    categoryUse: Map<String, Int>,
    onSave: (pattern: String, categoryId: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf("") }
    var income by rememberSaveable { mutableStateOf(false) }
    var categoryId by rememberSaveable { mutableStateOf(Categories.DEFAULT_EXPENSE) }
    val order = rememberCategoryOrder(categoryUse)

    val type = if (income) TxnType.INCOME else TxnType.EXPENSE
    val pattern = UserRule.patternOf(text)
    // A name marked "ask me every time" would have its rule dropped the moment it was
    // written, so say so here instead of accepting the word and silently discarding it.
    val blocked = pattern.isNotBlank() && pattern in askEachTime
    // Matching mirrors Categorizer.contains: short ASCII words match whole only.
    val wholeWord = pattern.length <= 4 && pattern.all { it in 'a'..'z' }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.management_add_keyword)) },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(R.string.management_keyword)) },
                    singleLine = true,
                    isError = blocked,
                )
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = !income,
                        onClick = {
                            income = false
                            categoryId = Categories.defaultFor(TxnType.EXPENSE)
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        modifier = Modifier.weight(1f),
                    ) { Text(stringResource(R.string.management_money_out), softWrap = false, maxLines = 1) }
                    SegmentedButton(
                        selected = income,
                        onClick = {
                            income = true
                            categoryId = Categories.defaultFor(TxnType.INCOME)
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        modifier = Modifier.weight(1f),
                    ) { Text(stringResource(R.string.management_money_in), softWrap = false, maxLines = 1) }
                }
                CategoryChips(
                    type = type,
                    selectedId = categoryId,
                    onSelect = { categoryId = it },
                    order = order,
                )
                if (blocked) {
                    Text(
                        stringResource(R.string.management_keyword_blocked, pattern),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                } else if (pattern.isNotBlank()) {
                    Text(
                        stringResource(
                        if (income) R.string.management_keyword_income_hint else R.string.management_keyword_expense_hint,
                        pattern, bidiValue(categoryLabel(Categories.byId(categoryId))),
                    ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (wholeWord) {
                        Text(
                            stringResource(R.string.management_keyword_whole_word, pattern),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = pattern.isNotBlank() && !blocked,
                onClick = { onSave(pattern, categoryId) },
            ) { Text(stringResource(R.string.management_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.management_cancel)) } },
    )
}

/** Create or rename a user category, and pick its colour from the shared palette. */
@Composable
private fun CategoryEditorDialog(
    category: Category,
    onSave: (name: String, income: Boolean, color: Int, icon: String) -> Unit,
    onDelete: (() -> Unit)?,
    onDismiss: () -> Unit,
) {
    val isNew = category.id.isBlank()
    var name by rememberSaveable { mutableStateOf(category.name) }
    var income by rememberSaveable { mutableStateOf(category.income) }
    var color by rememberSaveable {
        mutableStateOf(if (category.color != 0) category.color else Categories.PALETTE.first())
    }
    var icon by rememberSaveable { mutableStateOf(category.icon.ifBlank { CategoryVisuals.KEYS.first() }) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) stringResource(R.string.management_new_category) else stringResource(R.string.management_edit_category)) },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.management_name)) },
                    singleLine = true,
                )
                if (isNew) {
                    // Which side of the ledger a category lives on decides which pickers
                    // offer it, so it is fixed once records start using it.
                    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = !income,
                            onClick = { income = false },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            modifier = Modifier.weight(1f),
                        ) { Text(stringResource(R.string.management_expense), softWrap = false, maxLines = 1) }
                        SegmentedButton(
                            selected = income,
                            onClick = { income = true },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            modifier = Modifier.weight(1f),
                        ) { Text(stringResource(R.string.management_income), softWrap = false, maxLines = 1) }
                    }
                }
                // Shown above the swatches because the icon is what the badge reads as
                // at a glance in a list; the colour only tells it apart from its
                // neighbours.
                Text(stringResource(R.string.management_icon), style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    CategoryVisuals.KEYS.forEach { key ->
                        val picked = key == icon
                        Box(
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (picked) Color(color).copy(alpha = 0.24f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                                // The tint alone is not enough to say which one is
                                // picked: it is the colour being chosen next to it, and
                                // some of the palette sits close to the theme's own
                                // grey. The ring does not depend on that colour.
                                .then(
                                    if (picked) Modifier.border(
                                        2.dp,
                                        MaterialTheme.colorScheme.primary,
                                        CircleShape,
                                    ) else Modifier
                                )
                                .clickable { icon = key }
                                .pressBounce(0.9f),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painterResource(CategoryVisuals.byKey(key)),
                                contentDescription = stringResource(when (key) {
                                    "food" -> R.string.management_icon_food
                                    "groceries" -> R.string.management_icon_groceries
                                    "transport" -> R.string.management_icon_transport
                                    "telecom" -> R.string.management_icon_telecom
                                    "bills" -> R.string.management_icon_bills
                                    "utilities" -> R.string.management_icon_utilities
                                    "rent" -> R.string.management_icon_rent
                                    "home" -> R.string.management_icon_home
                                    "shopping" -> R.string.management_icon_shopping
                                    "health" -> R.string.management_icon_health
                                    "personalcare" -> R.string.management_icon_personalcare
                                    "entertainment" -> R.string.management_icon_entertainment
                                    "subscriptions" -> R.string.management_icon_subscriptions
                                    "travel" -> R.string.management_icon_travel
                                    "education" -> R.string.management_icon_education
                                    "insurance" -> R.string.management_icon_insurance
                                    "loan" -> R.string.management_icon_loan
                                    "charity" -> R.string.management_icon_charity
                                    "giving" -> R.string.management_icon_giving
                                    "government" -> R.string.management_icon_government
                                    "fees" -> R.string.management_icon_fees
                                    "cash" -> R.string.management_icon_cash
                                    "transfer" -> R.string.management_icon_transfer
                                    "salary" -> R.string.management_icon_salary
                                    "business" -> R.string.management_icon_business
                                    "investment" -> R.string.management_icon_investment
                                    "reimbursement" -> R.string.management_icon_reimbursement
                                    "cashback" -> R.string.management_icon_cashback
                                    "refund" -> R.string.management_icon_refund
                                    "gift" -> R.string.management_icon_gift
                                    "income" -> R.string.management_icon_income
                                    "other" -> R.string.management_icon_other
                                    else -> R.string.management_icon_other
                                }),
                                tint = if (picked) Color(color)
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                }
                Text(stringResource(R.string.management_color), style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Categories.PALETTE.forEach { swatch ->
                        Box(
                            Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(swatch))
                                .clickable { color = swatch },
                            contentAlignment = Alignment.Center,
                        ) {
                            if (swatch == color) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = stringResource(R.string.management_selected),
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = { onSave(name.trim(), income, color, icon) },
            ) { Text(stringResource(R.string.management_save)) }
        },
        dismissButton = {
            Row {
                if (onDelete != null) {
                    TextButton(onClick = onDelete) { Text(stringResource(R.string.management_delete)) }
                }
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.management_cancel)) }
            }
        },
    )
}

/**
 * The categories worth a row, most spent first, with the empty ones dropped unless
 * asked for. Ordering by what is in them rather than by the order they were declared
 * is the point: the one category carrying the period should not be the ninth row.
 */
private fun shown(
    cats: List<Category>,
    counts: Map<String, Int>,
    showEmpty: Boolean,
): List<Category> {
    val used = cats.filter { (counts[it.id] ?: 0) > 0 }
        .sortedByDescending { counts[it.id] ?: 0 }
    return if (showEmpty) used + cats.filterNot { it in used } else used
}
