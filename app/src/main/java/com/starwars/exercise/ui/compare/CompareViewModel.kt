package com.starwars.exercise.ui.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.starwars.exercise.data.core.Resource
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.model.PersonImage
import com.starwars.exercise.domain.usecase.CompareCharactersUseCase
import com.starwars.exercise.domain.usecase.GetAllCharactersUseCase
import com.starwars.exercise.domain.usecase.GetCharacterImagesUseCase
import com.starwars.exercise.domain.usecase.GetCharacterPagingUseCase
import com.starwars.exercise.domain.usecase.GetCharactersImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val getCharactersImageUseCase: GetCharactersImageUseCase,
    private val getCharacterPagingUseCase: GetCharacterPagingUseCase,
    private val compareCharactersUseCase: CompareCharactersUseCase,
    private val getAllCharactersUseCase: GetAllCharactersUseCase,
    private val getCharacterImagesUseCase: GetCharacterImagesUseCase,
) : ViewModel() {

    private val _selectedFirst = MutableStateFlow<Person?>(null)
    val selectedFirst: StateFlow<Person?> = _selectedFirst

    private val _selectedSecond = MutableStateFlow<Person?>(null)
    val selectedSecond: StateFlow<Person?> = _selectedSecond

    // which slot is currently being selected (1 or 2), null = not selecting
    private val _selectingSlot = MutableStateFlow<Int?>(null)
    val selectingSlot: StateFlow<Int?> = _selectingSlot

    // search query for character picker
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _allCharacters = MutableStateFlow<List<Person>>(emptyList())

    val filteredCharacters: StateFlow<List<Person>> = combine(
        _searchQuery, _allCharacters
    ) { query, characters ->
        if (query.isBlank()) characters
        else characters.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init { loadCharacters() }

    private fun loadCharacters() {
        viewModelScope.launch {
            // load flat list for picker using suspend use case — no flow abort
            when (val result = getAllCharactersUseCase()) {
                    is Resource.Success -> {
                        val imageMap: Map<String, PersonImage> = when (val images = getCharacterImagesUseCase()) {
                            is Resource.Success -> images.data.associateBy { it.id }
                            else -> emptyMap()
                        }
                        _allCharacters.value = result.data.map { person ->
                            person.copy(image = imageMap[person.id.toString()]?.image ?: "")
                        }.sortedBy { it.name }
                    }
                    else -> {}
            }


            // paging flow for uiState unchanged
            getCharactersImageUseCase()
                .combineTransform(
                    getCharacterPagingUseCase().cachedIn(viewModelScope)
                ) { images, characters ->
                    when (images) {
                        is Resource.Loading -> emit(CompareCharactersUiState.Loading)
                        is Resource.Success -> {
                            val imageMap: Map<String, PersonImage> = images.data.associateBy { it.id }
                            val pagingValue = characters.map { post ->
                                post.copy(
                                    image = imageMap.getOrDefault(post.id.toString(), null)?.image ?: ""
                                )
                            }
                            emit(CompareCharactersUiState.Success(flowOf(pagingValue)))
                        }
                        is Resource.Error -> emit(CompareCharactersUiState.Error(images.message))
                    }
                }
                .onStart { emit(CompareCharactersUiState.Loading) }
                .catch { error ->
                    val message = if(error is IOException) "Network error. Please check your internet connection and try again." else "An error as occured."
                    emit(CompareCharactersUiState.Error("An error has occurred."))
                }
                .launchIn(viewModelScope)
        }
    }

    fun onSlotTapped(slot: Int) {
        _selectingSlot.value = slot
        _searchQuery.value = ""
    }

    fun onPickerDismissed() {
        _selectingSlot.value = null
        _searchQuery.value = ""
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCharacterPicked(person: Person) {
        when (_selectingSlot.value) {
            1 -> _selectedFirst.value = person
            2 -> _selectedSecond.value = person
        }
        _selectingSlot.value = null
        _searchQuery.value = ""
    }

    fun clearFirst() { _selectedFirst.value = null }
    fun clearSecond() { _selectedSecond.value = null }

    suspend fun compareCharacters(firstId: Int, secondId: Int) =
        compareCharactersUseCase(firstId, secondId)
}

sealed class CompareCharactersUiState {
    data object Loading : CompareCharactersUiState()
    data class Success(val characters: Flow<PagingData<Person>>) : CompareCharactersUiState()
    data class Error(val message: String) : CompareCharactersUiState()
}