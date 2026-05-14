package com.starwars.exercise.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.starwars.exercise.ui.components.ErrorState
import com.starwars.exercise.ui.components.LoadingState
import com.starwars.exercise.ui.components.SectionHeader
import com.starwars.exercise.ui.detail.DetailViewModel
import com.starwars.exercise.ui.detail.DetailUiState

@Composable
fun CharacterProfileScreen(
    personId: Int,
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(personId) {
        viewModel.loadDetail(personId)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (uiState) {
            DetailUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
            is DetailUiState.Error -> ErrorState(
                message = (uiState as DetailUiState.Error).message,
                actionLabel = "Retry",
                onRetry = { viewModel.loadDetail(personId) },
                modifier = Modifier.fillMaxSize()
            )
            is DetailUiState.Success -> {
                val person = (uiState as DetailUiState.Success).person
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionHeader(title = "Profile: ${person.name}")
                    Text(text = "Birth year: ${person.birthYear}", color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "Species: ${person.species}", color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "Homeworld: ${person.homeworld}", color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "Hair: ${person.hairColor}", color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "Skin: ${person.skinColor}", color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "Eye color: ${person.eyeColor}", color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "Starships: ${person.starshipIds.size}", color = MaterialTheme.colorScheme.onBackground)
                    Button(onClick = onBack) {
                        Text(text = "Back")
                    }
                }
            }
        }
    }
}
