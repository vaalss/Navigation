package plat.lab7.hernandez_maldonado.location.data

import androidx.room3.immediateTransaction
import androidx.room3.useWriterConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import plat.lab7.hernandez_maldonado.core.data.AppDatabase
import plat.lab7.hernandez_maldonado.location.domain.LocationRepository
import plat.lab7.hernandez_maldonado.location.network.LocationApi

class LocationRepositoryImpl(
    private val database: AppDatabase,
    private val locationApi: LocationApi
) : LocationRepository {

    private val locationDao = database.locationDao()

    override fun getLocations(): Flow<List<Location>> {
        return locationDao.getAll().map { entities ->
            entities.map { entity -> entity.toModel() }
        }
    }

    override fun getLocationById(id: Int): Flow<Location?> {
        return locationDao.getById(id).map { entity -> entity?.toModel() }
    }

    override suspend fun synchronize() {
        val locations = locationApi.getAllLocations().map { dto -> dto.toEntity() }

        database.useWriterConnection { connection ->
            connection.immediateTransaction {
                locationDao.deleteAll()
                locationDao.insertAll(locations)
            }
        }
    }

    override suspend fun toggleFavorite(id: Int) {
        val updatedRows = locationDao.toggleFavorite(id)
        check(updatedRows == 1) {
            "La ubicación ya no existe."
        }
    }
}
