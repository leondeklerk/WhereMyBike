@file:OptIn(ExperimentalTime::class)

package com.leondeklerk.wheremybike.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
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
import com.leondeklerk.wheremybike.resources.expires_on
import com.leondeklerk.wheremybike.resources.location
import com.leondeklerk.wheremybike.resources.start_time
import com.leondeklerk.wheremybike.util.formatDate
import org.jetbrains.compose.resources.stringResource
import kotlin.time.ExperimentalTime

@Composable
fun TitleCardContent(
    expired: Boolean,
    location: BikeLocation?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                Icon(
                    Icons.Filled.Place,
                    contentDescription = stringResource(Res.string.location),
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = stringResource(Res.string.location),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = location?.location ?: "-",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    Spacer(Modifier.padding(horizontal = 8.dp, vertical = 8.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                Icon(
                    Icons.Filled.Schedule,
                    contentDescription = stringResource(Res.string.start_time),
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = stringResource(Res.string.start_time),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = location?.startDate?.formatDate("MM-dd-yyyy HH:mm") ?: "-",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                ExpiredIcon(expired)
                Text(
                    text = stringResource(Res.string.expires_on),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Row {
                Text(
                    text = location?.expiredDate?.formatDate("MM-dd-yyyy HH:mm") ?: "-",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ExpiredIcon(expired: Boolean) {
    val icon = if (expired) Icons.Filled.Warning else Icons.Filled.WarningAmber
    val tint = if (expired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface

    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.padding(end = 4.dp),
        tint = tint
    )
}

