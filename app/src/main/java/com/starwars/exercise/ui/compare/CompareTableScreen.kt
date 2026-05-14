package com.starwars.exercise.ui.compare

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CompareTableScreen(
    firstId: Int,
    secondId: Int,
    onBack: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Comparison table", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
            Text(text = "Showing attributes for $firstId and $secondId", color = MaterialTheme.colorScheme.onBackground)
            Button(onClick = onBack) {
                Text(text = "Back")
            }
        }
    }
}
