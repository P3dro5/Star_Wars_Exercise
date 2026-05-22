package com.starwars.exercise

import com.starwars.exercise.ui.compare.CompareViewModel
import androidx.paging.PagingData
import com.starwars.exercise.data.core.Resource
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.model.PersonImage
import com.starwars.exercise.domain.usecase.CompareCharactersUseCase
import com.starwars.exercise.domain.usecase.GetAllCharactersUseCase
import com.starwars.exercise.domain.usecase.GetCharacterImagesUseCase
import com.starwars.exercise.domain.usecase.GetCharacterPagingUseCase
import com.starwars.exercise.domain.usecase.GetCharactersImageUseCase
import com.starwars.exercise.ui.compare.PickerUiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CompareViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getCharactersImageUseCase: GetCharactersImageUseCase
    private lateinit var getCharacterPagingUseCase: GetCharacterPagingUseCase
    private lateinit var compareCharactersUseCase: CompareCharactersUseCase
    private lateinit var getAllCharactersUseCase: GetAllCharactersUseCase
    private lateinit var getCharacterImagesUseCase: GetCharacterImagesUseCase

    private lateinit var viewModel: CompareViewModel

    private val luke = Person(
        id = 1, name = "Luke Skywalker", image = "", birthYear = "19BBY",
        gender = "male", homeworld = "Tatooine", species = "Human",
        height = "172", mass = "77", hairColor = "blond",
        skinColor = "fair", eyeColor = "blue", filmCount = 4, starshipIds = listOf(12)
    )

    private val vader = Person(
        id = 4, name = "Darth Vader", image = "", birthYear = "41.9BBY",
        gender = "male", homeworld = "Tatooine", species = "Human",
        height = "202", mass = "136", hairColor = "none",
        skinColor = "white", eyeColor = "yellow", filmCount = 4, starshipIds = listOf(13)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getCharactersImageUseCase = mockk()
        getCharacterPagingUseCase = mockk()
        compareCharactersUseCase = mockk()
        getAllCharactersUseCase = mockk()
        getCharacterImagesUseCase = mockk()

        // Default stubs
        coEvery { getAllCharactersUseCase() } returns Resource.Success(listOf(luke, vader))
        coEvery { getCharacterImagesUseCase() } returns Resource.Success(emptyList())
        every { getCharactersImageUseCase() } returns flowOf(Resource.Success(emptyList()))
        every { getCharacterPagingUseCase(any(), any(), any(), any(), any(), any()) } returns
                flowOf(PagingData.empty())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial selectedFirst and selectedSecond are null`() {
        viewModel = CompareViewModel(
            getCharactersImageUseCase, getCharacterPagingUseCase,
            compareCharactersUseCase, getAllCharactersUseCase, getCharacterImagesUseCase
        )
        assertNull(viewModel.selectedFirst.value)
        assertNull(viewModel.selectedSecond.value)
    }

    @Test
    fun `onSlotTapped sets selectingSlot and clears searchQuery`() = runTest {
        viewModel = CompareViewModel(
            getCharactersImageUseCase, getCharacterPagingUseCase,
            compareCharactersUseCase, getAllCharactersUseCase, getCharacterImagesUseCase
        )
        advanceUntilIdle()

        // Pre-fill search query to verify it is cleared
        viewModel.onSearchQueryChanged("Darth")

        // When
        viewModel.onSlotTapped(1)

        // Then
        assertEquals(1, viewModel.selectingSlot.value)
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `onCharacterPicked assigns person to correct slot and clears selectingSlot`() = runTest {
        viewModel = CompareViewModel(
            getCharactersImageUseCase, getCharacterPagingUseCase,
            compareCharactersUseCase, getAllCharactersUseCase, getCharacterImagesUseCase
        )
        advanceUntilIdle()

        // When – pick for slot 1
        viewModel.onSlotTapped(1)
        viewModel.onCharacterPicked(luke)

        // When – pick for slot 2
        viewModel.onSlotTapped(2)
        viewModel.onCharacterPicked(vader)

        // Then
        assertEquals(luke, viewModel.selectedFirst.value)
        assertEquals(vader, viewModel.selectedSecond.value)
        assertNull(viewModel.selectingSlot.value)
    }

    @Test
    fun `pickerUiState becomes Success after characters load`() = runTest {
        // Given
        val fakeImage = PersonImage(id = "1", image = "https://example.com/luke.jpg")
        coEvery { getCharacterImagesUseCase() } returns Resource.Success(listOf(fakeImage))

        viewModel = CompareViewModel(
            getCharactersImageUseCase, getCharacterPagingUseCase,
            compareCharactersUseCase, getAllCharactersUseCase, getCharacterImagesUseCase
        )

        // When
        advanceUntilIdle()

        // Then
        assertEquals(PickerUiState.Success, viewModel.pickerUiState.value)
    }
}