package com.starwars.exercise.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.starwars.exercise.ui.theme.ThemeMode
import com.starwars.exercise.ui.theme.ThemeViewModel

@Composable
fun MenuScreen(
    onBack: () -> Unit,
    onCompare: () -> Unit,
    onGalaxyMap: () -> Unit,
    onLogout: () -> Unit,
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
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
                        onClick = { themeViewModel.setTheme(mode) },
                        label = {
                            Text(mode.name.lowercase().replaceFirstChar { it.uppercase() })
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when (mode) {
                                    ThemeMode.LIGHT -> Icons.AutoMirrored.Default.ArrowForward
                                    ThemeMode.DARK ->  Icons.AutoMirrored.Default.ArrowBack
                                    ThemeMode.SYSTEM ->  Icons.AutoMirrored.Default.Send
                                },
                                contentDescription = mode.name
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onCompare, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Compare heroes")
            }
            Button(onClick = onGalaxyMap, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Galaxy Map")
            }
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Logout")
            }
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Back")
            }
        }
    }
}