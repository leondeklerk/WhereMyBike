package com.leondeklerk.wheremybike.ui.screens.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leondeklerk.wheremybike.data.repository.ConfigRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapsUiState(
    val lat: Double? = null,
    val lon: Double? = null,
    val hasMarker: Boolean = false,
    val loading: Boolean = true
)

class MapsViewModel(
    private val configRepository: ConfigRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapsUiState())
    val uiState: StateFlow<MapsUiState> = _uiState.asStateFlow()

    init {
        observeSavedLocation()
    }

    fun saveMapLocation(lat: Double, lon: Double) {
        viewModelScope.launch(ioDispatcher) {
            val value = "$lat,$lon"
            configRepository.setConfig("maps_location", value)
        }
    }

    private fun observeSavedLocation() {
        viewModelScope.launch {
            configRepository.observeConfig("maps_location").collect { location ->
                val latLng = parseLatLong(location)

                _uiState.update { currentState ->
                    currentState.copy(
                        lat = latLng?.first,
                        lon = latLng?.second,
                        hasMarker = latLng != null,
                        loading = false
                    )
                }
            }
        }
    }

    private fun parseLatLong(value: String?): Pair<Double, Double>? {
        return value?.let {
            try {
                val parts = value.split(',')
                if (parts.size == 2) {
                    Pair(parts[0].toDouble(), parts[1].toDouble())
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
}

