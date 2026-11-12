package plat.lab7.hernandez_maldonado.core.network

import kotlinx.serialization.Serializable

@Serializable
data class PageResponseDTO<T>(
    val info: PageInfoDTO,
    val results: List<T>
)

@Serializable
data class PageInfoDTO(
    val pages: Int,
    val next: String?
)