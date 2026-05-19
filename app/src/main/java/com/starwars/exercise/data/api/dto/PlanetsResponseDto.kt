package com.starwars.exercise.data.api.dto

import com.squareup.moshi.Json

data class PlanetsResponseDto(
    val next: String?,
    val results: List<PlanetDto>
)