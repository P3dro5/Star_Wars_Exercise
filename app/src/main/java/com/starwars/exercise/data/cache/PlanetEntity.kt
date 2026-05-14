package com.starwars.exercise.data.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planets")
data class PlanetEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val climate: String,
    val terrain: String,
    val population: String,
    val gravity: String,
    val diameter: String,
    val orbitalPeriod: String,
    val rotationPeriod: String
)
