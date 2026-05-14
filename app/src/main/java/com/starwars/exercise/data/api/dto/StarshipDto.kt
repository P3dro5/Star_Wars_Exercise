package com.starwars.exercise.data.api.dto

import com.squareup.moshi.Json

data class StarshipDto(
    val name: String,
    val model: String,
    val manufacturer: String,
    @field:Json(name = "starship_class") val starshipClass: String,
    val crew: String,
    val passengers: String,
    @field:Json(name = "cost_in_credits") val costInCredits: String,
    val length: String,
    val url: String
)
