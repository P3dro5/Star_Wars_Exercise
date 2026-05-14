package com.starwars.exercise.di

import android.content.Context
import androidx.room.Room
import com.starwars.exercise.data.cache.StarWarsDatabase
import com.starwars.exercise.data.cache.PersonDao
import com.starwars.exercise.data.cache.StarshipDao
import com.starwars.exercise.data.cache.PlanetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StarWarsDatabase {
        return Room.databaseBuilder(
            context,
            StarWarsDatabase::class.java,
            "starwars_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun providePersonDao(database: StarWarsDatabase): PersonDao = database.personDao()

    @Provides
    fun provideStarshipDao(database: StarWarsDatabase): StarshipDao = database.starshipDao()

    @Provides
    fun providePlanetDao(database: StarWarsDatabase): PlanetDao = database.planetDao()
}
