package com.starwars.exercise.data.api.dto

data class SpeciesDto(
    val name: String = "Unknown",
    val people: List<String> = emptyList(),
    val url: String = ""
)