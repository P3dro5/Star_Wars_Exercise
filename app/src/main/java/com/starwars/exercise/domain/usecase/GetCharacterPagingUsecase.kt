package com.starwars.exercise.domain.usecase

import androidx.paging.PagingData
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.model.SortField
import com.starwars.exercise.domain.model.SortOrder
import com.starwars.exercise.domain.repository.PagingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharacterPagingUseCase @Inject constructor(
    private val repository: PagingRepository
) {
    operator fun invoke(
        searchQuery: String? = null,
        filteredIds: List<Int>? = null,
        selectedGenders: Set<String>? = null,
        sortField: SortField = SortField.NONE,
        sortOrder: SortOrder = SortOrder.ASCENDING,
        firstAppearanceMap: Map<Int, Int> = emptyMap()
    ): Flow<PagingData<Person>> = repository.getPagingCharacters(
        searchQuery, filteredIds, selectedGenders, sortField, sortOrder, firstAppearanceMap
    )
}