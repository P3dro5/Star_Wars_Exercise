package com.starwars.exercise.data.repository

import android.util.Log
import com.starwars.exercise.core.Resource
import com.starwars.exercise.data.api.StarWarsApi
import com.starwars.exercise.data.api.StarWarsImageApi
import com.starwars.exercise.data.api.dto.PlanetDto
import com.starwars.exercise.data.api.dto.SpeciesDto
import com.starwars.exercise.data.cache.PersonDao
import com.starwars.exercise.data.cache.PlanetDao
import com.starwars.exercise.data.cache.StarshipDao
import com.starwars.exercise.data.mapper.toCharacterAppearances
import com.starwars.exercise.data.mapper.toDomain
import com.starwars.exercise.data.mapper.toEntity
import com.starwars.exercise.data.mapper.toPersonImage
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.model.PersonImage
import com.starwars.exercise.domain.model.Planet
import com.starwars.exercise.domain.model.SearchResult
import com.starwars.exercise.domain.model.Species
import com.starwars.exercise.domain.model.Starship
import com.starwars.exercise.domain.repository.StarWarsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StarWarsRepositoryImpl @Inject constructor(
    private val api: StarWarsApi,
    private val imageApi : StarWarsImageApi,
    private val personDao: PersonDao,
    private val starshipDao: StarshipDao,
) : StarWarsRepository {
    override fun getCharacters(searchQuery: String?): Flow<Resource<List<Person>>> {
        return flow {
            try {
                emit(Resource.Loading)
                if (searchQuery.isNullOrBlank()) {
                    Log.d("StarWarsRepositoryImpl", "Loaded character images")
                    val initialList = personDao.getPeople().firstOrNull()
                    if (initialList.isNullOrEmpty()) {
                        val response = api.getPeople(null)
                        personDao.insertPeople(response.results.map { it.toEntity() })
                    }
                    personDao.getPeople().collect { entities ->
                        emit(Resource.Success(entities.map { it.toDomain() }))
                    }
                } else {
                    val response = api.getPeople(searchQuery)
                    val people = response.results.map { it.toDomain() }
                    personDao.insertPeople(response.results.map { it.toEntity() })
                    emit(Resource.Success(people))
                }
            } catch (error: Exception) {
                emit(Resource.Error(error.localizedMessage ?: "Unable to load characters"))
            }
        }
    }

    override suspend fun getCharacterDetail(personId: Int): Resource<Person> {
        return try {
            val response = api.getPerson(personId)
            Log.d("getCharacterDetail", "raw birthYear=${response.birthYear}, name=${response.name}")
            Resource.Success(response.toDomain())
        } catch (error: Exception) {
            Resource.Error(error.localizedMessage ?: "Unable to load character details")
        }
    }

    override fun getAllCharacterImages(): Flow<Resource<List<PersonImage>>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response = imageApi.getCharacterImage()
                emit(Resource.Success(response.map { it.toPersonImage()}))
            } catch (error: Exception) {
                emit(Resource.Error(error.localizedMessage ?: "Unable to load images"))
            }
        }
    }

    override suspend fun searchAll(query: String): Resource<List<SearchResult>> {
        return try {
            coroutineScope {
                val peopleDeferred = async {
                    try { api.getPeople(search = query).results.map { it.toDomain() } }
                    catch (e: Exception) { emptyList() }
                }
                val shipsDeferred = async {
                    try { api.getStarships(search = query).results.map { it.toDomain() } }
                    catch (e: Exception) { emptyList() }
                }
                val planetsDeferred = async {
                    try { api.getPlanets(search = query).results.map { it.toDomain() } }
                    catch (e: Exception) { emptyList() }
                }
                val imagesDeferred = async {
                    try { imageApi.getCharacterImage().associateBy { it.id } }
                    catch (e: Exception) { emptyMap() }
                }

                val people = peopleDeferred.await()
                val ships = shipsDeferred.await()
                val planets = planetsDeferred.await()
                val imageMap = imagesDeferred.await()

                val results = mutableListOf<SearchResult>()

                people.forEach { person ->
                    results.add(
                        SearchResult.CharacterResult(
                            person.copy(image = imageMap[person.id.toString()]?.image ?: "")
                        )
                    )
                }
                ships.forEach { results.add(SearchResult.StarshipResult(it)) }
                planets.forEach { results.add(SearchResult.PlanetResult(it)) }

                Resource.Success(results)
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Search failed")
        }
    }

        override fun getShips(searchQuery: String?): Flow<Resource<List<Starship>>> {
            return flow {
                try {
                    if (searchQuery.isNullOrBlank()) {
                        starshipDao.getStarships().collect { entities ->
                            emit(Resource.Success(entities.map { it.toDomain() }))
                        }
                    } else {
                        emit(Resource.Loading)
                        val response = api.getStarships(searchQuery)
                        val ships = response.results.map { it.toDomain() }
                        starshipDao.insertStarships(response.results.map { it.toEntity() })
                        emit(Resource.Success(ships))
                    }
                } catch (error: Exception) {
                    emit(Resource.Error(error.localizedMessage ?: "Unable to load starships"))
                }
            }
        }

        override suspend fun getShipDetail(starshipId: Int): Resource<Starship> {
            return try {
                val cached = starshipDao.getStarship(starshipId)
                if (cached != null) {
                    return Resource.Success(cached.toDomain())
                }
                val response = api.getStarship(starshipId)
                val entity = response.toEntity()
                starshipDao.insertStarships(listOf(entity))
                Resource.Success(response.toDomain())
            } catch (error: Exception) {
                Resource.Error(error.localizedMessage ?: "Unable to load starship details")
            }
        }

    override suspend fun getAllPlanets(): Resource<List<Planet>> {
        return try {
            val allPlanets = mutableListOf<PlanetDto>()
            var page = 1
            while (true) {
                val response = api.getPlanets(page = page)
                allPlanets.addAll(response.results)
                if (response.next == null) break
                page++
            }
            Resource.Success(allPlanets.map { it.toDomain() })
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
            // swapi paginates species, fetch all pages
            val allSpecies = mutableListOf<SpeciesDto>()
            var page = 1
            while (true) {
                val response = api.getSpeciesPage(page)
                allSpecies.addAll(response.results)
                if (response.next == null) break  // add `next: String?` to SpeciesResponseDto
                page++
            }
            Resource.Success(allSpecies.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to load species")
        }
    }

    override suspend fun getCharacterFirstAppearanceYears(): Resource<Map<Int, Int>> {
        return try {
            val films = api.getFilms().results

            // build map of characterId -> earliest year across all films
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
}
