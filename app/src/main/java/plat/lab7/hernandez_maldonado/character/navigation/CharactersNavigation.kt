package plat.lab7.hernandez_maldonado.character.navigation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import plat.lab7.hernandez_maldonado.character.data.CharacterDb
import plat.lab7.hernandez_maldonado.character.ui.characterdetails.CharacterDetailsScreen
import plat.lab7.hernandez_maldonado.character.ui.characters.CharactersScreen

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
            val characterDb = remember { CharacterDb() }

            BackHandler {
                activity?.finish()
            }

            CharactersScreen(
                characters = characterDb.getAllCharacters(),
                onCharacterClick = { characterId ->
                    navController.navigate(
                        CharacterDetailsDestination(
                            characterId = characterId
                        )
                    )
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