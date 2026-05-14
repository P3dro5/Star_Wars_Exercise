package com.starwars.exercise.ui.compare

import com.starwars.exercise.domain.model.Person

sealed interface CompareUiState {
    data object Idle: CompareUiState
    object Loading : CompareUiState
    data class Success(val characters: List<Person>) : CompareUiState
    data class Error(val message: String) : CompareUiState
}
