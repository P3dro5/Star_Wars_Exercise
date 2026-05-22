package com.starwars.exercise.data.mapper

import com.starwars.exercise.data.config.GalaxyPositions
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

// Update extractId extension to handle .json suffix
fun String.extractId(): Int {
    return this
        .trimEnd('/')
        .removeSuffix(".json")
        .split("/")
        .lastOrNull()
        ?.toIntOrNull() ?: 0
}

internal fun PersonDto.toDomain(): Person {
    return Person(
        id = url?.extractId() ?: 0,
        name = name,
        birthYear = birthYear?.takeIf { it.isNotBlank() && it.lowercase() != "unknown" } ?: "Unknown",
        gender = gender?.takeIf { it.isNotBlank() && it.lowercase() != "unknown" } ?: "Unknown",
        homeworld = homeworld ?: "",
        species = species.firstOrNull() ?: "",
        height = height?.takeIf { it.isNotBlank() && it.lowercase() != "unknown" } ?: "Unknown",
        mass = mass?.takeIf { it.isNotBlank() && it.lowercase() != "unknown" } ?: "Unknown",
        hairColor = hairColor?.takeIf { it.isNotBlank() && it.lowercase() != "unknown" } ?: "Unknown",
        skinColor = skinColor?.takeIf { it.isNotBlank() && it.lowercase() != "unknown" } ?: "Unknown",
        eyeColor = eyeColor?.takeIf { it.isNotBlank() && it.lowercase() != "unknown" } ?: "Unknown",
        filmCount = films.size,
        starshipIds = starships.map { it.extractId() }.filter { it > 0 },
        image = ""
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
        val id = url.extractId()
        if (id > 0) id else null
    }
)

fun FilmDto.toCharacterAppearances(): List<Pair<Int, Int>> {
    val year = releaseDate.split("-").first().toIntOrNull() ?: Int.MAX_VALUE
    return characters.mapNotNull { url ->
        val id = url.extractId()
        if (id > 0) Pair(id, year) else null
    }
}

fun PlanetDto.toDomain(): Planet {
    val id = url.extractId()
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
        residentIds = residents.mapNotNull { it.extractId().takeIf { id -> id > 0 } },
        galaxyPosition = GalaxyPositions.getPosition(name)
    )
}
