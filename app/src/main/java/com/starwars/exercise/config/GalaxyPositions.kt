package com.starwars.exercise.config

import com.starwars.exercise.domain.model.GalaxyPosition

object GalaxyPositions {
    // Positions are relative (0f..1f) on the galaxy map image
    // Based on approximate Star Wars Legends galaxy map layout
    private val planetPositions = mapOf(
        "Tatooine" to GalaxyPosition(0.72f, 0.78f),
        "Alderaan" to GalaxyPosition(0.48f, 0.52f),
        "Yavin IV" to GalaxyPosition(0.38f, 0.60f),
        "Hoth" to GalaxyPosition(0.30f, 0.35f),
        "Dagobah" to GalaxyPosition(0.55f, 0.70f),
        "Bespin" to GalaxyPosition(0.45f, 0.65f),
        "Endor" to GalaxyPosition(0.25f, 0.72f),
        "Naboo" to GalaxyPosition(0.52f, 0.68f),
        "Coruscant" to GalaxyPosition(0.50f, 0.50f),
        "Kamino" to GalaxyPosition(0.60f, 0.80f),
        "Geonosis" to GalaxyPosition(0.68f, 0.75f),
        "Utapau" to GalaxyPosition(0.58f, 0.62f),
        "Mustafar" to GalaxyPosition(0.65f, 0.70f),
        "Kashyyyk" to GalaxyPosition(0.42f, 0.58f),
        "Polis Massa" to GalaxyPosition(0.55f, 0.45f),
        "Mygeeto" to GalaxyPosition(0.35f, 0.40f),
        "Felucia" to GalaxyPosition(0.48f, 0.72f),
        "Cato Neimoidia" to GalaxyPosition(0.40f, 0.55f),
        "Saleucami" to GalaxyPosition(0.62f, 0.55f),
        "Stewjon" to GalaxyPosition(0.50f, 0.42f),
        "Eriadu" to GalaxyPosition(0.70f, 0.65f),
        "Corellia" to GalaxyPosition(0.45f, 0.48f),
        "Rodia" to GalaxyPosition(0.60f, 0.68f),
        "Nal Hutta" to GalaxyPosition(0.65f, 0.60f),
        "Dantooine" to GalaxyPosition(0.38f, 0.65f),
        "Bestine IV" to GalaxyPosition(0.72f, 0.72f),
        "Ord Mantell" to GalaxyPosition(0.35f, 0.55f),
        "unknown" to GalaxyPosition(0.50f, 0.50f),
        "Trandosha" to GalaxyPosition(0.42f, 0.62f),
        "Socorro" to GalaxyPosition(0.68f, 0.82f),
        "Mon Cala" to GalaxyPosition(0.32f, 0.60f),
        "Chandrila" to GalaxyPosition(0.45f, 0.45f),
        "Sullust" to GalaxyPosition(0.28f, 0.68f),
        "Toydaria" to GalaxyPosition(0.58f, 0.55f),
        "Malastare" to GalaxyPosition(0.52f, 0.72f),
        "Dathomir" to GalaxyPosition(0.40f, 0.70f),
        "Ryloth" to GalaxyPosition(0.55f, 0.58f),
        "Aleen Minor" to GalaxyPosition(0.62f, 0.62f),
        "Vulpter" to GalaxyPosition(0.48f, 0.40f),
        "Troiken" to GalaxyPosition(0.65f, 0.50f),
        "Tund" to GalaxyPosition(0.70f, 0.55f),
        "Haruun Kal" to GalaxyPosition(0.38f, 0.50f),
        "Cerea" to GalaxyPosition(0.52f, 0.55f),
        "Glee Anselm" to GalaxyPosition(0.45f, 0.65f),
        "Iridonia" to GalaxyPosition(0.60f, 0.45f),
        "Tholoth" to GalaxyPosition(0.55f, 0.40f),
        "Iktotch" to GalaxyPosition(0.48f, 0.58f),
        "Quermia" to GalaxyPosition(0.42f, 0.45f),
        "Dorin" to GalaxyPosition(0.35f, 0.48f),
        "Champala" to GalaxyPosition(0.40f, 0.42f),
        "Mirial" to GalaxyPosition(0.62f, 0.72f),
        "Serenno" to GalaxyPosition(0.32f, 0.45f),
        "Concord Dawn" to GalaxyPosition(0.68f, 0.48f),
        "Zolan" to GalaxyPosition(0.55f, 0.75f),
        "Ojom" to GalaxyPosition(0.28f, 0.55f),
        "Skako" to GalaxyPosition(0.72f, 0.60f),
        "Muunilinst" to GalaxyPosition(0.35f, 0.62f),
        "Shili" to GalaxyPosition(0.58f, 0.48f),
        "Kalee" to GalaxyPosition(0.75f, 0.70f),
        "Umbara" to GalaxyPosition(0.42f, 0.55f)
    )

    fun getPosition(planetName: String): GalaxyPosition {
        return planetPositions[planetName] ?: GalaxyPosition(0.5f, 0.5f)
    }
}