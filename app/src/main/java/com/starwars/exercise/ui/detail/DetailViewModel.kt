package com.starwars.exercise.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starwars.exercise.core.Resource
import com.starwars.exercise.data.api.StarWarsApi
import com.starwars.exercise.domain.usecase.GetCharacterDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase,
    private val api: StarWarsApi
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
                    Log.d("DetailViewModel", "birthYear=${person.birthYear}, homeworld=${person.homeworld}, species=${person.species}")

                    // resolve homeworld name from URL
                    val homeworldName = resolveHomeworldName(person.homeworld)

                    // resolve species name from URL
                    val speciesName = resolveSpeciesName(person.species)

                    _uiState.value = DetailUiState.Success(
                        person.copy(
                            homeworld = homeworldName,
                            species = speciesName
                        )
                    )
                }
            }
        }
    }

    private suspend fun resolveHomeworldName(url: String): String {
        if (url.isBlank()) return "Unknown"
        return try {
            val id = url.trimEnd('/').split("/").lastOrNull()?.toIntOrNull()
                ?: return "Unknown"
            api.getPlanet(id).name
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private suspend fun resolveSpeciesName(url: String): String {
        if (url.isBlank()) return "Human" // SWAPI returns empty list for humans
        return try {
            val id = url.trimEnd('/').split("/").lastOrNull()?.toIntOrNull()
                ?: return "Unknown"
            api.getSpeciesById(id).name
        } catch (e: Exception) {
            "Unknown"
        }
    }
}