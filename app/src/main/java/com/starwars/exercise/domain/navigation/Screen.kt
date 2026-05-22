package com.starwars.exercise.domain.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object OnboardingOne : Screen("onboarding_one")
    data object OnboardingTwo : Screen("onboarding_two")
    data object Home : Screen("home")
    data object Menu : Screen("menu")
    data object Compare : Screen("compare")
    data object GalaxyMap: Screen("galaxy_map")
    data object CompareResult : Screen("compare_result/{firstId}/{secondId}") {
        fun createRoute(firstId: Int, secondId: Int) = "compare_result/$firstId/$secondId"
    }
    data object Detail : Screen("detail/{personId}") {
        fun createRoute(personId: Int) = "detail/$personId"
    }
    data object Profile : Screen("profile/{personId}") {
        fun createRoute(personId: Int) = "profile/$personId"
    }
}
