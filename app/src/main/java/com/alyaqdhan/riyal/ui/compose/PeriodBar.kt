@file:OptIn(ExperimentalMaterial3Api::class)

package com.alyaqdhan.riyal.ui.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.alyaqdhan.riyal.R
import com.alyaqdhan.riyal.data.Txn
import java.time.YearMonth

/**
 * The period control used on Analysis, on the budget section and on a category page:
 * chevrons step by the slice's own length, and the title opens a picker with the usual
 * presets plus a calendar range. One control, so stepping months feels identical
 * wherever the user does it.
 */
@Composable
fun PeriodBar(
    slice: TimeSlice,
    onChange: (TimeSlice) -> Unit,
    txns: List<Txn>,
    modifier: Modifier = Modifier,
    allowFuture: Boolean = false,
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    var showRange by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { onChange(slice.shifted(back = true)) }) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = stringResource(R.string.forms_earlier_period))
        }
        AnimatedContent(targetState = timeSliceLabel(slice), label = "sliceTitle") { label ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { showPicker = true },
            ) {
                Text(label, style = MaterialTheme.typography.titleMedium)
                Icon(Icons.Filled.ArrowDropDown, contentDescription = stringResource(R.string.forms_pick_period))
            }
        }
        IconButton(
            onClick = { onChange(slice.shifted(back = false)) },
            enabled = allowFuture || slice.endExclusive <= System.currentTimeMillis(),
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = stringResource(R.string.forms_later_period))
        }
    }

    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text(stringResource(R.string.forms_pick_period)) },
            text = {
                Column {
                    val now = YearMonth.now()
                    fun choose(s: TimeSlice) {
                        onChange(s)
                        showPicker = false
                    }
                    PickerOption(stringResource(R.string.forms_this_month)) { choose(TimeSlice.ofMonth(now)) }
                    PickerOption(stringResource(R.string.forms_last_month)) { choose(TimeSlice.ofMonth(now.minusMonths(1))) }
                    PickerOption(stringResource(R.string.forms_this_week)) { choose(TimeSlice.thisWeek()) }
                    PickerOption(stringResource(R.string.forms_last_three_months)) { choose(TimeSlice.lastMonths(3)) }
                    PickerOption(stringResource(R.string.forms_last_six_months)) { choose(TimeSlice.lastMonths(6)) }
                    PickerOption(stringResource(R.string.forms_this_year)) { choose(TimeSlice.thisYear()) }
                    PickerOption(stringResource(R.string.forms_all_time)) { choose(TimeSlice.allTime(txns)) }
                    PickerOption(stringResource(R.string.forms_custom_range)) {
                        showPicker = false
                        showRange = true
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showPicker = false }) { Text(stringResource(R.string.forms_close)) } },
        )
    }

    if (showRange) {
        val rangeState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showRange = false },
            confirmButton = {
                TextButton(
                    enabled = rangeState.selectedStartDateMillis != null &&
                        rangeState.selectedEndDateMillis != null,
                    onClick = {
                        onChange(
                            TimeSlice.ofDays(
                                TimeSlice.utcDay(rangeState.selectedStartDateMillis!!),
                                TimeSlice.utcDay(rangeState.selectedEndDateMillis!!),
                            )
                        )
                        showRange = false
                    },
                ) { Text(stringResource(R.string.forms_apply)) }
            },
            dismissButton = { TextButton(onClick = { showRange = false }) { Text(stringResource(R.string.forms_cancel)) } },
        ) {
            DateRangePicker(state = rangeState, modifier = Modifier.height(460.dp))
        }
    }
}

@Composable
fun PickerOption(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(label, modifier = Modifier.fillMaxWidth())
    }
}
