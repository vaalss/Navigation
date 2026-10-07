package plat.lab7.hernandez_maldonado.location.ui.locations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import plat.lab7.hernandez_maldonado.core.ui.theme.RickAndMortyAppTheme
import plat.lab7.hernandez_maldonado.location.data.Location
import plat.lab7.hernandez_maldonado.core.ui.components.ErrorContent
import plat.lab7.hernandez_maldonado.core.ui.components.LoadingContent

@Composable
private fun LocationItem(
    location: Location,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = location.name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = location.type,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    state: LocationsUiState,
    onLocationClick: (Int) -> Unit,
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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (!state.isLoading && !state.hasError) {
                TopAppBar(
                    title = {
                        Text(text = "Locations")
                    },
                    modifier = Modifier.height(60.dp),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }

            when {
                state.isLoading -> {
                    LoadingContent(
                        onLoadingClick = onLoadingClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                state.hasError -> {
                    ErrorContent(
                        message = "Error al obtener listado de ubicaciones.",
                        onRetryClick = onRetryClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(
                            items = state.data,
                            key = { location -> location.id }
                        ) { location ->
                            LocationItem(
                                location = location,
                                onClick = {
                                    onLocationClick(location.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Locations list",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun LocationsScreenPreview() {
    val previewLocations = listOf(
        Location(
            id = 1,
            name = "Earth (C-137)",
            type = "Planet",
            dimension = "Dimension C-137"
        ),
        Location(
            id = 2,
            name = "Abadango",
            type = "Cluster",
            dimension = "unknown"
        ),
        Location(
            id = 3,
            name = "Citadel of Ricks",
            type = "Space station",
            dimension = "unknown"
        )
    )

    RickAndMortyAppTheme {
        LocationsScreen(
            state = LocationsUiState(
                isLoading = false,
                data = previewLocations,
                hasError = false
            ),
            onLocationClick = {},
            onLoadingClick = {},
            onRetryClick = {}
        )
    }
}