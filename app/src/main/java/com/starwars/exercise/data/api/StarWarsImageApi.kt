package com.starwars.exercise.data.api

import com.starwars.exercise.data.api.dto.PersonImageDto
import retrofit2.http.GET

interface StarWarsImageApi {
    @GET("all.json")
    suspend fun getCharacterImage(): List<PersonImageDto>
}
