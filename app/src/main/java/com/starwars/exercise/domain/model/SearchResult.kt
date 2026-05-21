package com.starwars.exercise.domain.model

sealed class SearchResult {
    data class CharacterResult(val person: Person) : SearchResult()
    data class StarshipResult(val starship: Starship) : SearchResult()
    data class PlanetResult(val planet: Planet) : SearchResult()
}