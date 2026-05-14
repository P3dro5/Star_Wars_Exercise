package com.starwars.exercise.di


import com.starwars.exercise.data.repository.PagingRepositoryImpl
import com.starwars.exercise.data.repository.StarWarsRepositoryImpl
import com.starwars.exercise.domain.repository.PagingRepository
import com.starwars.exercise.domain.repository.StarWarsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindStarWarsRepository(
        implementation: StarWarsRepositoryImpl
    ): StarWarsRepository

    @Binds
    @Singleton
    abstract fun bindPagingRepository(
        implementation: PagingRepositoryImpl
    ): PagingRepository
}
