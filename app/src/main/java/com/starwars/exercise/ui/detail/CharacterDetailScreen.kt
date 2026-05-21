package com.starwars.exercise.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.starwars.exercise.ui.components.ErrorState
import com.starwars.exercise.ui.components.LoadingState
import com.starwars.exercise.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    personId: Int,
    onBack: () -> Unit,
    onProfile: (Int) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(personId) {
        viewModel.loadDetail(personId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (uiState is DetailUiState.Success) {
                        Text((uiState as DetailUiState.Success).person.name)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (uiState) {
            DetailUiState.Loading -> LoadingState(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            )
            is DetailUiState.Error -> ErrorState(
                message = (uiState as DetailUiState.Error).message,
                actionLabel = "Retry",
                onRetry = { viewModel.loadDetail(personId) },
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            )
            is DetailUiState.Success -> {
                val person = (uiState as DetailUiState.Success).person
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionHeader(
                        title = "${person.species} • ${person.homeworld}"
                    )
                    Text(text = "Birth year: ${person.birthYear}", color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "Gender: ${person.gender}", color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "Height: ${person.height}", color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "Mass: ${person.mass}", color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "Film appearances: ${person.filmCount}", color = MaterialTheme.colorScheme.onBackground)
                    Button(
                        onClick = { onProfile(person.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "View profile")
                    }
                }
            }
        }
    }
}