package plat.lab7.hernandez_maldonado.character.navigation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable
import plat.lab7.hernandez_maldonado.character.ui.characterdetails.CharacterDetailsScreen
import plat.lab7.hernandez_maldonado.character.ui.characterdetails.CharacterDetailsViewModel
import plat.lab7.hernandez_maldonado.character.ui.characterform.CharacterFormScreen
import plat.lab7.hernandez_maldonado.character.ui.characterform.CharacterFormViewModel
import plat.lab7.hernandez_maldonado.character.ui.characters.CharactersScreen
import plat.lab7.hernandez_maldonado.character.ui.characters.CharactersViewModel

@Serializable
data object CharactersGraph

@Serializable
data object CharactersDestination

@Serializable
data class CharacterDetailsDestination(val characterId: Int)

// El ID 0 representa la creación. Los IDs locales son negativos.
@Serializable
data class CharacterFormDestination(val characterId: Int = 0)

fun NavGraphBuilder.charactersGraph(navController: NavHostController) {
    navigation<CharactersGraph>(startDestination = CharactersDestination) {
        composable<CharactersDestination> {
            val activity = LocalActivity.current
            val charactersViewModel: CharactersViewModel = viewModel()
            val state by charactersViewModel.uiState.collectAsStateWithLifecycle()

            BackHandler { activity?.finish() }

            CharactersScreen(
                state = state,
                onCharacterClick = { id ->
                    navController.navigate(CharacterDetailsDestination(id))
                },
                onLoadingClick = charactersViewModel::showError,
                onRetryClick = charactersViewModel::loadCharacters,
                onFavoriteClick = charactersViewModel::toggleFavorite,
                onAddClick = {
                    navController.navigate(CharacterFormDestination())
                }
            )
        }

        composable<CharacterDetailsDestination> {
            val detailsViewModel: CharacterDetailsViewModel = viewModel()
            val state by detailsViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(state.isDeleted) {
                if (state.isDeleted) {
                    navController.popBackStack()
                }
            }

            CharacterDetailsScreen(
                state = state,
                onBackClick = { navController.popBackStack() },
                onLoadingClick = detailsViewModel::showError,
                onRetryClick = detailsViewModel::loadCharacter,
                onFavoriteClick = detailsViewModel::toggleFavorite,
                onDeleteClick = detailsViewModel::deleteCharacter,
                onEditClick = {
                    state.data?.let { character ->
                        navController.navigate(CharacterFormDestination(character.id))
                    }
                }
            )
        }

        composable<CharacterFormDestination> {
            val formViewModel: CharacterFormViewModel = viewModel()
            val state by formViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(state.isSaved) {
                if (state.isSaved) {
                    navController.popBackStack()
                }
            }

            CharacterFormScreen(
                state = state,
                onNameChange = formViewModel::onNameChange,
                onStatusChange = formViewModel::onStatusChange,
                onSpeciesChange = formViewModel::onSpeciesChange,
                onGenderChange = formViewModel::onGenderChange,
                onSaveClick = formViewModel::save,
                onBackClick = { navController.popBackStack() },
                onRetryClick = formViewModel::loadCharacter
            )
        }
    }
}

