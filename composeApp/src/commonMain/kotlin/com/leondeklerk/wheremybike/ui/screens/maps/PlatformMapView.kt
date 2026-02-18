package com.leondeklerk.wheremybike.ui.screens.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform-specific map view.
 * Android uses Google Maps, iOS uses Apple Maps.
 */
@Composable
expect fun PlatformMapView(
    modifier: Modifier,
    lat: Double?,
    lon: Double?,
    hasMarker: Boolean,
    onSaveLocation: (lat: Double, lon: Double) -> Unit
)

