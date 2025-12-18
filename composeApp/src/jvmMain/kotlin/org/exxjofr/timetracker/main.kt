package org.exxjofr.timetracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.exxjofr.timetracker.ViewModel.SettingsModel
import org.exxjofr.timetracker.components.App
import org.exxjofr.timetracker.components.TrayPopupWindow
import org.exxjofr.timetracker.components.setupSystemTray
import java.io.File

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    var showTrayWindow by remember { mutableStateOf(false) }
    var showMainWindow by remember { mutableStateOf(true) }

    val dataStore = remember {
        PreferenceDataStoreFactory.create(scope = CoroutineScope(Dispatchers.IO)) {
            File("settings.preferences_pb")  // Speicherort
        }
    }
    val settingsRepo = remember { SettingsRepository(dataStore) }
    val settingsModel = remember { SettingsModel.create(settingsRepo) }


    setupSystemTray { showTrayWindow = true }

    // === Compose Fenster ===
    if (showTrayWindow) {
        Window(
            onCloseRequest = { showTrayWindow = false },
            title = "Control Panel",
            state = WindowState(position = WindowPosition.Aligned(Alignment.BottomStart),
                width = 300.dp, height = 300.dp),
            resizable = false,
        ) {
            TrayPopupWindow(model = settingsModel) {
                showTrayWindow = false
            }
        }
    }


    if (showMainWindow) {
        Window(
            onCloseRequest = { showMainWindow = false }, // nur verstecken!
            title = "Time Tracker"
        ) {
            App(model = settingsModel)
        }
    }
}