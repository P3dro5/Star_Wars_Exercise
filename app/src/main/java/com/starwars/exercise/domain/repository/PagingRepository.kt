package com.starwars.exercise.domain.repository

import androidx.paging.PagingData
import com.starwars.exercise.domain.model.Person
import kotlinx.coroutines.flow.Flow

interface PagingRepository {
    fun getPagingCharacters(): Flow<PagingData<Person>>
}