@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package com.leondeklerk.wheremybike.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.leondeklerk.wheremybike.resources.Res
import com.leondeklerk.wheremybike.resources.cancel
import com.leondeklerk.wheremybike.resources.expiration_date
import com.leondeklerk.wheremybike.resources.nummer
import com.leondeklerk.wheremybike.resources.rij
import com.leondeklerk.wheremybike.resources.save
import com.leondeklerk.wheremybike.resources.select_expire_date
import com.leondeklerk.wheremybike.resources.stalling
import com.leondeklerk.wheremybike.resources.validation_required
import com.leondeklerk.wheremybike.resources.validation_too_long
import com.leondeklerk.wheremybike.util.formatDate
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
fun ManualLocationModal(
    onDismissRequest: () -> Unit,
    onSubmitClick: () -> Unit,
    expiredDate: Instant,
    stalling: String,
    rij: String,
    nummer: String,
    stallingError: String? = null,
    rijError: String? = null,
    nummerError: String? = null,
    isFormValid: Boolean = true,
    onExpiredDateChange: (newExpiredDate: Instant) -> Unit,
    onStallingChange: (newStalling: String) -> Unit,
    onRijChange: (newRij: String) -> Unit,
    onNummerChange: (newNummer: String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        confirmValueChange = { it != SheetValue.PartiallyExpanded },
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        contentWindowInsets = { BottomSheetDefaults.windowInsets },
        modifier = Modifier.fillMaxSize(),
        shape = AbsoluteCutCornerShape(0f)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .imePadding()
                .padding(16.dp),
        ) {
            val focusRequester = remember { FocusRequester() }
            var showDatePicker by remember { mutableStateOf(false) }

            val requiredError = stringResource(Res.string.validation_required)
            val tooLongError = stringResource(Res.string.validation_too_long)

            fun resolveErrorMessage(errorKey: String?): String? {
                return when (errorKey) {
                    "required" -> requiredError
                    "too_long" -> tooLongError
                    else -> null
                }
            }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                TextFieldRow(
                    value = stalling,
                    onValueChange = onStallingChange,
                    label = stringResource(Res.string.stalling),
                    modifier = Modifier.focusRequester(focusRequester),
                    isError = stallingError != null,
                    errorMessage = resolveErrorMessage(stallingError)
                )

                TextFieldRow(
                    value = rij,
                    onValueChange = onRijChange,
                    label = stringResource(Res.string.rij),
                    isError = rijError != null,
                    errorMessage = resolveErrorMessage(rijError)
                )

                TextFieldRow(
                    value = nummer,
                    onValueChange = onNummerChange,
                    label = stringResource(Res.string.nummer),
                    imeAction = ImeAction.Done,
                    isError = nummerError != null,
                    errorMessage = resolveErrorMessage(nummerError)
                )

                // Date picker field
                var expiredDateString by remember {
                    mutableStateOf(expiredDate.formatDate("dd-MM-yyyy"))
                }

                Column {
                    Text(
                        stringResource(Res.string.expiration_date),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    OutlinedTextField(
                        value = expiredDateString,
                        onValueChange = { },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { showDatePicker = true }),
                        trailingIcon = {
                            Icon(
                                Icons.Default.EditCalendar,
                                contentDescription = stringResource(Res.string.select_expire_date),
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = Color.Transparent,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        )
                    )
                }

                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = expiredDate.toEpochMilliseconds(),
                        selectableDates = object : SelectableDates {
                            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
                            }
                        }
                    )

                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    val newDate = Instant.fromEpochMilliseconds(it)
                                    onExpiredDateChange(newDate)
                                    expiredDateString = newDate.formatDate("dd-MM-yyyy")
                                }
                                showDatePicker = false
                            }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text(stringResource(Res.string.cancel))
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onDismissRequest) {
                    Text(stringResource(Res.string.cancel))
                }
                Button(
                    onClick = onSubmitClick,
                    enabled = isFormValid
                ) {
                    Text(stringResource(Res.string.save))
                }
            }
        }
    }
}

@Composable
private fun TextFieldRow(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(value, TextRange(value.length)))
    }

    // Sync external value changes into the TextFieldValue
    LaunchedEffect(value) {
        if (textFieldValue.text != value) {
            textFieldValue = textFieldValue.copy(text = value, selection = TextRange(value.length))
        }
    }

    Column(modifier) {
        Text(label, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                onValueChange(newValue.text)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = imeAction
            ),
            isError = isError,
            supportingText = if (isError && errorMessage != null) {
                { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        textFieldValue = textFieldValue.copy(
                            selection = TextRange(textFieldValue.text.length)
                        )
                    }
                }
        )
    }
}

