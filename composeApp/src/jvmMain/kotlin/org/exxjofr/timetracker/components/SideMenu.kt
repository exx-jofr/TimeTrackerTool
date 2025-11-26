package org.exxjofr.timetracker.components

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.exxjofr.timetracker.TimeTable

@Composable
fun SideMenu(onNavigateTo: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Row (Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp).border(width = 1.dp, color = Color
        (0xFF000000)).focusable()) {
        NavigationBar( onNavigateTo = onNavigateTo)
    }
}