package com.starwars.exercise.data.api.dto

import com.squareup.moshi.Json

data class PersonDto(
    val name: String,
    @field:Json(name = "birth_year") val birthYear: String? = null,
    val gender: String? = null,
    val homeworld: String? = null,
    val species: List<String> = emptyList(),
    val height: String? = null,
    val mass: String? = null,
    @field:Json(name = "hair_color") val hairColor: String? = null,
    @field:Json(name = "skin_color") val skinColor: String? = null,
    @field:Json(name = "eye_color") val eyeColor: String? = null,
    val films: List<String> = emptyList(),
    val starships: List<String> = emptyList(),
    val url: String? = null
)
