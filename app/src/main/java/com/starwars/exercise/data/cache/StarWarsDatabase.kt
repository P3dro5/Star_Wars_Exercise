package com.starwars.exercise.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PersonEntity::class, StarshipEntity::class, PlanetEntity::class],
    version = 2
)
abstract class StarWarsDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao
    abstract fun starshipDao(): StarshipDao
    abstract fun planetDao(): PlanetDao
}
