package com.starwars.exercise.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.starwars.exercise.data.api.StarWarsApi
import com.starwars.exercise.data.mapper.toDomain
import com.starwars.exercise.domain.model.Person
import retrofit2.HttpException

class PostPagingSource(
    private val api: StarWarsApi,
    private val validIds: Set<Int>,
    private val searchQuery: String? = null,
    private val filteredIds: List<Int>? = null,
    private val selectedGenders: Set<String>? = null
) : PagingSource<Int, Person>() {

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
                val pageIds = filteredIds.subList(start, end)

                val characters = pageIds
                    .filter { it in validIds }
                    .mapNotNull { id ->
                        try { api.getPerson(id).toDomain() } catch (e: Exception) { null }
                    }
                    .filter { person ->
                        selectedGenders == null || person.gender in selectedGenders
                    }

                LoadResult.Page(
                    data = characters,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (end >= filteredIds.size) null else page + 1
                )
            } else {
                val characters = if (searchQuery.isNullOrBlank()) {
                    api.getPeople(page = page).results.map { it.toDomain() }
                } else {
                    api.getPeople(search = searchQuery, page = page).results.map { it.toDomain() }
                }

                val filtered = characters
                    .filter { it.id in validIds }
                    .filter { person ->
                        selectedGenders == null || person.gender in selectedGenders
                    }

                LoadResult.Page(
                    data = filtered,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (characters.isEmpty() || filtered.isEmpty()) null else page + 1
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