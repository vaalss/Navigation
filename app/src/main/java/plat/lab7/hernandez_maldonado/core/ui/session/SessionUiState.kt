package plat.lab7.hernandez_maldonado.core.ui.session

data class SessionUiState(
    val isLoading: Boolean = true,
    val name: String? = null,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null
)