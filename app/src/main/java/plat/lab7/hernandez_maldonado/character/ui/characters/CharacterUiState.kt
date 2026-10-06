package plat.lab7.hernandez_maldonado.character.ui.characters

import plat.lab7.hernandez_maldonado.character.data.Character

data class CharactersUiState(
    val isLoading: Boolean = true,
    val data: List<Character> = emptyList(),
    val hasError: Boolean = false
)