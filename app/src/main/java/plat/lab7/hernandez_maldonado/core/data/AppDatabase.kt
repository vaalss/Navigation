package plat.lab7.hernandez_maldonado.core.data

import androidx.room3.Database
import androidx.room3.RoomDatabase
import plat.lab7.hernandez_maldonado.character.data.CharacterDao
import plat.lab7.hernandez_maldonado.character.data.CharacterEntity
import plat.lab7.hernandez_maldonado.location.data.LocationDao
import plat.lab7.hernandez_maldonado.location.data.LocationEntity

@Database(
    entities = [
        CharacterEntity::class,
        LocationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun characterDao(): CharacterDao

    abstract fun locationDao(): LocationDao
}