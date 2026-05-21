package com.starwars.exercise.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.model.CharacterFilter
import com.starwars.exercise.domain.model.SearchResult
import com.starwars.exercise.domain.model.SortField
import com.starwars.exercise.domain.model.SortOrder
import com.starwars.exercise.domain.model.Species
import com.starwars.exercise.domain.usecase.GetCharacterFirstAppearanceUseCase
import com.starwars.exercise.domain.usecase.GetCharacterPagingUseCase
import com.starwars.exercise.domain.usecase.GetCharactersImageUseCase
import com.starwars.exercise.domain.usecase.GetSpeciesUseCase
import com.starwars.exercise.domain.usecase.SearchAllUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCharactersImageUseCase: GetCharactersImageUseCase,
    private val getCharacterPagingUseCase: GetCharacterPagingUseCase,
    private val getSpeciesUseCase: GetSpeciesUseCase,
    private val getCharacterFirstAppearanceUseCase: GetCharacterFirstAppearanceUseCase,
    private val searchAllUseCase: SearchAllUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchState: StateFlow<SearchUiState> = _searchState

    val isSearching: StateFlow<Boolean> = _searchQuery
        .map { it.isNotBlank() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _speciesUiState = MutableStateFlow<SpeciesUiState>(SpeciesUiState.Idle)
    val speciesUiState: StateFlow<SpeciesUiState> = _speciesUiState

    private val _filter = MutableStateFlow(CharacterFilter())
    val filter: StateFlow<CharacterFilter> = _filter

    private val _firstAppearanceMap = MutableStateFlow<Map<Int, Int>>(emptyMap())

    init {
        viewModelScope.launch {

            // debounce search by 400ms
            _searchQuery
                .debounce(400)
                .collect { query ->
                    if (query.isBlank()) {
                        _searchState.value = SearchUiState.Idle
                    } else {
                        performSearch(query)
                    }
                }
        }
        viewModelScope.launch {
            loadFilmData()
            loadCharacters()
        }
        loadSpecies()
    }

    private suspend fun performSearch(query: String) {
        _searchState.value = SearchUiState.Loading
        _searchState.value = when (val result = searchAllUseCase(query)) {
            is Resource.Success -> SearchUiState.Success(result.data)
            is Resource.Error -> SearchUiState.Error(result.message)
            else -> SearchUiState.Error("Unknown error")
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        // clear search state immediately when query is cleared
        if (query.isBlank()) _searchState.value = SearchUiState.Idle
    }

    private suspend fun loadFilmData() {
        when (val result = getCharacterFirstAppearanceUseCase()) {
            is Resource.Success -> _firstAppearanceMap.value = result.data
            is Resource.Error -> Log.e("HomeViewModel", "Failed to load films: ${result.message}")
            else -> {}
        }
    }

    fun loadCharacters() {
        val currentFilter = _filter.value
        val searchQuery = _searchQuery.value.ifBlank { null }
        val filteredIds = currentFilter.selectedSpecies
            .flatMap { it.peopleIds }.distinct().ifEmpty { null }
        val selectedGenders = currentFilter.selectedGenders.ifEmpty { null }

        getCharactersImageUseCase().combineTransform(
            getCharacterPagingUseCase(
                searchQuery = searchQuery,
                filteredIds = filteredIds,
                selectedGenders = selectedGenders,
                sortField = currentFilter.sortField,
                sortOrder = currentFilter.sortOrder,
                firstAppearanceMap = _firstAppearanceMap.value  // always up to date
            ).cachedIn(viewModelScope)
        ) { images, characters ->
            when (images) {
                is Resource.Loading -> _uiState.value = HomeUiState.Loading
                is Resource.Success -> {
                    val imageMap = images.data.associateBy { it.id }
                    val pagingValue = characters.map { post ->
                        // getValue crashes if id missing, getOrDefault returns empty string
                        post.copy(image = imageMap.getOrDefault(post.id.toString(), null)?.image ?: "")
                    }
                    _uiState.value = HomeUiState.Success(flowOf(pagingValue))
                }
                is Resource.Error -> emit(HomeUiState.Error(images.message))
            }
        }
            .onStart { _uiState.value = HomeUiState.Loading }
            .catch { _uiState.value = HomeUiState.Error(it.localizedMessage ?: "Unknown error") }
            .launchIn(viewModelScope)
    }

    fun onSpeciesToggled(species: Species) {
        _filter.update { current ->
            val updated = if (species in current.selectedSpecies)
                current.selectedSpecies - species
            else current.selectedSpecies + species
            current.copy(selectedSpecies = updated)
        }
    }

    fun onGenderToggled(gender: String) {
        _filter.update { current ->
            val updated = if (gender in current.selectedGenders)
                current.selectedGenders - gender
            else current.selectedGenders + gender
            current.copy(selectedGenders = updated)
        }
    }

    fun onSortChanged(field: SortField, order: SortOrder) {
        if (field == SortField.YEAR && _firstAppearanceMap.value.isEmpty()) {
            viewModelScope.launch {
                loadFilmData()
                _filter.update { it.copy(sortField = field, sortOrder = order) }
                loadCharacters()
            }
            return
        }
        _filter.update { it.copy(sortField = field, sortOrder = order) }
        loadCharacters()
    }

    fun applyFilters() = loadCharacters()

    fun clearFilters() {
        _filter.value = CharacterFilter()
        loadCharacters()
    }

    private fun loadSpecies() {
        viewModelScope.launch {
            _speciesUiState.value = SpeciesUiState.Loading
            _speciesUiState.value = when (val result = getSpeciesUseCase()) {
                is Resource.Success -> SpeciesUiState.Success(result.data)
                is Resource.Error -> SpeciesUiState.Error(result.message)
                else -> SpeciesUiState.Error("Unknown error")
            }
        }
    }
}

sealed class SpeciesUiState {
    data object Idle : SpeciesUiState()
    data object Loading : SpeciesUiState()
    data class Success(val species: List<Species>) : SpeciesUiState()
    data class Error(val message: String) : SpeciesUiState()
}

sealed class SearchUiState {
    data object Idle : SearchUiState()
    data object Loading : SearchUiState()
    data class Success(val results: List<SearchResult>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}
