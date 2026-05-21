package com.starwars.exercise.data.api.dto

data class SpeciesResponseDto(
    val results: List<SpeciesDto>,
    val next: String?
)

data class SpeciesDto(
    val name: String = "Unknown",
    val next: String? = null,
    val people: List<String> = emptyList()
)