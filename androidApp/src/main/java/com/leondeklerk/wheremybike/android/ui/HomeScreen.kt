package com.leondeklerk.wheremybike.android.ui

import com.leondeklerk.wheremybike.android.ui.theme.FietsLocatieTheme
import SettingModal
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leondeklerk.wheremybike.HomeViewModel
import com.leondeklerk.wheremybike.android.R
import com.leondeklerk.wheremybike.android.ui.components.LocationHistoryList
import com.leondeklerk.wheremybike.android.ui.components.ManualLocationModal
import com.leondeklerk.wheremybike.android.ui.components.TitleCardContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier, setSettingClickListener: (()->Unit) -> Unit, viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showPopup by remember { mutableStateOf(false) }

    setSettingClickListener {
        showPopup = true
    }

    Box(modifier = modifier) {
        Column(modifier.padding(16.dp)) {
            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.current_location),
                        style = MaterialTheme.typography.headlineLarge
                    )
                    HorizontalDivider(Modifier.padding(top = 8.dp, bottom = 16.dp))
                    TitleCardContent(state.expired, state.manualLocation)
                }

            }
            Spacer(Modifier.height(16.dp))
            LocationHistoryList(state.locationHistory, Modifier.fillMaxHeight())
        }

        HomeScreenFab(
            { viewModel.startAddingLocation() },
            Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )

        if (state.isAddingLocation) {
            ManualLocationModal(
                { viewModel.stopAddingLocation() },
                { viewModel.setManualLocation() },
                viewModel.expiredDate,
                viewModel.stalling,
                viewModel.rij,
                viewModel.nummer,
                { viewModel.updateExpiredDate(it) },
                { viewModel.updateStalling(it) },
                { viewModel.updateRij(it) },
                { viewModel.updateNummer(it) }
            )
        }
    }

    // Include the popup dialog
    if (showPopup) {
        SettingModal(
            textValue = state.defaultExpireDays,
            onTextChange = { viewModel.configureDefaultExpireDays(it) },
            onDismiss = { showPopup = false }
        )
    }
}

@Preview("Home screen", showBackground = true)
@Composable
fun PreviewHomeScreen() {
    FietsLocatieTheme {
        HomeScreen(Modifier.fillMaxSize(), {})
    }
}

@Composable
fun HomeScreenFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = { onClick() },
        icon = { Icon(Icons.Filled.Add, stringResource(R.string.add_location)) },
        text = { Text(text = stringResource(R.string.location)) },
    )
}





