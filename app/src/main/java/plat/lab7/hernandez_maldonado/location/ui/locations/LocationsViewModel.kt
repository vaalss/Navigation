package plat.lab7.hernandez_maldonado.location.ui.locations

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import plat.lab7.hernandez_maldonado.RickAndMortyApp
import plat.lab7.hernandez_maldonado.location.data.toModel

class LocationsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val locationDao = (application as RickAndMortyApp)
        .database
        .locationDao()

    private val _uiState = MutableStateFlow(LocationsUiState())
    val uiState: StateFlow<LocationsUiState> = _uiState.asStateFlow()

    private var loadingJob: Job? = null

    init {
        loadLocations()
    }

    fun loadLocations() {
        loadingJob?.cancel()

        _uiState.value = LocationsUiState(
            isLoading = true,
            data = emptyList(),
            hasError = false
        )

        loadingJob = viewModelScope.launch {
            try {
                delay(4000)

                locationDao.getAll().collect { entities ->
                    val locations = entities.map { entity ->
                        entity.toModel()
                    }

                    _uiState.value = LocationsUiState(
                        isLoading = false,
                        data = locations,
                        hasError = false
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = LocationsUiState(
                    isLoading = false,
                    data = emptyList(),
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

        _uiState.value = LocationsUiState(
            isLoading = false,
            data = emptyList(),
            hasError = true
        )
    }
}