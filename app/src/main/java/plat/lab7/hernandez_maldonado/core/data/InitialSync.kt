package plat.lab7.hernandez_maldonado.core.data

import androidx.room3.immediateTransaction
import androidx.room3.useWriterConnection
import kotlinx.coroutines.delay
import plat.lab7.hernandez_maldonado.character.data.CharacterDb
import plat.lab7.hernandez_maldonado.character.data.toEntity
import plat.lab7.hernandez_maldonado.location.data.LocationDb
import plat.lab7.hernandez_maldonado.location.data.toEntity

class InitialSync(
    private val database: AppDatabase
) {

    private val characterDao = database.characterDao()
    private val locationDao = database.locationDao()

    suspend fun synchronize() {
        val hasCharacters = characterDao.count() > 0
        val hasLocations = locationDao.count() > 0

        if (hasCharacters && hasLocations) {
            return
        }

        delay(4000)

        database.useWriterConnection { connection ->
            connection.immediateTransaction {

                if (characterDao.count() == 0) {
                    val characters = CharacterDb()
                        .getAllCharacters()
                        .map { character ->
                            character.toEntity()
                        }

                    characterDao.insertAll(characters)
                }

                if (locationDao.count() == 0) {
                    val locations = LocationDb()
                        .getAllLocations()
                        .map { location ->
                            location.toEntity()
                        }

                    locationDao.insertAll(locations)
                }
            }
        }
    }
}