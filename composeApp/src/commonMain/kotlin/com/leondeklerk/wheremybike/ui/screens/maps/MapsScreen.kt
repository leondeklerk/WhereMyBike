package com.leondeklerk.wheremybike.ui.screens.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MapsScreen(
    modifier: Modifier = Modifier,
    viewModel: MapsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    PlatformMapView(
        modifier = modifier,
        lat = state.lat,
        lon = state.lon,
        hasMarker = state.hasMarker,
        onSaveLocation = { lat, lon ->
            viewModel.saveMapLocation(lat, lon)
        }
    )
}

