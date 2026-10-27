package plat.lab7.hernandez_maldonado.character.ui.characters

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
import plat.lab7.hernandez_maldonado.character.data.toModel

class CharactersViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val characterDao = (application as RickAndMortyApp)
        .database
        .characterDao()

    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    private var loadingJob: Job? = null

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        loadingJob?.cancel()

        _uiState.value = CharactersUiState(
            isLoading = true,
            data = emptyList(),
            hasError = false
        )

        loadingJob = viewModelScope.launch {
            try {
                delay(4000)

                characterDao.getAll().collect { entities ->
                    val characters = entities.map { entity ->
                        entity.toModel()
                    }

                    _uiState.value = CharactersUiState(
                        isLoading = false,
                        data = characters,
                        hasError = false
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = CharactersUiState(
                    isLoading = false,
                    data = emptyList(),
                    hasError = true
                )
            }
        }
    }

    fun toggleFavorite(characterId: Int) {
        val character = _uiState.value.data.firstOrNull {
            it.id == characterId
        } ?: return

        viewModelScope.launch {
            characterDao.updateFavorite(
                id = characterId,
                isFavorite = !character.isFavorite
            )
        }
    }

    fun showError() {
        if (!_uiState.value.isLoading) {
            return
        }

        loadingJob?.cancel()

        _uiState.value = CharactersUiState(
            isLoading = false,
            data = emptyList(),
            hasError = true
        )
    }
}