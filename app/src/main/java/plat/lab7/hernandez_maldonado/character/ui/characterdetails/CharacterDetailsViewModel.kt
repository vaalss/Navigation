package plat.lab7.hernandez_maldonado.character.ui.characterdetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import plat.lab7.hernandez_maldonado.RickAndMortyApp
import plat.lab7.hernandez_maldonado.character.data.toModel
import plat.lab7.hernandez_maldonado.character.navigation.CharacterDetailsDestination

class CharacterDetailsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val characterDao = (application as RickAndMortyApp)
        .database
        .characterDao()

    private val characterId = savedStateHandle
        .toRoute<CharacterDetailsDestination>()
        .characterId

    private val _uiState = MutableStateFlow(CharacterDetailsUiState())
    val uiState: StateFlow<CharacterDetailsUiState> = _uiState.asStateFlow()

    private var loadingJob: Job? = null

    init {
        loadCharacter()
    }

    fun loadCharacter() {
        loadingJob?.cancel()

        _uiState.value = CharacterDetailsUiState(
            isLoading = true,
            data = null,
            hasError = false
        )

        loadingJob = viewModelScope.launch {
            try {
                delay(2000)

                characterDao.getById(characterId).collect { entity ->
                    val character = entity?.toModel()

                    _uiState.value = CharacterDetailsUiState(
                        isLoading = false,
                        data = character,
                        hasError = character == null
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = CharacterDetailsUiState(
                    isLoading = false,
                    data = null,
                    hasError = true
                )
            }
        }
    }

    fun toggleFavorite() {
        val character = _uiState.value.data ?: return

        viewModelScope.launch {
            characterDao.updateFavorite(
                id = character.id,
                isFavorite = !character.isFavorite
            )
        }
    }

    fun showError() {
        if (!_uiState.value.isLoading) {
            return
        }

        loadingJob?.cancel()

        _uiState.value = CharacterDetailsUiState(
            isLoading = false,
            data = null,
            hasError = true
        )
    }
}