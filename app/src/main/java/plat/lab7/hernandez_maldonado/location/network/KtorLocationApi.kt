package plat.lab7.hernandez_maldonado.location.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import plat.lab7.hernandez_maldonado.core.network.PageResponseDTO

class KtorLocationApi(
    private val httpClient: HttpClient
) : LocationApi {

    override suspend fun getAllLocations(): List<LocationDTO> {
        val locations = mutableListOf<LocationDTO>()
        var page = 1

        do {
            val response = httpClient.get("location") {
                parameter("page", page)
            }.body<PageResponseDTO<LocationDTO>>()

            locations.addAll(response.results)
            page++
        } while (response.info.next != null)

        return locations
    }
}