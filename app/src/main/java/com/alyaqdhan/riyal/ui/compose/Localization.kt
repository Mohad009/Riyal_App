package com.alyaqdhan.riyal.ui.compose

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import androidx.core.text.BidiFormatter
import com.alyaqdhan.riyal.R
import com.alyaqdhan.riyal.data.Categories
import com.alyaqdhan.riyal.data.Category
import com.alyaqdhan.riyal.data.Account

/** Background operations need app locales on Android 12 too, where Application stays global. */
fun localizedContext(context: Context): Context {
    val selected = AppCompatDelegate.getApplicationLocales()
    val configuration = Configuration(context.resources.configuration)
    if (selected.isEmpty) configuration.setLocales(Resources.getSystem().configuration.locales)
    else configuration.setLocales(android.os.LocaleList.forLanguageTags(selected.toLanguageTags()))
    return context.createConfigurationContext(configuration)
}

/** Pure display lookup: identifiers, custom names and saved records are never rewritten. */
fun categoryNameResource(category: Category): Int? {
    val original = Categories.BUILTIN.firstOrNull { it.id == category.id } ?: return null
    if (category.custom || category.name != original.name) return null
    return when (category.id) {
        "food" -> R.string.category_food
        "groceries" -> R.string.category_groceries
        "transport" -> R.string.category_transport
        "bills" -> R.string.category_bills
        "utilities" -> R.string.category_utilities
        "rent" -> R.string.category_rent
        "shopping" -> R.string.category_shopping
        "health" -> R.string.category_health
        "entertainment" -> R.string.category_entertainment
        "subscriptions" -> R.string.category_subscriptions
        "travel" -> R.string.category_travel
        "education" -> R.string.category_education
        "personalcare" -> R.string.category_personalcare
        "home" -> R.string.category_home
        "insurance" -> R.string.category_insurance
        "loan" -> R.string.category_loan
        "charity" -> R.string.category_charity
        "giving" -> R.string.category_giving
        "government" -> R.string.category_government
        "fees" -> R.string.category_fees
        "cash" -> R.string.category_cash
        "sending" -> R.string.category_sending
        "transfer" -> R.string.category_transfer
        "other" -> R.string.category_other
        "salary" -> R.string.category_salary
        "business" -> R.string.category_business
        "investment" -> R.string.category_investment
        "rental" -> R.string.category_rental
        "reimbursement" -> R.string.category_reimbursement
        "cashback" -> R.string.category_cashback
        "refund" -> R.string.category_refund
        "gift" -> R.string.category_gift
        "borrowed" -> R.string.category_borrowed
        "income" -> R.string.category_income
        else -> null
    }
}

fun categoryLabel(context: Context, category: Category): String =
    categoryNameResource(category)?.let(context::getString) ?: category.name

@Composable
fun categoryLabel(category: Category): String =
    categoryNameResource(category)?.let { stringResource(it) } ?: category.name

fun accountLabel(context: Context, account: Account): String {
    if (account.name.isNotBlank() || account.bankName.isNotBlank()) return account.displayName
    val tail = account.last4?.trim()?.takeIf(String::isNotEmpty)
    return if (tail == null) context.getString(R.string.account_generic)
    else context.getString(R.string.account_number, tail)
}

@Composable
fun accountLabel(account: Account): String {
    if (account.name.isNotBlank() || account.bankName.isNotBlank()) return account.displayName
    val tail = account.last4?.trim()?.takeIf(String::isNotEmpty)
    return if (tail == null) stringResource(R.string.account_generic)
    else stringResource(R.string.account_number, tail)
}

/** Isolate user/source data for display. Never use these controls in storage or CSV. */
@Composable
fun bidiValue(value: String): String {
    val locale = LocalConfiguration.current.locales[0]
    return BidiFormatter.getInstance(locale).unicodeWrap(value)
}

@Composable
fun AppLanguageSetting() {
    var open by rememberSaveable { mutableStateOf(false) }
    val selected = AppCompatDelegate.getApplicationLocales().toLanguageTags().substringBefore('-')
    val options = listOf("" to stringResource(R.string.language_device), "en" to "English", "ar" to "العربية")
    val current = options.firstOrNull { it.first == selected }?.second ?: options.first().second
    TextButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
        ListItem(supportingContent = { Text(current) }) { Text(stringResource(R.string.app_language)) }
    }
    if (open) AlertDialog(
        onDismissRequest = { open = false },
        title = { Text(stringResource(R.string.app_language)) },
        text = {
            Column {
                Text(stringResource(R.string.language_explanation))
                options.forEach { (tag, label) ->
                    TextButton(onClick = {
                        open = false
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
                    }, modifier = Modifier.fillMaxWidth()) {
                        RadioButton(selected = tag == selected, onClick = null)
                        Text(label)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { open = false }) { Text(stringResource(R.string.cancel)) } },
    )
}
