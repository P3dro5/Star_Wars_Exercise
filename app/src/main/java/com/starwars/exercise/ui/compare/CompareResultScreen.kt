package com.starwars.exercise.ui.compare

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.starwars.exercise.R
import com.starwars.exercise.core.Resource
import com.starwars.exercise.domain.model.Person
import com.starwars.exercise.ui.components.ErrorState
import com.starwars.exercise.ui.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareResultScreen(
    firstId: Int,
    secondId: Int,
    onBack: () -> Unit,
    viewModel: CompareViewModel = hiltViewModel()
) {
    val resultState = remember {
        mutableStateOf<Resource<Pair<Person, Person>>>(Resource.Loading)
    }

    LaunchedEffect(firstId, secondId) {
        resultState.value = Resource.Loading
        resultState.value = viewModel.compareCharacters(firstId, secondId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compare Result") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val result = resultState.value) {
            is Resource.Loading -> LoadingState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
            is Resource.Error -> ErrorState(
                message = result.message,
                actionLabel = "Back",
                onRetry = onBack,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
            is Resource.Success -> {
                val (first, second) = result.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // hero portraits
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HeroPortrait(person = first)
                        Text(
                            text = "VS",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        HeroPortrait(person = second)
                    }

                    HorizontalDivider()

                    // quick stats comparison
                    CompareStatRow("Height", first.height, second.height)
                    CompareStatRow("Mass", first.mass, second.mass)
                    CompareStatRow("Birth Year", first.birthYear, second.birthYear)
                    CompareStatRow("Gender", first.gender, second.gender)
                    CompareStatRow("Species", first.species, second.species)
                    CompareStatRow("Homeworld", first.homeworld, second.homeworld)
                    CompareStatRow("Films", first.filmCount.toString(), second.filmCount.toString())

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun HeroPortrait(person: Person) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = person.image,
            contentDescription = person.name,
            placeholder = painterResource(R.drawable.darth_vader),
            error = painterResource(R.drawable.darth_vader),
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentScale = ContentScale.Crop
        )
        Text(
            text = person.name,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
fun CompareStatRow(label: String, firstValue: String, secondValue: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = firstValue,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = secondValue,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )
    }
}