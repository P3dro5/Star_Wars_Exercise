package com.starwars.exercise.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.starwars.exercise.R
import com.starwars.exercise.ui.theme.StarWarsTheme
import com.starwars.exercise.ui.theme.ThemeMode
import com.starwars.exercise.ui.theme.ThemeViewModel

@Composable
fun MenuScreen(
    onBack: () -> Unit,
    onCompare: () -> Unit,
    onGalaxyMap: () -> Unit,
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()

    MenuScreenContent(
        isDarkTheme = isDarkTheme,
        onBack = onBack,
        onCompare = onCompare,
        onGalaxyMap = onGalaxyMap,
        onThemeChanged = { themeViewModel.setTheme(it) }
    )
}

@Composable
private fun MenuScreenContent(
    isDarkTheme: Boolean?,
    onBack: () -> Unit,
    onCompare: () -> Unit,
    onGalaxyMap: () -> Unit,
    onThemeChanged: (ThemeMode) -> Unit,
) {
    val currentMode = when (isDarkTheme) {
        true -> ThemeMode.DARK
        false -> ThemeMode.LIGHT
        null -> ThemeMode.SYSTEM
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Galaxy Menu",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Theme selector
            Text(
                text = "Theme",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Start)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = currentMode == mode,
                        onClick = { onThemeChanged.invoke(mode) },
                        label = {
                            Text(mode.name.lowercase().replaceFirstChar { it.uppercase() })
                        },
                        leadingIcon = {
                            Image(
                                painter = painterResource(id = when (mode) {
                                    ThemeMode.LIGHT -> R.drawable.lightsaber_green
                                    ThemeMode.DARK ->  R.drawable.lightsaber_red
                                    ThemeMode.SYSTEM -> R.drawable.darth_vader
                                }),
                                contentDescription =  mode.name,
                                modifier = Modifier
                                    .size(24.dp),
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onCompare, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Compare characters")
            }
            Button(onClick = onGalaxyMap, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Galaxy Map")
            }
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Back")
            }
        }
    }
}

@Preview(showBackground = true, name = "Menu Screen - Dark")
@Composable
fun MenuScreenDarkPreview() {
    StarWarsTheme(darkTheme = true) {
        MenuScreenContent(
            isDarkTheme = true,
            onBack = {},
            onCompare = {},
            onGalaxyMap = {},
            onThemeChanged = {}
        )
    }
}

@Preview(showBackground = true, name = "Menu Screen - Light")
@Composable
fun MenuScreenLightPreview() {
    StarWarsTheme(darkTheme = false) {
        MenuScreenContent(
            isDarkTheme = false,
            onBack = {},
            onCompare = {},
            onGalaxyMap = {},
            onThemeChanged = {}
        )
    }
}
