package com.starwars.exercise.domain.model

import com.squareup.moshi.Json

data class FilmsResponseDto(
    val results: List<FilmDto>
)

data class FilmDto(
    @Json(name = "release_date") val releaseDate: String,  // "1977-05-25"
    val characters: List<String>  // ["https://swapi.info/api/people/1/", ...]
)

data class CharacterFirstAppearance(
    val characterId: Int,
    val year: Int  // earliest release year across all films
)