package com.starwars.exercise.domain.usecase

import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.repository.StarWarsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: StarWarsRepository
) {
    operator fun invoke(searchQuery: String? = null): Flow<Resource<List<Person>>> {
        return repository.getCharacters(searchQuery)
    }
}
