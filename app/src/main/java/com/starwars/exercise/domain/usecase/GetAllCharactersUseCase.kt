package com.starwars.exercise.domain.usecase

import com.starwars.exercise.data.core.Resource
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.repository.StarWarsRepository
import javax.inject.Inject

class GetAllCharactersUseCase @Inject constructor(
    private val repository: StarWarsRepository
) {
    suspend operator fun invoke(): Resource<List<Person>> =
        repository.getAllCharacters()
}