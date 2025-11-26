package org.exxjofr.timetracker.ViewModel

import org.exxjofr.timetracker.SettingsRepository

class SettingsModel(private val repo: SettingsRepository) {

    val pathFileCsv = repo.pathFileCsv
    val pathFileExcel = repo.pathFileExcel
    val apiKey = repo.apiKey
    val username = repo.userName

    suspend fun saveAll(pathFile: String, pathExcel: String, apiKey: String, user: String) {
        repo.savePathFileCsv(pathFile)
        repo.savePathFileExcel(pathExcel)
        repo.saveApiKey(apiKey)
        repo.saveUserName(user)
    }
    companion object {
        fun create(userRepository: SettingsRepository): SettingsModel {
            return SettingsModel(userRepository)
        }
    }
}

