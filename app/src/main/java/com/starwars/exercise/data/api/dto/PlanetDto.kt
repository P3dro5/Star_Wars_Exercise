package com.starwars.exercise.data.api.dto

import com.squareup.moshi.Json

data class PlanetDto(
    val name: String,
    val climate: String,
    val terrain: String,
    val population: String,
    val gravity: String,
    val diameter: String,
    @field:Json(name = "orbital_period") val orbitalPeriod: String,
    @field:Json(name = "rotation_period") val rotationPeriod: String,
    val url: String
)
