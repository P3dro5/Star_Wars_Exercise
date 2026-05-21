package com.starwars.exercise.data.api

import com.starwars.exercise.data.api.dto.PersonDto
import com.starwars.exercise.data.api.dto.PlanetDto
import com.starwars.exercise.data.api.dto.SpeciesDto
import com.starwars.exercise.data.api.dto.StarshipDto
import com.starwars.exercise.domain.model.FilmDto
import retrofit2.http.GET
import retrofit2.http.Path

interface StarWarsApi {

    @GET("people/all.json")
    suspend fun getPeople(): List<PersonDto>

    @GET("people/{id}.json")
    suspend fun getPerson(@Path("id") id: Int): PersonDto

    @GET("starships/all.json")
    suspend fun getStarships(): List<StarshipDto>

    @GET("starships/{id}.json")
    suspend fun getStarship(@Path("id") id: Int): StarshipDto

    @GET("planets/all.json")
    suspend fun getPlanets(): List<PlanetDto>

    @GET("planets/{id}.json")
    suspend fun getPlanet(@Path("id") id: Int): PlanetDto

    @GET("species/all.json")
    suspend fun getSpecies(): List<SpeciesDto>

    @GET("species/{id}.json")
    suspend fun getSpeciesById(@Path("id") id: Int): SpeciesDto

    @GET("films/all.json")
    suspend fun getFilms(): List<FilmDto>
}