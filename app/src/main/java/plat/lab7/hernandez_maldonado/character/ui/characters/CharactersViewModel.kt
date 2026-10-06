package plat.lab7.hernandez_maldonado.character.ui.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import plat.lab7.hernandez_maldonado.character.data.CharacterDb

class CharactersViewModel : ViewModel() {

    private val characterDb = CharacterDb()

    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    private var loadingJob: Job? = null

    init {loadCharacters()}

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

                val characters = characterDb.getAllCharacters()

                _uiState.value = CharactersUiState(
                    isLoading = false,
                    data = characters,
                    hasError = false
                )
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