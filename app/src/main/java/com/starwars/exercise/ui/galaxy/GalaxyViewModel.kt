package com.starwars.exercise.ui.galaxy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starwars.exercise.data.core.Resource
import com.starwars.exercise.domain.model.Planet
import com.starwars.exercise.domain.usecase.GetAllPlanetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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

    // zoom commands as a channel so each emission is consumed once
    private val _zoomCommand = MutableSharedFlow<ZoomCommand>(extraBufferCapacity = 1)
    val zoomCommand: SharedFlow<ZoomCommand> = _zoomCommand

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

    fun onZoomIn() {
        viewModelScope.launch { _zoomCommand.emit(ZoomCommand.ZoomIn) }
    }

    fun onZoomOut() {
        viewModelScope.launch { _zoomCommand.emit(ZoomCommand.ZoomOut) }
    }

    fun onZoomReset() {
        viewModelScope.launch { _zoomCommand.emit(ZoomCommand.Reset) }
    }
}

sealed class ZoomCommand {
    data object ZoomIn : ZoomCommand()
    data object ZoomOut : ZoomCommand()
    data object Reset : ZoomCommand()
}