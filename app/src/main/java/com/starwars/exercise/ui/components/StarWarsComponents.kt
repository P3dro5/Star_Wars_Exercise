package com.starwars.exercise.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.starwars.exercise.R
import com.starwars.exercise.ui.theme.StarWarsTheme

@Composable
fun SectionHeader(modifier: Modifier = Modifier, title: String) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun CharacterImage(name: String, image: String) {
    AsyncImage(
        model = image,
        placeholder = painterResource(R.drawable.darth_vader),
        error = painterResource(R.drawable.darth_vader),
        contentDescription = name,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape),
    )
}

@Composable
fun PersonListItem(
    name: String,
    image: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick).border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
        colors = CardColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onPrimary, disabledContentColor = MaterialTheme.colorScheme.onPrimary, disabledContainerColor = MaterialTheme.colorScheme.onPrimary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
                CharacterImage(name = name, image = image)
                Spacer(modifier = Modifier.padding(start = 10.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
    }
}

@Preview(showBackground = true, name = "Person List Item")
@Composable
fun PersonListItemPreview() {
    StarWarsTheme {
        PersonListItem(
            name = "Luke Skywalker",
            image = "",
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Section Header")
@Composable
fun SectionHeaderPreview() {
    StarWarsTheme {
        SectionHeader(title = "Section Title")
    }
}

@Preview(showBackground = true, name = "Character Image")
@Composable
fun CharacterImagePreview() {
    StarWarsTheme {
        CharacterImage(name = "Luke Skywalker", image = "")
    }
}
