package com.starwars.exercise.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.starwars.exercise.domain.navigation.StarWarsNavHost
import com.starwars.exercise.ui.theme.StarWarsTheme
import com.starwars.exercise.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val darkTheme = isDarkTheme ?: isSystemInDarkTheme()

            StarWarsTheme(darkTheme = darkTheme) {
                StarWarsNavHost(themeViewModel = themeViewModel)
            }
        }
    }
}