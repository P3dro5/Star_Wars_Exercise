package com.starwars.exercise.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.cachedIn
import androidx.paging.map
import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.usecase.GetCharacterPagingUseCase
import com.starwars.exercise.domain.usecase.GetCharactersImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCharactersImageUseCase: GetCharactersImageUseCase,
    private val getCharacterPagingUseCase: GetCharacterPagingUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadCharacters()
    }

    fun loadCharacters(searchQuery: String? = null) {
                getCharactersImageUseCase().combineTransform(
                    getCharacterPagingUseCase().cachedIn(viewModelScope)
                ) { images, characters ->
                    when (images) {
                        is Resource.Loading -> _uiState.value = HomeUiState.Loading
                        is Resource.Success -> {
                            Log.d("StarWarsRepositoryImpl", "Success character images")
                            val imageMap = images.data.associateBy { it.id }

                            val pagingValue = characters.map { post ->
                                post.copy(image = imageMap.getValue(post.id.toString()).image)
                            }

                            _uiState.value = HomeUiState.Success(flowOf(pagingValue))
                        }

                        is Resource.Error -> {
                            Log.i("HomeViewModel", "Error loading images: ${images.message}")
                            emit(HomeUiState.Error(images.message))
                        }
                    }
                }
                    .onStart { _uiState.value = HomeUiState.Loading }
                    .catch { _uiState.value = HomeUiState.Error(it.localizedMessage ?: "Unknown error") }
                    .launchIn(viewModelScope)
        }
}

