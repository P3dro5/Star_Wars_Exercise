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
import java.io.IOException
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
        return try {
            cachedValidIds ?: imageApi.getCharacterImage()
                .mapNotNull { it.id.toIntOrNull() }
                .toSet()
                .also { cachedValidIds = it }
        } catch (e: Exception) {
            emptySet()
        }
    }

    private suspend fun getAllPeople(): List<PersonDto> {
        return try {
            cachedAllPeople ?: api.getPeople().also {
                cachedAllPeople = it
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getPagingCharacters(
        searchQuery: String?,
        filteredIds: List<Int>?,
        selectedGenders: Set<String>?,
        sortField: SortField,
        sortOrder: SortOrder,
        firstAppearanceMap: Map<Int, Int>
    ): Flow<PagingData<Person>> = flow {

        try {
            val validIds = getValidIds()
            val allPeople = getAllPeople()

            var people = allPeople.map { it.toDomain() }

            if (!searchQuery.isNullOrBlank()) {
                people = people.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                }
            }

            if (!filteredIds.isNullOrEmpty()) {
                people = people.filter { it.id in filteredIds }
            }

            if (!selectedGenders.isNullOrEmpty()) {
                people = people.filter { it.gender in selectedGenders }
            }

            people = people.filter { it.id in validIds }

            // Apply sort
            people = when (sortField) {
                SortField.NAME -> if (sortOrder == SortOrder.ASCENDING)
                    people.sortedBy { it.name }
                else
                    people.sortedByDescending { it.name }
                SortField.YEAR -> if (sortOrder == SortOrder.ASCENDING)
                    people.sortedBy { firstAppearanceMap[it.id] ?: Int.MAX_VALUE }
                else
                    people.sortedBy { firstAppearanceMap[it.id] ?: Int.MAX_VALUE }.reversed()
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

        } catch (e: IOException) {
            emit(PagingData.empty())
        } catch (e: Exception) {
            emit(PagingData.empty())
        }
    }
}