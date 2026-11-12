package plat.lab7.hernandez_maldonado.character.network

interface CharacterApi {
    suspend fun getAllCharacters(): List<CharacterDTO>
}