package plat.lab7.hernandez_maldonado.character.navigation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import plat.lab7.hernandez_maldonado.character.data.CharacterDb
import plat.lab7.hernandez_maldonado.character.ui.characterdetails.CharacterDetailsScreen
import plat.lab7.hernandez_maldonado.character.ui.characters.CharactersScreen
import plat.lab7.hernandez_maldonado.character.ui.characters.CharactersViewModel

@Serializable
data object CharactersGraph

@Serializable
data object CharactersDestination

@Serializable
data class CharacterDetailsDestination(
    val characterId: Int
)

fun NavGraphBuilder.charactersGraph(
    navController: NavHostController
) {
    navigation<CharactersGraph>(
        startDestination = CharactersDestination
    ) {
        composable<CharactersDestination> {
            val activity = LocalActivity.current

            val charactersViewModel: CharactersViewModel = viewModel()
            val state by charactersViewModel.uiState.collectAsStateWithLifecycle()

            BackHandler {
                activity?.finish()
            }

            CharactersScreen(
                state = state,
                onCharacterClick = { characterId ->
                    navController.navigate(
                        CharacterDetailsDestination(
                            characterId = characterId
                        )
                    )
                },
                onLoadingClick = {
                    charactersViewModel.showError()
                },
                onRetryClick = {
                    charactersViewModel.loadCharacters()
                }
            )
        }

        composable<CharacterDetailsDestination> { entry ->
            val destination =
                entry.toRoute<CharacterDetailsDestination>()

            val characterDb = remember { CharacterDb() }
            val character = characterDb.getCharacterById(
                id = destination.characterId
            )

            CharacterDetailsScreen(
                character = character,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}