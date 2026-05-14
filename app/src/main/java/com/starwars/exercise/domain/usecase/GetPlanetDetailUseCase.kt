package com.starwars.exercise.domain.usecase

import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.model.Planet
import com.starwars.exercise.domain.repository.StarWarsRepository

class GetPlanetDetailUseCase(
    private val repository: StarWarsRepository
) {
    suspend operator fun invoke(planetId: Int): Resource<Planet> {
        return repository.getPlanetDetail(planetId)
    }
}
