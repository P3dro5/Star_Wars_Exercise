package com.starwars.exercise.data.api.dto

import com.squareup.moshi.Json

data class PlanetDto(
    val name: String = "unknown",
    val climate: String = "unknown",
    val terrain: String = "unknown",
    val population: String = "unknown",
    val gravity: String = "unknown",
    val diameter: String = "unknown",
    @Json(name = "orbital_period") val orbitalPeriod: String = "unknown",
    @Json(name = "rotation_period") val rotationPeriod: String = "unknown",
    val url: String = "",
    val residents: List<String> = emptyList()
)