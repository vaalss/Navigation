package plat.lab7.hernandez_maldonado.character.ui.characterform

data class CharacterFormUiState(
    val name: String = "",
    val status: String = "Alive",
    val species: String = "",
    val gender: String = "unknown",
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val loadError: String? = null,
    val errorMessage: String? = null
)
