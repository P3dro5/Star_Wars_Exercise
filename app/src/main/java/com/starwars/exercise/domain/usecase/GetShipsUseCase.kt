package com.starwars.exercise.domain.usecase

import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.model.Starship
import com.starwars.exercise.domain.repository.StarWarsRepository
import kotlinx.coroutines.flow.Flow

class GetShipsUseCase(
    private val repository: StarWarsRepository
) {
    operator fun invoke(searchQuery: String? = null): Flow<Resource<List<Starship>>> {
        return repository.getShips(searchQuery)
    }
}
