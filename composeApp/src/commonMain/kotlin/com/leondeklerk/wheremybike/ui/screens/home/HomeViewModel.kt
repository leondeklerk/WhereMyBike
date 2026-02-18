@file:OptIn(ExperimentalTime::class)

package com.leondeklerk.wheremybike.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leondeklerk.wheremybike.data.model.BikeLocation
import com.leondeklerk.wheremybike.data.repository.BikeLocationRepository
import com.leondeklerk.wheremybike.data.repository.ConfigRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class FormState(
    val stalling: String = "1",
    val rij: String = "1",
    val nummer: String = "1",
    val expiredDate: Instant = Clock.System.now().plus(2, DateTimeUnit.WEEK, TimeZone.UTC),
    val stallingError: String? = null,
    val rijError: String? = null,
    val nummerError: String? = null
) {
    val isValid: Boolean
        get() = stallingError == null && rijError == null && nummerError == null
}

data class HomeUiState(
    val currentLocation: BikeLocation? = null,
    val locationHistory: List<BikeLocation> = emptyList(),
    val expired: Boolean = false,
    val isAddingLocation: Boolean = false,
    val loading: Boolean = true,
    val defaultExpireDays: String = "14",
    val formState: FormState = FormState()
)

class HomeViewModel(
    private val bikeLocationRepository: BikeLocationRepository,
    private val configRepository: ConfigRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var initialExpiredDate = Clock.System.now().plus(2, DateTimeUnit.WEEK, TimeZone.UTC)

    companion object {
        const val MAX_FIELD_LENGTH = 25
    }

    private fun validateField(value: String): String? {
        return when {
            value.isEmpty() -> "required"
            value.length > MAX_FIELD_LENGTH -> "too_long"
            else -> null
        }
    }

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                bikeLocationRepository.observeCurrentLocation(),
                bikeLocationRepository.observeLocationHistory(),
                configRepository.observeConfig("default_expire_days")
            ) { currentLocation, locationHistory, defaultExpireDays ->
                defaultExpireDays?.toIntOrNull()?.let { defaultDayValue ->
                    initialExpiredDate = Clock.System.now().plus(defaultDayValue, DateTimeUnit.DAY, TimeZone.UTC)
                }

                _uiState.value.copy(
                    currentLocation = currentLocation,
                    locationHistory = locationHistory,
                    expired = currentLocation?.isExpired ?: false,
                    loading = false,
                    defaultExpireDays = defaultExpireDays ?: "14",
                    formState = if (_uiState.value.loading) {
                        _uiState.value.formState.copy(expiredDate = initialExpiredDate)
                    } else {
                        _uiState.value.formState
                    }
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun configureDefaultExpireDays(days: String) {
        viewModelScope.launch(ioDispatcher) {
            configRepository.setConfig("default_expire_days", days)

            days.toIntOrNull()?.let { defaultDayValue ->
                initialExpiredDate = Clock.System.now().plus(defaultDayValue, DateTimeUnit.DAY, TimeZone.UTC)
            }

            _uiState.update { currentState ->
                currentState.copy(
                    formState = currentState.formState.copy(expiredDate = initialExpiredDate)
                )
            }
        }
    }

    fun setManualLocation() {
        val form = _uiState.value.formState
        val newLocation = BikeLocation(
            startDate = Clock.System.now(),
            expiredDate = form.expiredDate,
            location = "${form.stalling}-${form.rij}-${form.nummer}"
        )

        _uiState.update { currentState ->
            currentState.copy(
                currentLocation = newLocation,
                locationHistory = listOf(newLocation) + currentState.locationHistory,
                expired = false,
                isAddingLocation = false,
                formState = FormState(expiredDate = initialExpiredDate)
            )
        }

        viewModelScope.launch(ioDispatcher) {
            bikeLocationRepository.insertLocation(newLocation)
        }
    }

    fun updateExpiredDate(newExpiredDate: Instant) {
        _uiState.update { it.copy(formState = it.formState.copy(expiredDate = newExpiredDate)) }
    }

    fun updateStalling(newStalling: String) {
        _uiState.update {
            it.copy(formState = it.formState.copy(
                stalling = newStalling,
                stallingError = validateField(newStalling)
            ))
        }
    }

    fun updateRij(newRij: String) {
        _uiState.update {
            it.copy(formState = it.formState.copy(
                rij = newRij,
                rijError = validateField(newRij)
            ))
        }
    }

    fun updateNummer(newNummer: String) {
        _uiState.update {
            it.copy(formState = it.formState.copy(
                nummer = newNummer,
                nummerError = validateField(newNummer)
            ))
        }
    }

    fun startAddingLocation() {
        _uiState.update { currentState ->
            currentState.copy(isAddingLocation = true)
        }
    }

    fun stopAddingLocation() {
        _uiState.update { currentState ->
            currentState.copy(isAddingLocation = false)
        }
    }
}

