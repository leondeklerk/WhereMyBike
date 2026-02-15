package com.leondeklerk.wheremybike.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leondeklerk.wheremybike.ManualLocationEntry
import com.leondeklerk.wheremybike.android.R
import com.leondeklerk.wheremybike.formatDate
import kotlin.time.Clock

@Composable
fun TitleCardContent(expired: Boolean, location: ManualLocationEntry?) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                Icon(
                    Icons.Filled.Place,
                    contentDescription = stringResource(R.string.location),
                    modifier = Modifier.padding(end = 4.dp)
                )

                Text(
                    text = stringResource(R.string.location),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = location?.location ?: "-",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.MiddleEllipsis
            )
        }

    }
    Spacer(Modifier.padding(horizontal = 8.dp, vertical = 8.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row() {
                Icon(
                    painter = painterResource(id = R.drawable.calendar_clock),
                    contentDescription = stringResource(R.string.start_time),
                    modifier = Modifier.padding(end = 4.dp)
                )

                Text(
                    text = stringResource(R.string.start_time),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = location?.startDate?.formatDate("MM-dd-yyyy HH:mm") ?: "-",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.MiddleEllipsis,
            )
        }
    }
    Spacer(Modifier.padding(horizontal = 8.dp, vertical = 8.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                ExpiredIcon(expired)
                Text(
                    text = stringResource(R.string.expires_on),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Row {
                Text(
                    text = location?.expiredDate?.formatDate("MM-dd-yyyy HH:mm") ?: "-",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis
                )
            }
        }
    }
}

@Preview("Expired Title Card", showBackground = true)
@Composable
fun PreviewExpiredTitleCardContent() {
    val activeLocation = ManualLocationEntry(Clock.System.now(), Clock.System.now(), "1-2-3")
    Column {
        TitleCardContent(expired = true, location = activeLocation)
    }
}

@Preview("Empty Title Card", showBackground = true)
@Composable
fun PreviewTitleCardContent() {
    Column {
        TitleCardContent(expired = false, location = null)
    }
}

@Preview("Title Card", showBackground = true)
@Composable
fun PreviewEmptyTitleCardContent() {
    val activeLocation = ManualLocationEntry(Clock.System.now(), Clock.System.now(), "1-2-3")
    Column {
        TitleCardContent(expired = false, location = activeLocation)

    }
}
