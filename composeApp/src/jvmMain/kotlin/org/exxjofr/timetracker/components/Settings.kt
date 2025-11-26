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
import kotlinx.coroutines.launch
import org.exxjofr.timetracker.TimeTable
import org.exxjofr.timetracker.ViewModel.SettingsModel
import java.io.File

@Composable
fun Settings(viewModel: SettingsModel, onNavigateTo: (String) -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    // Werte aus Repo beobachten
    val pathFile by viewModel.pathFileCsv.collectAsState(initial = "")
    val pathExcel by viewModel.pathFileExcel.collectAsState(initial = "")
    val apiKey by viewModel.apiKey.collectAsState(initial = "")
    val user by viewModel.username.collectAsState(initial = "")

    // Bearbeitbarer State
    var pathCsv by remember { mutableStateOf(pathFile) }
    var pathExcelVar by remember { mutableStateOf(pathExcel) }
    var apiKeyVar by remember { mutableStateOf(apiKey) }
    var userVar by remember { mutableStateOf(user) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp).focusable(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
            value = apiKeyVar,
            onValueChange = { apiKeyVar = it },
            label = { Text("Dein API-Key") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Button(
            modifier = Modifier.padding(16.dp),
            onClick = {
                if (saveUserName(userVar) && saveApiKey(apiKeyVar)) {
                    SnackbarManager.showMessage("Einstellungen werden gespeichert...")
                    coroutineScope.launch {
                        viewModel.saveAll(
                            pathCsv,
                            pathExcelVar,
                            apiKeyVar,
                            userVar
                        )
                        saveCSV(pathCsv)
                        onNavigateTo("main")
                    }
                }
            }
        ) {
            Text("Speichern")
        }
        Button(
            onClick = {
                File("settings.preferences_pb").delete()
            }
        ) {
            Text("Zurücksetzen der Einstellungen")
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

