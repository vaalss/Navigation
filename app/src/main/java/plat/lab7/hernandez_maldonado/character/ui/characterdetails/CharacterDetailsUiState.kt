package plat.lab7.hernandez_maldonado.character.ui.characterdetails

import plat.lab7.hernandez_maldonado.character.data.Character

data class CharacterDetailsUiState(
    val isLoading: Boolean = true,
    val data: Character? = null,
    val hasError: Boolean = false
)