package plat.lab7.hernandez_maldonado.core.ui.session

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import plat.lab7.hernandez_maldonado.core.data.DataStorePreferences

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = DataStorePreferences(application)

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.getName().collect { name ->
                _uiState.value = SessionUiState(
                    isLoading = false,
                    name = name
                )
            }
        }
    }

    fun login(name: String) {
        val cleanName = name.trim()

        if (cleanName.isBlank()) {
            return
        }

        viewModelScope.launch {
            preferences.saveName(cleanName)
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferences.clearName()
        }
    }
}