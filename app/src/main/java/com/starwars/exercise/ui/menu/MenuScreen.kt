package com.starwars.exercise.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen(
    onBack: () -> Unit,
    onCompare: () -> Unit,
    onLogout: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Galaxy menu",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(onClick = onCompare, modifier = Modifier.fillMaxSize()) {
                Text(text = "Compare heroes")
            }
            Button(onClick = onLogout, modifier = Modifier.fillMaxSize()) {
                Text(text = "Logout")
            }
            Button(onClick = onBack, modifier = Modifier.fillMaxSize()) {
                Text(text = "Back")
            }
        }
    }
}
