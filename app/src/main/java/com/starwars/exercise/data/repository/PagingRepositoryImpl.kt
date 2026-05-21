package com.starwars.exercise.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.starwars.exercise.data.api.StarWarsApi
import com.starwars.exercise.data.api.StarWarsImageApi
import com.starwars.exercise.data.api.dto.PersonDto
import com.starwars.exercise.data.mapper.toDomain
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.model.SortField
import com.starwars.exercise.domain.model.SortOrder
import com.starwars.exercise.domain.repository.PagingRepository
import com.starwars.exercise.ui.paging.LocalListPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PagingRepositoryImpl @Inject constructor(
    private val api: StarWarsApi,
    private val imageApi: StarWarsImageApi
) : PagingRepository {

    private var cachedValidIds: Set<Int>? = null
    private var cachedAllPeople: List<PersonDto>? = null

    private suspend fun getValidIds(): Set<Int> {
        return cachedValidIds ?: imageApi.getCharacterImage()
            .map { it.id.toIntOrNull() ?: 1 }
            .toSet()
            .also { cachedValidIds = it }
    }

    private suspend fun getAllPeople(): List<PersonDto> {
        return cachedAllPeople ?: api.getPeople().also { cachedAllPeople = it }
    }

    override fun getPagingCharacters(
        searchQuery: String?,
        filteredIds: List<Int>?,
        selectedGenders: Set<String>?,
        sortField: SortField,
        sortOrder: SortOrder,
        firstAppearanceMap: Map<Int, Int>
    ): Flow<PagingData<Person>> = flow {
        val validIds = getValidIds()
        val allPeople = getAllPeople()

        // apply search, filter, gender locally
        var people = allPeople.map { it.toDomain() }

        if (!searchQuery.isNullOrBlank()) {
            people = people.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
        if (!filteredIds.isNullOrEmpty()) {
            people = people.filter { it.id in filteredIds }
        }
        if (!selectedGenders.isNullOrEmpty()) {
            people = people.filter { it.gender in selectedGenders }
        }

        people = people.filter { it.id in validIds }

        // apply sorting
        people = when (sortField) {
            SortField.NAME -> if (sortOrder == SortOrder.ASCENDING)
                people.sortedWith(compareBy({ it.name.lowercase() }, { it.id }))
            else
                people.sortedWith(compareByDescending<Person> { it.name.lowercase() }.thenByDescending { it.id })
            SortField.YEAR -> if (sortOrder == SortOrder.ASCENDING)
                people.sortedWith(compareBy(
                    { firstAppearanceMap.getOrDefault(it.id, Int.MAX_VALUE) }, { it.id }
                ))
            else
                people.sortedWith(
                    compareByDescending<Person> { firstAppearanceMap.getOrDefault(it.id, Int.MAX_VALUE) }
                        .thenByDescending { it.id }
                )
            SortField.NONE -> people
        }

        emitAll(
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 2,
                    initialLoadSize = 10
                ),
                pagingSourceFactory = {
                    LocalListPagingSource(people)
                }
            ).flow
        )
    }
}