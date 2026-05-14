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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.starwars.exercise.ui.components.ErrorState
import com.starwars.exercise.ui.components.LoadingState
import com.starwars.exercise.ui.components.PersonListItem

@Composable
fun CompareScreen(
    onBack: () -> Unit,
    onCompareResult: (Int, Int) -> Unit,
    viewModel: CompareViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedIds = remember { mutableStateListOf<Int>() }

    LaunchedEffect(Unit) {
        viewModel.loadCharacters()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (uiState) {
            CompareUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
            is CompareUiState.Error -> ErrorState(
                message = (uiState as CompareUiState.Error).message,
                actionLabel = "Retry",
                onRetry = { viewModel.loadCharacters() },
                modifier = Modifier.fillMaxSize()
            )
            is CompareUiState.Success -> {
                val characters = (uiState as CompareUiState.Success).characters
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Select two heroes to compare",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    characters.take(6).forEach { person ->
                        PersonListItem(
                            id = person.id,
                            name = person.name,
                            image = person.image,
                            subtitle = "${person.species} • ${person.homeworld}",
                            onClick = {
                                if (selectedIds.contains(person.id)) {
                                    selectedIds.remove(person.id)
                                } else if (selectedIds.size < 2) {
                                    selectedIds.add(person.id)
                                }
                            }
                        )
                    }
                    Button(
                        onClick = {
                            if (selectedIds.size == 2) {
                                onCompareResult(selectedIds[0], selectedIds[1])
                            }
                        },
                        enabled = selectedIds.size == 2
                    ) {
                        Text(text = "Compare")
                    }
                    Button(onClick = onBack) {
                        Text(text = "Cancel")
                    }
                }
            }
            else -> {}
        }
    }
}
