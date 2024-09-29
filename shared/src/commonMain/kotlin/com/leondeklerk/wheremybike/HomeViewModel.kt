package com.leondeklerk.wheremybike

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.cash.sqldelight.db.SqlDriver
import com.hoc081098.kmp.viewmodel.ViewModel
import com.hoc081098.kmp.viewmodel.wrapper.NonNullFlowWrapper
import com.hoc081098.kmp.viewmodel.wrapper.NonNullStateFlowWrapper
import com.hoc081098.kmp.viewmodel.wrapper.wrap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

data class HomeUiState(
    val manualLocation: ManualLocationEntry? = null,
    val locationHistory: List<ManualLocationEntry> = emptyList(),
    val expired: Boolean = false,
    val isAddingLocation: Boolean = false,
    val loading: Boolean = true,
    val defaultExpireDays: String = "14"
)

class HomeViewModel(driverFactory: DriverFactory) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var initialExpiredDate = Clock.System.now().plus(2, DateTimeUnit.WEEK, TimeZone.UTC)

    private val database = createDatabase(driverFactory)

    var stalling by mutableStateOf("1")
        private set

    var rij by mutableStateOf("1")
        private set

    var nummer by mutableStateOf("1")
        private set

    var expiredDate by mutableStateOf(initialExpiredDate)
        private set

    init {
        retrieveData()
    }

    private fun retrieveData() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentLocation = database.getCurrentLocation()
            val locationHistory = database.getHistory()
            val defaultExpireDays = database.getConfig("default_expire_days")
            defaultExpireDays?.let {
                it.toIntOrNull()?.let { defaultDayValue ->
                    initialExpiredDate =
                        Clock.System.now().plus(defaultDayValue, DateTimeUnit.DAY, TimeZone.UTC)
                    viewModelScope.launch(Dispatchers.Main) {
                        expiredDate = initialExpiredDate
                    }
                }
            }

            _uiState.update { currentState ->
                currentState.copy(
                    manualLocation = currentLocation,
                    locationHistory = locationHistory,
                    expired = currentLocation?.let { it.expiredDate <= Clock.System.now() }
                        ?: false,
                    loading = false,
                    defaultExpireDays = defaultExpireDays ?: "14"
                )
            }
        }

    }

    fun configureDefaultExpireDays(days: String) {
        viewModelScope.launch(Dispatchers.IO) {
            database.setConfig("default_expire_days", days)
            days.let {
                it.toIntOrNull()?.let { defaultDayValue ->
                    initialExpiredDate =
                        Clock.System.now().plus(defaultDayValue, DateTimeUnit.DAY, TimeZone.UTC)
                    viewModelScope.launch(Dispatchers.Main) {
                        expiredDate = initialExpiredDate
                    }
                }
            }

            _uiState.update { currentState ->
                currentState.copy(
                    defaultExpireDays = days
                )
            }
        }
    }

    fun setManualLocation() {
        val newLocation = ManualLocationEntry(
            Clock.System.now(),
            expiredDate,
            "$stalling-$rij-$nummer"
        )
        _uiState.update { currentState ->

            currentState.copy(
                manualLocation = newLocation,
                locationHistory = listOf(newLocation).plus(currentState.locationHistory),
                expired = false,
                isAddingLocation = false
            )

        }

        expiredDate = initialExpiredDate

        viewModelScope.launch(Dispatchers.IO) {
            database.insertLocation(newLocation)
        }
    }

    fun updateExpiredDate(newExpiredDate: Instant) {
        expiredDate = newExpiredDate
    }

    fun updateStalling(newStalling: String) {
        stalling = newStalling
    }

    fun updateRij(newRij: String) {
        rij = newRij
    }

    fun updateNummer(newNummer: String) {
        nummer = newNummer
    }

    fun startAddingLocation() {
        _uiState.update { currentState ->
            currentState.copy(
                isAddingLocation = true
            )

        }
    }

    fun stopAddingLocation() {
        _uiState.update { currentState ->
            currentState.copy(
                isAddingLocation = false
            )

        }
    }
}
