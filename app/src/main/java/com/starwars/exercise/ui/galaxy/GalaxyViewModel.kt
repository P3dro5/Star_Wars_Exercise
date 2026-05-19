package com.starwars.exercise.ui.galaxy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.model.Planet
import com.starwars.exercise.domain.usecase.GetAllPlanetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class GalaxyUiState {
    data object Loading : GalaxyUiState()
    data class Success(val planets: List<Planet>) : GalaxyUiState()
    data class Error(val message: String) : GalaxyUiState()
}

@HiltViewModel
class GalaxyViewModel @Inject constructor(
    private val getAllPlanetsUseCase: GetAllPlanetsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<GalaxyUiState>(GalaxyUiState.Loading)
    val uiState: StateFlow<GalaxyUiState> = _uiState

    private val _selectedPlanet = MutableStateFlow<Planet?>(null)
    val selectedPlanet: StateFlow<Planet?> = _selectedPlanet

    init { loadPlanets() }

    fun loadPlanets() {
        viewModelScope.launch {
            _uiState.value = GalaxyUiState.Loading
            _uiState.value = when (val result = getAllPlanetsUseCase()) {
                is Resource.Success -> GalaxyUiState.Success(result.data)
                is Resource.Error -> GalaxyUiState.Error(result.message)
                else -> GalaxyUiState.Error("Unknown error")
            }
        }
    }

    fun onPlanetSelected(planet: Planet?) {
        _selectedPlanet.value = planet
    }
}