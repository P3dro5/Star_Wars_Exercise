package com.starwars.exercise.domain.usecase

import com.starwars.exercise.data.core.Resource
import com.starwars.exercise.domain.model.Planet
import com.starwars.exercise.domain.repository.StarWarsRepository
import javax.inject.Inject

class GetAllPlanetsUseCase @Inject constructor(
    private val repository: StarWarsRepository
) {
    suspend operator fun invoke(): Resource<List<Planet>> = repository.getAllPlanets()
}