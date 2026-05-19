package com.starwars.exercise.ui.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.starwars.exercise.data.api.StarWarsApi
import com.starwars.exercise.data.mapper.toDomain
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.model.SortField
import com.starwars.exercise.domain.model.SortOrder
import retrofit2.HttpException

class PostPagingSource(
    private val api: StarWarsApi,
    private val validIds: Set<Int>,
    private val searchQuery: String? = null,
    private val filteredIds: List<Int>? = null,
    private val selectedGenders: Set<String>? = null,
    private val sortField: SortField = SortField.NONE,
    private val sortOrder: SortOrder = SortOrder.ASCENDING,
    private val firstAppearanceMap: Map<Int, Int> = emptyMap()  // characterId -> year
) : PagingSource<Int, Person>() {

    private fun List<Person>.applySorting(): List<Person> = when (sortField) {
        SortField.NAME -> if (sortOrder == SortOrder.ASCENDING) sortedBy { it.name }
        else sortedByDescending { it.name }
        SortField.YEAR -> if (sortOrder == SortOrder.ASCENDING) {
            sortedBy { firstAppearanceMap.getOrDefault(it.id, Int.MAX_VALUE) }
        } else {
            sortedByDescending { firstAppearanceMap.getOrDefault(it.id, Int.MAX_VALUE) }
        }
        SortField.NONE -> this
    }

    override fun getRefreshKey(state: PagingState<Int, Person>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Person> {
        val page = params.key ?: 1
        return try {
            if (!filteredIds.isNullOrEmpty()) {
                val start = (page - 1) * params.loadSize
                if (start >= filteredIds.size) {
                    return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
                }
                val end = minOf(start + params.loadSize, filteredIds.size)

                Log.d("PostPagingSource", "page=$page, start=$start, end=$end")
                Log.d("PostPagingSource", "IDs to fetch=${filteredIds.subList(start, end)}")

                val charactersById = filteredIds.subList(start, end)
                    .mapNotNull { id ->
                        try { api.getPerson(id).toDomain() } catch (e: Exception) { null }
                    }
                    .filter { selectedGenders == null || it.gender in selectedGenders }
                    .associateBy { it.id }

                val ordered = filteredIds.subList(start, end)
                    .mapNotNull { charactersById[it] }

                Log.d("PostPagingSource", "ordered names=${ordered.map { it.name }}")

                LoadResult.Page(
                    data = ordered,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (end >= filteredIds.size) null else page + 1
                )
            } else {
                val response = if (searchQuery.isNullOrBlank()) {
                    api.getPeople(page = page).results.map { it.toDomain() }
                } else {
                    api.getPeople(search = searchQuery, page = page).results.map { it.toDomain() }
                }
                val filtered = response
                    .filter { it.id in validIds }
                    .filter { selectedGenders == null || it.gender in selectedGenders }
                    .applySorting() // only sort here when no pre-sorted list exists

                LoadResult.Page(
                    data = filtered,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (response.isEmpty() || filtered.isEmpty()) null else page + 1
                )
            }
        } catch (e: HttpException) {
            if (e.code() == 404) LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
            else LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}