package com.starwars.exercise.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.starwars.exercise.domain.model.Person

class LocalListPagingSource(
    private val items: List<Person>
) : PagingSource<Int, Person>() {

    override fun getRefreshKey(state: PagingState<Int, Person>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Person> {
        val page = params.key ?: 0
        val start = page * params.loadSize
        if (start >= items.size) {
            return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
        }
        val end = minOf(start + params.loadSize, items.size)
        return LoadResult.Page(
            data = items.subList(start, end),
            prevKey = if (page == 0) null else page - 1,
            nextKey = if (end >= items.size) null else page + 1
        )
    }
}