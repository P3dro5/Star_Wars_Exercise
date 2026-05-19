package com.starwars.exercise.domain.usecase

import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.repository.StarWarsRepository
import javax.inject.Inject

class GetCharacterFirstAppearanceUseCase @Inject constructor(
    private val repository: StarWarsRepository
) {
    suspend operator fun invoke(): Resource<Map<Int, Int>> =
        repository.getCharacterFirstAppearanceYears()
}