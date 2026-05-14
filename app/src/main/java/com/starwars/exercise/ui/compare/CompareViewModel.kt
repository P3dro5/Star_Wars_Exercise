package com.starwars.exercise.ui.compare

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.usecase.CompareCharactersUseCase
import com.starwars.exercise.domain.usecase.GetCharactersImageUseCase
import com.starwars.exercise.domain.usecase.GetCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val getCharactersImageUseCase: GetCharactersImageUseCase,
    private val compareCharactersUseCase: CompareCharactersUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<CompareUiState>(CompareUiState.Loading)
    val uiState: StateFlow<CompareUiState> = _uiState

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        viewModelScope.launch {
            getCharactersImageUseCase().onStart {
                _uiState.value = CompareUiState.Loading
            }.onEach { imageState ->
                when (imageState) {
                    is Resource.Loading -> _uiState.value = CompareUiState.Loading
                    is Resource.Success -> {
                        _uiState.value = CompareUiState.Success(
                            listOf(
                                Person(
                                    id = 1,
                                    name = "Loading",
                                    image = "https://vignette.wikia.nocookie.net/starwars/images/2/20/LukeTLJ.jpg",
                                    species = "",
                                    homeworld = "",
                                    gender = "",
                                    birthYear = "",
                                    height = "0",
                                    mass = "",
                                    hairColor = "",
                                    skinColor = "",
                                    eyeColor = "",
                                    filmCount = 1,
                                    starshipIds = emptyList()
                                )
                            )
                        )
                    }

                    is Resource.Error -> {
                        Log.d(
                            "StarWarsRepositoryImpl", "" +
                                    "error character images"
                        )

                    }
                }

            }.launchIn(viewModelScope)
        }
    }

    suspend fun compareCharacters(firstId: Int, secondId: Int) = compareCharactersUseCase(firstId, secondId)
}
