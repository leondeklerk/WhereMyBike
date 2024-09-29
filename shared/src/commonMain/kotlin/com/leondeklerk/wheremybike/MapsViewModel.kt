package com.leondeklerk.wheremybike

import com.hoc081098.kmp.viewmodel.ViewModel
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
    val hasMarker: Boolean = false
)

class MapsViewModel(driverFactory: DriverFactory) : ViewModel() {
    private val _uiState = MutableStateFlow(MapsUiState())
    val uiState: StateFlow<MapsUiState> = _uiState.asStateFlow()

    private val database = createDatabase(driverFactory)

    init {
        initialize()
    }

    fun saveMapLocation(value: String) {
        viewModelScope.launch {
            database.setConfig("maps_location", value)
            val latLng = getLatLong(value)
            _uiState.update { currentState ->
                currentState.copy(
                    lat = latLng?.first,
                    lon = latLng?.second,
                    hasMarker = latLng != null
                )
            }
        }
    }

    private fun initialize() {
        viewModelScope.launch(Dispatchers.IO) {
            val location = database.getConfig("maps_location")
            val latLng = getLatLong(location)
            _uiState.update { currentState ->
                currentState.copy(
                    lat = latLng?.first,
                    lon = latLng?.second,
                    hasMarker = latLng != null
                )
            }
        }
    }

    private fun getLatLong(value: String?): Pair<Double, Double>? {
        return value?.let {
            val latLongArr = value.split(',');
            return Pair(latLongArr[0].toDouble(), latLongArr[1].toDouble())
        }
    }
}
