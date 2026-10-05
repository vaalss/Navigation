package plat.lab7.hernandez_maldonado.location.navigation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import plat.lab7.hernandez_maldonado.location.data.LocationDb
import plat.lab7.hernandez_maldonado.location.ui.locationdetails.LocationDetailsScreen
import plat.lab7.hernandez_maldonado.location.ui.locations.LocationsScreen

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
            val locationDb = remember { LocationDb() }

            BackHandler {
                activity?.finish()
            }

            LocationsScreen(
                locations = locationDb.getAllLocations(),
                onLocationClick = { locationId ->
                    navController.navigate(
                        LocationDetailsDestination(
                            locationId = locationId
                        )
                    )
                }
            )
        }

        composable<LocationDetailsDestination> { entry ->
            val destination =
                entry.toRoute<LocationDetailsDestination>()

            val locationDb = remember { LocationDb() }
            val location = locationDb.getLocationById(
                id = destination.locationId
            )

            LocationDetailsScreen(
                location = location,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}