package plat.lab7.hernandez_maldonado.location.ui.locationdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import plat.lab7.hernandez_maldonado.core.ui.theme.RickAndMortyAppTheme
import plat.lab7.hernandez_maldonado.location.data.Location
import plat.lab7.hernandez_maldonado.core.ui.components.ErrorContent
import plat.lab7.hernandez_maldonado.core.ui.components.LoadingContent

@Composable
private fun LocationDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = value,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailsScreen(
    state: LocationDetailsUiState,
    onBackClick: () -> Unit,
    onLoadingClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        val location = state.data

        when {
            state.isLoading -> {
                LoadingContent(
                    onLoadingClick = onLoadingClick
                )
            }

            state.hasError -> {
                ErrorContent(
                    message = "Error al obtener detalle de la ubicación.",
                    onRetryClick = onRetryClick
                )
            }

            location != null -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TopAppBar(
                        title = {
                            Text(text = "Location details")
                        },
                        modifier = Modifier.height(60.dp),
                        navigationIcon = {
                            IconButton(
                                onClick = onBackClick
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Regresar"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        Text(
                            text = location.name,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            LocationDetailRow(
                                label = "ID:",
                                value = location.id.toString()
                            )

                            LocationDetailRow(
                                label = "Type:",
                                value = location.type
                            )

                            LocationDetailRow(
                                label = "Dimension:",
                                value = location.dimension
                            )
                        }
                    }
                }
            }

            else -> {
                ErrorContent(
                    message = "No se encontró la ubicación.",
                    onRetryClick = onRetryClick
                )
            }
        }
    }
}

@Preview(
    name = "Location details",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun LocationDetailsScreenPreview() {
    val previewLocation = Location(
        id = 1,
        name = "Earth (C-137)",
        type = "Planet",
        dimension = "Dimension C-137"
    )

    RickAndMortyAppTheme {
        LocationDetailsScreen(
            state = LocationDetailsUiState(
                isLoading = false,
                data = previewLocation,
                hasError = false
            ),
            onBackClick = {},
            onLoadingClick = {},
            onRetryClick = {}
        )
    }
}