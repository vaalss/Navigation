package plat.lab7.hernandez_maldonado

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data object LoginDestination

@Serializable
data object CharactersDestination

@Serializable
data class CharacterDetailsDestination(
    val characterId: Int
)

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val characterDb = remember {
        CharacterDb()
    }

    NavHost(
        navController = navController,
        startDestination = LoginDestination
    ) {
        composable<LoginDestination> {
            LoginScreen(
                onStartClick = {
                    navController.navigate(CharactersDestination) {
                        popUpTo<LoginDestination> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<CharactersDestination> {
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

        composable<CharacterDetailsDestination> { backStackEntry ->
            val destination =
                backStackEntry.toRoute<CharacterDetailsDestination>()

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