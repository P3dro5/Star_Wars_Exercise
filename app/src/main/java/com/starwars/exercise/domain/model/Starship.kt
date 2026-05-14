package com.starwars.exercise.domain.model

data class Starship(
    val id: Int,
    val name: String,
    val model: String,
    val manufacturer: String,
    val starshipClass: String,
    val crew: String,
    val passengers: String,
    val costInCredits: String,
    val length: String
)
