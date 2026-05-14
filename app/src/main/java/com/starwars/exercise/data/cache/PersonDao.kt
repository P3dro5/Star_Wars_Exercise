package com.starwars.exercise.data.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM persons ORDER BY name ASC")
    fun getPeople(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM persons WHERE id = :id")
    suspend fun getPerson(id: Int): PersonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeople(people: List<PersonEntity>)
}
