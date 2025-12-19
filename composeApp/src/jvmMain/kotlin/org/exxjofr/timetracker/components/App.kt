package org.exxjofr.timetracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.apache.logging.log4j.LogManager
import org.exxjofr.timetracker.TimeTable
import org.exxjofr.timetracker.ViewModel.SettingsModel


private val logger = LogManager.getLogger("App")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(model: SettingsModel) {
    var currentScreen by remember { mutableStateOf("main") }
    val focusManager = LocalFocusManager.current

    // Load settings from DataStore
    val pathCsv by model.pathFileCsv.collectAsState(initial = "")
    val pathExcel by model.pathFileExcel.collectAsState(initial = "")
    val username by model.username.collectAsState(initial = "")
    val apiKey by model.apiKey.collectAsState(initial = "")

    // Only try to load TimeTable if a valid CSV path is set and file exists
    val timeTable by remember(pathCsv) {
        mutableStateOf(
            if (pathCsv.isNotEmpty()) {
                val csvDirectory = java.io.File(pathCsv)
                // pathCsv is a directory, construct the full file path
                val year = java.time.LocalDate.now().year
                val csvFile = java.io.File("$pathCsv/timetracker_$year.csv")

                if (csvDirectory.exists() && csvDirectory.isDirectory && csvFile.exists() && csvFile.isFile) {
                    try {
                        TimeTable("$pathCsv/timetracker_$year.csv")
                    } catch (e: Exception) {
                        logger.error("Error loading TimeTable from path: $pathCsv", e)
                        null
                    }
                } else {
                    null
                }
            } else {
                null
            }
        )
    }

    // Check if CSV path is valid and TimeTable loaded successfully
    val csvPathValid = pathCsv.isNotEmpty() && timeTable != null

    Scaffold(
        topBar = { SideMenu(
            onNavigateTo = { newScreen ->
                currentScreen = newScreen
            }) },
        snackbarHost = { SnackbarHost(hostState = SnackbarManager.snackbarHostState, modifier = Modifier.width(300
            .dp)) }
    ) {

        MaterialTheme {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .safeContentPadding()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp)
                    .fillMaxSize()
                    .onPreviewKeyEvent { keyEvent ->
                        if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Tab) {
                            // Shift+Tab zurück, Tab vorwärts
                            if (keyEvent.isShiftPressed) focusManager.moveFocus(FocusDirection.Previous)
                            else focusManager.moveFocus(FocusDirection.Next)
                            true
                        } else false
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(40.dp))
                Box(
                    Modifier
                        .weight(1f) // nimmt alles zwischen Header und Buttons ein
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()) // Scroll nur für Body-Inhalt
                        .padding(horizontal = 20.dp)
                        .focusable()
                ) {
                    when (currentScreen) {
                        "main" -> {
                            if (csvPathValid && timeTable != null) {
                                Body(timeTable = timeTable!!)
                            } else {
                                // Show helpful message when no CSV path is configured
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Bitte konfigurieren Sie zuerst einen CSV-Dateipfad", style = MaterialTheme.typography.headlineSmall)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(onClick = { currentScreen = "settings" }) {
                                        Text("Zu den Einstellungen")
                                    }
                                }
                            }
                        }

                        "settings" -> {
                            Settings(
                                viewModel = model,
                                onNavigateTo = { newScreen ->
                                    currentScreen = newScreen
                                }
                            )
                        }

                        "overview" -> {
                            if (csvPathValid && timeTable != null) {
                                TableScreen(timeTable = timeTable!!, pathExcel = pathExcel, userName= username,
                                    apiKey = apiKey)
                            } else {
                                // Show helpful message when no CSV path is configured
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Bitte konfigurieren Sie zuerst einen CSV-Dateipfad", style = MaterialTheme.typography.headlineSmall)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(onClick = { currentScreen = "settings" }) {
                                        Text("Zu den Einstellungen")
                                    }
                                }
                            }
                        }

                        "help" -> {
                            Help()
                        }
                    }
                }

                logger.info("Current Screen: $currentScreen")
            }
        }
    }
}