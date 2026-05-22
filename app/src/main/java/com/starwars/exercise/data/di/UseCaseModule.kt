package com.starwars.exercise.data.di

import com.starwars.exercise.domain.repository.StarWarsRepository
import com.starwars.exercise.domain.usecase.CompareCharactersUseCase
import com.starwars.exercise.domain.usecase.GetCharacterDetailUseCase
import com.starwars.exercise.domain.usecase.GetCharactersUseCase
import com.starwars.exercise.domain.usecase.GetPlanetDetailUseCase
import com.starwars.exercise.domain.usecase.GetShipDetailUseCase
import com.starwars.exercise.domain.usecase.GetShipsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetCharactersUseCase(repository: StarWarsRepository): GetCharactersUseCase {
        return GetCharactersUseCase(repository)
    }

    @Provides
    fun provideGetCharacterDetailUseCase(repository: StarWarsRepository): GetCharacterDetailUseCase {
        return GetCharacterDetailUseCase(repository)
    }

    @Provides
    fun provideGetShipsUseCase(repository: StarWarsRepository): GetShipsUseCase {
        return GetShipsUseCase(repository)
    }

    @Provides
    fun provideGetShipDetailUseCase(repository: StarWarsRepository): GetShipDetailUseCase {
        return GetShipDetailUseCase(repository)
    }

    @Provides
    fun provideGetPlanetDetailUseCase(repository: StarWarsRepository): GetPlanetDetailUseCase {
        return GetPlanetDetailUseCase(repository)
    }

    @Provides
    fun provideCompareCharactersUseCase(repository: StarWarsRepository): CompareCharactersUseCase {
        return CompareCharactersUseCase(repository)
    }
}
