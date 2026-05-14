package com.starwars.exercise.domain.usecase

import androidx.paging.PagingData
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.repository.PagingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharacterPagingUseCase @Inject constructor(private val repository: PagingRepository) {

    operator fun invoke(): Flow<PagingData<Person>> {
        return repository.getPagingCharacters()
    }
}