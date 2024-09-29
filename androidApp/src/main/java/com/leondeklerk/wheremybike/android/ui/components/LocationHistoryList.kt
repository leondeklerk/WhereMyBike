package com.leondeklerk.wheremybike.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leondeklerk.wheremybike.ManualLocationEntry
import com.leondeklerk.wheremybike.formatDate
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

@Composable
fun LocationHistoryList(data: List<ManualLocationEntry>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(
            count = data.size,
            key = {
                it
            }
        ) { index ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                val item = data[index]
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)) {
                    Text(text = "(${item.location}) - ${item.startDate.formatDate("MM-dd-yyyy HH:mm")}", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.WarningAmber,
                        contentDescription = "Expired",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(18.dp),
                    )
                    Text(text = item.expiredDate.formatDate("MM-dd-yyyy HH:mm"), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Preview("Location History List", showBackground = true)
@Composable
fun PreviewLocationHistoryList() {
    val historyList = listOf(
        ManualLocationEntry(Clock.System.now(), Clock.System.now(), "1-1-1"),
        ManualLocationEntry(
            Clock.System.now().plus(1L, DateTimeUnit.HOUR),
            Clock.System.now().plus(11L, DateTimeUnit.HOUR),
            "3-4-5"
        ),
        ManualLocationEntry(
            Clock.System.now().plus(2L, DateTimeUnit.HOUR),
            Clock.System.now().plus(12L, DateTimeUnit.HOUR),
            "1-2-4"
        ),
        ManualLocationEntry(
            Clock.System.now().plus(3L, DateTimeUnit.HOUR),
            Clock.System.now().plus(13L, DateTimeUnit.HOUR),
            "2-4-9"
        ),
        ManualLocationEntry(
            Clock.System.now().plus(4L, DateTimeUnit.HOUR),
            Clock.System.now().plus(14L, DateTimeUnit.HOUR),
            "2-2-2"
        ),
    )
    LocationHistoryList(data = historyList, Modifier.fillMaxWidth())
}
