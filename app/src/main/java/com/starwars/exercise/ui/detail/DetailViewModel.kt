package com.starwars.exercise.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.repository.StarWarsRepository
import com.starwars.exercise.domain.usecase.GetCharacterDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase,
    private val repository: StarWarsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    fun loadDetail(personId: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            when (val resource = getCharacterDetailUseCase(personId)) {
                is Resource.Loading -> _uiState.value = DetailUiState.Loading
                is Resource.Error -> _uiState.value = DetailUiState.Error(
                    resource.message ?: "Unable to load detail"
                )
                is Resource.Success -> {
                    val person = resource.data
                    _uiState.value = DetailUiState.Success(
                        person.copy(
                            homeworld = repository.resolveHomeworldName(person.homeworld),
                            species = repository.resolveSpeciesName(person.species)
                        )
                    )
                }
            }
        }
    }
}