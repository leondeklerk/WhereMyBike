@file:OptIn(ExperimentalTime::class)

package com.leondeklerk.wheremybike.ui.screens.home

import com.leondeklerk.wheremybike.data.model.BikeLocation
import com.leondeklerk.wheremybike.data.repository.FakeBikeLocationRepository
import com.leondeklerk.wheremybike.data.repository.FakeConfigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var bikeLocationRepository: FakeBikeLocationRepository
    private lateinit var configRepository: FakeConfigRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        bikeLocationRepository = FakeBikeLocationRepository()
        configRepository = FakeConfigRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(bikeLocationRepository, configRepository, testDispatcher)
    }

    @Test
    fun initialState_isLoading() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value
        assertTrue(state.loading)
    }

    @Test
    fun afterDataLoaded_isNotLoading() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.loading)
    }

    @Test
    fun afterDataLoaded_noLocations_currentLocationIsNull() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.currentLocation)
        assertTrue(viewModel.uiState.value.locationHistory.isEmpty())
    }

    @Test
    fun afterDataLoaded_withLocations_showsCurrentLocation() = runTest(testDispatcher) {
        val location = BikeLocation(
            startDate = Clock.System.now(),
            expiredDate = Clock.System.now() + 14.days,
            location = "1-2-3"
        )
        bikeLocationRepository.setLocations(listOf(location))

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.currentLocation)
        assertEquals("1-2-3", viewModel.uiState.value.currentLocation?.location)
        assertEquals(1, viewModel.uiState.value.locationHistory.size)
    }

    @Test
    fun defaultExpireDays_loadedFromConfig() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals("28", viewModel.uiState.value.defaultExpireDays)
    }

    @Test
    fun updateFormFields_reflectedInState() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateStalling("5")
        assertEquals("5", viewModel.uiState.value.formState.stalling)

        viewModel.updateRij("3")
        assertEquals("3", viewModel.uiState.value.formState.rij)

        viewModel.updateNummer("7")
        assertEquals("7", viewModel.uiState.value.formState.nummer)
    }

    @Test
    fun updateFormFields_emptyValue_hasRequiredError() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateStalling("")
        assertEquals("required", viewModel.uiState.value.formState.stallingError)
        assertFalse(viewModel.uiState.value.formState.isValid)

        viewModel.updateRij("")
        assertEquals("required", viewModel.uiState.value.formState.rijError)

        viewModel.updateNummer("")
        assertEquals("required", viewModel.uiState.value.formState.nummerError)
    }

    @Test
    fun updateFormFields_tooLongValue_hasTooLongError() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val longValue = "a".repeat(26)
        viewModel.updateStalling(longValue)
        assertEquals("too_long", viewModel.uiState.value.formState.stallingError)
        assertFalse(viewModel.uiState.value.formState.isValid)

        viewModel.updateRij(longValue)
        assertEquals("too_long", viewModel.uiState.value.formState.rijError)

        viewModel.updateNummer(longValue)
        assertEquals("too_long", viewModel.uiState.value.formState.nummerError)
    }

    @Test
    fun updateFormFields_validValue_noError() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateStalling("valid")
        assertNull(viewModel.uiState.value.formState.stallingError)

        viewModel.updateRij("valid")
        assertNull(viewModel.uiState.value.formState.rijError)

        viewModel.updateNummer("valid")
        assertNull(viewModel.uiState.value.formState.nummerError)

        assertTrue(viewModel.uiState.value.formState.isValid)
    }

    @Test
    fun updateFormFields_maxLengthValue_noError() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val maxLengthValue = "a".repeat(25)
        viewModel.updateStalling(maxLengthValue)
        assertNull(viewModel.uiState.value.formState.stallingError)
        assertTrue(viewModel.uiState.value.formState.isValid)
    }

    @Test
    fun startAddingLocation_setsFlag() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isAddingLocation)
        viewModel.startAddingLocation()
        assertTrue(viewModel.uiState.value.isAddingLocation)
    }

    @Test
    fun stopAddingLocation_clearsFlag() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.startAddingLocation()
        assertTrue(viewModel.uiState.value.isAddingLocation)

        viewModel.stopAddingLocation()
        assertFalse(viewModel.uiState.value.isAddingLocation)
    }

    @Test
    fun setManualLocation_addsLocationAndResetsForm() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateStalling("2")
        viewModel.updateRij("3")
        viewModel.updateNummer("4")
        viewModel.setManualLocation()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.currentLocation)
        assertEquals("2-3-4", state.currentLocation?.location)
        assertFalse(state.isAddingLocation)
        // Form should be reset
        assertEquals("1", state.formState.stalling)
        assertEquals("1", state.formState.rij)
        assertEquals("1", state.formState.nummer)
    }

    @Test
    fun configureDefaultExpireDays_updatesConfig() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.configureDefaultExpireDays("30")
        advanceUntilIdle()

        assertEquals("30", configRepository.getConfigValue("default_expire_days"))
    }

    @Test
    fun expiredLocation_setsExpiredFlag() = runTest(testDispatcher) {
        val expiredLocation = BikeLocation(
            startDate = Clock.System.now() - 30.days,
            expiredDate = Clock.System.now() - 1.days,
            location = "1-1-1"
        )
        bikeLocationRepository.setLocations(listOf(expiredLocation))

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.expired)
    }
}
