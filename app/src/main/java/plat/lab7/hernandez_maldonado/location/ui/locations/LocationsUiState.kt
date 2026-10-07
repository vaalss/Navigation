package plat.lab7.hernandez_maldonado.location.ui.locations

import plat.lab7.hernandez_maldonado.location.data.Location

data class LocationsUiState(
    val isLoading: Boolean = true,
    val data: List<Location> = emptyList(),
    val hasError: Boolean = false
)