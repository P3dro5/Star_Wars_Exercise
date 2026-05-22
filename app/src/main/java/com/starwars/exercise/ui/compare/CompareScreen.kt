package com.starwars.exercise.ui.compare

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.starwars.exercise.R
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.ui.theme.StarWarsTheme

@Composable
fun CompareScreen(
    onBack: () -> Unit,
    onCompareResult: (Int, Int) -> Unit,
    viewModel: CompareViewModel = hiltViewModel()
) {
    val selectedFirst by viewModel.selectedFirst.collectAsStateWithLifecycle()
    val selectedSecond by viewModel.selectedSecond.collectAsStateWithLifecycle()
    val selectingSlot by viewModel.selectingSlot.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filteredCharacters by viewModel.filteredCharacters.collectAsStateWithLifecycle()

    // show character picker sheet when a slot is tapped
    if (selectingSlot != null) {
        CharacterPickerSheet(
            searchQuery = searchQuery,
            characters = filteredCharacters,
            onSearchChanged = { viewModel.onSearchQueryChanged(it) },
            onCharacterPicked = { viewModel.onCharacterPicked(it) },
            onDismiss = { viewModel.onPickerDismissed() }
        )
    }

    CompareScreenContent(
        selectedFirst = selectedFirst,
        selectedSecond = selectedSecond,
        onSlotTapped = { viewModel.onSlotTapped(it) },
        onClearFirst = { viewModel.clearFirst() },
        onClearSecond = { viewModel.clearSecond() },
        onCompare = { id1, id2 ->
            onCompareResult.invoke(id1, id2)
                    },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreenContent(
    selectedFirst: Person?,
    selectedSecond: Person?,
    onSlotTapped: (Int) -> Unit,
    onClearFirst: () -> Unit,
    onClearSecond: () -> Unit,
    onCompare: (Int, Int) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compare Characters") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select two characters to compare",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // two selector cards side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CharacterSlotCard(
                    person = selectedFirst,
                    label = "Hero 1",
                    onTap = { onSlotTapped.invoke(1) },
                    onClear = { onClearFirst.invoke() },
                    modifier = Modifier.weight(1f)
                )
                CharacterSlotCard(
                    person = selectedSecond,
                    label = "Hero 2",
                    onTap = { onSlotTapped.invoke(2) },
                    onClear = { onClearSecond.invoke() },
                    modifier = Modifier.weight(1f)
                )
            }

            // VS divider
            if (selectedFirst != null || selectedSecond != null) {
                Text(
                    text = "VS",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val first = selectedFirst ?: return@Button
                    val second = selectedSecond ?: return@Button
                    onCompare.invoke(first.id, second.id)
                },
                enabled = selectedFirst != null && selectedSecond != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Compare")
            }
        }
    }
}

@Composable
fun CharacterSlotCard(
    person: Person?,
    label: String,
    onTap: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(0.75f)
            .clickable { onTap() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 2.dp,
            color = if (person != null)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (person != null)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (person == null) {
                // empty state — + icon
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add hero",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // filled state character info
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = person.image,
                        contentDescription = person.name,
                        placeholder = painterResource(R.drawable.darth_vader),
                        error = painterResource(R.drawable.darth_vader),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = person.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // clear button top right
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
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

private val fakePersonTwo = Person(
    id = 4,
    name = "Darth Vader",
    image = "",
    birthYear = "41.9BBY",
    gender = "male",
    homeworld = "Tatooine",
    species = "Human",
    height = "202",
    mass = "136",
    hairColor = "none",
    skinColor = "white",
    eyeColor = "yellow",
    filmCount = 4,
    starshipIds = listOf(13)
)

@Preview(showBackground = true, name = "Compare - Empty")
@Composable
fun CompareScreenEmptyPreview() {
    StarWarsTheme {
        CompareScreenContent(
            selectedFirst = null,
            selectedSecond = null,
            onSlotTapped = {},
            onClearFirst = {},
            onClearSecond = {},
            onCompare = {_, _ ->},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Compare - One Selected")
@Composable
fun CompareScreenOneSelectedPreview() {
    StarWarsTheme {
        CompareScreenContent(
            selectedFirst = fakePerson,
            selectedSecond = null,
            onSlotTapped = {},
            onClearFirst = {},
            onClearSecond = {},
            onCompare = {_,_ ->},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Compare - Both Selected")
@Composable
fun CompareScreenBothSelectedPreview() {
    StarWarsTheme {
        CompareScreenContent(
            selectedFirst = fakePerson,
            selectedSecond = fakePersonTwo,
            onSlotTapped = {},
            onClearFirst = {},
            onClearSecond = {},
            onCompare = {_,_ ->},
            onBack = {}
        )
    }
}

@Preview
@Composable
fun CharacterSlotCardPreview() {
    CharacterSlotCard(
        person = fakePerson,
        label = "Hero 1",
        onTap = {},
        onClear = {}
    )
}

@Preview
@Composable
fun CharacterSlotCardEmptyPreview() {
    CharacterSlotCard(
        person = null,
        label = "Hero 1",
        onTap = {},
        onClear = {}
    )
}

@Preview
@Composable
fun CompareScreenPreview(){
    CompareScreen(
        onBack = {},
        onCompareResult = { _, _ -> }
    )
}