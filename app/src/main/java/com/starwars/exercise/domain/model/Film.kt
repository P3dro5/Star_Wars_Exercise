package com.starwars.exercise.domain.model

import com.squareup.moshi.Json

data class FilmDto(
    @Json(name = "release_date") val releaseDate: String = "",
    val characters: List<String> = emptyList()
)

