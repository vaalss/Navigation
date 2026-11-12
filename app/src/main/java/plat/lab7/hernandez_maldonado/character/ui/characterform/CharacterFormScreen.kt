package plat.lab7.hernandez_maldonado.character.ui.characterform

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterFormScreen(
    state: CharacterFormUiState,
    onNameChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onSpeciesChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(enabled = state.isSaving) { }

    Surface(
        modifier = modifier.fillMaxSize().safeDrawingPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(if (state.isEditing) "Editar personaje" else "Crear personaje")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        enabled = !state.isSaving
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when {
                    state.isLoading -> CircularProgressIndicator()
                    state.loadError != null -> {
                        Text(state.loadError, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onRetryClick) { Text("Reintentar") }
                    }
                    else -> {
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = onNameChange,
                            label = { Text("Nombre") },
                            singleLine = true,
                            enabled = !state.isSaving,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = state.species,
                            onValueChange = onSpeciesChange,
                            label = { Text("Especie") },
                            singleLine = true,
                            enabled = !state.isSaving,
                            modifier = Modifier.fillMaxWidth()
                        )

                        CharacterChoiceField(
                            label = "Estado",
                            value = state.status,
                            options = listOf("Alive", "Dead", "unknown"),
                            enabled = !state.isSaving,
                            onChange = onStatusChange
                        )

                        CharacterChoiceField(
                            label = "Género",
                            value = state.gender,
                            options = listOf("Female", "Male", "Genderless", "unknown"),
                            enabled = !state.isSaving,
                            onChange = onGenderChange
                        )

                        state.errorMessage?.let { message ->
                            Text(message, color = MaterialTheme.colorScheme.error)
                        }

                        Button(
                            onClick = onSaveClick,
                            enabled = !state.isSaving && !state.isSaved,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (state.isSaving) "Guardando..."
                                else "Guardar"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterChoiceField(
    label: String,
    value: String,
    options: List<String>,
    enabled: Boolean,
    onChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("$label: $value")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        expanded = false
                        onChange(option)
                    }
                )
            }
        }
    }
}
