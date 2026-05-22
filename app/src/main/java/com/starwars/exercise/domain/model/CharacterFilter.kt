package com.starwars.exercise.domain.model

enum class SortOrder { ASCENDING, DESCENDING }

enum class SortField { NONE, NAME, YEAR }

val availableGenders = listOf("male", "female", "hermaphrodite", "none", "n/a")

data class CharacterFilter(
    val selectedSpecies: Set<Species> = emptySet(),
    val selectedGenders: Set<String> = emptySet(),
    val sortField: SortField = SortField.NONE,
    val sortOrder: SortOrder = SortOrder.ASCENDING
)