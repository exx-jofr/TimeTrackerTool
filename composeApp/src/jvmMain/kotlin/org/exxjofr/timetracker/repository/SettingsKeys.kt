package org.exxjofr.timetracker.repository

import androidx.datastore.preferences.core.stringPreferencesKey

object SettingsKeys {
        var PATH_FILE = stringPreferencesKey("path_file")
        var PATH_EXCEL = stringPreferencesKey("path_excel")
        var API_KEY = stringPreferencesKey("api_key")
        var USER = stringPreferencesKey("user")
}