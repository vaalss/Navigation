package plat.lab7.hernandez_maldonado.character.ui.characterdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import plat.lab7.hernandez_maldonado.character.data.CharacterDb
import plat.lab7.hernandez_maldonado.character.navigation.CharacterDetailsDestination

class CharacterDetailsViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val characterDb = CharacterDb()

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

                val character = characterDb.getCharacterById(
                    id = characterId
                )

                _uiState.value = CharacterDetailsUiState(
                    isLoading = false,
                    data = character,
                    hasError = false
                )
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