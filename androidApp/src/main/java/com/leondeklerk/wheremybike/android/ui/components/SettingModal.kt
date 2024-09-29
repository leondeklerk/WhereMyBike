import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.leondeklerk.wheremybike.android.R
import com.leondeklerk.wheremybike.android.ui.components.NumericField

@Composable
fun SettingModal(
    textValue: String,
    onTextChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var internalValue by remember { mutableStateOf(textValue) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.config)) },

        text = {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {

                NumericField(
                    value = internalValue,
                    onValueChange = {
                        internalValue = it
                        showError = it.isEmpty()
                    },
                    label = { Text(text = stringResource(R.string.default_expire_days_label)) },
                    isError = showError
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (internalValue.isNotEmpty()) {
                        onTextChange(internalValue)
                        onDismiss()
                    } else {
                        showError = true
                    }
                }) {
                    Text(stringResource(R.string.ok))
                }
            }
        }
    )
}
