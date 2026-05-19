package com.starwars.exercise.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.starwars.exercise.data.api.StarWarsApi
import com.starwars.exercise.data.api.StarWarsImageApi
import com.starwars.exercise.data.mapper.toDomain
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.model.SortField
import com.starwars.exercise.domain.model.SortOrder
import com.starwars.exercise.domain.repository.PagingRepository
import com.starwars.exercise.ui.paging.PostPagingSource
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

    private suspend fun getValidIds(): Set<Int> {
        Log.d("PagingRepository", "cachedValidIds is null: ${cachedValidIds == null}")
        return cachedValidIds ?: imageApi.getCharacterImage()
            .map { it.id.toIntOrNull() ?: 1 }
            .toSet()
            .also {
                Log.d("PagingRepository", "fetched validIds, count=${it.size}")
                cachedValidIds = it
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
        val validIds = getValidIds()  // cached, same set every time

        val resolvedFilteredIds: List<Int>? = if (sortField != SortField.NONE) {
            fetchAllAndSort(validIds, filteredIds, selectedGenders, sortField, sortOrder, firstAppearanceMap)
        } else {
            filteredIds
        }

        emitAll(
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 2,
                    initialLoadSize = 10
                ),
                pagingSourceFactory = {
                    PostPagingSource(
                        api = api,
                        validIds = validIds,
                        searchQuery = if (sortField != SortField.NONE) null else searchQuery,
                        filteredIds = resolvedFilteredIds,
                        selectedGenders = if (sortField != SortField.NONE) null else selectedGenders,
                        sortField = SortField.NONE,
                        sortOrder = sortOrder,
                        firstAppearanceMap = firstAppearanceMap
                    )
                }
            ).flow
        )
    }

    private suspend fun fetchAllAndSort(
        validIds: Set<Int>,
        filteredIds: List<Int>?,
        selectedGenders: Set<String>?,
        sortField: SortField,
        sortOrder: SortOrder,
        firstAppearanceMap: Map<Int, Int>
    ): List<Int> {
        val allCharacters = mutableListOf<Person>()

        if (!filteredIds.isNullOrEmpty()) {
            filteredIds.forEach { id ->
                try {
                    val person = api.getPerson(id).toDomain()
                    if (selectedGenders == null || person.gender in selectedGenders) {
                        allCharacters.add(person)
                    }
                } catch (e: Exception) { }
            }
        } else {
            var page = 1
            while (true) {
                val response = api.getPeople(page = page)
                val people = response.results
                    .map { it.toDomain() }
                    .filter { selectedGenders == null || it.gender in selectedGenders }
                // removed .filter { it.id in validIds }
                allCharacters.addAll(people)
                if (response.next == null) break
                page++
            }
        }
        Log.d("fetchAllAndSort", "final sorted count=${allCharacters.size}")

        return when (sortField) {
            SortField.NAME -> if (sortOrder == SortOrder.ASCENDING)
                allCharacters.sortedWith(compareBy({ it.name.lowercase() }, { it.id }))
            else
                allCharacters.sortedWith(
                    compareByDescending<Person> { it.name.lowercase() }.thenByDescending { it.id }
                )
            SortField.YEAR -> if (sortOrder == SortOrder.ASCENDING)
                allCharacters.sortedWith(compareBy(
                    { firstAppearanceMap.getOrDefault(it.id, Int.MAX_VALUE) },
                    { it.id }
                ))
            else
                allCharacters.sortedWith(
                    compareByDescending<Person> { firstAppearanceMap.getOrDefault(it.id, Int.MAX_VALUE) }
                        .thenByDescending { it.id }
                )
            SortField.NONE -> allCharacters
        }.map { it.id }
    }
}