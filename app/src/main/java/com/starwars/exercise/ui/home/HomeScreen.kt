package com.starwars.exercise.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.starwars.exercise.ui.components.ErrorState
import com.starwars.exercise.ui.components.LoadingState
import com.starwars.exercise.ui.components.PersonListItem
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.starwars.exercise.domain.model.CharacterFilter
import com.starwars.exercise.domain.model.SortField
import com.starwars.exercise.domain.model.SortOrder
import com.starwars.exercise.domain.model.Species
import com.starwars.exercise.domain.model.availableGenders

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMenu: () -> Unit,
    onCharacterSelected: (Int) -> Unit,
    onGalaxyMap: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val speciesUiState by viewModel.speciesUiState.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    var showFilterSheet by remember { mutableStateOf(false) }

    if (showFilterSheet) {
        FilterBottomSheet(
            speciesUiState = speciesUiState,
            availableGenders = availableGenders,
            currentFilter = filter,
            onSpeciesToggled = { viewModel.onSpeciesToggled(it) },
            onGenderToggled = { viewModel.onGenderToggled(it) },
            onSortChanged = { field, order -> viewModel.onSortChanged(field, order) },
            onApply = {
                viewModel.applyFilters()
                showFilterSheet = false
            },
            onClear = {
                viewModel.clearFilters()
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = { Text("Search...") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = onGalaxyMap) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Galaxy Map")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // hide sort/filter row when searching
            if (!isSearching) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SortChip(
                        label = "Name",
                        isSelected = filter.sortField == SortField.NAME,
                        sortOrder = filter.sortOrder,
                        onClick = {
                            val newOrder = if (filter.sortField == SortField.NAME) {
                                if (filter.sortOrder == SortOrder.ASCENDING) SortOrder.DESCENDING
                                else SortOrder.ASCENDING
                            } else SortOrder.ASCENDING
                            viewModel.onSortChanged(SortField.NAME, newOrder)
                            viewModel.applyFilters()
                        }
                    )
                    SortChip(
                        label = "Year",
                        isSelected = filter.sortField == SortField.YEAR,
                        sortOrder = filter.sortOrder,
                        onClick = {
                            val newOrder = if (filter.sortField == SortField.YEAR) {
                                if (filter.sortOrder == SortOrder.ASCENDING) SortOrder.DESCENDING
                                else SortOrder.ASCENDING
                            } else SortOrder.ASCENDING
                            viewModel.onSortChanged(SortField.YEAR, newOrder)
                            viewModel.applyFilters()
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    BadgedBox(
                        badge = {
                            val count = filter.selectedSpecies.size + filter.selectedGenders.size
                            if (count > 0) Badge { Text("$count") }
                        }
                    ) {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(Icons.AutoMirrored.Default.List, contentDescription = "Filters")
                        }
                    }
                }
            }

            // switch between search results and normal paged list
            if (isSearching) {
                SearchResultsView(
                    searchState = searchState,
                    onCharacterSelected = onCharacterSelected
                )
            } else {
                when (uiState) {
                    HomeUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
                    is HomeUiState.Success -> {
                        val characters = (uiState as HomeUiState.Success)
                            .characters.collectAsLazyPagingItems()
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(
                                count = characters.itemCount,
                                key = characters.itemKey { it.id }
                            ) { index ->
                                val person = characters[index]
                                PersonListItem(
                                    name = person?.name ?: "",
                                    id = person?.id ?: 0,
                                    image = person?.image ?: "",
                                    onClick = { onCharacterSelected(person?.id ?: 0) }
                                )
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                            when (characters.loadState.append) {
                                is LoadState.Loading -> item {
                                    Box(
                                        Modifier.fillMaxWidth().padding(16.dp),
                                        Alignment.Center
                                    ) { CircularProgressIndicator() }
                                }
                                is LoadState.NotLoading -> {
                                    if (characters.loadState.append.endOfPaginationReached) {
                                        item {
                                            Text(
                                                text = "No more characters",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                                is LoadState.Error -> item {
                                    ErrorState(
                                        message = "Failed to load more",
                                        actionLabel = "Retry",
                                        onRetry = { characters.retry() }
                                    )
                                }
                            }
                        }
                    }
                    is HomeUiState.Error -> ErrorState(
                        message = (uiState as HomeUiState.Error).message,
                        actionLabel = "Retry",
                        onRetry = { viewModel.loadCharacters() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun SortChip(
    label: String,
    isSelected: Boolean,
    sortOrder: SortOrder,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        trailingIcon = {
            if (isSelected) {
                Icon(
                    imageVector = if (sortOrder == SortOrder.ASCENDING)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = sortOrder.name
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    speciesUiState: SpeciesUiState,
    availableGenders: List<String>,
    currentFilter: CharacterFilter,
    onSpeciesToggled: (Species) -> Unit,
    onGenderToggled: (String) -> Unit,
    onSortChanged: (SortField, SortOrder) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding().verticalScroll(scrollState),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filters", style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = onClear) { Text("Clear all") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Gender", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                availableGenders.forEach { gender ->
                    FilterChip(
                        selected = gender in currentFilter.selectedGenders,
                        onClick = { onGenderToggled(gender) },
                        label = { Text(gender.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Species", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            when (speciesUiState) {
                is SpeciesUiState.Loading -> {
                    Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is SpeciesUiState.Success -> {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        speciesUiState.species.forEach { species ->
                            FilterChip(
                                selected = species in currentFilter.selectedSpecies,
                                onClick = { onSpeciesToggled(species) },
                                label = { Text(species.name) }
                            )
                        }
                    }
                }
                is SpeciesUiState.Error -> {
                    Text(speciesUiState.message, color = MaterialTheme.colorScheme.error)
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onApply, modifier = Modifier.fillMaxWidth()) {
                Text("Apply filters")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}