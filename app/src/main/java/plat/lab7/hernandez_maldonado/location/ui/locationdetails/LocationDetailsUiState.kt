package plat.lab7.hernandez_maldonado.location.ui.locationdetails

import plat.lab7.hernandez_maldonado.location.data.Location

data class LocationDetailsUiState(
    val isLoading: Boolean = true,
    val data: Location? = null,
    val hasError: Boolean = false
)