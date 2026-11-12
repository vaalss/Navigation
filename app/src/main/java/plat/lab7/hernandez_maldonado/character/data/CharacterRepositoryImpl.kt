package plat.lab7.hernandez_maldonado.character.data

import androidx.room3.immediateTransaction
import androidx.room3.useWriterConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import plat.lab7.hernandez_maldonado.character.domain.CharacterRepository
import plat.lab7.hernandez_maldonado.character.network.CharacterApi
import plat.lab7.hernandez_maldonado.core.data.AppDatabase
import android.util.Log
import kotlinx.coroutines.delay

class CharacterRepositoryImpl(
    private val database: AppDatabase,
    private val characterApi: CharacterApi,
    private val imageStorage: CharacterImageStorage
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
        val remoteCharacters = characterApi.getAllCharacters()
        val characters = mutableListOf<CharacterEntity>()

        for ((index, dto) in remoteCharacters.withIndex()) {
            val savedImage = imageStorage.getSavedImage(dto.id)

            val localImage = if (savedImage != null) {
                savedImage
            } else {
                delay(1_000)

                val bytes = characterApi.downloadImage(dto.image)

                imageStorage.save(
                    characterId = dto.id,
                    bytes = bytes
                )
            }

            characters.add(
                dto.toEntity().copy(
                    image = localImage
                )
            )

            Log.d(
                "LoginSync",
                "Imagen ${index + 1}/${remoteCharacters.size}: " +
                        if (savedImage != null) "reutilizada" else "descargada"
            )
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