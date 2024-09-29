package com.leondeklerk.wheremybike.android.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.leondeklerk.wheremybike.android.R

@Composable
fun NumericField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String = stringResource(R.string.validation_required)
) {
    var internalValue by remember { mutableStateOf(value) }
    var showError by remember { mutableStateOf(isError) }

    OutlinedTextField(
        value = internalValue,
        onValueChange = {
            if (it.all { char -> char.isDigit() }) {
                internalValue = it
                onValueChange(it)
                showError = it.isEmpty()
            }
        },
        label = label,
        supportingText = { if (showError) Text(errorMessage, color = MaterialTheme.colorScheme.error) },
        isError = showError,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )
}
