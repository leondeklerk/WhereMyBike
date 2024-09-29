package com.leondeklerk.wheremybike.android.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leondeklerk.wheremybike.android.R

@Composable
fun ExpiredIcon(expired: Boolean) {
    val iconModifier = Modifier.padding(end = 4.dp)
    if (expired) {
        Icon(
            Icons.Default.WarningAmber,
            contentDescription = stringResource(R.string.expired),
            modifier = iconModifier,
        )
    } else {
        Icon(
            painter = painterResource(id = R.drawable.baseline_access_time_24),
            contentDescription = stringResource(R.string.expires),
            modifier = iconModifier,
        )
    }
}

@Preview(name = "Expired icon")
@Composable
fun PreviewExpiredIcon() {
    ExpiredIcon(expired = true)
}

@Preview(name = "Expired icon - not-expired")
@Composable
fun PreviewNonExpiredIcon() {
    ExpiredIcon(expired = false)
}
