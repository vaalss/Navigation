package plat.lab7.hernandez_maldonado

import android.app.Application
import androidx.room3.Room
import plat.lab7.hernandez_maldonado.core.data.AppDatabase

class RickAndMortyApp : Application() {

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "rick_and_morty.db"
        ).build()
    }
}