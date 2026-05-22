package com.starwars.exercise

import com.starwars.exercise.ui.detail.DetailViewModel
import com.starwars.exercise.data.core.Resource
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.repository.StarWarsRepository
import com.starwars.exercise.domain.usecase.GetCharacterDetailUseCase
import com.starwars.exercise.ui.detail.DetailUiState
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getCharacterDetailUseCase: GetCharacterDetailUseCase
    private lateinit var repository: StarWarsRepository
    private lateinit var viewModel: DetailViewModel

    private val fakePerson = Person(
        id = 4,
        name = "Darth Vader",
        image = "",
        birthYear = "41.9BBY",
        gender = "male",
        homeworld = "https://swapi.dev/api/planets/1/",
        species = "https://swapi.dev/api/species/1/",
        height = "202",
        mass = "136",
        hairColor = "none",
        skinColor = "white",
        eyeColor = "yellow",
        filmCount = 4,
        starshipIds = listOf(13)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getCharacterDetailUseCase = mockk()
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Test 1 ──────────────────────────────────────────────────────────────
    @Test
    fun `initial uiState is Loading`() {
        viewModel = DetailViewModel(getCharacterDetailUseCase, repository)
        assertEquals(DetailUiState.Loading, viewModel.uiState.value)
    }

    // ── Test 2 ──────────────────────────────────────────────────────────────
    @Test
    fun `loadDetail emits Success with resolved homeworld and species`() = runTest {
        // Given
        val resolvedHomeworld = "Tatooine"
        val resolvedSpecies = "Human"
        coEvery { getCharacterDetailUseCase(4) } returns Resource.Success(fakePerson)
        coEvery { repository.resolveHomeworldName(fakePerson.homeworld) } returns resolvedHomeworld
        coEvery { repository.resolveSpeciesName(fakePerson.species) } returns resolvedSpecies

        viewModel = DetailViewModel(getCharacterDetailUseCase, repository)

        // When
        viewModel.loadDetail(4)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is DetailUiState.Success)
        val person = (state as DetailUiState.Success).person
        assertEquals(resolvedHomeworld, person.homeworld)
        assertEquals(resolvedSpecies, person.species)
    }

    // ── Test 3 ──────────────────────────────────────────────────────────────
    @Test
    fun `loadDetail emits Error when use case returns error`() = runTest {
        // Given
        coEvery { getCharacterDetailUseCase(99) } returns Resource.Error("Not found")
        viewModel = DetailViewModel(getCharacterDetailUseCase, repository)

        // When
        viewModel.loadDetail(99)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is DetailUiState.Error)
        assertEquals("Not found", (state as DetailUiState.Error).message)
    }

    @Test
    fun `loadDetail resets state to Loading before fetching`() = runTest {
        // Given – simulate a slow response by capturing states
        val states = mutableListOf<DetailUiState>()
        coEvery { getCharacterDetailUseCase(4) } coAnswers {
            // Capture state before the coroutine resumes
            Resource.Success(fakePerson)
        }
        coEvery { repository.resolveHomeworldName(any()) } returns "Tatooine"
        coEvery { repository.resolveSpeciesName(any()) } returns "Human"
        viewModel = DetailViewModel(getCharacterDetailUseCase, repository)

        // When
        viewModel.loadDetail(4)
        // Immediately after the call (before advancing) state should be Loading
        states.add(viewModel.uiState.value)
        advanceUntilIdle()
        states.add(viewModel.uiState.value)

        // Then
        assertEquals(DetailUiState.Loading, states.first())
        assertTrue(states.last() is DetailUiState.Success)
    }

    @Test
    fun `loadDetail calls resolveHomeworldName and resolveSpeciesName exactly once`() = runTest {
        // Given
        coEvery { getCharacterDetailUseCase(4) } returns Resource.Success(fakePerson)
        coEvery { repository.resolveHomeworldName(fakePerson.homeworld) } returns "Tatooine"
        coEvery { repository.resolveSpeciesName(fakePerson.species) } returns "Human"
        viewModel = DetailViewModel(getCharacterDetailUseCase, repository)

        // When
        viewModel.loadDetail(4)
        advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { repository.resolveHomeworldName(fakePerson.homeworld) }
        coVerify(exactly = 1) { repository.resolveSpeciesName(fakePerson.species) }
    }
}