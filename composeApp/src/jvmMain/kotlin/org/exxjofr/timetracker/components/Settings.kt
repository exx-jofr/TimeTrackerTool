package org.exxjofr.timetracker.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.exxjofr.timetracker.TimeTable
import org.exxjofr.timetracker.ViewModel.SettingsViewModel

@Composable
fun Settings(viewModel: SettingsViewModel, onNavigateTo: (String) -> Unit) {
    val pathFile by viewModel.pathFile.collectAsState(initial = "")
    val pathExcel by viewModel.pathExcel.collectAsState(initial = "")
    val apiKey by viewModel.apiKey.collectAsState(initial = "")
    val user by viewModel.user.collectAsState(initial = "")
    var pathCsv = ""
    var pathExcelVar = ""
    var apiKeyVar = ""
    var userVar = ""

    Column (modifier = Modifier.fillMaxWidth().padding(16.dp).focusable(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = pathCsv,
            onValueChange = { pathCsv = it },
            label = { Text("CSV-Datei-Pfad") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = pathExcelVar,
            onValueChange = { pathExcelVar = it },
            label = { Text("Excel-Datei-Pfad") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = userVar,
            onValueChange = { userVar = it },
            label = { Text("Jira Benutzer") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKeyVar = it },
            label = { Text("Dein API-Key") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(), // 👈 hier wird der Text maskiert
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Button(
            modifier = Modifier.padding(16.dp),
            onClick = {
                if (saveUserName(userVar) && saveApiKey(apiKeyVar)) {
                    viewModel.saveAll(
                        pathFile = pathCsv,
                        pathExcel = pathExcelVar,
                        apiKey = apiKeyVar,
                        user = userVar
                    )
                }
                saveCSV(pathFile)

                onNavigateTo("main")
            }
        ) {

            Text("Speichern")
        }
    }

}

private fun saveCSV(pathFile: String) {
    if (pathFile.isNotEmpty()) {
        print("Speicherort gespeichert: $pathFile")
        TimeTable.createFile(pathFile)
    }
}

private fun saveUserName(user: String): Boolean {
    if (user.isNotEmpty()) {
        SnackbarManager.showMessage("Benutzername gespeichert: $user")
        return true
    }
    return false
}

private fun saveApiKey(apiKey: String): Boolean {
    if (apiKey.isNotEmpty()) {
        SnackbarManager.showMessage("API-Key gespeichert.")
        return true
    }
    return false
}

