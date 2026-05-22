package com.starwars.exercise

import androidx.paging.PagingData
import com.starwars.exercise.data.core.Resource
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.model.PersonImage
import com.starwars.exercise.domain.model.SortField
import com.starwars.exercise.domain.model.SortOrder
import com.starwars.exercise.domain.usecase.GetCharacterFirstAppearanceUseCase
import com.starwars.exercise.domain.usecase.GetCharacterPagingUseCase
import com.starwars.exercise.domain.usecase.GetCharactersImageUseCase
import com.starwars.exercise.domain.usecase.GetSpeciesUseCase
import com.starwars.exercise.domain.usecase.SearchAllUseCase
import com.starwars.exercise.ui.home.HomeUiState
import com.starwars.exercise.ui.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.every
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
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
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getCharactersImageUseCase: GetCharactersImageUseCase
    private lateinit var getCharacterPagingUseCase: GetCharacterPagingUseCase
    private lateinit var getSpeciesUseCase: GetSpeciesUseCase
    private lateinit var getCharacterFirstAppearanceUseCase: GetCharacterFirstAppearanceUseCase
    private lateinit var searchAllUseCase: SearchAllUseCase

    private lateinit var viewModel: HomeViewModel

    private val fakePerson = Person(
        id = 1,
        name = "Luke Skywalker",
        image = "",
        birthYear = "19BBY",
        gender = "male",
        homeworld = "Tatooine",
        species = "Human",
        height = "172",
        mass = "77",
        hairColor = "blond",
        skinColor = "fair",
        eyeColor = "blue",
        filmCount = 4,
        starshipIds = listOf(12)
    )

    private val fakeImage = PersonImage(id = "1", image = "https://img.example.com/luke.jpg")

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getCharactersImageUseCase = mockk()
        getCharacterPagingUseCase = mockk()
        getSpeciesUseCase = mockk()
        getCharacterFirstAppearanceUseCase = mockk()
        searchAllUseCase = mockk()

        // Default happy-path
        every { getCharactersImageUseCase() } returns flowOf(Resource.Success(listOf(fakeImage)))
        every { getCharacterPagingUseCase(any(), any(), any(), any(), any(), any()) } returns
                flowOf(PagingData.from(listOf(fakePerson)))
        coEvery { getCharacterFirstAppearanceUseCase() } returns Resource.Success(mapOf(1 to 4))
        coEvery { getSpeciesUseCase() } returns Resource.Success(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState is Loading`() {
        // Given / When – ViewModel created but coroutines not yet advanced
        viewModel = HomeViewModel(
            getCharactersImageUseCase,
            getCharacterPagingUseCase,
            getSpeciesUseCase,
            getCharacterFirstAppearanceUseCase,
            searchAllUseCase
        )

        // Then
        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `uiState becomes Success when character images load successfully`() = runTest {
        // Given – stubs already set in setUp()
        viewModel = HomeViewModel(
            getCharactersImageUseCase,
            getCharacterPagingUseCase,
            getSpeciesUseCase,
            getCharacterFirstAppearanceUseCase,
            searchAllUseCase
        )

        // When
        advanceUntilIdle()

        // Then
        assertTrue(
            "Expected Success but was ${viewModel.uiState.value}",
            viewModel.uiState.value is HomeUiState.Success
        )
    }

    @Test
    fun `uiState becomes Error when character images fail`() = runTest {
        // Given
        every { getCharactersImageUseCase() } returns flowOf(Resource.Error("Network failure"))

        viewModel = HomeViewModel(
            getCharactersImageUseCase,
            getCharacterPagingUseCase,
            getSpeciesUseCase,
            getCharacterFirstAppearanceUseCase,
            searchAllUseCase
        )

        // When
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Error)
        assertEquals("Network failure", (state as HomeUiState.Error).message)
    }

    @Test
    fun `onSearchQueryChanged updates searchQuery and sets isSearching`() = runTest {
        // Given
        coEvery { searchAllUseCase(any()) } returns Resource.Success(emptyList())
        viewModel = HomeViewModel(
            getCharactersImageUseCase,
            getCharacterPagingUseCase,
            getSpeciesUseCase,
            getCharacterFirstAppearanceUseCase,
            searchAllUseCase
        )
        // isSearching uses SharingStarted.WhileSubscribed so it must have an active
        // collector before the upstream _searchQuery changes, otherwise the
        // stateIn operator never wakes up and .value stays at its initial false.
        val isSearchingJob = viewModel.isSearching.launchIn(this)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChanged("Luke")
        advanceUntilIdle()

        // Then
        assertEquals("Luke", viewModel.searchQuery.value)
        assertTrue(viewModel.isSearching.value)

        isSearchingJob.cancel()
    }

    @Test
    fun `clearFilters resets filter to default`() = runTest {
        // Given
        viewModel = HomeViewModel(
            getCharactersImageUseCase,
            getCharacterPagingUseCase,
            getSpeciesUseCase,
            getCharacterFirstAppearanceUseCase,
            searchAllUseCase
        )
        advanceUntilIdle()

        viewModel.onGenderToggled("male")
        viewModel.onSortChanged(SortField.NAME, SortOrder.DESCENDING)
        advanceUntilIdle()

        // When
        viewModel.clearFilters()
        advanceUntilIdle()

        // Then
        val filter = viewModel.filter.value
        assertTrue(filter.selectedGenders.isEmpty())
        assertEquals(SortField.NONE, filter.sortField)
        assertEquals(SortOrder.ASCENDING, filter.sortOrder)
    }
}