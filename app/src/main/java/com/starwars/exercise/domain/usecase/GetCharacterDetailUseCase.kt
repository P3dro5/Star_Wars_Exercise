package com.starwars.exercise.domain.usecase

import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.repository.StarWarsRepository
import javax.inject.Inject

class GetCharacterDetailUseCase @Inject constructor(
    private val repository: StarWarsRepository
) {
    suspend operator fun invoke(personId: Int): Resource<Person> {
        return repository.getCharacterDetail(personId)
    }
}
