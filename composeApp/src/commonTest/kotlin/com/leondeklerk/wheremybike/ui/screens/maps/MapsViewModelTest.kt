package com.leondeklerk.wheremybike.ui.screens.maps

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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MapsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var configRepository: FakeConfigRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        configRepository = FakeConfigRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): MapsViewModel {
        return MapsViewModel(configRepository, testDispatcher)
    }

    @Test
    fun initialState_isLoading() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        assertTrue(viewModel.uiState.value.loading)
    }

    @Test
    fun afterLoaded_noSavedLocation_stateHasNullLatLon() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.loading)
        assertNull(state.lat)
        assertNull(state.lon)
        assertFalse(state.hasMarker)
    }

    @Test
    fun saveMapLocation_updatesStateReactively() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.saveMapLocation(52.0, 4.5)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(52.0, state.lat)
        assertEquals(4.5, state.lon)
        assertTrue(state.hasMarker)
    }

    @Test
    fun savedLocation_loadedOnInit() = runTest(testDispatcher) {
        configRepository.setConfig("maps_location", "51.9,4.47")

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(51.9, state.lat)
        assertEquals(4.47, state.lon)
        assertTrue(state.hasMarker)
    }

    @Test
    fun invalidSavedLocation_parsedAsNull() = runTest(testDispatcher) {
        configRepository.setConfig("maps_location", "invalid")

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.lat)
        assertNull(state.lon)
        assertFalse(state.hasMarker)
    }
}
