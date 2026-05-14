package com.starwars.exercise.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.usecase.GetCharacterDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    fun loadDetail(personId: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            when (val resource = getCharacterDetailUseCase(personId)) {
                is Resource.Loading -> _uiState.value = DetailUiState.Loading
                is Resource.Success -> _uiState.value = DetailUiState.Success(resource.data)
                is Resource.Error -> _uiState.value = DetailUiState.Error(resource.message ?: "Unable to load detail")
            }
        }
    }
}
