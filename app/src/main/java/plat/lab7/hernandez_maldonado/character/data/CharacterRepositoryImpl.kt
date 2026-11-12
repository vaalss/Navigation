package plat.lab7.hernandez_maldonado.character.data

import android.os.SystemClock
import android.util.Log
import androidx.room3.immediateTransaction
import androidx.room3.useWriterConnection
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import plat.lab7.hernandez_maldonado.character.domain.CharacterRepository
import plat.lab7.hernandez_maldonado.character.network.CharacterApi
import plat.lab7.hernandez_maldonado.core.data.AdaptiveDownloadController
import plat.lab7.hernandez_maldonado.core.data.AppDatabase

class CharacterRepositoryImpl(
    private val database: AppDatabase,
    private val characterApi: CharacterApi,
    private val imageStorage: CharacterImageStorage
) : CharacterRepository {

    private val characterDao = database.characterDao()

    override fun getCharacters(): Flow<List<Character>> {
        return characterDao.getAll().map { entities ->
            entities.map { entity -> entity.toModel() }
        }
    }

    override fun getCharacterById(id: Int): Flow<Character?> {
        return characterDao.getById(id).map { entity -> entity?.toModel() }
    }

    override suspend fun synchronize() {
        val startedAt = SystemClock.elapsedRealtime()
        val remoteCharacters = characterApi.getAllCharacters()
        val imagesStartedAt = SystemClock.elapsedRealtime()

        Log.d(
            "LoginSync",
            "JSON de personajes: ${imagesStartedAt - startedAt}ms"
        )

        val controller = AdaptiveDownloadController()
        val completed = AtomicInteger(0)
        val downloaded = AtomicInteger(0)

        val characters = coroutineScope {
            remoteCharacters.map { dto ->
                async {
                    val savedImage = imageStorage.getSavedImage(dto.id)

                    val localImage = if (savedImage != null) {
                        savedImage
                    } else {
                        val bytes = controller.execute {
                            characterApi.downloadImage(dto.image)
                        }

                        val image = imageStorage.save(dto.id, bytes)
                        downloaded.incrementAndGet()
                        image
                    }

                    val count = completed.incrementAndGet()
                    Log.d(
                        "LoginSync",
                        "Imagen $count/${remoteCharacters.size}: " +
                                if (savedImage != null) "reutilizada" else "descargada"
                    )

                    dto.toEntity().copy(image = localImage)
                }
            }.awaitAll()
        }

        Log.d(
            "LoginSync",
            "Imágenes: ${SystemClock.elapsedRealtime() - imagesStartedAt}ms; " +
                    "descargadas=${downloaded.get()}; " +
                    "reutilizadas=${remoteCharacters.size - downloaded.get()}"
        )

        database.useWriterConnection { connection ->
            connection.immediateTransaction {
                characterDao.deleteAll()
                characterDao.insertAll(characters)
            }
        }
    }

    override suspend fun updateFavorite(id: Int, isFavorite: Boolean) {
        characterDao.updateFavorite(id, isFavorite)
    }

    override suspend fun createCharacter(
        name: String,
        status: String,
        species: String,
        gender: String
    ): Int {
        val cleanName = name.trim()

        require(cleanName.isNotBlank()) {
            "El nombre es obligatorio."
        }

        return database.useWriterConnection { connection ->
            connection.immediateTransaction {
                val id = characterDao.getNextLocalId()

                characterDao.insert(
                    CharacterEntity(
                        id = id,
                        name = cleanName,
                        status = status.trim(),
                        species = species.trim(),
                        gender = gender.trim(),
                        image = "",
                        isFavorite = false
                    )
                )

                id
            }
        }
    }

    override suspend fun updateCharacter(
        id: Int,
        name: String,
        status: String,
        species: String,
        gender: String
    ) {
        val cleanName = name.trim()

        require(cleanName.isNotBlank()) {
            "El nombre es obligatorio."
        }

        val updatedRows = characterDao.updateCharacter(
            id = id,
            name = cleanName,
            status = status.trim(),
            species = species.trim(),
            gender = gender.trim()
        )

        check(updatedRows == 1) {
            "El personaje ya no existe."
        }
    }

    override suspend fun deleteCharacter(id: Int) {
        val deletedRows = characterDao.deleteCharacter(id)

        check(deletedRows == 1) {
            "El personaje ya no existe."
        }
    }
}
