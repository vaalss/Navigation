package plat.lab7.hernandez_maldonado.location.ui.locationdetails

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import plat.lab7.hernandez_maldonado.RickAndMortyApp
import plat.lab7.hernandez_maldonado.location.navigation.LocationDetailsDestination

class LocationDetailsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = (application as RickAndMortyApp).locationRepository
    private val locationId = savedStateHandle
        .toRoute<LocationDetailsDestination>().locationId

    private val _uiState = MutableStateFlow(LocationDetailsUiState())
    val uiState = _uiState.asStateFlow()
    private var loadingJob: Job? = null

    init {
        loadLocation()
    }

    fun loadLocation() {
        loadingJob?.cancel()
        _uiState.update {
            it.copy(isLoading = true, hasError = false, actionErrorMessage = null)
        }

        loadingJob = viewModelScope.launch {
            try {
                repository.getLocationById(locationId).collect { location ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            data = location,
                            hasError = location == null
                        )
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e("LocationDetails", "Error al cargar ubicación", exception)
                _uiState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    fun toggleFavorite() {
        if (_uiState.value.data == null) return

        viewModelScope.launch {
            try {
                repository.toggleFavorite(locationId)
                _uiState.update { it.copy(actionErrorMessage = null) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e("LocationDetails", "Error al cambiar favorito", exception)
                _uiState.update {
                    it.copy(actionErrorMessage = "No se pudo cambiar el favorito.")
                }
            }
        }
    }

    fun showError() {
        if (!_uiState.value.isLoading) return
        loadingJob?.cancel()
        _uiState.update { it.copy(isLoading = false, hasError = true) }
    }
}
