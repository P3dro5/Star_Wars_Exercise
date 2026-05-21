package com.starwars.exercise.domain.model

import com.squareup.moshi.Json

data class FilmsResponseDto(
    val results: List<FilmDto>
)

data class FilmDto(
    @Json(name = "release_date") val releaseDate: String,
    val characters: List<String>
)

data class CharacterFirstAppearance(
    val characterId: Int,
    val year: Int  // earliest release year across all films
)