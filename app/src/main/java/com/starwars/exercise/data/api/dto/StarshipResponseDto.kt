package com.starwars.exercise.data.api.dto

data class StarshipResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<StarshipDto>
)
