package com.starwars.exercise.domain.model

data class CharacterFilter(
    val selectedSpecies: Set<Species> = emptySet(),
    val selectedGenders: Set<String> = emptySet()
) {
    val isActive: Boolean get() = selectedSpecies.isNotEmpty() || selectedGenders.isNotEmpty()
}

val availableGenders = listOf("male", "female", "hermaphrodite", "none", "n/a")