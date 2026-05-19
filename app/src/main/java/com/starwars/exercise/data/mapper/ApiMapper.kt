package com.starwars.exercise.data.mapper

import com.starwars.exercise.config.GalaxyPositions
import com.starwars.exercise.data.api.dto.PersonDto
import com.starwars.exercise.data.api.dto.PersonImageDto
import com.starwars.exercise.data.api.dto.PlanetDto
import com.starwars.exercise.data.api.dto.SpeciesDto
import com.starwars.exercise.data.api.dto.StarshipDto
import com.starwars.exercise.data.cache.PersonEntity
import com.starwars.exercise.data.cache.PlanetEntity
import com.starwars.exercise.data.cache.StarshipEntity
import com.starwars.exercise.domain.model.FilmDto
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.model.PersonImage
import com.starwars.exercise.domain.model.Planet
import com.starwars.exercise.domain.model.Species
import com.starwars.exercise.domain.model.Starship

internal fun String.extractId(): Int {
    return trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0
}

internal fun PersonDto.toDomain(): Person {
    return Person(
        id = url?.extractId() ?: 0,
        name = name,
        birthYear = birthYear ?: "Unknown",
        gender = gender ?: "Unknown",
        homeworld = homeworld?.extractId()?.toString() ?: "Unknown",
        species = species.firstOrNull() ?: "Unknown",
        height = height ?: "Unknown",
        mass = mass ?: "Unknown",
        hairColor = hairColor ?: "Unknown",
        skinColor = skinColor ?: "Unknown",
        eyeColor = eyeColor ?: "Unknown",
        filmCount = films.size,
        starshipIds = starships.map { it.extractId() }.filter { it > 0 },
        image = ""
    )
}

internal fun PersonDto.toEntity(): PersonEntity {
    return PersonEntity(
        id = url?.extractId() ?: 0,
        name = name,
        birthYear = birthYear ?: "Unknown",
        gender = gender ?: "Unknown",
        homeworld = homeworld?.extractId()?.toString() ?: "Unknown",
        species = species.firstOrNull() ?: "Unknown",
        height = height ?: "Unknown",
        mass = mass ?: "Unknown",
        hairColor = hairColor ?: "Unknown",
        skinColor = skinColor ?: "Unknown",
        eyeColor = eyeColor ?: "Unknown",
        filmCount = films.size,
        starshipIds = starships.map { it.extractId() }.joinToString(",")
    )
}

internal fun PersonEntity.toDomain(): Person {
    return Person(
        id = id,
        name = name,
        birthYear = birthYear,
        gender = gender,
        homeworld = homeworld,
        species = species,
        height = height,
        mass = mass,
        hairColor = hairColor,
        skinColor = skinColor,
        eyeColor = eyeColor,
        filmCount = filmCount,
        starshipIds = starshipIds.split(',').filter { it.isNotBlank() }.mapNotNull { it.toIntOrNull() },
        image = ""
    )
}

internal fun StarshipDto.toDomain(): Starship {
    return Starship(
        id = url.extractId(),
        name = name,
        model = model,
        manufacturer = manufacturer,
        starshipClass = starshipClass,
        crew = crew,
        passengers = passengers,
        costInCredits = costInCredits,
        length = length
    )
}

internal fun PersonImageDto.toPersonImage(): PersonImage {
    return PersonImage(
        image = this.image,
        id = this.id
    )
}

internal fun StarshipDto.toEntity(): StarshipEntity {
    return StarshipEntity(
        id = url.extractId(),
        name = name,
        model = model,
        manufacturer = manufacturer,
        starshipClass = starshipClass,
        crew = crew,
        passengers = passengers,
        costInCredits = costInCredits,
        length = length
    )
}

internal fun StarshipEntity.toDomain(): Starship {
    return Starship(
        id = id,
        name = name,
        model = model,
        manufacturer = manufacturer,
        starshipClass = starshipClass,
        crew = crew,
        passengers = passengers,
        costInCredits = costInCredits,
        length = length
    )
}

internal fun PlanetDto.toEntity(): PlanetEntity {
    return PlanetEntity(
        id = url.extractId(),
        name = name,
        climate = climate,
        terrain = terrain,
        population = population,
        gravity = gravity,
        diameter = diameter,
        orbitalPeriod = orbitalPeriod,
        rotationPeriod = rotationPeriod
    )
}

internal fun PlanetEntity.toDomain(): Planet {
    return Planet(
        id = id,
        name = name,
        climate = climate,
        terrain = terrain,
        population = population,
        gravity = gravity,
        diameter = diameter,
        orbitalPeriod = orbitalPeriod,
        rotationPeriod = rotationPeriod
    )
}

internal fun SpeciesDto.toDomain(): Species = Species(
    name = name,
    peopleIds = people.mapNotNull { url ->
        url.trimEnd('/').split("/").lastOrNull()?.toIntOrNull()
    }
)

fun FilmDto.toCharacterAppearances(): List<Pair<Int, Int>> {
    val year = releaseDate.split("-").first().toIntOrNull() ?: Int.MAX_VALUE
    return characters.mapNotNull { url ->
        val id = url.trimEnd('/').split("/").lastOrNull()?.toIntOrNull()
        id?.let { Pair(it, year) }
    }
}

fun PlanetDto.toDomain(): Planet {
    val id = url.trimEnd('/').split("/").lastOrNull()?.toIntOrNull() ?: 0
    return Planet(
        id = id,
        name = name,
        climate = climate,
        terrain = terrain,
        population = population,
        gravity = gravity,
        diameter = diameter,
        orbitalPeriod = orbitalPeriod,
        rotationPeriod = rotationPeriod,
        residentIds = residents.mapNotNull { residentUrl ->
            residentUrl.trimEnd('/').split("/").lastOrNull()?.toIntOrNull()
        },
        galaxyPosition = GalaxyPositions.getPosition(name)
    )
}
