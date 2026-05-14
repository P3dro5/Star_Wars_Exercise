package com.starwars.exercise.data.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "persons")
data class PersonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    @ColumnInfo(name = "birth_year") val birthYear: String,
    val gender: String,
    val homeworld: String,
    val species: String,
    val height: String,
    val mass: String,
    @ColumnInfo(name = "hair_color") val hairColor: String,
    @ColumnInfo(name = "skin_color") val skinColor: String,
    @ColumnInfo(name = "eye_color") val eyeColor: String,
    @ColumnInfo(name = "film_count") val filmCount: Int,
    @ColumnInfo(name = "starship_ids") val starshipIds: String
)
