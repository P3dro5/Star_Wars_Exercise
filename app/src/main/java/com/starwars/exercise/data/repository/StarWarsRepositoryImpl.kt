package com.starwars.exercise.data.repository

import com.starwars.exercise.data.core.Resource
import com.starwars.exercise.data.api.StarWarsApi
import com.starwars.exercise.data.api.StarWarsImageApi
import com.starwars.exercise.data.mapper.toCharacterAppearances
import com.starwars.exercise.data.mapper.toDomain
import com.starwars.exercise.data.mapper.toPersonImage
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.model.PersonImage
import com.starwars.exercise.domain.model.Planet
import com.starwars.exercise.domain.model.SearchResult
import com.starwars.exercise.domain.model.Species
import com.starwars.exercise.domain.model.Starship
import com.starwars.exercise.domain.repository.StarWarsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StarWarsRepositoryImpl @Inject constructor(
    private val api: StarWarsApi,
    private val imageApi: StarWarsImageApi,
) : StarWarsRepository {

    override fun getCharacters(searchQuery: String?): Flow<Resource<List<Person>>> = flow {
        emit(Resource.Loading)
        try {
            val all = api.getPeople()
            val filtered = if (searchQuery.isNullOrBlank()) all
            else all.filter { it.name.contains(searchQuery, ignoreCase = true) }
            emit(Resource.Success(filtered.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Unable to load characters"))
        }
    }.catch { emit(Resource.Error(it.localizedMessage ?: "Unable to load characters")) }

    override suspend fun getCharacterDetail(personId: Int): Resource<Person> {
        return try {
            Resource.Success(api.getPerson(personId).toDomain())
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unable to load character details")
        }
    }

    override suspend fun getAllCharacters(): Resource<List<Person>> {
        return try {
            Resource.Success(api.getPeople().map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to load characters")
        }
    }

    override fun getAllCharacterImages(): Flow<Resource<List<PersonImage>>> = flow {
        emit(Resource.Loading)
        val response = imageApi.getCharacterImage()
        emit(Resource.Success(response.map { it.toPersonImage() }))
    }.catch { emit(Resource.Error(it.localizedMessage ?: "Unable to load images")) }

    override suspend fun getCharacterImages(): Resource<List<PersonImage>> {
        return try {
            Resource.Success(imageApi.getCharacterImage().map { it.toPersonImage() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unable to load images")
        }
    }

    override fun getShips(searchQuery: String?): Flow<Resource<List<Starship>>> = flow {
        emit(Resource.Loading)
        val all = api.getStarships()
        val filtered = if (searchQuery.isNullOrBlank()) all
        else all.filter { it.name.contains(searchQuery, ignoreCase = true) }
        emit(Resource.Success(filtered.map { it.toDomain() }))
    }.catch { emit(Resource.Error(it.localizedMessage ?: "Unable to load starships")) }

    override suspend fun getShipDetail(starshipId: Int): Resource<Starship> {
        return try {
            Resource.Success(api.getStarship(starshipId).toDomain())
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unable to load starship details")
        }
    }

    override suspend fun getAllPlanets(): Resource<List<Planet>> {
        return try {
            Resource.Success(api.getPlanets().map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to load planets")
        }
    }

    override suspend fun getPlanet(planetId: Int): Resource<Planet> {
        return try {
            Resource.Success(api.getPlanet(planetId).toDomain())
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to load planet")
        }
    }

    override suspend fun getAllSpecies(): Resource<List<Species>> {
        return try {
            Resource.Success(api.getSpecies().map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to load species")
        }
    }

    override suspend fun getCharacterFirstAppearanceYears(): Resource<Map<Int, Int>> {
        return try {
            val films = api.getFilms()
            val appearanceMap = mutableMapOf<Int, Int>()
            films.forEach { film ->
                film.toCharacterAppearances().forEach { (characterId, year) ->
                    appearanceMap[characterId] = minOf(
                        appearanceMap.getOrDefault(characterId, Int.MAX_VALUE),
                        year
                    )
                }
            }
            Resource.Success(appearanceMap)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to load films")
        }
    }

    override suspend fun resolveHomeworldName(url: String): String {
        if (url.isBlank()) return "Unknown"
        return try {
            val id = url.removeSuffix(".json").trimEnd('/').split("/").lastOrNull()?.toIntOrNull()
                ?: return "Unknown"
            api.getPlanet(id).name
        } catch (e: Exception) { "Unknown" }
    }

    override suspend fun resolveSpeciesName(url: String): String {
        if (url.isBlank()) return "Human"
        return try {
            val id = url.removeSuffix(".json").trimEnd('/').split("/").lastOrNull()?.toIntOrNull()
                ?: return "Unknown"
            api.getSpeciesById(id).name
        } catch (e: Exception) { "Unknown" }
    }

    override suspend fun searchAll(query: String): Resource<List<SearchResult>> {
        return try {
            val people = try {
                api.getPeople()
                    .filter { it.name.contains(query, ignoreCase = true) }
                    .map { it.toDomain() }
            } catch (e: Exception) { emptyList() }

            val ships = try {
                api.getStarships()
                    .filter { it.name.contains(query, ignoreCase = true) }
                    .map { it.toDomain() }
            } catch (e: Exception) { emptyList() }

            val planets = try {
                api.getPlanets()
                    .filter { it.name.contains(query, ignoreCase = true) }
                    .map { it.toDomain() }
            } catch (e: Exception) { emptyList() }

            val imageMap = try {
                imageApi.getCharacterImage().associateBy { it.id }
            } catch (e: Exception) { emptyMap() }

            val results = mutableListOf<SearchResult>()
            people.forEach { person ->
                results.add(SearchResult.CharacterResult(
                    person.copy(image = imageMap[person.id.toString()]?.image ?: "")
                ))
            }
            ships.forEach { results.add(SearchResult.StarshipResult(it)) }
            planets.forEach { results.add(SearchResult.PlanetResult(it)) }

            Resource.Success(results)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Search failed")
        }
    }
}