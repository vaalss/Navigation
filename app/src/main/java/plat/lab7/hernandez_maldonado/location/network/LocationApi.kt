package plat.lab7.hernandez_maldonado.location.network

interface LocationApi {
    suspend fun getAllLocations(): List<LocationDTO>
}