package plat.lab7.hernandez_maldonado.character.network

import kotlinx.serialization.Serializable

@Serializable
data class CharacterDTO(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String
)