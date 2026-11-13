package plat.lab7.hernandez_maldonado.location.ui.locations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import plat.lab7.hernandez_maldonado.core.ui.components.ErrorContent
import plat.lab7.hernandez_maldonado.core.ui.components.LoadingContent
import plat.lab7.hernandez_maldonado.core.ui.theme.RickAndMortyAppTheme
import plat.lab7.hernandez_maldonado.location.data.Location

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    state: LocationsUiState,
    onLocationClick: (Int) -> Unit,
    onLoadingClick: () -> Unit,
    onRetryClick: () -> Unit,
    onFavoriteClick: (Int) -> Unit = {},
    onFilterChange: (LocationFilter) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val visibleLocations = when (state.filter) {
        LocationFilter.ALL -> state.data
        LocationFilter.FAVORITES -> state.data.filter { it.isFavorite }
        LocationFilter.NON_FAVORITES -> state.data.filterNot { it.isFavorite }
    }

    Surface(
        modifier = modifier.fillMaxSize().safeDrawingPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Locations") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            if (!state.isLoading && !state.hasError) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LocationFilter.entries.forEach { filter ->
                        FilterChip(
                            selected = state.filter == filter,
                            onClick = { onFilterChange(filter) },
                            label = { Text(filter.label) }
                        )
                    }
                }
            }

            state.actionErrorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(12.dp)
                )
            }

            when {
                state.isLoading -> LoadingContent(
                    onLoadingClick = onLoadingClick,
                    modifier = Modifier.weight(1f)
                )
                state.hasError -> ErrorContent(
                    message = "Error al obtener las ubicaciones.",
                    onRetryClick = onRetryClick,
                    modifier = Modifier.weight(1f)
                )
                visibleLocations.isEmpty() -> {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            when (state.filter) {
                                LocationFilter.ALL -> "No hay ubicaciones."
                                LocationFilter.FAVORITES -> "No hay ubicaciones favoritas."
                                LocationFilter.NON_FAVORITES -> "No hay ubicaciones sin marcar como favoritas."
                            }
                        )
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        items(visibleLocations, key = { it.id }) { location ->
                            LocationItem(
                                location = location,
                                onClick = { onLocationClick(location.id) },
                                onFavoriteClick = { onFavoriteClick(location.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationItem(
    location: Location,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(location.name, style = MaterialTheme.typography.titleMedium)
            Text(
                location.type,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (location.isFavorite) {
                    Icons.Filled.Favorite
                } else {
                    Icons.Filled.FavoriteBorder
                },
                contentDescription = if (location.isFavorite) {
                    "Quitar de favoritos"
                } else {
                    "Agregar a favoritos"
                },
                tint = if (location.isFavorite) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationsScreenPreview() {
    RickAndMortyAppTheme {
        LocationsScreen(
            state = LocationsUiState(
                isLoading = false,
                data = listOf(
                    Location(1, "Earth (C-137)", "Planet", "Dimension C-137", true),
                    Location(2, "Abadango", "Cluster", "unknown")
                )
            ),
            onLocationClick = {},
            onLoadingClick = {},
            onRetryClick = {}
        )
    }
}
