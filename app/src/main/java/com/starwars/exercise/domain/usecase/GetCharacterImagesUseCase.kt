package com.starwars.exercise.domain.usecase

import com.starwars.exercise.data.core.Resource
import com.starwars.exercise.domain.model.PersonImage
import com.starwars.exercise.domain.repository.StarWarsRepository
import javax.inject.Inject

class GetCharacterImagesUseCase @Inject constructor(
    private val repository: StarWarsRepository
) {
    suspend operator fun invoke(): Resource<List<PersonImage>> =
        repository.getCharacterImages()
}