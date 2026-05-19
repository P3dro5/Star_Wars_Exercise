package com.starwars.exercise.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object OnboardingOne : Screen("onboarding_one")
    object OnboardingTwo : Screen("onboarding_two")
    object Home : Screen("home")
    object Menu : Screen("menu")
    object Compare : Screen("compare")
    object GalaxyMap: Screen("galaxy_map")
    object CompareResult : Screen("compare_result/{firstId}/{secondId}") {
        fun createRoute(firstId: Int, secondId: Int) = "compare_result/$firstId/$secondId"
    }
    object CompareVs : Screen("compare_vs/{firstId}/{secondId}") {
        fun createRoute(firstId: Int, secondId: Int) = "compare_vs/$firstId/$secondId"
    }
    object CompareTable : Screen("compare_table/{firstId}/{secondId}") {
        fun createRoute(firstId: Int, secondId: Int) = "compare_table/$firstId/$secondId"
    }
    object Detail : Screen("detail/{personId}") {
        fun createRoute(personId: Int) = "detail/$personId"
    }
    object Profile : Screen("profile/{personId}") {
        fun createRoute(personId: Int) = "profile/$personId"
    }
}
