package plat.lab7.hernandez_maldonado.core.ui.session

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import plat.lab7.hernandez_maldonado.RickAndMortyApp
import plat.lab7.hernandez_maldonado.core.data.DataStorePreferences
import plat.lab7.hernandez_maldonado.core.data.InitialSync

class SessionViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val preferences = DataStorePreferences(application)

    private val initialSync = InitialSync(
        database = (application as RickAndMortyApp).database
    )

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.getName().collect { name ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        name = name
                    )
                }
            }
        }
    }

    fun login(name: String) {
        val cleanName = name.trim()

        if (
            cleanName.isBlank() ||
            _uiState.value.isLoading ||
            _uiState.value.isSyncing ||
            _uiState.value.name != null
        ) {
            return
        }

        _uiState.update { currentState ->
            currentState.copy(
                isSyncing = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                initialSync.synchronize()
                preferences.saveName(cleanName)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = "No se pudo iniciar sesión. Intentá de nuevo."
                    )
                }
            } finally {
                _uiState.update { currentState ->
                    currentState.copy(
                        isSyncing = false
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferences.clearName()
        }
    }
}