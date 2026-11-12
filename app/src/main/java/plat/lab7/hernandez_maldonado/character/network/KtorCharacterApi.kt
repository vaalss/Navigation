package plat.lab7.hernandez_maldonado.character.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import plat.lab7.hernandez_maldonado.core.network.PageResponseDTO

class KtorCharacterApi(
    private val httpClient: HttpClient
) : CharacterApi {

    override suspend fun getAllCharacters(): List<CharacterDTO> {
        val characters = mutableListOf<CharacterDTO>()
        var page = 1

        do {
            val response = httpClient.get("character") {
                parameter("page", page)
            }.body<PageResponseDTO<CharacterDTO>>()

            characters.addAll(response.results)
            page++
        } while (response.info.next != null)

        return characters
    }
}