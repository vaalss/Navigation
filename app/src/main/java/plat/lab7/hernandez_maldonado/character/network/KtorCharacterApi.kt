package plat.lab7.hernandez_maldonado.character.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlinx.coroutines.delay
import plat.lab7.hernandez_maldonado.core.domain.ApiRateLimitException
import plat.lab7.hernandez_maldonado.core.network.PageResponseDTO

class KtorCharacterApi(
    private val httpClient: HttpClient
) : CharacterApi {

    override suspend fun getAllCharacters(): List<CharacterDTO> {
        val characters = mutableListOf<CharacterDTO>()
        var page = 1

        do {
            delay(1_000)

            val response = httpClient.get("character") {
                parameter("page", page)
            }.body<PageResponseDTO<CharacterDTO>>()

            characters.addAll(response.results)
            page++
        } while (
            response.info.next != null &&
            characters.size < 120
        )

        return characters.take(120)
    }

    override suspend fun downloadImage(url: String): ByteArray {
        try {
            return httpClient.get(url).body()
        } catch (exception: ClientRequestException) {
            if (exception.response.status == HttpStatusCode.TooManyRequests) {
                throw ApiRateLimitException(
                    cause = exception,
                    retryAfterMillis = parseRetryAfter(
                        exception.response.headers["Retry-After"]
                    )
                )
            }

            throw exception
        }
    }

    private fun parseRetryAfter(value: String?): Long? {
        val header = value?.trim() ?: return null

        header.toLongOrNull()?.let { seconds ->
            return seconds.coerceIn(0L, Long.MAX_VALUE / 1_000) * 1_000
        }

        return try {
            val retryAt = ZonedDateTime.parse(
                header,
                DateTimeFormatter.RFC_1123_DATE_TIME
            ).toInstant()

            Duration.between(Instant.now(), retryAt)
                .toMillis()
                .coerceAtLeast(0L)
        } catch (exception: DateTimeParseException) {
            null
        }
    }
}
