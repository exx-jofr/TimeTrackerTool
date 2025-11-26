package org.exxjofr.timetracker

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.exxjofr.timetracker.repository.SettingsKeys
import java.io.File
import java.io.IOException

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    val pathFile: Flow<String> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[SettingsKeys.PATH_FILE] ?: "" }

    val pathExcel: Flow<String> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[SettingsKeys.PATH_EXCEL] ?: "" }

    val apiKey: Flow<String> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[SettingsKeys.API_KEY] ?: "" }

    val user: Flow<String> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[SettingsKeys.USER] ?: "" }

    suspend fun savePathFile(value: String) {
        dataStore.edit { prefs -> prefs[SettingsKeys.PATH_FILE] = value }
    }
    suspend fun savePathExcel(value: String) {
        dataStore.edit { prefs -> prefs[SettingsKeys.PATH_EXCEL] = value }
    }
    suspend fun saveApiKey(value: String) {
        dataStore.edit { prefs -> prefs[SettingsKeys.API_KEY] = value }
    }
    suspend fun saveUser(value: String) {
        dataStore.edit { prefs -> prefs[SettingsKeys.USER] = value }
    }

    companion object {
        private const val DEFAULT_FILENAME = ".timetracker_prefs.preferences_pb"

        fun createDefault(filename: String = DEFAULT_FILENAME): SettingsRepository {
            val finalName = if (filename.endsWith(".preferences_pb")) filename else "$filename.preferences_pb"
            val file = File(System.getProperty("user.home"), finalName)
            file.parentFile?.mkdirs() // sicherstellen, dass das Verzeichnis existiert

            val ds = PreferenceDataStoreFactory.create(
                scope = CoroutineScope(Dispatchers.IO),
                produceFile = { file }
            )
            return SettingsRepository(ds)
        }
    }
}