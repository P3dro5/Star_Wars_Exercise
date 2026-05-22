package com.starwars.exercise

import com.starwars.exercise.data.core.Resource
import com.starwars.exercise.domain.model.GalaxyPosition
import com.starwars.exercise.domain.model.Planet
import com.starwars.exercise.domain.usecase.GetAllPlanetsUseCase
import com.starwars.exercise.ui.galaxy.GalaxyUiState
import com.starwars.exercise.ui.galaxy.GalaxyViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GalaxyViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getAllPlanetsUseCase: GetAllPlanetsUseCase
    private lateinit var viewModel: GalaxyViewModel

    private val tatooine = Planet(
        id = 1, name = "Tatooine", climate = "arid", terrain = "desert",
        population = "200000", gravity = "1 standard",
        diameter = "10465", orbitalPeriod = "304", rotationPeriod = "23",
        residentIds = listOf(1, 4),
        galaxyPosition = GalaxyPosition(100f, 200f)
    )

    private val alderaan = Planet(
        id = 2, name = "Alderaan", climate = "temperate", terrain = "grasslands",
        population = "2000000000", gravity = "1 standard",
        diameter = "12500", orbitalPeriod = "364", rotationPeriod = "24",
        residentIds = listOf(5),
        galaxyPosition = GalaxyPosition(-50f, 80f)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getAllPlanetsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState is Loading`() {
        // Stub returns a response that won't arrive until we advance
        coEvery { getAllPlanetsUseCase() } returns Resource.Success(listOf(tatooine))

        viewModel = GalaxyViewModel(getAllPlanetsUseCase)

        // State should still be Loading before coroutines run
        assertEquals(GalaxyUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `loadPlanets emits Success with planet list`() = runTest {
        // Given
        coEvery { getAllPlanetsUseCase() } returns Resource.Success(listOf(tatooine, alderaan))
        viewModel = GalaxyViewModel(getAllPlanetsUseCase)

        // When
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is GalaxyUiState.Success)
        val planets = (state as GalaxyUiState.Success).planets
        assertEquals(2, planets.size)
        assertEquals("Tatooine", planets.first().name)
    }

    @Test
    fun `loadPlanets emits Error when use case returns error`() = runTest {
        // Given
        coEvery { getAllPlanetsUseCase() } returns Resource.Error("Timeout")
        viewModel = GalaxyViewModel(getAllPlanetsUseCase)

        // When
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is GalaxyUiState.Error)
        assertEquals("Timeout", (state as GalaxyUiState.Error).message)
    }

    @Test
    fun `onPlanetSelected updates selectedPlanet state`() = runTest {
        // Given
        coEvery { getAllPlanetsUseCase() } returns Resource.Success(listOf(tatooine))
        viewModel = GalaxyViewModel(getAllPlanetsUseCase)
        advanceUntilIdle()

        // When
        viewModel.onPlanetSelected(tatooine)

        // Then
        assertEquals(tatooine, viewModel.selectedPlanet.value)

        // And clearing works
        viewModel.onPlanetSelected(null)
        assertNull(viewModel.selectedPlanet.value)
    }
}