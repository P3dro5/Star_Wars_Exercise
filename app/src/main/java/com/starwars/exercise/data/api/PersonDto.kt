package com.starwars.exercise.data.api

import com.squareup.moshi.Json

data class PersonDto(
    val name: String,
    @Json(name = "birth_year") val birthYear: String,
    val gender: String,
    val homeworld: String,
    val species: List<String>,
    val height: String,
    val mass: String,
    @Json(name = "hair_color") val hairColor: String,
    @Json(name = "skin_color") val skinColor: String,
    @Json(name = "eye_color") val eyeColor: String,
    val films: List<String>,
    val starships: List<String>,
    val url: String
)
