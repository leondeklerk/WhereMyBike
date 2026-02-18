@file:OptIn(ExperimentalTime::class)

package com.leondeklerk.wheremybike.ui.screens.home

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.leondeklerk.wheremybike.resources.Res
import com.leondeklerk.wheremybike.resources.add_location
import com.leondeklerk.wheremybike.resources.current_location
import com.leondeklerk.wheremybike.resources.location
import com.leondeklerk.wheremybike.ui.components.LocationHistoryList
import com.leondeklerk.wheremybike.ui.components.ManualLocationModal
import com.leondeklerk.wheremybike.ui.components.SettingModal
import com.leondeklerk.wheremybike.ui.components.TitleCardContent
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    setSettingClickListener: (() -> Unit) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showPopup by remember { mutableStateOf(false) }

    setSettingClickListener {
        showPopup = true
    }

    Box(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(16.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = stringResource(Res.string.current_location),
                        style = MaterialTheme.typography.headlineLarge
                    )
                    HorizontalDivider(Modifier.padding(top = 8.dp, bottom = 16.dp))
                    TitleCardContent(state.expired, state.currentLocation)
                }
            }
            Spacer(Modifier.height(16.dp))
            LocationHistoryList(state.locationHistory, Modifier.fillMaxHeight())
        }

        HomeScreenFab(
            onClick = { viewModel.startAddingLocation() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )

        if (state.isAddingLocation) {
            ManualLocationModal(
                onDismissRequest = { viewModel.stopAddingLocation() },
                onSubmitClick = { viewModel.setManualLocation() },
                expiredDate = state.formState.expiredDate,
                stalling = state.formState.stalling,
                rij = state.formState.rij,
                nummer = state.formState.nummer,
                stallingError = state.formState.stallingError,
                rijError = state.formState.rijError,
                nummerError = state.formState.nummerError,
                isFormValid = state.formState.isValid,
                onExpiredDateChange = { viewModel.updateExpiredDate(it) },
                onStallingChange = { viewModel.updateStalling(it) },
                onRijChange = { viewModel.updateRij(it) },
                onNummerChange = { viewModel.updateNummer(it) }
            )
        }
    }

    if (showPopup) {
        SettingModal(
            textValue = state.defaultExpireDays,
            onTextChange = { viewModel.configureDefaultExpireDays(it) },
            onDismiss = { showPopup = false }
        )
    }
}

@Composable
private fun HomeScreenFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        icon = { Icon(Icons.Filled.Add, stringResource(Res.string.add_location)) },
        text = { Text(text = stringResource(Res.string.location)) }
    )
}


