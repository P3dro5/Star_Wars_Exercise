package com.starwars.exercise.data.api.dto

import com.squareup.moshi.Json

data class PersonDto(
    val name: String,
    @Json(name = "birth_year") val birthYear: String? = null,
    val gender: String? = null,
    val homeworld: String? = null,
    val species: List<String> = emptyList(),
    val height: String? = null,
    val mass: String? = null,
    @Json(name = "hair_color") val hairColor: String? = null,
    @Json(name = "skin_color") val skinColor: String? = null,
    @Json(name = "eye_color") val eyeColor: String? = null,
    val films: List<String> = emptyList(),
    val starships: List<String> = emptyList(),
    val url: String? = null
)
