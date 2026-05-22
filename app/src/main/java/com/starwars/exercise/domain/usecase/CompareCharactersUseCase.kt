package com.starwars.exercise.domain.usecase

import com.starwars.exercise.data.core.Resource
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.model.PersonImage
import com.starwars.exercise.domain.repository.StarWarsRepository
import javax.inject.Inject

class CompareCharactersUseCase @Inject constructor(
    private val repository: StarWarsRepository
) {
    suspend operator fun invoke(firstId: Int, secondId: Int): Resource<Pair<Person, Person>> {
        val first = repository.getCharacterDetail(firstId)
        val second = repository.getCharacterDetail(secondId)
        val images = repository.getCharacterImages()

        return when {
            first is Resource.Error -> Resource.Error(first.message)
            second is Resource.Error -> Resource.Error(second.message)
            first is Resource.Success && second is Resource.Success -> {
                val imageMap = if (images is Resource.Success) {
                    images.data.associateBy { it.id }
                } else emptyMap()

                val resolvedFirst = resolveCharacter(first.data, imageMap)
                val resolvedSecond = resolveCharacter(second.data, imageMap)

                Resource.Success(resolvedFirst to resolvedSecond)
            }
            else -> Resource.Error("Unable to compare characters")
        }
    }

    private suspend fun resolveCharacter(
        person: Person,
        imageMap: Map<String, PersonImage>
    ): Person = person.copy(
        homeworld = repository.resolveHomeworldName(person.homeworld),
        species = repository.resolveSpeciesName(person.species),
        image = imageMap[person.id.toString()]?.image ?: ""
    )
}