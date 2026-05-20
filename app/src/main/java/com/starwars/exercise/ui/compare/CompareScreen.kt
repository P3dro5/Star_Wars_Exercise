package com.starwars.exercise.ui.compare

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.ui.components.ErrorState
import com.starwars.exercise.ui.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    onBack: () -> Unit,
    onCompareResult: (Int, Int) -> Unit,
    viewModel: CompareViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQueryFirst by viewModel.searchQueryFirst.collectAsStateWithLifecycle()
    val searchQuerySecond by viewModel.searchQuerySecond.collectAsStateWithLifecycle()
    val filteredFirst by viewModel.filteredFirst.collectAsStateWithLifecycle()
    val filteredSecond by viewModel.filteredSecond.collectAsStateWithLifecycle()
    val selectedFirst by viewModel.selectedFirst.collectAsStateWithLifecycle()
    val selectedSecond by viewModel.selectedSecond.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compare Heroes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (uiState) {
            CompareCharactersUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
            is CompareCharactersUiState.Error -> ErrorState(
                message = (uiState as CompareCharactersUiState.Error).message,
                actionLabel = "Retry",
                onRetry = { viewModel.loadCharacters() },
                modifier = Modifier.fillMaxSize()
            )
            is CompareCharactersUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Select two heroes to compare",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    // First character selector
                    CharacterSearchField(
                        label = "First hero",
                        query = searchQueryFirst,
                        selectedPerson = selectedFirst,
                        suggestions = filteredFirst,
                        onQueryChanged = { viewModel.onSearchFirstChanged(it) },
                        onPersonSelected = { viewModel.onFirstSelected(it) },
                        onClear = { viewModel.clearFirst() }
                    )

                    // Second character selector
                    CharacterSearchField(
                        label = "Second hero",
                        query = searchQuerySecond,
                        selectedPerson = selectedSecond,
                        suggestions = filteredSecond,
                        onQueryChanged = { viewModel.onSearchSecondChanged(it) },
                        onPersonSelected = { viewModel.onSecondSelected(it) },
                        onClear = { viewModel.clearSecond() }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            val first = selectedFirst ?: return@Button
                            val second = selectedSecond ?: return@Button
                            onCompareResult(first.id, second.id)
                        },
                        enabled = selectedFirst != null && selectedSecond != null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Compare")
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun CharacterSearchField(
    label: String,
    query: String,
    selectedPerson: Person?,
    suggestions: List<Person>,
    onQueryChanged: (String) -> Unit,
    onPersonSelected: (Person) -> Unit,
    onClear: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = onClear) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            leadingIcon = {
                if (selectedPerson != null) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (selectedPerson != null)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outline
            )
        )

        // dropdown suggestions
        if (suggestions.isNotEmpty() && selectedPerson == null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                LazyColumn {
                    items(suggestions, key = { it.id }) { person ->
                        ListItem(
                            headlineContent = { Text(person.name) },
                            supportingContent = {
                                Text(
                                    "${person.species} • ${person.homeworld}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            leadingContent = {
                                AsyncImage(
                                    model = person.image,
                                    contentDescription = person.name,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            },
                            modifier = Modifier.clickable { onPersonSelected(person) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

        // selected person preview card
        if (selectedPerson != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = selectedPerson.image,
                        contentDescription = selectedPerson.name,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedPerson.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${selectedPerson.species} • ${selectedPerson.homeworld}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(onClick = onClear) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}