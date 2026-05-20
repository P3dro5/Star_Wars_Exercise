package com.starwars.exercise.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.starwars.exercise.ui.compare.CompareScreen
import com.starwars.exercise.ui.compare.CompareVsScreen
import com.starwars.exercise.ui.compare.CompareTableScreen
import com.starwars.exercise.ui.detail.CharacterDetailScreen
import com.starwars.exercise.ui.galaxy.GalaxyMapScreen
import com.starwars.exercise.ui.home.HomeScreen
import com.starwars.exercise.ui.menu.MenuScreen
import com.starwars.exercise.ui.onboarding.OnboardingOneScreen
import com.starwars.exercise.ui.onboarding.OnboardingTwoScreen
import com.starwars.exercise.ui.profile.CharacterProfileScreen
import com.starwars.exercise.ui.splash.SplashScreen
import com.starwars.exercise.ui.theme.ThemeViewModel

@Composable
fun StarWarsNavHost(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(onNext = { navController.navigate(Screen.OnboardingOne.route) })
        }
        composable(Screen.OnboardingOne.route) {
            OnboardingOneScreen(onNext = { navController.navigate(Screen.OnboardingTwo.route) })
        }
        composable(Screen.OnboardingTwo.route) {
            OnboardingTwoScreen(onNext = { navController.navigate(Screen.Home.route) })
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onMenu = { navController.navigate(Screen.Menu.route) },
                onCharacterSelected = { navController.navigate(Screen.Detail.createRoute(it)) },
                onGalaxyMap = { navController.navigate(Screen.GalaxyMap.route) }
            )
        }
        composable(Screen.Menu.route) {
            MenuScreen(
                onBack = { navController.popBackStack() },
                onCompare = { navController.navigate(Screen.Compare.route) },
                onGalaxyMap = { navController.navigate(Screen.GalaxyMap.route) },
                themeViewModel = themeViewModel
            )
        }
        composable(Screen.GalaxyMap.route) {
            GalaxyMapScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Compare.route) {
            CompareScreen(
                onBack = { navController.popBackStack() },
                onCompareResult = { firstId, secondId ->
                    navController.navigate(Screen.CompareResult.createRoute(firstId, secondId))
                }
            )
        }
        composable(
            route = Screen.CompareVs.route,
            arguments = listOf(
                navArgument("firstId") { type = NavType.IntType },
                navArgument("secondId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val firstId = backStackEntry.arguments?.getInt("firstId") ?: 1
            val secondId = backStackEntry.arguments?.getInt("secondId") ?: 2
            CompareVsScreen(
                firstId = firstId,
                secondId = secondId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.CompareTable.route,
            arguments = listOf(
                navArgument("firstId") { type = NavType.IntType },
                navArgument("secondId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val firstId = backStackEntry.arguments?.getInt("firstId") ?: 1
            val secondId = backStackEntry.arguments?.getInt("secondId") ?: 2
            CompareTableScreen(
                firstId = firstId,
                secondId = secondId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("personId") { type = NavType.IntType })
        ) { backStackEntry ->
            val personId = backStackEntry.arguments?.getInt("personId") ?: 1
            CharacterDetailScreen(
                personId = personId,
                onBack = { navController.popBackStack() },
                onProfile = { navController.navigate(Screen.Profile.createRoute(it)) }
            )
        }
        composable(
            route = Screen.Profile.route,
            arguments = listOf(navArgument("personId") { type = NavType.IntType })
        ) { backStackEntry ->
            val personId = backStackEntry.arguments?.getInt("personId") ?: 1
            CharacterProfileScreen(personId = personId, onBack = { navController.popBackStack() })
        }
    }
}
