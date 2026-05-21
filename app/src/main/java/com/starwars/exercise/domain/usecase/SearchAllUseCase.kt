package com.starwars.exercise.domain.usecase

import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.model.SearchResult
import com.starwars.exercise.domain.repository.StarWarsRepository
import javax.inject.Inject

class SearchAllUseCase @Inject constructor(
    private val repository: StarWarsRepository
) {
    suspend operator fun invoke(query: String): Resource<List<SearchResult>> =
        repository.searchAll(query)
}