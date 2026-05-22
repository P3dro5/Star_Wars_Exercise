package com.starwars.exercise.domain.usecase

import com.starwars.exercise.data.core.Resource
import com.starwars.exercise.domain.model.Starship
import com.starwars.exercise.domain.repository.StarWarsRepository

class GetShipDetailUseCase(
    private val repository: StarWarsRepository
) {
    suspend operator fun invoke(starshipId: Int): Resource<Starship> {
        return repository.getShipDetail(starshipId)
    }
}
