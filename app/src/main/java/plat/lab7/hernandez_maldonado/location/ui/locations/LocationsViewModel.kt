package plat.lab7.hernandez_maldonado.location.ui.locations

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import plat.lab7.hernandez_maldonado.RickAndMortyApp

class LocationsViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = (application as RickAndMortyApp).locationRepository

    private val initialFilter = LocationFilter.entries.firstOrNull {
        it.name == savedStateHandle.get<String>("location_filter")
    } ?: LocationFilter.ALL

    private val _uiState = MutableStateFlow(LocationsUiState(filter = initialFilter))
    val uiState = _uiState.asStateFlow()
    private var loadingJob: Job? = null

    init {
        loadLocations()
    }

    fun loadLocations() {
        loadingJob?.cancel()
        _uiState.update {
            it.copy(isLoading = true, hasError = false, actionErrorMessage = null)
        }

        loadingJob = viewModelScope.launch {
            try {
                repository.getLocations().collect { locations ->
                    _uiState.update {
                        it.copy(isLoading = false, data = locations, hasError = false)
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e("Locations", "Error al cargar ubicaciones", exception)
                _uiState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    fun setFilter(filter: LocationFilter) {
        savedStateHandle["location_filter"] = filter.name
        _uiState.update { it.copy(filter = filter) }
    }

    fun toggleFavorite(locationId: Int) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(locationId)
                _uiState.update { it.copy(actionErrorMessage = null) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e("Locations", "Error al cambiar favorito", exception)
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
