package plat.lab7.hernandez_maldonado.location.ui.locations

import plat.lab7.hernandez_maldonado.location.data.Location

enum class LocationFilter(val label: String) {
    ALL("Todas"),
    FAVORITES("Favoritas"),
    NON_FAVORITES("No favoritas")
}

data class LocationsUiState(
    val isLoading: Boolean = true,
    val data: List<Location> = emptyList(),
    val hasError: Boolean = false,
    val filter: LocationFilter = LocationFilter.ALL,
    val actionErrorMessage: String? = null
)
