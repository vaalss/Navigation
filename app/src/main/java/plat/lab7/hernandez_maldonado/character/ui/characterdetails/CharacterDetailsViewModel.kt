package plat.lab7.hernandez_maldonado.character.ui.characterdetails

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import plat.lab7.hernandez_maldonado.RickAndMortyApp
import plat.lab7.hernandez_maldonado.character.navigation.CharacterDetailsDestination

class CharacterDetailsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val characterRepository = (application as RickAndMortyApp)
        .characterRepository

    private val characterId = savedStateHandle
        .toRoute<CharacterDetailsDestination>().characterId

    private val _uiState = MutableStateFlow(CharacterDetailsUiState())
    val uiState: StateFlow<CharacterDetailsUiState> = _uiState.asStateFlow()

    private var loadingJob: Job? = null

    init {
        loadCharacter()
    }

    fun loadCharacter() {
        if (_uiState.value.isDeleting || _uiState.value.isDeleted) return
        loadingJob?.cancel()
        _uiState.update {
            it.copy(isLoading = true, hasError = false, actionErrorMessage = null)
        }

        loadingJob = viewModelScope.launch {
            try {
                characterRepository.getCharacterById(characterId).collect { character ->
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            data = character,
                            hasError = character == null &&
                                    !current.isDeleting && !current.isDeleted
                        )
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e("CharacterDetails", "Error al cargar personaje", exception)
                _uiState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    fun toggleFavorite() {
        val current = _uiState.value
        val character = current.data ?: return
        if (current.isDeleting || current.isDeleted) return

        viewModelScope.launch {
            try {
                characterRepository.updateFavorite(character.id, !character.isFavorite)
                _uiState.update { it.copy(actionErrorMessage = null) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e("CharacterDetails", "Error al cambiar favorito", exception)
                _uiState.update {
                    it.copy(actionErrorMessage = "No se pudo cambiar el favorito.")
                }
            }
        }
    }

    fun deleteCharacter() {
        val current = _uiState.value
        if (current.data == null || current.isDeleting || current.isDeleted) return

        _uiState.update { it.copy(isDeleting = true, actionErrorMessage = null) }

        viewModelScope.launch {
            try {
                characterRepository.deleteCharacter(characterId)
                _uiState.update { it.copy(isDeleting = false, isDeleted = true) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e("CharacterDetails", "Error al eliminar personaje", exception)
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        actionErrorMessage = "No se pudo eliminar. Intentá de nuevo."
                    )
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
