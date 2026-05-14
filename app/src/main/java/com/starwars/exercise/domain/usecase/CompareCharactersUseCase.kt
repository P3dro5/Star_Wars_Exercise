package com.starwars.exercise.domain.usecase

import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.repository.StarWarsRepository

class CompareCharactersUseCase(
    private val repository: StarWarsRepository
) {
    suspend operator fun invoke(firstId: Int, secondId: Int): Resource<Pair<Person, Person>> {
        val first = repository.getCharacterDetail(firstId)
        val second = repository.getCharacterDetail(secondId)
        return when {
            first is Resource.Error -> Resource.Error(first.message)
            second is Resource.Error -> Resource.Error(second.message)
            first is Resource.Success && second is Resource.Success -> Resource.Success(first.data to second.data)
            else -> Resource.Error("Unable to compare characters")
        }
    }
}
