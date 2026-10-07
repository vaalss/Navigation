package plat.lab7.hernandez_maldonado.location.navigation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable
import plat.lab7.hernandez_maldonado.location.ui.locationdetails.LocationDetailsScreen
import plat.lab7.hernandez_maldonado.location.ui.locations.LocationsScreen
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import plat.lab7.hernandez_maldonado.location.ui.locations.LocationsViewModel
import plat.lab7.hernandez_maldonado.location.ui.locationdetails.LocationDetailsViewModel

@Serializable
data object LocationsGraph

@Serializable
data object LocationsDestination

@Serializable
data class LocationDetailsDestination(
    val locationId: Int
)

fun NavGraphBuilder.locationsGraph(
    navController: NavHostController
) {
    navigation<LocationsGraph>(
        startDestination = LocationsDestination
    ) {
        composable<LocationsDestination> {
            val activity = LocalActivity.current

            val locationsViewModel: LocationsViewModel = viewModel()
            val state by locationsViewModel.uiState.collectAsStateWithLifecycle()

            BackHandler {
                activity?.finish()
            }

            LocationsScreen(
                state = state,
                onLocationClick = { locationId ->
                    navController.navigate(
                        LocationDetailsDestination(
                            locationId = locationId
                        )
                    )
                },
                onLoadingClick = {
                    locationsViewModel.showError()
                },
                onRetryClick = {
                    locationsViewModel.loadLocations()
                }
            )
        }

        composable<LocationDetailsDestination> {
            val locationDetailsViewModel: LocationDetailsViewModel = viewModel()
            val state by locationDetailsViewModel.uiState.collectAsStateWithLifecycle()

            LocationDetailsScreen(
                state = state,
                onBackClick = {
                    navController.popBackStack()
                },
                onLoadingClick = {
                    locationDetailsViewModel.showError()
                },
                onRetryClick = {
                    locationDetailsViewModel.loadLocation()
                }
            )
        }
    }
}