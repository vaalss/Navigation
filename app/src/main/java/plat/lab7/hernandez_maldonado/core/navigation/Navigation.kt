package plat.lab7.hernandez_maldonado.core.navigation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import plat.lab7.hernandez_maldonado.character.navigation.CharactersGraph
import plat.lab7.hernandez_maldonado.character.navigation.charactersGraph
import plat.lab7.hernandez_maldonado.core.ui.components.BottomNavigationBar
import plat.lab7.hernandez_maldonado.core.ui.components.MainTab
import plat.lab7.hernandez_maldonado.location.navigation.LocationsGraph
import plat.lab7.hernandez_maldonado.location.navigation.locationsGraph
import plat.lab7.hernandez_maldonado.login.ui.LoginScreen
import plat.lab7.hernandez_maldonado.profile.ui.ProfileScreen

@Serializable
data object LoginDestination

@Serializable
data object MainGraph

@Serializable
data object ProfileDestination

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any {
        it.hasRoute<MainGraph>()
    } == true

    val selectedTab = when {
        currentDestination?.hierarchy?.any {
            it.hasRoute<LocationsGraph>()
        } == true -> MainTab.Locations

        currentDestination?.hasRoute<ProfileDestination>() == true ->
            MainTab.Profile

        else -> MainTab.Characters
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabClick = { tab ->
                        if (tab != selectedTab) {
                            val route = when (tab) {
                                MainTab.Characters -> CharactersGraph
                                MainTab.Locations -> LocationsGraph
                                MainTab.Profile -> ProfileDestination
                            }

                            navController.navigate(route) {
                                popUpTo<MainGraph> {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = LoginDestination,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable<LoginDestination> {
                val activity = LocalActivity.current

                BackHandler {
                    activity?.finish()
                }

                LoginScreen(
                    onStartClick = {
                        navController.navigate(MainGraph) {
                            popUpTo<LoginDestination> {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            navigation<MainGraph>(
                startDestination = CharactersGraph
            ) {
                charactersGraph(navController)

                locationsGraph(navController)

                composable<ProfileDestination> {
                    val activity = LocalActivity.current

                    BackHandler {
                        activity?.finish()
                    }

                    ProfileScreen(
                        onLogoutClick = {
                            navController.navigate(LoginDestination) {
                                popUpTo<MainGraph> {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }
}