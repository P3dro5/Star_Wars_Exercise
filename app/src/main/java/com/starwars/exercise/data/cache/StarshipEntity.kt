package com.starwars.exercise.data.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "starships")
data class StarshipEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val model: String,
    val manufacturer: String,
    val starshipClass: String,
    val crew: String,
    val passengers: String,
    val costInCredits: String,
    val length: String
)
