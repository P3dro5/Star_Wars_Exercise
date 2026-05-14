package com.starwars.exercise.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.starwars.exercise.data.api.StarWarsApi
import com.starwars.exercise.data.mapper.toDomain
import com.starwars.exercise.domain.model.Person
import retrofit2.HttpException

class PostPagingSource(private val api: StarWarsApi, private val validIds: Set<Int>) : PagingSource<Int, Person>() {

    override fun getRefreshKey(state: PagingState<Int, Person>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Person> {
        val page = params.key ?: 1
        return try {
            val posts = api.getPeople(page = page)
            val results = posts.results.map { it.toDomain() }
            val filtered = results.filter { it.id in validIds }  // discard missing characters
            val previousPage = posts.previous?.toIntOrNull() ?: if(page == 1) null else page - 1
            val nextPage = posts.next?.toIntOrNull() ?: if (posts.results.isEmpty()) null else page + 1
            LoadResult.Page(
                data = filtered,
                prevKey = previousPage,
                nextKey = nextPage
            )
        } catch (e: HttpException) {
        if (e.code() == 404) {
            // treated 404 as end of pagination instead of an error
            LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null  // null nextKey = endOfPaginationReached
            )
        } else {
            LoadResult.Error(e)
        }
    } catch (e: Exception) {
        LoadResult.Error(e)
    }
    }

}