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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import org.apache.logging.log4j.LogManager
import org.exxjofr.timetracker.SettingsRepository
import org.exxjofr.timetracker.TimeTable
import org.exxjofr.timetracker.ViewModel.SettingsViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview


private val logger = LogManager.getLogger("App")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf("main") }
    val timetable by remember { mutableStateOf(TimeTable("C:\\Users\\jofr\\Downloads\\timetracker_2025.csv")) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = { SideMenu(
            onNavigateTo = { newScreen ->
                currentScreen = newScreen
            }) },
        snackbarHost = { SnackbarHost(hostState = SnackbarManager.snackbarHostState, modifier = Modifier.width(300
            .dp)) }
    ) {
        innerPadding ->
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
                            Body(timetable)
                        }

                        "settings" -> {
                            val settingsViewModel: SettingsViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(
                                        modelClass: kotlin.reflect.KClass<T>,
                                        extras: androidx.lifecycle.viewmodel.CreationExtras
                                    ): T {
                                        if (modelClass.java.isAssignableFrom(SettingsViewModel::class.java)) {
                                            @Suppress("UNCHECKED_CAST")
                                            return SettingsViewModel(SettingsRepository.createDefault()) as T
                                        }
                                        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
                                    }
                                }
                            )
                            Settings(
                                viewModel = settingsViewModel,
                                onNavigateTo = { newScreen ->
                                    currentScreen = newScreen
                                }
                            )
                        }

                        "overview" -> TableScreen(
                            timeTable = timetable
                        )
                    }
                }

                logger.info("Current Screen: $currentScreen")
            }
        }
    }
}