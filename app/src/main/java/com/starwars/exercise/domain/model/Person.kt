package com.starwars.exercise.domain.model

data class Person(
    val id: Int,
    val name: String,
    val image: String,
    val birthYear: String,
    val gender: String,
    val homeworld: String,
    val species: String,
    val height: String,
    val mass: String,
    val hairColor: String,
    val skinColor: String,
    val eyeColor: String,
    val filmCount: Int,
    val starshipIds: List<Int>
)
