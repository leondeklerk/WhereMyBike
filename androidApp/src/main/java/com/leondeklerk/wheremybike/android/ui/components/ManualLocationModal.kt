package com.leondeklerk.wheremybike.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leondeklerk.wheremybike.android.R
import com.leondeklerk.wheremybike.formatDate
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualLocationModal(
    onDismissRequest: () -> Unit,
    onSubmitClick: () -> Unit,
    expiredDate: Instant,
    stalling: String,
    rij: String,
    nummer: String,
    onExpiredDateChange: (newExpiredDate: Instant) -> Unit,
    onStallingChange: (newStalling: String) -> Unit,
    onRijChange: (newRij: String) -> Unit,
    onNummerChange: (newNummer: String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        confirmValueChange = { it != SheetValue.PartiallyExpanded }, skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        windowInsets = BottomSheetDefaults.windowInsets,
        modifier = Modifier.fillMaxSize(),
        shape = AbsoluteCutCornerShape(0f)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            val focusRequester = remember { FocusRequester() }
            var showDatePicker by remember {
                mutableStateOf(false)
            }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            TextFieldRow(
                stalling,
                onStallingChange,
                stringResource(R.string.stalling),
                modifier = Modifier.focusRequester(focusRequester)
            )
            TextFieldRow(rij, onRijChange, stringResource(R.string.rij), maxLength = 2)
            TextFieldRow(
                nummer, onNummerChange,
                stringResource(R.string.nummer), imeAction = ImeAction.Done, maxLength = 3
            )

            Row(
                Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically
            ) {
                var expiredDateString by remember {
                    mutableStateOf(expiredDate.formatDate("dd-MM-yyyy"))
                }
                Column {
                    Text(stringResource(R.string.expiration_date), modifier = Modifier.padding(bottom = 4.dp))

                    OutlinedTextField(
                        value = expiredDateString,
                        onValueChange = { },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {
                                showDatePicker = true
                            }),
                        trailingIcon = {
                            Icon(
                                Icons.Default.EditCalendar,
                                contentDescription = stringResource(R.string.select_expire_date),
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = Color.Transparent,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledSuffixColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                    )
                }

                if (showDatePicker) {
                    ExpiredDatePickerDialog(startDate = expiredDate, onDateSelected = {
                        it?.let {
                            onExpiredDateChange(it)
                            expiredDateString = it.formatDate("dd-MM-yyyy")
                        }
                    }, onDismiss = { showDatePicker = false })
                }
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                val keyboardController = LocalSoftwareKeyboardController.current
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), onClick = {
                    keyboardController?.hide()
                    onSubmitClick();
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
//                        if (!sheetState.isVisible) {
//                            onSubmitClick()
//                        }
                    }
                }) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpiredDatePickerDialog(
    startDate: Instant, onDateSelected: (Instant?) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = startDate.toEpochMilliseconds(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= System.currentTimeMillis()
                }
            })

    DatePickerDialog(onDismissRequest = { onDismiss() }, confirmButton = {
        Button(onClick = {
            onDateSelected(datePickerState.selectedDateMillis?.let {
                Instant.fromEpochMilliseconds(it)
            })
            onDismiss()
        }

        ) {
            Text(text = stringResource(R.string.ok))
        }
    }, dismissButton = {
        Button(onClick = {
            onDismiss()
        }) {
            Text(text = stringResource(R.string.cancel))
        }
    }) {
        DatePicker(
            state = datePickerState
        )
    }
}

@Composable
fun TextFieldRow(
    initialValue: String,
    onChange: (fieldValue: String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Number,
    imeAction: ImeAction = ImeAction.Next,
    maxLength: Int = 1,
) {

    var value by remember { mutableStateOf(TextFieldValue(initialValue)) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        value = value.copy(
            selection = if (isFocused) {
                TextRange(
                    start = 0, end = value.text.length
                )
            } else {
                TextRange.Zero
            }
        )
    }

    Row(Modifier.fillMaxWidth()) {
        Column {
            Text(label, Modifier.padding(bottom = 4.dp))
            OutlinedTextField(
                modifier = modifier.fillMaxWidth(),
                value = value,
                onValueChange = {
                    if (it.text.length <= maxLength) {
                        value = it
                        onChange(it.text)
                    }
                },
                isError = false,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType, imeAction = imeAction
                ),
                textStyle = TextStyle.Default.copy(fontSize = 18.sp),
                interactionSource = interactionSource,
            )
        }

    }
}
