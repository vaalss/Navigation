package plat.lab7.hernandez_maldonado

import android.app.Application
import androidx.room3.Room
import io.ktor.client.HttpClient
import plat.lab7.hernandez_maldonado.character.network.CharacterApi
import plat.lab7.hernandez_maldonado.character.network.KtorCharacterApi
import plat.lab7.hernandez_maldonado.core.data.AppDatabase
import plat.lab7.hernandez_maldonado.core.network.HttpClientFactory
import plat.lab7.hernandez_maldonado.location.network.KtorLocationApi
import plat.lab7.hernandez_maldonado.location.network.LocationApi
import plat.lab7.hernandez_maldonado.character.data.CharacterRepositoryImpl
import plat.lab7.hernandez_maldonado.character.domain.CharacterRepository
import plat.lab7.hernandez_maldonado.location.data.LocationRepositoryImpl
import plat.lab7.hernandez_maldonado.location.domain.LocationRepository

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

    val characterApi: CharacterApi by lazy {
        KtorCharacterApi(httpClient)
    }

    val locationApi: LocationApi by lazy {
        KtorLocationApi(httpClient)
    }

    val characterRepository: CharacterRepository by lazy {
        CharacterRepositoryImpl(
            database = database,
            characterApi = characterApi
        )
    }

    val locationRepository: LocationRepository by lazy {
        LocationRepositoryImpl(
            database = database,
            locationApi = locationApi
        )
    }
}