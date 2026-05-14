package com.starwars.exercise.ui.home

import androidx.paging.PagingData
import com.starwars.exercise.domain.model.Person
import kotlinx.coroutines.flow.Flow

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val characters: Flow<PagingData<Person>>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}