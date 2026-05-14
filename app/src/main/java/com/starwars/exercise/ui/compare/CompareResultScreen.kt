package com.starwars.exercise.ui.compare

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.starwars.exercise.core.Resource

@Composable
fun CompareResultScreen(
    firstId: Int,
    secondId: Int,
    onBack: () -> Unit,
    onVs: () -> Unit,
    onTable: () -> Unit,
    viewModel: CompareViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val resultState = remember { mutableStateOf<Resource<Pair<com.starwars.exercise.domain.model.Person, com.starwars.exercise.domain.model.Person>>>(Resource.Loading) }

    LaunchedEffect(firstId, secondId) {
        resultState.value = Resource.Loading
        resultState.value = viewModel.compareCharacters(firstId, secondId)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (val result = resultState.value) {
            is Resource.Loading -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            is Resource.Error -> Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = result.message ?: "Unable to compare", color = MaterialTheme.colorScheme.error)
                Button(onClick = onBack) {
                    Text(text = "Back")
                }
            }
            is Resource.Success -> {
                val (first, second) = result.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Compare result", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "${first.name} vs ${second.name}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "First character: ${first.species}, homeworld ${first.homeworld}", color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "Second character: ${second.species}, homeworld ${second.homeworld}", color = MaterialTheme.colorScheme.onBackground)
                    Button(onClick = onVs) {
                        Text(text = "View side by side")
                    }
                    Button(onClick = onTable) {
                        Text(text = "View table")
                    }
                    Button(onClick = onBack) {
                        Text(text = "Back")
                    }
                }
            }
        }
    }
}
