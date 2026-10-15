package plat.lab7.hernandez_maldonado.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)

class DataStorePreferences(context: Context) {
    private val dataStore = context.applicationContext.dataStore
    private val nameKey = stringPreferencesKey("name")

    suspend fun saveName(name: String) {
        dataStore.edit { preferences ->
            preferences[nameKey] = name
        }
    }

    fun getName(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[nameKey]
        }
    }

    suspend fun clearName() {
        dataStore.edit { preferences ->
            preferences.remove(nameKey)
        }
    }
}