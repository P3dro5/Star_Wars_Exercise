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
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.material3.IconButtonColors
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
import androidx.compose.ui.tooling.preview.Preview
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
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.domain.model.Planet
import com.starwars.exercise.domain.model.SearchResult
import com.starwars.exercise.domain.model.SortField
import com.starwars.exercise.domain.model.SortOrder
import com.starwars.exercise.domain.model.Species
import com.starwars.exercise.domain.model.Starship
import com.starwars.exercise.domain.model.availableGenders
import com.starwars.exercise.ui.theme.StarWarsTheme

@Composable
fun HomeScreen(
    onMenu: () -> Unit,
    onCharacterSelected: (Int) -> Unit,
    onGalaxyMap: () -> Unit,
    onCompareCharacters: () -> Unit,
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

    HomeScreenContent(
        uiState = uiState,
        searchQuery = searchQuery,
        searchState = searchState,
        isSearching = isSearching,
        filter = filter,
        onSearchValueChanged = { viewModel.onSearchQueryChanged(it) },
        onMenu = { onMenu.invoke() },
        onCharacterSelected = { onCharacterSelected.invoke(it) },
        onSortNameSelected = {
            val newOrder = if (filter.sortField == SortField.NAME) {
                if (filter.sortOrder == SortOrder.ASCENDING) SortOrder.DESCENDING
                else SortOrder.ASCENDING
            } else SortOrder.ASCENDING
            viewModel.onSortChanged(SortField.NAME, newOrder)
            viewModel.applyFilters()
        },
        onSortYearSelected = {
            val newOrder = if (filter.sortField == SortField.YEAR) {
                if (filter.sortOrder == SortOrder.ASCENDING) SortOrder.DESCENDING
                else SortOrder.ASCENDING
            } else SortOrder.ASCENDING
            viewModel.onSortChanged(SortField.YEAR, newOrder)
            viewModel.applyFilters()
        },
        onFilterSelected = {
            showFilterSheet = true
        },
        onRetryCharacters = { viewModel.loadCharacters() },
        onGalaxyMap = { onGalaxyMap.invoke() },
        onCompareCharacters = { onCompareCharacters.invoke() }

    )

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    searchQuery: String,
    searchState: SearchUiState,
    isSearching: Boolean,
    filter: CharacterFilter,
    onSearchValueChanged: (String) -> Unit,
    onMenu: () -> Unit,
    onCharacterSelected: (Int) -> Unit,
    onSortNameSelected: () -> Unit,
    onSortYearSelected: () -> Unit,
    onFilterSelected: () -> Unit,
    onRetryCharacters: () -> Unit,
    onGalaxyMap: () -> Unit,
    onCompareCharacters: () -> Unit,
)
{
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { onSearchValueChanged.invoke(it) },
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
                                IconButton(onClick = { onSearchValueChanged.invoke("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMenu, colors = IconButtonColors(contentColor = MaterialTheme.colorScheme.primary, containerColor = Color.Transparent, disabledContentColor = Color.Transparent, disabledContainerColor = Color.Transparent)) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { onGalaxyMap.invoke() },  colors = IconButtonColors(contentColor = MaterialTheme.colorScheme.primary, containerColor = Color.Transparent, disabledContentColor = Color.Transparent, disabledContainerColor = Color.Transparent)) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Galaxy Map")
                    }
                    IconButton(onClick = { onCompareCharacters.invoke() } , colors = IconButtonColors(contentColor = MaterialTheme.colorScheme.primary, containerColor = Color.Transparent, disabledContentColor = Color.Transparent, disabledContainerColor = Color.Transparent)) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Compare")
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
                            onSortNameSelected.invoke()
                        }
                    )
                    SortChip(
                        label = "Year",
                        isSelected = filter.sortField == SortField.YEAR,
                        sortOrder = filter.sortOrder,
                        onClick = {
                            onSortYearSelected.invoke()
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    BadgedBox(
                        badge = {
                            val count = filter.selectedSpecies.size + filter.selectedGenders.size
                            if (count > 0) Badge { Text("$count") }
                        }
                    ) {
                        IconButton(onClick = {
                            onFilterSelected.invoke()
                                             },  colors = IconButtonColors(contentColor = MaterialTheme.colorScheme.primary, containerColor = Color.Transparent, disabledContentColor = Color.Transparent, disabledContainerColor = Color.Transparent)) {
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
                        val characters = uiState
                            .characters.collectAsLazyPagingItems()
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(
                                count = characters.itemCount,
                                key = characters.itemKey { it.id }
                            ) { index ->
                                val person = characters[index]
                                PersonListItem(
                                    name = person?.name ?: "",
                                    image = person?.image ?: "",
                                    onClick = { onCharacterSelected.invoke(person?.id ?: 0) }
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
                        message = uiState.message,
                        actionLabel = "Retry",
                        onRetry = {
                            onRetryCharacters.invoke()
                                  },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

private val fakePerson = Person(
    id = 1,
    name = "Luke Skywalker",
    image = "",
    birthYear = "19BBY",
    gender = "male",
    homeworld = "Tatooine",
    species = "Human",
    height = "172",
    mass = "77",
    hairColor = "blond",
    skinColor = "fair",
    eyeColor = "blue",
    filmCount = 4,
    starshipIds = listOf(12, 22)
)

@Preview(showBackground = true, name = "Home Light Mode - Loading")
@Composable
fun HomeScreenLoadingLightPreview() {
    StarWarsTheme(darkTheme = false) {
        HomeScreenContent(
            uiState = HomeUiState.Loading,
            searchQuery = "",
            searchState = SearchUiState.Idle,
            isSearching = false,
            filter = CharacterFilter(),
            onSearchValueChanged = {},
            onMenu = {},
            onCharacterSelected = {},
            onSortNameSelected = {},
            onSortYearSelected = {},
            onFilterSelected = {},
            onRetryCharacters = {},
            onGalaxyMap = {},
            onCompareCharacters = {}
        )
    }
}

@Preview(showBackground = true, name = "Home Dark Mode - Loading")
@Composable
fun HomeScreenLoadingDarkPreview() {
    StarWarsTheme(darkTheme = true) {
        HomeScreenContent(
            uiState = HomeUiState.Loading,
            searchQuery = "",
            searchState = SearchUiState.Idle,
            isSearching = false,
            filter = CharacterFilter(),
            onSearchValueChanged = {},
            onMenu = {},
            onCharacterSelected = {},
            onSortNameSelected = {},
            onSortYearSelected = {},
            onFilterSelected = {},
            onRetryCharacters = {},
            onGalaxyMap = {},
            onCompareCharacters = {}
        )
    }
}

@Preview(showBackground = true, name = "Home Light Mode - Error")
@Composable
fun HomeScreenErrorLightPreview() {
    StarWarsTheme(darkTheme = false) {
        HomeScreenContent(
            uiState = HomeUiState.Error("Failed to load characters"),
            searchQuery = "",
            searchState = SearchUiState.Idle,
            isSearching = false,
            filter = CharacterFilter(),
            onSearchValueChanged = {},
            onMenu = {},
            onCharacterSelected = {},
            onSortNameSelected = {},
            onSortYearSelected = {},
            onFilterSelected = {},
            onRetryCharacters = {},
            onGalaxyMap = {},
            onCompareCharacters = {}
        )
    }
}

@Preview(showBackground = true, name = "Home Dark Mode - Error")
@Composable
fun HomeScreenErrorDarkPreview() {
    StarWarsTheme(darkTheme = true) {
        HomeScreenContent(
            uiState = HomeUiState.Error("Failed to load characters"),
            searchQuery = "",
            searchState = SearchUiState.Idle,
            isSearching = false,
            filter = CharacterFilter(),
            onSearchValueChanged = {},
            onMenu = {},
            onCharacterSelected = {},
            onSortNameSelected = {},
            onSortYearSelected = {},
            onFilterSelected = {},
            onRetryCharacters = {},
            onGalaxyMap = {},
            onCompareCharacters = {}
        )
    }
}


@Preview(showBackground = true, name = "Home Light Mode - Search Results")
@Composable
fun HomeScreenSearchPreview() {
    StarWarsTheme(darkTheme = false) {
        HomeScreenContent(
            uiState = HomeUiState.Loading,
            searchQuery = "Luke",
            searchState = SearchUiState.Success(
                listOf(
                    SearchResult.CharacterResult(fakePerson),
                    SearchResult.StarshipResult(
                        Starship(1, "X-Wing", "T-65", "Incom", "Starfighter", "1", "0", "149999", "12.5")
                    ),
                    SearchResult.PlanetResult(
                        Planet(1, "Tatooine", "arid", "desert", "200000", "1 standard", "10465", "304", "23", emptyList())
                    )
                )
            ),
            isSearching = true,
            filter = CharacterFilter(),
            onMenu = {},
            onSearchValueChanged = {},
            onCharacterSelected = {},
            onSortNameSelected = {},
            onSortYearSelected = {},
            onFilterSelected = {},
            onRetryCharacters = {},
            onGalaxyMap = {},
            onCompareCharacters = {}
        )
    }
}

@Preview(showBackground = true, name = "Home Dark Mode- Search Results")
@Composable
fun HomeScreenSearchDarkPreview() {
    StarWarsTheme(darkTheme = true) {
        HomeScreenContent(
            uiState = HomeUiState.Loading,
            searchQuery = "Luke",
            searchState = SearchUiState.Success(
                listOf(
                    SearchResult.CharacterResult(fakePerson),
                    SearchResult.StarshipResult(
                        Starship(1, "X-Wing", "T-65", "Incom", "Starfighter", "1", "0", "149999", "12.5")
                    ),
                    SearchResult.PlanetResult(
                        Planet(1, "Tatooine", "arid", "desert", "200000", "1 standard", "10465", "304", "23", emptyList())
                    )
                )
            ),
            isSearching = true,
            filter = CharacterFilter(),
            onMenu = {},
            onSearchValueChanged = {},
            onCharacterSelected = {},
            onSortNameSelected = {},
            onSortYearSelected = {},
            onFilterSelected = {},
            onRetryCharacters = {},
            onGalaxyMap = {},
            onCompareCharacters = {}
        )
    }
}



@Preview(showBackground = true, name = "Sort Chip - Unselected")
@Composable
fun SortChipUnselectedPreview() {
    StarWarsTheme {
        SortChip(
            label = "Name",
            isSelected = false,
            sortOrder = SortOrder.ASCENDING,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Sort Chip - Ascending")
@Composable
fun SortChipAscendingPreview() {
    StarWarsTheme {
        SortChip(
            label = "Name",
            isSelected = true,
            sortOrder = SortOrder.ASCENDING,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Sort Chip - Descending")
@Composable
fun SortChipDescendingPreview() {
    StarWarsTheme {
        SortChip(
            label = "Year",
            isSelected = true,
            sortOrder = SortOrder.DESCENDING,
            onClick = {}
        )
    }
}