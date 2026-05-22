package com.starwars.exercise.ui.galaxy

import android.graphics.Paint
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.starwars.exercise.R
import com.starwars.exercise.domain.model.Planet
import com.starwars.exercise.ui.components.ErrorState
import kotlin.math.pow
import androidx.core.graphics.toColorInt
import com.starwars.exercise.domain.model.GalaxyPosition
import kotlin.math.sqrt

@Composable
fun GalaxyMapScreen(
    onBack: () -> Unit,
    viewModel: GalaxyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedPlanet by viewModel.selectedPlanet.collectAsStateWithLifecycle()

    GalaxyMapContent(
        uiState = uiState,
        selectedPlanet = selectedPlanet,
        onBack = onBack,
        onRetry = { viewModel.loadPlanets() },
        onPlanetSelected = { viewModel.onPlanetSelected(it) },
        onZoomIn = { viewModel.onZoomIn() },
        onZoomOut = { viewModel.onZoomOut() },
        onZoomReset = { viewModel.onZoomReset() },
        onDismiss = { viewModel.onPlanetSelected(null) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalaxyMapContent(
    uiState: GalaxyUiState,
    selectedPlanet: Planet?,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onPlanetSelected: (Planet?) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onZoomReset: () -> Unit,
    onDismiss: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Galaxy Map") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            when (uiState) {
                is GalaxyUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is GalaxyUiState.Error -> {
                    ErrorState(
                        message = uiState.message,
                        actionLabel = "Retry",
                        onRetry = { onRetry.invoke() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is GalaxyUiState.Success -> {
                    GalaxyMap(
                        planets = uiState.planets,
                        selectedPlanet = selectedPlanet,
                        onPlanetSelected = { onPlanetSelected.invoke(it) }
                    )

                    // zoom controls — bottom right
                    ZoomControls(
                        onZoomIn = { onZoomIn.invoke() },
                        onZoomOut = { onZoomOut.invoke() },
                        onReset = { onZoomReset.invoke() },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    )

                    selectedPlanet?.let { planet ->
                        PlanetInfoCard(
                            planet = planet,
                            onDismiss = { onDismiss.invoke() },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(start = 16.dp, end = 80.dp, bottom = 16.dp)
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
    onPlanetSelected: (Planet?) -> Unit,
    viewModel: GalaxyViewModel = hiltViewModel()
) {
    var imageView by remember { mutableStateOf<SubsamplingScaleImageView?>(null) }
    var isReady by remember { mutableStateOf(false) }
    var stateVersion by remember { mutableIntStateOf(0) }

    // consume zoom commands from ViewModel
    LaunchedEffect(Unit) {
        viewModel.zoomCommand.collect { command ->
            val iv = imageView ?: return@collect
            when (command) {
                ZoomCommand.ZoomIn -> {
                    val newScale = (iv.scale * 1.5f).coerceAtMost(iv.maxScale)
                    iv.animatingScale(newScale)
                }
                ZoomCommand.ZoomOut -> {
                    val newScale = (iv.scale / 1.5f).coerceAtLeast(iv.minScale)
                    iv.animatingScale(newScale)
                }
                ZoomCommand.Reset -> {
                    iv.animateScaleAndCenter(iv.minScale, PointF(iv.sWidth / 2f, iv.sHeight / 2f))?.start()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                SubsamplingScaleImageView(ctx).apply {
                    setImage(ImageSource.resource(R.drawable.galaxy_map))
                    setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE)
                    maxScale = 8f
                    setOnImageEventListener(object :
                        SubsamplingScaleImageView.OnImageEventListener {
                        override fun onReady() { isReady = true }
                        override fun onImageLoaded() {}
                        override fun onPreviewLoadError(e: Exception) {}
                        override fun onImageLoadError(e: Exception) {}
                        override fun onTileLoadError(e: Exception) {}
                        override fun onPreviewReleased() {}
                    })
                    setOnStateChangedListener(object :
                        SubsamplingScaleImageView.OnStateChangedListener {
                        override fun onScaleChanged(newScale: Float, origin: Int) {
                            stateVersion++
                        }
                        override fun onCenterChanged(newCenter: PointF?, origin: Int) {
                            stateVersion++
                        }
                    })
                    imageView = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isReady) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(planets) {
                        detectTapGestures { tapOffset ->
                            val tapped = planets.firstOrNull { planet ->
                                val iv = imageView ?: return@detectTapGestures
                                val imageX = planet.galaxyPosition.x * iv.sWidth
                                val imageY = planet.galaxyPosition.y * iv.sHeight
                                val screenPoint = iv.sourceToViewCoord(imageX, imageY)
                                    ?: return@firstOrNull false
                                val distance = sqrt(
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
                    val screenPoint = iv.sourceToViewCoord(imageX, imageY) ?: return@forEach

                    val isSelected = planet.id == selectedPlanet?.id
                    val dotColor = if (isSelected) Color(0xFFFFD700) else Color(0xFF4FC3F7)
                    val dotRadius = if (isSelected) 14f else 8f

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
                        Paint().apply {
                            textSize = if (isSelected) 32f else 24f
                            color = if (isSelected)
                                "#FFD700".toColorInt()
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

@Composable
fun ZoomControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        )
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            IconButton(onClick = onZoomIn) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Zoom in",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            HorizontalDivider(modifier = Modifier.width(32.dp))
            IconButton(onClick = onZoomOut) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Zoom out",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            HorizontalDivider(modifier = Modifier.width(32.dp))
            IconButton(onClick = onReset) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset zoom",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun SubsamplingScaleImageView.animatingScale(targetScale: Float) {
    val center = center ?: return
    animateScaleAndCenter(targetScale, center)?.start()
}

@Preview(showBackground = true, name = "Galaxy Map")
@Composable
fun GalaxyMapPreview() {
    GalaxyMapContent(
        uiState = GalaxyUiState.Success(listOf(
            Planet(
                id = 1, name = "Tatooine", galaxyPosition = GalaxyPosition(0.72f, 0.78f),
                climate = "",
                terrain = "",
                population = "",
                gravity = "",
                diameter = "",
                orbitalPeriod = "",
                rotationPeriod = "",
                residentIds = listOf(1)
            )
        )),
        selectedPlanet = null,
        onBack = {},
        onRetry = {},
        onPlanetSelected = {},
        onZoomIn = {},
        onZoomOut = {},
        onZoomReset = {},
        onDismiss = {}
    )
}