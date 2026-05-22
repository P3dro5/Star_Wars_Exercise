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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.ui.components.ErrorState
import com.starwars.exercise.ui.components.LoadingState
import com.starwars.exercise.ui.components.SectionHeader
import com.starwars.exercise.ui.theme.StarWarsTheme

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

    CharacterDetailContent(
        uiState = uiState,
        onBack = onBack,
        onProfile = onProfile,
        onRetry = { viewModel.loadDetail(personId) }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailContent(
    uiState: DetailUiState,
    onBack: () -> Unit,
    onProfile: (Int) -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (uiState is DetailUiState.Success) {
                        Text(uiState.person.name)
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
                message = uiState.message,
                actionLabel = "Retry",
                onRetry = { onRetry.invoke() },
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            )
            is DetailUiState.Success -> {
                val person = uiState.person
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

@Preview(showBackground = true, name = "Detail Light Mode - Loading")
@Composable
fun CharacterDetailLoadingLightPreview() {
    StarWarsTheme(darkTheme = false) {
        CharacterDetailContent(
            uiState = DetailUiState.Loading,
            onBack = {},
            onProfile = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail Dark Mode - Loading")
@Composable
fun CharacterDetailLoadingDarkPreview() {
    StarWarsTheme(darkTheme = true) {
        CharacterDetailContent(
            uiState = DetailUiState.Loading,
            onBack = {},
            onProfile = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail Light Mode - Success")
@Composable
fun CharacterDetailSuccessLightPreview() {
    StarWarsTheme(darkTheme = false) {
        CharacterDetailContent(
            uiState = DetailUiState.Success(fakePerson),
            onBack = {},
            onProfile = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail Dark Mode - Success")
@Composable
fun CharacterDetailSuccessPreview() {
    StarWarsTheme(darkTheme = true) {
        CharacterDetailContent(
            uiState = DetailUiState.Success(fakePerson),
            onBack = {},
            onProfile = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail Light Mode - Error")
@Composable
fun CharacterDetailErrorLightPreview() {
    StarWarsTheme(darkTheme = false) {
        CharacterDetailContent(
            uiState = DetailUiState.Error("Unable to load character"),
            onBack = {},
            onProfile = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true, name = "Detail - Error")
@Composable
fun CharacterDetailErrorDarkPreview() {
    StarWarsTheme(darkTheme = true) {
        CharacterDetailContent(
            uiState = DetailUiState.Error("Unable to load character"),
            onBack = {},
            onProfile = {},
            onRetry = {}
        )
    }
}