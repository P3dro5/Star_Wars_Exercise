package com.starwars.exercise.ui.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.usecase.CompareCharactersUseCase
import com.starwars.exercise.domain.usecase.GetCharacterPagingUseCase
import com.starwars.exercise.domain.usecase.GetCharactersImageUseCase
import com.starwars.exercise.ui.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val getCharactersImageUseCase: GetCharactersImageUseCase,
    private val getCharacterPagingUseCase: GetCharacterPagingUseCase,
    private val compareCharactersUseCase: CompareCharactersUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CompareCharactersUiState>(CompareCharactersUiState.Loading)
    val uiState: StateFlow<CompareCharactersUiState> = _uiState

    // separate search queries for each selector
    private val _searchQueryFirst = MutableStateFlow("")
    val searchQueryFirst: StateFlow<String> = _searchQueryFirst

    private val _searchQuerySecond = MutableStateFlow("")
    val searchQuerySecond: StateFlow<String> = _searchQuerySecond

    // full character list cached once
    private var allCharacters: List<Person> = emptyList()

    private val _filteredFirst = MutableStateFlow<List<Person>>(emptyList())
    val filteredFirst: StateFlow<List<Person>> = _filteredFirst

    private val _filteredSecond = MutableStateFlow<List<Person>>(emptyList())
    val filteredSecond: StateFlow<List<Person>> = _filteredSecond

    private val _selectedFirst = MutableStateFlow<Person?>(null)
    val selectedFirst: StateFlow<Person?> = _selectedFirst

    private val _selectedSecond = MutableStateFlow<Person?>(null)
    val selectedSecond: StateFlow<Person?> = _selectedSecond

    init {
        loadCharacters()
        // filter lists reactively as search queries change
        viewModelScope.launch {
            _searchQueryFirst.collect { query ->
                _filteredFirst.value = if (query.isBlank()) emptyList()
                else allCharacters.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            }
        }
        viewModelScope.launch {
            _searchQuerySecond.collect { query ->
                _filteredSecond.value = if (query.isBlank()) emptyList()
                else allCharacters.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            }
        }
    }

    fun loadCharacters() {
            getCharactersImageUseCase().combineTransform(
                getCharacterPagingUseCase().cachedIn(viewModelScope)
            ) { images, characters ->
                when (images) {
                    is Resource.Loading -> _uiState.value = CompareCharactersUiState.Loading
                    is Resource.Success -> {
                        val imageMap = images.data.associateBy { it.id }
                        val pagingValue = characters.map { post ->
                            // getValue crashes if id missing, getOrDefault returns empty string
                            post.copy(image = imageMap.getOrDefault(post.id.toString(), null)?.image ?: "")
                        }
                        _uiState.value = CompareCharactersUiState.Success(flowOf(pagingValue))
                    }
                    is Resource.Error -> emit(HomeUiState.Error(images.message))
                }
            }
                .onStart { _uiState.value = CompareCharactersUiState.Loading }
                .catch { _uiState.value = CompareCharactersUiState.Error(it.localizedMessage ?: "Unknown error") }
                .launchIn(viewModelScope)
    }

    fun onSearchFirstChanged(query: String) { _searchQueryFirst.value = query }
    fun onSearchSecondChanged(query: String) { _searchQuerySecond.value = query }

    fun onFirstSelected(person: Person) {
        _selectedFirst.value = person
        _searchQueryFirst.value = person.name
        _filteredFirst.value = emptyList()
    }

    fun onSecondSelected(person: Person) {
        _selectedSecond.value = person
        _searchQuerySecond.value = person.name
        _filteredSecond.value = emptyList()
    }

    fun clearFirst() {
        _selectedFirst.value = null
        _searchQueryFirst.value = ""
    }

    fun clearSecond() {
        _selectedSecond.value = null
        _searchQuerySecond.value = ""
    }

    suspend fun compareCharacters(firstId: Int, secondId: Int) =
        compareCharactersUseCase(firstId, secondId)
}

sealed class CompareCharactersUiState {
    data object Loading : CompareCharactersUiState()
    data class Success(val characters: Flow<PagingData<Person>>) : CompareCharactersUiState()
    data class Error(val message: String) : CompareCharactersUiState()
}