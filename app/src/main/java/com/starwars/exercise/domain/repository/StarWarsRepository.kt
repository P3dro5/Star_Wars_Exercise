package com.starwars.exercise.domain.repository

import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.model.PersonImage
import com.starwars.exercise.domain.model.Planet
import com.starwars.exercise.domain.model.SearchResult
import com.starwars.exercise.domain.model.Species
import com.starwars.exercise.domain.model.Starship
import kotlinx.coroutines.flow.Flow

interface StarWarsRepository {
    fun getCharacters(searchQuery: String? = null): Flow<Resource<List<Person>>>
    suspend fun getCharacterDetail(personId: Int): Resource<Person>
    fun getAllCharacterImages(): Flow<Resource<List<PersonImage>>>
    fun getShips(searchQuery: String? = null): Flow<Resource<List<Starship>>>
    suspend fun getShipDetail(starshipId: Int): Resource<Starship>
    suspend fun getPlanet(planetId: Int): Resource<Planet>
    suspend fun getAllPlanets(): Resource<List<Planet>>
    suspend fun getAllSpecies(): Resource<List<Species>>
    suspend fun getCharacterFirstAppearanceYears(): Resource<Map<Int, Int>>
    suspend fun searchAll(query: String): Resource<List<SearchResult>>
    suspend fun getAllCharacters(): Resource<List<Person>>
    suspend fun getCharacterImages(): Resource<List<PersonImage>>
    suspend fun resolveHomeworldName(url: String): String
    suspend fun resolveSpeciesName(url: String): String
}
