package com.starwars.exercise.data.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StarshipDao {
    @Query("SELECT * FROM starships ORDER BY name ASC")
    fun getStarships(): Flow<List<StarshipEntity>>

    @Query("SELECT * FROM starships WHERE id = :id")
    suspend fun getStarship(id: Int): StarshipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStarships(starships: List<StarshipEntity>)
}
