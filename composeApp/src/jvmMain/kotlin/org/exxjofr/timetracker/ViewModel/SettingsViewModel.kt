package org.exxjofr.timetracker.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.exxjofr.timetracker.SettingsRepository

class SettingsViewModel(private val repo: SettingsRepository) : ViewModel() {
    val pathFile = repo.pathFile
    val pathExcel = repo.pathExcel
    val apiKey = repo.apiKey
    val user = repo.user

    fun saveAll(pathFile: String, pathExcel: String, apiKey: String, user: String) {
        viewModelScope.launch {
            repo.savePathFile(pathFile)
            repo.savePathExcel(pathExcel)
            repo.saveApiKey(apiKey)
            repo.saveUser(user)
        }
    }
}