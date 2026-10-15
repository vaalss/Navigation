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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import plat.lab7.hernandez_maldonado.core.data.DataStorePreferences
import plat.lab7.hernandez_maldonado.core.ui.session.SessionViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment

@Serializable
data object LoginDestination

@Serializable
data object MainGraph

@Serializable
data object ProfileDestination

@Serializable
data object SessionCheckDestination

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {

    val appContext = LocalContext.current.applicationContext
    val sessionViewModel: SessionViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                SessionViewModel(
                    preferences = DataStorePreferences(appContext)
                )
            }
        }
    )

    val sessionState by sessionViewModel.uiState.collectAsStateWithLifecycle()

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
            startDestination = SessionCheckDestination,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable<SessionCheckDestination> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                LaunchedEffect(sessionState.isLoading, sessionState.name) {
                    if (!sessionState.isLoading) {
                        if (sessionState.name != null) {
                            navController.navigate(MainGraph) {
                                popUpTo<SessionCheckDestination> {
                                    inclusive = true
                                }
                            }
                        } else {
                            navController.navigate(LoginDestination) {
                                popUpTo<SessionCheckDestination> {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
            }

            composable<LoginDestination> {
                val activity = LocalActivity.current

                BackHandler {
                    activity?.finish()
                }

                LaunchedEffect(sessionState.name) {
                    if (sessionState.name != null) {
                        navController.navigate(MainGraph) {
                            popUpTo<LoginDestination> {
                                inclusive = true
                            }
                        }
                    }
                }

                LoginScreen(
                    onStartClick = { name ->
                        sessionViewModel.login(name)
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

                    LaunchedEffect(sessionState.isLoading, sessionState.name) {
                        if (!sessionState.isLoading && sessionState.name == null) {
                            navController.navigate(LoginDestination) {
                                popUpTo<MainGraph> {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }

                    ProfileScreen(
                        name = sessionState.name.orEmpty(),
                        onLogoutClick = {
                            sessionViewModel.logout()
                        }
                    )
                }
            }
        }
    }
}