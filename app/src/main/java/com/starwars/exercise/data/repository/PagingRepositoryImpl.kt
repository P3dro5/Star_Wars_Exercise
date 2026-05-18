package com.starwars.exercise.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.starwars.exercise.data.api.StarWarsApi
import com.starwars.exercise.data.api.StarWarsImageApi
import com.starwars.exercise.domain.model.Person
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

    override fun getPagingCharacters(
        searchQuery: String?,
        filteredIds: List<Int>?,
        selectedGenders: Set<String>?
    ): Flow<PagingData<Person>> = flow {
        val validIds = imageApi.getCharacterImage()
            .map { it.id.toIntOrNull() ?: 1 }
            .toSet()

        emitAll(
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 2,
                    initialLoadSize = 10
                ),
                pagingSourceFactory = {
                    PostPagingSource(api, validIds, searchQuery, filteredIds, selectedGenders)
                }
            ).flow
        )
    }
}