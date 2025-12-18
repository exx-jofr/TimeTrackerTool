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
    var vorgangsId by remember { mutableStateOf("") }
    var beschreibung by remember { mutableStateOf("") }
    var startzeit by remember { mutableStateOf("") }
    var endzeit by remember { mutableStateOf("") }
    var dauer by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    var expanded by remember { mutableStateOf(false) }

    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val ids by remember { mutableStateOf(timeTable.loadingIDs()) }

    fun berechneDauer() {
        try {
            val start = LocalTime.parse(startzeit, timeFormatter)
            val ende = LocalTime.parse(endzeit, timeFormatter)
            val diff = Duration.between(start, ende)
            dauer = "${diff.toHours()}h ${diff.toMinutesPart()}min"
        } catch (e: Exception) {
            dauer = "Ungültige Zeitangabe"
            println("Fehler bei der Dauerberechnung: $e")
        }
    }

    fun resetFields() {
        vorgangsId = ""
        date = ""
        beschreibung = ""
        startzeit = ""
        endzeit = ""
        dauer = ""
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp).focusable(), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        ExposedDropdownMenuBox(
            expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            // Das "integrierte" Dropdown-Textfeld
            OutlinedTextField(
                value = vorgangsId,
                onValueChange = {vorgangsId = it},
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
                        vorgangsId = label
                        expanded = false
                    })
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = beschreibung,
            onValueChange = { beschreibung = it },
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
                value = startzeit, onValueChange = {
                startzeit = it
                if (endzeit.isNotEmpty()) berechneDauer()
            }, label = { Text("Startzeit") }, modifier = Modifier.weight(1f), placeholder = { Text("HH:mm") }
            )

            OutlinedTextField(
                value = endzeit, onValueChange = {
                endzeit = it
                if (startzeit.isNotEmpty()) berechneDauer()
            }, label = { Text("Endzeit") }, modifier = Modifier.weight(1f), placeholder = { Text("HH:mm") }
            )
            OutlinedTextField(
                value = dauer,
                onValueChange = {
                    dauer = it
                    val timeRegex = Regex("^([01]\\d|2[0-3]):([0-5]\\d)$")
                    if (timeRegex.matches(dauer)){
                        if (startzeit.isNotEmpty()) {
                            endzeit = calcEndTime(dauer, startzeit)
                        } else if (endzeit.isNotEmpty()) {
                            startzeit = calcStartTime(dauer, endzeit)
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
                        initialDate = LocalDate.parse(date), initialId = vorgangsId, initialDesc = beschreibung
                    )
                    task.addTime(startzeit, endzeit)
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
    var endTime = LocalTime.parse(end)
    var durationTime = LocalTime.parse(duration)
    var startTime = endTime.minusHours(durationTime.hour.toLong()).minusMinutes(durationTime.minute.toLong())
    return startTime.toString()
}

private fun calcEndTime(duration: String, start:String): String {
    var startTime = LocalTime.parse(start)
    var durationTime = LocalTime.parse(duration)
    var endTime = startTime.plusHours(durationTime.hour.toLong()).plusMinutes(durationTime.minute.toLong())
    return endTime.toString()
}

