package com.starwars.exercise.data.api.dto

data class SpeciesResponseDto(
    val results: List<SpeciesDto>,
    val next: String?
)

data class SpeciesDto(
    val name: String,
    val people: List<String>  // list of URLs e.g. "https://swapi.info/api/people/1/"
)