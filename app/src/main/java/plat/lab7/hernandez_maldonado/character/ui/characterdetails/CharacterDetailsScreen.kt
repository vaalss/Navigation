package plat.lab7.hernandez_maldonado.character.ui.characterdetails

import androidx.compose.foundation.layout.Row
import androidx.compose.ui.tooling.preview.Preview
import plat.lab7.hernandez_maldonado.core.ui.theme.RickAndMortyAppTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import plat.lab7.hernandez_maldonado.character.data.Character
import plat.lab7.hernandez_maldonado.core.ui.components.ErrorContent
import plat.lab7.hernandez_maldonado.core.ui.components.LoadingContent

@Composable
private fun CharacterDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = value,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(
    state: CharacterDetailsUiState,
    onBackClick: () -> Unit,
    onLoadingClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        val character = state.data

        when {
            state.isLoading -> {
                LoadingContent(
                    onLoadingClick = onLoadingClick
                )
            }

            state.hasError -> {
                ErrorContent(
                    message = "Error al obtener detalle del personaje.",
                    onRetryClick = onRetryClick
                )
            }

            character != null -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TopAppBar(
                        modifier = Modifier.height(60.dp),
                        title = {
                            Text(text = "Characters details")
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = onBackClick
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Regresar"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = character.image,
                            contentDescription = "Imagen de ${character.name}",
                            modifier = Modifier
                                .size(220.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = character.name,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CharacterDetailRow(
                                label = "Species:",
                                value = character.species
                            )

                            CharacterDetailRow(
                                label = "Status:",
                                value = character.status
                            )

                            CharacterDetailRow(
                                label = "Gender:",
                                value = character.gender
                            )
                        }
                    }
                }
            }

            else -> {
                ErrorContent(
                    message = "No se encontró el personaje.",
                    onRetryClick = onRetryClick
                )
            }
        }
    }
}

@Preview(
    name = "Character details",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun CharacterDetailsScreenPreview() {
    val previewCharacter = Character(
        id = 2,
        name = "Morty Smith",
        status = "Alive",
        species = "Human",
        gender = "Male",
        image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg"
    )

    RickAndMortyAppTheme {
        CharacterDetailsScreen(
            state = CharacterDetailsUiState(
                isLoading = false,
                data = previewCharacter,
                hasError = false
            ),
            onBackClick = {},
            onLoadingClick = {},
            onRetryClick = {}
        )
    }
}