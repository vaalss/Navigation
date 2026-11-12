package plat.lab7.hernandez_maldonado.character.ui.characterform

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import plat.lab7.hernandez_maldonado.RickAndMortyApp
import plat.lab7.hernandez_maldonado.character.navigation.CharacterFormDestination

class CharacterFormViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = (application as RickAndMortyApp).characterRepository
    private val characterId = savedStateHandle
        .toRoute<CharacterFormDestination>().characterId

    private val _uiState = MutableStateFlow(
        CharacterFormUiState(isEditing = characterId != 0)
    )
    val uiState = _uiState.asStateFlow()
    private var loadingJob: Job? = null

    init {
        if (characterId != 0) {
            loadCharacter()
        }
    }

    fun loadCharacter() {
        if (characterId == 0 || _uiState.value.isSaving) return
        loadingJob?.cancel()
        _uiState.update { it.copy(isLoading = true, loadError = null) }

        loadingJob = viewModelScope.launch {
            try {
                val character = repository.getCharacterById(characterId).first()
                if (character == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadError = "El personaje ya no existe."
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            name = character.name,
                            status = character.status,
                            species = character.species,
                            gender = character.gender,
                            isLoading = false,
                            loadError = null
                        )
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e("CharacterForm", "Error al cargar personaje", exception)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loadError = "No se pudo cargar el personaje."
                    )
                }
            }
        }
    }

    private fun edit(change: (CharacterFormUiState) -> CharacterFormUiState) {
        _uiState.update { current ->
            if (current.isLoading || current.isSaving || current.isSaved) {
                current
            } else {
                change(current).copy(errorMessage = null)
            }
        }
    }

    fun onNameChange(value: String) = edit { it.copy(name = value) }
    fun onStatusChange(value: String) = edit { it.copy(status = value) }
    fun onSpeciesChange(value: String) = edit { it.copy(species = value) }
    fun onGenderChange(value: String) = edit { it.copy(gender = value) }

    fun save() {
        val current = _uiState.value
        if (current.isLoading || current.isSaving || current.isSaved ||
            current.loadError != null
        ) return

        if (current.name.isBlank() || current.species.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Ingresá el nombre y la especie.")
            }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                if (characterId == 0) {
                    repository.createCharacter(
                        name = current.name,
                        status = current.status,
                        species = current.species,
                        gender = current.gender
                    )
                } else {
                    repository.updateCharacter(
                        id = characterId,
                        name = current.name,
                        status = current.status,
                        species = current.species,
                        gender = current.gender
                    )
                }

                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e("CharacterForm", "Error al guardar personaje", exception)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "No se pudo guardar. Intentá de nuevo."
                    )
                }
            }
        }
    }
}
