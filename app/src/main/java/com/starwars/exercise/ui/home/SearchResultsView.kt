package com.starwars.exercise.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.starwars.exercise.domain.model.SearchResult
import com.starwars.exercise.ui.components.ErrorState
import com.starwars.exercise.ui.components.LoadingState
import com.starwars.exercise.ui.components.PersonListItem

@Composable
fun SearchResultsView(
    searchState: SearchUiState,
    onCharacterSelected: (Int) -> Unit
) {
    when (searchState) {
        is SearchUiState.Idle -> Unit
        is SearchUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
        is SearchUiState.Error -> ErrorState(
            message = searchState.message,
            actionLabel = "Retry",
            onRetry = {},
            modifier = Modifier.fillMaxSize()
        )
        is SearchUiState.Success -> {
            if (searchState.results.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No results found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            } else {
                val characters = searchState.results.filterIsInstance<SearchResult.CharacterResult>()
                val ships = searchState.results.filterIsInstance<SearchResult.StarshipResult>()
                val planets = searchState.results.filterIsInstance<SearchResult.PlanetResult>()

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Characters section
                    if (characters.isNotEmpty()) {
                        item {
                            SearchSectionHeader(
                                title = "Characters",
                                count = characters.size
                            )
                        }
                        items(characters, key = { "character_${it.person.id}" }) { result ->
                            PersonListItem(
                                name = result.person.name,
                                id = result.person.id,
                                image = result.person.image,
                                onClick = { onCharacterSelected(result.person.id) }
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    // Ships section
                    if (ships.isNotEmpty()) {
                        item {
                            SearchSectionHeader(
                                title = "Starships",
                                count = ships.size
                            )
                        }
                        items(ships, key = { "ship_${it.starship.id}" }) { result ->
                            SearchListItem(
                                name = result.starship.name,
                                subtitle = "${result.starship.model} • ${result.starship.starshipClass}",
                                icon = Icons.AutoMirrored.Default.ArrowForward
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    // Planets section
                    if (planets.isNotEmpty()) {
                        item {
                            SearchSectionHeader(
                                title = "Planets",
                                count = planets.size
                            )
                        }
                        items(planets, key = { "planet_${it.planet.id}" }) { result ->
                            SearchListItem(
                                name = result.planet.name,
                                subtitle = "${result.planet.terrain} • ${result.planet.climate}",
                                icon = Icons.Default.Search
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchSectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$count results",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun SearchListItem(
    name: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}