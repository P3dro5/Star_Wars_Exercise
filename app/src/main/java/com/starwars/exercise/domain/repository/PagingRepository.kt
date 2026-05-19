package com.starwars.exercise.domain.repository

import androidx.paging.PagingData
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.model.SortField
import com.starwars.exercise.domain.model.SortOrder
import kotlinx.coroutines.flow.Flow

interface PagingRepository {
    fun getPagingCharacters(
        searchQuery: String? = null,
        filteredIds: List<Int>? = null,
        selectedGenders: Set<String>? = null,
        sortField: SortField = SortField.NONE,
        sortOrder: SortOrder = SortOrder.ASCENDING,
        firstAppearanceMap: Map<Int, Int> = emptyMap()
    ): Flow<PagingData<Person>>
}