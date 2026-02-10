package com.leondeklerk.wheremybike.android.ui

import com.leondeklerk.wheremybike.android.ui.theme.FietsLocatieTheme
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.leondeklerk.wheremybike.MapsViewModel
import com.leondeklerk.wheremybike.android.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapsScreen(modifier: Modifier = Modifier, viewModel: MapsViewModel = viewModel()) {

    val showRationalDialog = remember { mutableStateOf(false) }

    val state by viewModel.uiState.collectAsState()

    val locationPermission =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    val currentLocation = if (state.lat != null && state.lon != null) {
        LatLng(state.lat!!, state.lon!!)
    } else {
        getCurrentLocation()
    }

    if (!locationPermission.status.isGranted) {
        if (locationPermission.status.shouldShowRationale) {
            TextButton(onClick = { showRationalDialog.value = true }) {
                Text(stringResource(R.string.request_location_permission))
            }
        } else {
            LaunchedEffect(Unit) {
                locationPermission.launchPermissionRequest()

            }
        }
    } else {
        MapsView(modifier, currentLocation, state.hasMarker) { value ->
            viewModel.saveMapLocation(
                value
            )
        }
    }

    val context = LocalContext.current
    if (showRationalDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showRationalDialog.value = false
            },
            title = {
                Text(
                    text = stringResource(R.string.permission),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    stringResource(R.string.location_permissions_are_required_for_the_map_to_function_please_grant_the_permission_if_you_want_to_use_this_feature),
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRationalDialog.value = false
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(context, intent, null)

                    }) {
                    Text(stringResource(android.R.string.ok), style = TextStyle(color = Color.Black))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRationalDialog.value = false
                    }) {
                    Text(stringResource(R.string.cancel), style = TextStyle(color = Color.Black))
                }
            },
        )
    }


}

@Composable
fun MapsView(
    modifier: Modifier = Modifier,
    currentLocation: LatLng? = null,
    hasMarker: Boolean,
    onSaveLocation: (value: String) -> Unit
) {
    var isMapLoaded by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position =
            CameraPosition.fromLatLngZoom(currentLocation ?: LatLng(51.919662988, 4.475331432), 15f)
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.move(CameraUpdateFactory.newLatLng(it))
        }
    }

    Box(modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            onMapLoaded = {
                isMapLoaded = true
            },
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isIndoorEnabled = true, isMyLocationEnabled = true),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
                indoorLevelPickerEnabled = true,
                mapToolbarEnabled = true,
            )
        ) {
            if (hasMarker && currentLocation != null) {
                val markerState =
                    remember(currentLocation) { MarkerState(position = currentLocation) }
                Marker(
                    state = markerState,
                )
            }
        }
        if (!isMapLoaded) {
            AnimatedVisibility(
                modifier = Modifier.matchParentSize(),
                visible = !isMapLoaded,
                enter = EnterTransition.None,
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.wrapContentSize()
                )
            }
        }

        SaveLocationButton(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp), onSaveLocation
        )

    }
}

@Preview("Home screen", showBackground = true)
@Composable
fun PreviewMapsScreen() {
    FietsLocatieTheme {
        MapsScreen(Modifier.fillMaxSize())
    }
}

@Composable
fun SaveLocationButton(modifier: Modifier = Modifier, onSaveLocation: (value: String) -> Unit) {
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    Button(
        onClick = {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            onSaveLocation("${location.latitude},${location.longitude}")
                        }
                    }
            }
        },
        modifier
    ) {
        Text(stringResource(R.string.save_current_location))
    }
}

@Composable
fun getCurrentLocation(): LatLng? {
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current
    val fusedLocationProviderClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)
                }
            }
        }
    }

    return currentLocation
}
