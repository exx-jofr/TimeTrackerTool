package org.exxjofr.timetracker.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SnackbarManager {

    // Globaler Snackbar Host State
    val snackbarHostState = SnackbarHostState()

    // interner CoroutineScope fürs Anzeigen
    private val scope = CoroutineScope(Dispatchers.Main)

    fun showMessage(message: String, duration: SnackbarDuration = SnackbarDuration.Short) {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = duration
            )
        }
    }
}