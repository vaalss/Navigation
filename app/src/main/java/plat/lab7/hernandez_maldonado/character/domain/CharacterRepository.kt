package plat.lab7.hernandez_maldonado.character.domain

import kotlinx.coroutines.flow.Flow
import plat.lab7.hernandez_maldonado.character.data.Character

interface CharacterRepository {

    fun getCharacters(): Flow<List<Character>>

    fun getCharacterById(id: Int): Flow<Character?>

    suspend fun synchronize()

    suspend fun updateFavorite(
        id: Int,
        isFavorite: Boolean
    )

    suspend fun createCharacter(
        name: String,
        status: String,
        species: String,
        gender: String
    ): Int

    suspend fun updateCharacter(
        id: Int,
        name: String,
        status: String,
        species: String,
        gender: String
    )

    suspend fun deleteCharacter(id: Int)
}