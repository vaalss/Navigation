package plat.lab7.hernandez_maldonado.character.ui.characterdetails

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import plat.lab7.hernandez_maldonado.R
import plat.lab7.hernandez_maldonado.core.ui.components.ErrorContent
import plat.lab7.hernandez_maldonado.core.ui.components.LoadingContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(
    state: CharacterDetailsUiState,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onLoadingClick: () -> Unit,
    onRetryClick: () -> Unit,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val character = state.data

    BackHandler(enabled = state.isDeleting) { }

    if (showDeleteDialog && character != null && !state.isDeleted) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar personaje") },
            text = { Text("¿Quieres eliminar a ${character.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    },
                    enabled = !state.isDeleting
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Surface(
        modifier = modifier.fillMaxSize().safeDrawingPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Detalle del personaje") },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !state.isDeleting) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Regresar")
                    }
                },
                actions = {
                    if (character != null && !state.isDeleted) {
                        IconButton(onClick = onEditClick, enabled = !state.isDeleting) {
                            Icon(Icons.Filled.Edit, "Editar personaje")
                        }
                        IconButton(onClick = onFavoriteClick, enabled = !state.isDeleting) {
                            Icon(
                                imageVector = if (character.isFavorite) {
                                    Icons.Filled.Favorite
                                } else {
                                    Icons.Filled.FavoriteBorder
                                },
                                contentDescription = if (character.isFavorite) {
                                    "Quitar de favoritos"
                                } else {
                                    "Agregar a favoritos"
                                }
                            )
                        }
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            enabled = !state.isDeleting
                        ) {
                            Icon(Icons.Filled.Delete, "Eliminar personaje")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when {
                    state.isDeleting -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Eliminando...")
                        }
                    }
                    state.isDeleted -> Unit
                    state.isLoading -> LoadingContent(onLoadingClick = onLoadingClick)
                    state.hasError || character == null -> ErrorContent(
                        message = "No se pudo obtener el personaje.",
                        onRetryClick = onRetryClick
                    )
                    else -> {
                        Column(
                            modifier = Modifier.fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = character.image.takeIf { it.isNotBlank() }
                                    ?: R.drawable.rick_and_morty,
                                contentDescription = "Imagen de ${character.name}",
                                modifier = Modifier.size(220.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                text = character.name,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            CharacterDetailRow("Species:", character.species)
                            CharacterDetailRow("Status:", character.status)
                            CharacterDetailRow("Gender:", character.gender)

                            state.actionErrorMessage?.let { message ->
                                Text(message, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
