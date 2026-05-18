package com.starwars.exercise.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.model.CharacterFilter
import com.starwars.exercise.domain.model.Species
import com.starwars.exercise.domain.usecase.GetCharacterPagingUseCase
import com.starwars.exercise.domain.usecase.GetCharactersImageUseCase
import com.starwars.exercise.domain.usecase.GetSpeciesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCharactersImageUseCase: GetCharactersImageUseCase,
    private val getCharacterPagingUseCase: GetCharacterPagingUseCase,
    private val getSpeciesUseCase: GetSpeciesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _speciesUiState = MutableStateFlow<SpeciesUiState>(SpeciesUiState.Idle)
    val speciesUiState: StateFlow<SpeciesUiState> = _speciesUiState

    private val _filter = MutableStateFlow(CharacterFilter())
    val filter: StateFlow<CharacterFilter> = _filter

    init {
        loadCharacters()
        loadSpecies()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        loadCharacters()
    }

    fun onSpeciesToggled(species: Species) {
        _filter.update { current ->
            val updated = if (species in current.selectedSpecies) {
                current.selectedSpecies - species
            } else {
                current.selectedSpecies + species
            }
            current.copy(selectedSpecies = updated)
        }
    }

    fun onGenderToggled(gender: String) {
        _filter.update { current ->
            val updated = if (gender in current.selectedGenders) {
                current.selectedGenders - gender
            } else {
                current.selectedGenders + gender
            }
            current.copy(selectedGenders = updated)
        }
    }

    fun applyFilters() {
        loadCharacters()
    }

    fun clearFilters(selectedSpecies: Species? = null, selectedGender: String = "") {
        if(selectedSpecies == null && selectedGender.isEmpty()) {
            _filter.value = CharacterFilter()
            loadCharacters()
        }
        else {
            _filter.value = _filter.value.copy(
                selectedSpecies = _filter.value.selectedSpecies.filter { it != selectedSpecies }.toSet(),
                selectedGenders = _filter.value.selectedGenders.filter { it != selectedGender }.toSet()
            )
            loadCharacters()
        }
    }

    fun loadCharacters() {
        val currentFilter = _filter.value
        val searchQuery = _searchQuery.value.ifBlank { null }

        // merge all people IDs from selected species
        val filteredIds = if (currentFilter.selectedSpecies.isNotEmpty()) {
            currentFilter.selectedSpecies.flatMap { it.peopleIds }.distinct()
        } else null

        val selectedGenders = currentFilter.selectedGenders.ifEmpty { null }

        getCharactersImageUseCase().combineTransform(
            getCharacterPagingUseCase(searchQuery, filteredIds, selectedGenders)
                .cachedIn(viewModelScope)
        ) { images, characters ->
            when (images) {
                is Resource.Loading -> _uiState.value = HomeUiState.Loading
                is Resource.Success -> {
                    val imageMap = images.data.associateBy { it.id }
                    val pagingValue = characters.map { post ->
                        post.copy(image = imageMap.getValue(post.id.toString()).image)
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
