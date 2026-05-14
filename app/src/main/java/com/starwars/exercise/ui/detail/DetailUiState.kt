package com.starwars.exercise.ui.detail

import com.starwars.exercise.domain.model.Person

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val person: Person) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
