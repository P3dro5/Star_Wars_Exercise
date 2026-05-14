package com.starwars.exercise.data.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanetDao {
    @Query("SELECT * FROM planets ORDER BY name ASC")
    fun getPlanets(): Flow<List<PlanetEntity>>

    @Query("SELECT * FROM planets WHERE id = :id")
    suspend fun getPlanet(id: Int): PlanetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanets(planets: List<PlanetEntity>)
}
