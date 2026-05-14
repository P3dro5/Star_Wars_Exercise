package com.starwars.exercise.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun SectionHeader(modifier: Modifier = Modifier, title: String, subtitle: String? = null) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        subtitle?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun CharacterImage(name: String, characterId: Int, image: String) {
    AsyncImage(
        model = image,
        contentDescription = name,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun PersonListItem(name: String, id: Int, image: String, subtitle: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                CharacterImage(name = name, characterId = id, image = image)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
        }
    }
}
