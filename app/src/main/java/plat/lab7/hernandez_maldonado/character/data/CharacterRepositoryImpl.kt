package plat.lab7.hernandez_maldonado.character.data

import androidx.room3.immediateTransaction
import androidx.room3.useWriterConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import plat.lab7.hernandez_maldonado.character.domain.CharacterRepository
import plat.lab7.hernandez_maldonado.character.network.CharacterApi
import plat.lab7.hernandez_maldonado.core.data.AppDatabase

class CharacterRepositoryImpl(
    private val database: AppDatabase,
    private val characterApi: CharacterApi
) : CharacterRepository {

    private val characterDao = database.characterDao()

    override fun getCharacters(): Flow<List<Character>> {
        return characterDao.getAll().map { entities ->
            entities.map { entity ->
                entity.toModel()
            }
        }
    }

    override fun getCharacterById(id: Int): Flow<Character?> {
        return characterDao.getById(id).map { entity ->
            entity?.toModel()
        }
    }

    override suspend fun synchronize() {
        val characters = characterApi.getAllCharacters().map { dto ->
            dto.toEntity()
        }

        database.useWriterConnection { connection ->
            connection.immediateTransaction {
                characterDao.deleteAll()
                characterDao.insertAll(characters)
            }
        }
    }

    override suspend fun updateFavorite(
        id: Int,
        isFavorite: Boolean
    ) {
        characterDao.updateFavorite(
            id = id,
            isFavorite = isFavorite
        )
    }
}