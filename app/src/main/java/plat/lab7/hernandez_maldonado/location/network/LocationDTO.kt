package plat.lab7.hernandez_maldonado.location.network

import kotlinx.serialization.Serializable

@Serializable
data class LocationDTO(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String
)