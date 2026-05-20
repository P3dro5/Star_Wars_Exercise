package com.starwars.exercise.ui.galaxy

import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.starwars.exercise.R
import com.starwars.exercise.domain.model.GalaxyPosition
import com.starwars.exercise.domain.model.Planet
import com.starwars.exercise.ui.components.ErrorState
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalaxyMapScreen(
    onBack: () -> Unit,
    viewModel: GalaxyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedPlanet by viewModel.selectedPlanet.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Galaxy Map") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is GalaxyUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is GalaxyUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        actionLabel = "Retry",
                        onRetry = { viewModel.loadPlanets() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is GalaxyUiState.Success -> {
                    GalaxyMap(
                        planets = state.planets,
                        selectedPlanet = selectedPlanet,
                        onPlanetSelected = { viewModel.onPlanetSelected(it) }
                    )
                    selectedPlanet?.let { planet ->
                        PlanetInfoCard(
                            planet = planet,
                            onDismiss = { viewModel.onPlanetSelected(null) },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GalaxyMap(
    planets: List<Planet>,
    selectedPlanet: Planet?,
    onPlanetSelected: (Planet?) -> Unit
) {
    var imageView by remember { mutableStateOf<SubsamplingScaleImageView?>(null) }
    var isReady by remember { mutableStateOf(false) }
    // trigger recomposition on pan/zoom
    var stateVersion by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                SubsamplingScaleImageView(ctx).apply {
                    setImage(ImageSource.resource(R.drawable.galaxy_map))
                    setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE)
                    maxScale = 8f
                    setOnImageEventListener(object :
                        SubsamplingScaleImageView.OnImageEventListener {
                        override fun onReady() {
                            isReady = true
                        }
                        override fun onImageLoaded() {}
                        override fun onPreviewLoadError(e: Exception) {}
                        override fun onImageLoadError(e: Exception) {}
                        override fun onTileLoadError(e: Exception) {}
                        override fun onPreviewReleased() {}
                    })
                    setOnStateChangedListener(object :
                        SubsamplingScaleImageView.OnStateChangedListener {
                        override fun onScaleChanged(newScale: Float, origin: Int) {
                            stateVersion++  // trigger recomposition so markers follow zoom
                        }
                        override fun onCenterChanged(newCenter: PointF?, origin: Int) {
                            stateVersion++  // trigger recomposition so markers follow pan
                        }
                    })
                    imageView = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // only draw markers once image is ready and view is available
        if (isReady) {
            // stateVersion read here so recomposition triggers on pan/zoom
            val state = stateVersion
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(planets) {
                        detectTapGestures { tapOffset ->
                            val tapped = planets.firstOrNull { planet ->
                                val iv = imageView ?: return@detectTapGestures
                                val imageX = planet.galaxyPosition.x * (iv.sWidth)
                                val imageY = planet.galaxyPosition.y * (iv.sHeight)
                                // use library's own conversion
                                val screenPoint = iv.sourceToViewCoord(imageX, imageY)
                                    ?: return@firstOrNull false
                                val distance = kotlin.math.sqrt(
                                    (tapOffset.x - screenPoint.x).pow(2) +
                                            (tapOffset.y - screenPoint.y).pow(2)
                                )
                                distance < 40f
                            }
                            onPlanetSelected(tapped)
                        }
                    }
            ) {
                planets.forEach { planet ->
                    val iv = imageView ?: return@Canvas
                    val imageX = planet.galaxyPosition.x * iv.sWidth
                    val imageY = planet.galaxyPosition.y * iv.sHeight

                    // use library's own sourceToViewCoord for accurate mapping
                    val screenPoint = iv.sourceToViewCoord(imageX, imageY) ?: return@forEach

                    val isSelected = planet.id == selectedPlanet?.id
                    val dotRadius = if (isSelected) 14f else 8f
                    val dotColor = if (isSelected)
                        Color(0xFFFFD700)  // gold
                    else
                        Color(0xFF4FC3F7)  // light blue

                    // glow for selected
                    if (isSelected) {
                        drawCircle(
                            color = dotColor.copy(alpha = 0.3f),
                            radius = 28f,
                            center = Offset(screenPoint.x, screenPoint.y)
                        )
                    }

                    drawCircle(
                        color = dotColor,
                        radius = dotRadius,
                        center = Offset(screenPoint.x, screenPoint.y)
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        planet.name,
                        screenPoint.x + 16f,
                        screenPoint.y + 5f,
                        android.graphics.Paint().apply {
                            textSize = if (isSelected) 32f else 24f
                            color = if (isSelected)
                                android.graphics.Color.parseColor("#FFD700")
                            else
                                android.graphics.Color.WHITE
                            setShadowLayer(4f, 0f, 0f, android.graphics.Color.BLACK)
                            isFakeBoldText = isSelected
                        }
                    )
                }
            }
        }
    }
}

// converts galaxy relative position (0f..1f) to screen pixel coordinates
private fun GalaxyPosition.toScreenCoords(
    viewW: Float, viewH: Float,
    imageW: Float, imageH: Float,
    scale: Float, centerX: Float, centerY: Float
): Pair<Float, Float> {
    val imageX = x * imageW
    val imageY = y * imageH
    val screenX = (imageX - centerX) * scale + viewW / 2f
    val screenY = (imageY - centerY) * scale + viewH / 2f
    return Pair(screenX, screenY)
}

@Composable
fun PlanetInfoCard(
    planet: Planet,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = planet.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Dismiss")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            PlanetInfoRow("Climate", planet.climate)
            PlanetInfoRow("Terrain", planet.terrain)
            PlanetInfoRow("Population", planet.population)
            PlanetInfoRow("Diameter", planet.diameter)
            PlanetInfoRow("Gravity", planet.gravity)
            PlanetInfoRow("Orbital Period", "${planet.orbitalPeriod} days")
            PlanetInfoRow("Rotation Period", "${planet.rotationPeriod} hours")
        }
    }
}

@Composable
fun PlanetInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}