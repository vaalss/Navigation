package plat.lab7.hernandez_maldonado.location.domain

import kotlinx.coroutines.flow.Flow
import plat.lab7.hernandez_maldonado.location.data.Location

interface LocationRepository {
    fun getLocations(): Flow<List<Location>>
    fun getLocationById(id: Int): Flow<Location?>
    suspend fun synchronize()
    suspend fun toggleFavorite(id: Int)
}
