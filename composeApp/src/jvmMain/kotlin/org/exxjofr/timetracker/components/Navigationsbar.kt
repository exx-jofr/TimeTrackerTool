package org.exxjofr.timetracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NavigationBar( onNavigateTo: (String) -> Unit) {
    var selectedItem by remember { mutableStateOf("Startseite") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF333333))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .focusable(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem("Startseite", selectedItem) { selectedItem = it
        onNavigateTo("main")}
        NavItem("Übersicht", selectedItem) { selectedItem = it
        onNavigateTo("overview")}
        NavItem("Einstellungen", selectedItem) { selectedItem = it
        onNavigateTo("settings")}
        NavItem("Hilfe", selectedItem) { selectedItem = it
            onNavigateTo("help")}
    }
}

@Composable
fun NavItem(label: String, selectedItem: String, onClick: (String) -> Unit) {
    Text(
        text = label,
        fontSize = 18.sp,
        color = if (selectedItem == label) Color.Yellow else Color.White,
        modifier = Modifier.clickable { onClick(label) }
    )
}