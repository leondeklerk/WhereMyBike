package com.leondeklerk.wheremybike.ui.screens.maps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import com.leondeklerk.wheremybike.resources.Res
import com.leondeklerk.wheremybike.resources.save_current_location
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import org.jetbrains.compose.resources.stringResource
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKUserTrackingModeFollow
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformMapView(
    modifier: Modifier,
    lat: Double?,
    lon: Double?,
    hasMarker: Boolean,
    onSaveLocation: (lat: Double, lon: Double) -> Unit
) {
    var currentLat by remember { mutableStateOf(lat ?: 51.919662988) }
    var currentLon by remember { mutableStateOf(lon ?: 4.475331432) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    val locationManager = remember { CLLocationManager() }

    DisposableEffect(Unit) {
        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: Int) {
                hasLocationPermission = didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse ||
                        didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedAlways
            }

            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val location = didUpdateLocations.lastOrNull()
                if (location != null) {
                    @Suppress("UNCHECKED_CAST")
                    val clLocation = location as? platform.CoreLocation.CLLocation
                    clLocation?.let {
                        it.coordinate.useContents {
                            currentLat = latitude
                            currentLon = longitude
                        }
                    }
                }
            }
        }

        locationManager.delegate = delegate
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()

        onDispose {
            locationManager.stopUpdatingLocation()
        }
    }

    Box(modifier = modifier) {
        UIKitView(
            factory = {
                MKMapView().apply {
                    showsUserLocation = true
                    userTrackingMode = MKUserTrackingModeFollow

                    val coordinate = CLLocationCoordinate2DMake(
                        lat ?: currentLat,
                        lon ?: currentLon
                    )
                    val region = MKCoordinateRegionMakeWithDistance(coordinate, 1000.0, 1000.0)
                    setRegion(region, animated = true)

                    if (hasMarker && lat != null && lon != null) {
                        val annotation = MKPointAnnotation().apply {
                            setCoordinate(CLLocationCoordinate2DMake(lat, lon))
                            setTitle("Bike Location")
                        }
                        addAnnotation(annotation)
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { mapView ->
                // Update map when location changes
                if (lat != null && lon != null) {
                    val coordinate = CLLocationCoordinate2DMake(lat, lon)
                    val region = MKCoordinateRegionMakeWithDistance(coordinate, 1000.0, 1000.0)
                    mapView.setRegion(region, animated = true)

                    // Remove old annotations and add new one if needed
                    if (hasMarker) {
                        mapView.removeAnnotations(mapView.annotations)
                        val annotation = MKPointAnnotation().apply {
                            setCoordinate(coordinate)
                            setTitle("Bike Location")
                        }
                        mapView.addAnnotation(annotation)
                    }
                }
            }
        )

        Button(
            onClick = {
                onSaveLocation(currentLat, currentLon)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(stringResource(Res.string.save_current_location))
        }
    }
}

