package org.exxjofr.timetracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.apache.logging.log4j.LogManager
import org.exxjofr.timetracker.SettingsRepository
import org.exxjofr.timetracker.TimeTable
import org.exxjofr.timetracker.ViewModel.SettingsModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.io.File


private val logger = LogManager.getLogger("App")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf("main") }
    var checkedPathCsv by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // 🆕 DataStore für Desktop erstellen
    val dataStore = remember {
        PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO)) {
            File("settings.preferences_pb")  // Speicherort
        }
    }
    val settingsRepo = remember { SettingsRepository(dataStore) }
    val settingsModel = remember { SettingsModel.create(settingsRepo) }
    val pathCsv by settingsModel.pathFileCsv.collectAsState(initial = "")

    if (pathCsv.isNotEmpty() && pathCsv.contains("jofr")) {
        checkedPathCsv = true
    }

    val timeTable by remember(checkedPathCsv, pathCsv) {
        mutableStateOf(if (checkedPathCsv) TimeTable(pathCsv) else null)
    }

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
                            if (checkedPathCsv && timeTable != null) {
                                Body(timeTable = timeTable!!)
                            }
                        }

                        "settings" -> {
                            Settings(
                                viewModel = settingsModel,
                                onNavigateTo = { newScreen ->
                                    currentScreen = newScreen
                                }
                            )
                        }

                        "overview" -> {
                            if (checkedPathCsv && timeTable != null) {
                                TableScreen(timeTable = timeTable!!)
                            }
                        }

                    }
                }

                logger.info("Current Screen: $currentScreen")
            }
        }
    }
}