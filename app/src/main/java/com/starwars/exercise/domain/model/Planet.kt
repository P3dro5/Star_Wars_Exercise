package com.starwars.exercise.domain.model

data class Planet(
    val id: Int,
    val name: String,
    val climate: String,
    val terrain: String,
    val population: String,
    val gravity: String,
    val diameter: String,
    val orbitalPeriod: String,
    val rotationPeriod: String,
    val residentIds: List<Int> = emptyList(),
    val galaxyPosition: GalaxyPosition = GalaxyPosition(-1000f, -1000f)
)