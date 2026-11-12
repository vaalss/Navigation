package plat.lab7.hernandez_maldonado

import android.app.Application
import androidx.room3.Room
import io.ktor.client.HttpClient
import plat.lab7.hernandez_maldonado.core.data.AppDatabase
import plat.lab7.hernandez_maldonado.core.network.HttpClientFactory

class RickAndMortyApp : Application() {

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "rick_and_morty.db"
        ).build()
    }

    val httpClient: HttpClient by lazy {
        HttpClientFactory.create()
    }
}