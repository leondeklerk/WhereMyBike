package com.leondeklerk.wheremybike.ui.screens.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.leondeklerk.wheremybike.resources.Res
import com.leondeklerk.wheremybike.resources.cancel
import com.leondeklerk.wheremybike.resources.location_permission_rationale
import com.leondeklerk.wheremybike.resources.permission
import com.leondeklerk.wheremybike.resources.request_location_permission
import com.leondeklerk.wheremybike.resources.save_current_location
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun PlatformMapView(
    modifier: Modifier,
    lat: Double?,
    lon: Double?,
    hasMarker: Boolean,
    onSaveLocation: (lat: Double, lon: Double) -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val showRationalDialog = remember { mutableStateOf(false) }
    val locationPermission = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    val currentLocation = if (lat != null && lon != null) {
        LatLng(lat, lon)
    } else {
        resolveCurrentLocation(fusedLocationClient)
    }

    if (!locationPermission.status.isGranted) {
        if (locationPermission.status.shouldShowRationale) {
            TextButton(onClick = { showRationalDialog.value = true }) {
                Text(stringResource(Res.string.request_location_permission))
            }
        } else {
            LaunchedEffect(Unit) {
                locationPermission.launchPermissionRequest()
            }
        }
    } else {
        GoogleMapsView(
            modifier = modifier,
            currentLocation = currentLocation,
            hasMarker = hasMarker,
            fusedLocationClient = fusedLocationClient,
            onSaveLocation = onSaveLocation
        )
    }

    if (showRationalDialog.value) {
        LocationPermissionRationaleDialog(
            onDismiss = { showRationalDialog.value = false },
            onConfirm = {
                showRationalDialog.value = false
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ContextCompat.startActivity(context, intent, null)
            }
        )
    }
}

@Composable
private fun LocationPermissionRationaleDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(Res.string.permission),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Text(
                stringResource(Res.string.location_permission_rationale),
                fontSize = 16.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK", style = TextStyle(color = Color.Black))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel), style = TextStyle(color = Color.Black))
            }
        }
    )
}

@Composable
private fun GoogleMapsView(
    modifier: Modifier = Modifier,
    currentLocation: LatLng? = null,
    hasMarker: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    onSaveLocation: (lat: Double, lon: Double) -> Unit
) {
    var isMapLoaded by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            currentLocation ?: LatLng(51.919662988, 4.475331432),
            15f
        )
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.move(CameraUpdateFactory.newLatLng(it))
        }
    }

    Box(modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            onMapLoaded = { isMapLoaded = true },
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isIndoorEnabled = true, isMyLocationEnabled = true),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
                indoorLevelPickerEnabled = true,
                mapToolbarEnabled = true,
            )
        ) {
            if (hasMarker && currentLocation != null) {
                val markerState = remember(currentLocation) { MarkerState(position = currentLocation) }
                Marker(state = markerState)
            }
        }

        if (!isMapLoaded) {
            AnimatedVisibility(
                modifier = Modifier.matchParentSize(),
                visible = !isMapLoaded,
                enter = EnterTransition.None,
                exit = fadeOut()
            ) {
                CircularProgressIndicator(modifier = Modifier.wrapContentSize())
            }
        }

        SaveLocationButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            fusedLocationClient = fusedLocationClient,
            onSaveLocation = onSaveLocation
        )
    }
}

@Composable
private fun SaveLocationButton(
    modifier: Modifier = Modifier,
    fusedLocationClient: FusedLocationProviderClient,
    onSaveLocation: (lat: Double, lon: Double) -> Unit
) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        onSaveLocation(it.latitude, it.longitude)
                    }
                }
            }
        },
        modifier = modifier
    ) {
        Text(stringResource(Res.string.save_current_location))
    }
}

@Composable
private fun resolveCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient
): LatLng? {
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)
                }
            }
        }
    }

    return currentLocation
}

