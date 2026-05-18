package com.starwars.exercise.domain.repository

import androidx.paging.PagingData
import com.starwars.exercise.domain.model.Person
import kotlinx.coroutines.flow.Flow

interface PagingRepository {
    fun getPagingCharacters(
        searchQuery: String? = null,
        filteredIds: List<Int>? = null,
        selectedGenders: Set<String>? = null
    ): Flow<PagingData<Person>>
}