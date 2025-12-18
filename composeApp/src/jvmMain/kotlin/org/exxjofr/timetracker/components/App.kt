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
    var checkedPathCsv by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // 🆕 DataStore für Desktop erstellen

    val pathCsv by model.pathFileCsv.collectAsState(initial = "")
    val pathExcel by model.pathFileExcel.collectAsState(initial = "")
    val username by model.username.collectAsState(initial = "")
    val apiKey by model.apiKey.collectAsState(initial = "")

    if (pathCsv.isNotEmpty()) {
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
                                viewModel = model,
                                onNavigateTo = { newScreen ->
                                    currentScreen = newScreen
                                }
                            )
                        }

                        "overview" -> {
                            if (checkedPathCsv && timeTable != null) {
                                TableScreen(timeTable = timeTable!!, pathExcel = pathExcel, userName= username,
                                    apiKey = apiKey)
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