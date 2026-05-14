package com.starwars.exercise.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.starwars.exercise.R
import com.starwars.exercise.ui.components.ErrorState
import com.starwars.exercise.ui.components.LoadingState
import com.starwars.exercise.ui.components.PersonListItem
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMenu: () -> Unit,
    onCharacterSelected: (Int) -> Unit,
    onCompare: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Character roster") },
                navigationIcon = {
                    IconButton(onClick = onMenu) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCompare) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Compare"
                        )
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
            when (uiState) {
                HomeUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
                is HomeUiState.Success -> {
                    val characters = (uiState as HomeUiState.Success).characters.collectAsLazyPagingItems()

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(count = characters.itemCount, key = characters.itemKey { it.id }) { index ->
                                val person = characters[index]
                                PersonListItem(
                                    id = person?.id ?: 0,
                                    name = person?.name ?: "",
                                    subtitle = "${person?.species} • ${person?.homeworld}",
                                    onClick = { onCharacterSelected(person?.id ?: 0) },
                                    image = person?.image ?: ""
                                )
                            }

                        when (characters.loadState.append) {
                            is LoadState.Loading -> item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
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
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            is LoadState.Error -> item {
                                ErrorState(
                                    message = "Failed to load more",
                                    actionLabel = "Retry",
                                    onRetry = { characters.retry() },
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
