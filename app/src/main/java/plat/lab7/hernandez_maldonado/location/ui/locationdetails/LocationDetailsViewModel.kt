package plat.lab7.hernandez_maldonado.location.ui.locationdetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import plat.lab7.hernandez_maldonado.RickAndMortyApp
import plat.lab7.hernandez_maldonado.location.navigation.LocationDetailsDestination

class LocationDetailsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val locationRepository = (application as RickAndMortyApp)
        .locationRepository

    private val locationId = savedStateHandle
        .toRoute<LocationDetailsDestination>()
        .locationId

    private val _uiState = MutableStateFlow(LocationDetailsUiState())
    val uiState: StateFlow<LocationDetailsUiState> = _uiState.asStateFlow()

    private var loadingJob: Job? = null

    init {
        loadLocation()
    }

    fun loadLocation() {
        loadingJob?.cancel()

        _uiState.value = LocationDetailsUiState(
            isLoading = true,
            data = null,
            hasError = false
        )

        loadingJob = viewModelScope.launch {
            try {
                locationRepository.getLocationById(locationId).collect { location ->

                    _uiState.value = LocationDetailsUiState(
                        isLoading = false,
                        data = location,
                        hasError = location == null
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = LocationDetailsUiState(
                    isLoading = false,
                    data = null,
                    hasError = true
                )
            }
        }
    }

    fun showError() {
        if (!_uiState.value.isLoading) {
            return
        }

        loadingJob?.cancel()

        _uiState.value = LocationDetailsUiState(
            isLoading = false,
            data = null,
            hasError = true
        )
    }
}