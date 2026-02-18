@file:OptIn(ExperimentalTime::class)

package com.leondeklerk.wheremybike.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.leondeklerk.wheremybike.data.model.BikeLocation
import com.leondeklerk.wheremybike.resources.Res
import com.leondeklerk.wheremybike.resources.expired
import com.leondeklerk.wheremybike.resources.expires_on
import com.leondeklerk.wheremybike.resources.start_time
import com.leondeklerk.wheremybike.util.formatDate
import org.jetbrains.compose.resources.stringResource
import kotlin.time.ExperimentalTime

@Composable
fun LocationHistoryList(
    data: List<BikeLocation>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(
            count = data.size,
            key = { it }
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
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = item.location,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = stringResource(Res.string.start_time),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(18.dp),
                        )
                        Text(
                            text = item.expiredDate.formatDate("MM-dd-yyyy HH:mm"),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = "-",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )
                    Row {
                        Icon(
                            Icons.Default.WarningAmber,
                            contentDescription = stringResource(Res.string.expires_on),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(18.dp),
                        )
                        Text(
                            text = item.expiredDate.formatDate("MM-dd-yyyy HH:mm"),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                }
            }
        }
    }
}

