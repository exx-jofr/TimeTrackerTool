package org.exxjofr.timetracker

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    val pathFileCsv: Flow<String> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[PATH_FILE_CSV] ?: "Unknown" }

    val pathFileExcel: Flow<String> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[PATH_FILE_EXCEL] ?: "Unknown" }

    val apiKey: Flow<String> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[API_KEY] ?: "Unknown" }

    val userName: Flow<String> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[USER_NAME] ?: "Unknown" }

    suspend fun savePathFileCsv(path: String) {
        dataStore.edit { prefs -> prefs[PATH_FILE_CSV] = path }
    }
    suspend fun savePathFileExcel(path: String) {
        dataStore.edit { prefs -> prefs[PATH_FILE_EXCEL] = path }
    }
    suspend fun saveApiKey(apiKey: String) {
        dataStore.edit { prefs -> prefs[API_KEY] = apiKey }
    }
    suspend fun saveUserName(userName: String) {
        dataStore.edit { prefs -> prefs[USER_NAME] = userName }
    }

    companion object {
        val PATH_FILE_CSV = stringPreferencesKey("path_file_csv")
        val PATH_FILE_EXCEL = stringPreferencesKey("path_file_excel")
        val API_KEY = stringPreferencesKey("api_key")
        val USER_NAME = stringPreferencesKey("user_name")
    }
}