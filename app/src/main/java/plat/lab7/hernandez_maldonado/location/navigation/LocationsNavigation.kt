package plat.lab7.hernandez_maldonado.location.navigation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable
import plat.lab7.hernandez_maldonado.location.ui.locationdetails.LocationDetailsScreen
import plat.lab7.hernandez_maldonado.location.ui.locationdetails.LocationDetailsViewModel
import plat.lab7.hernandez_maldonado.location.ui.locations.LocationsScreen
import plat.lab7.hernandez_maldonado.location.ui.locations.LocationsViewModel

@Serializable
data object LocationsGraph

@Serializable
data object LocationsDestination

@Serializable
data class LocationDetailsDestination(val locationId: Int)

fun NavGraphBuilder.locationsGraph(navController: NavHostController) {
    navigation<LocationsGraph>(startDestination = LocationsDestination) {
        composable<LocationsDestination> {
            val activity = LocalActivity.current
            val locationsViewModel: LocationsViewModel = viewModel()
            val state by locationsViewModel.uiState.collectAsStateWithLifecycle()

            BackHandler { activity?.finish() }

            LocationsScreen(
                state = state,
                onLocationClick = { id ->
                    navController.navigate(LocationDetailsDestination(id))
                },
                onLoadingClick = locationsViewModel::showError,
                onRetryClick = locationsViewModel::loadLocations,
                onFavoriteClick = locationsViewModel::toggleFavorite,
                onFilterChange = locationsViewModel::setFilter
            )
        }

        composable<LocationDetailsDestination> {
            val detailsViewModel: LocationDetailsViewModel = viewModel()
            val state by detailsViewModel.uiState.collectAsStateWithLifecycle()

            LocationDetailsScreen(
                state = state,
                onBackClick = { navController.popBackStack() },
                onLoadingClick = detailsViewModel::showError,
                onRetryClick = detailsViewModel::loadLocation,
                onFavoriteClick = detailsViewModel::toggleFavorite
            )
        }
    }
}
