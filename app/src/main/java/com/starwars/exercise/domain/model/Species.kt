package com.starwars.exercise.domain.model

data class Species(
    val name: String,
    val peopleIds: List<Int>  // extracted IDs from URLs
)