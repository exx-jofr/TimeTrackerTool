package org.exxjofr.timetracker.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.exxjofr.timetracker.Task
import org.exxjofr.timetracker.TimeTable
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Body(timeTable: TimeTable) {
    var initialJiraID by remember { mutableStateOf("") }
    var initialDesc by remember { mutableStateOf("") }
    var initialStart by remember { mutableStateOf("") }
    var initialEnd by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    var expanded by remember { mutableStateOf(false) }

    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val ids by remember { mutableStateOf(timeTable.loadingIDs()) }

    fun berechneDauer() {
        try {
            val start = LocalTime.parse(initialStart, timeFormatter)
            val end = LocalTime.parse(initialEnd, timeFormatter)
            val diff = Duration.between(start, end)
            duration = "${diff.toHours()}h ${diff.toMinutesPart()}min"
        } catch (_: Exception) {
            duration = ""
        }
    }

    fun resetFields() {
        initialJiraID = ""
        date = ""
        initialDesc = ""
        initialStart = ""
        initialEnd = ""
        duration = ""
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp).focusable(), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        if (ids.isNotEmpty()) {
            ExposedDropdownMenuBox(
                expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                // Das "integrierte" Dropdown-Textfeld
                OutlinedTextField(
                    value = initialJiraID,
                    onValueChange = {initialJiraID = it},
                    label = { Text("Vorgangs-ID") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )

                // Das Menü
                ExposedDropdownMenu(
                    expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.height(100.dp),
                ) {
                    ids.forEach { label ->
                        DropdownMenuItem(text = { Text(label, textAlign = TextAlign.Center) }, onClick = {
                            initialJiraID = label
                            expanded = false
                        })
                    }
                }
            }
        } else {
            // No suggestions available - show plain text field for manual entry
            OutlinedTextField(
                value = initialJiraID,
                onValueChange = {initialJiraID = it},
                label = { Text("Vorgangs-ID") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = initialDesc,
            onValueChange = { initialDesc = it },
            label = { Text("Vorgangs-Beschreibung") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp), // Einheitliche Höhe für alle Felder
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Abstand
        ) {
            DatePickerFieldToModal(
                modifier = Modifier.weight(1f), pickedDate = { pickedDate ->
                    date = pickedDate.toString()
                })

            OutlinedTextField(
                value = initialStart, onValueChange = {
                initialStart = it
                if (initialEnd.isNotEmpty()) berechneDauer()
            }, label = { Text("Startzeit") }, modifier = Modifier.weight(1f), placeholder = { Text("HH:mm") }
            )

            OutlinedTextField(
                value = initialEnd, onValueChange = {
                initialEnd = it
                if (initialStart.isNotEmpty()) berechneDauer()
            }, label = { Text("Endzeit") }, modifier = Modifier.weight(1f), placeholder = { Text("HH:mm") }
            )
            OutlinedTextField(
                value = duration,
                onValueChange = {
                    duration = it
                    val timeRegex = Regex("^([01]\\d|2[0-3]):([0-5]\\d)$")
                    if (timeRegex.matches(duration)){
                        if (initialStart.isNotEmpty()) {
                            initialEnd = calcEndTime(duration, initialStart)
                        } else if (initialEnd.isNotEmpty()) {
                            initialStart = calcStartTime(duration, initialEnd)
                        }
                    }
                 },
                label = { Text("Dauer") },
                modifier = Modifier.weight(1f),
                placeholder = { Text("HH:mm") }
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                var task: Task? = null
                try {
                    task = Task(
                        initialDate = LocalDate.parse(date), initialId = initialJiraID, initialDesc = initialDesc
                    )
                    task.addTime(initialStart, initialEnd)
                } catch (e: Exception) {
                    SnackbarManager.showMessage("Fehler beim Erstellen der Aufgabe: ${e.message}")
                    return@Button
                }

                timeTable.addTask(task)
                SnackbarManager.showMessage("Aufgabe hinzugefügt: ${task.id}")
                resetFields()
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hinzufügen")
        }
    }
}

private fun calcStartTime(duration: String, end:String): String {
    val endTime = LocalTime.parse(end)
    val durationTime = LocalTime.parse(duration)
    val startTime = endTime.minusHours(durationTime.hour.toLong()).minusMinutes(durationTime.minute.toLong())
    return startTime.toString()
}

private fun calcEndTime(duration: String, start:String): String {
    val startTime = LocalTime.parse(start)
    val durationTime = LocalTime.parse(duration)
    val endTime = startTime.plusHours(durationTime.hour.toLong()).plusMinutes(durationTime.minute.toLong())
    return endTime.toString()
}

