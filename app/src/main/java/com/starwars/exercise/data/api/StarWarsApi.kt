package com.starwars.exercise.data.api

import com.starwars.exercise.data.api.dto.PeopleResponseDto
import com.starwars.exercise.data.api.dto.PersonDto
import com.starwars.exercise.data.api.dto.PlanetDto
import com.starwars.exercise.data.api.dto.StarshipDto
import com.starwars.exercise.data.api.dto.StarshipResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StarWarsApi {
    @GET("people/")
    suspend fun getPeople(
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1
    ): PeopleResponseDto

    @GET("people/{id}/")
    suspend fun getPerson(@Path("id") id: Int): PersonDto

    @GET("starships/")
    suspend fun getStarships(
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1
    ): StarshipResponseDto

    @GET("starships/{id}/")
    suspend fun getStarship(@Path("id") id: Int): StarshipDto

    @GET("planets/{id}/")
    suspend fun getPlanet(@Path("id") id: Int): PlanetDto
}
