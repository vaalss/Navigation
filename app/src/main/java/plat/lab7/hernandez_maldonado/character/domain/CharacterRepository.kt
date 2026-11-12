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
}