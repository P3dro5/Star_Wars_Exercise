package com.starwars.exercise.domain.usecase

import com.starwars.exercise.data.core.Resource
import com.starwars.exercise.domain.model.PersonImage
import com.starwars.exercise.domain.repository.StarWarsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersImageUseCase @Inject constructor(
    private val repository: StarWarsRepository
) {
    operator fun invoke(): Flow<Resource<List<PersonImage>>> {
        return repository.getAllCharacterImages()
    }
}
