package org.exxjofr.timetracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.exxjofr.timetracker.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        maxLines = 1,                         // nur eine Zeile
        overflow = TextOverflow.Ellipsis,     // bei Überlänge "…"
        modifier = Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
            .height(56.dp),
        fontSize = 14.sp
    )
}

@Composable
fun TableScreen(timeTable: TimeTable, pathExcel: String, userName: String, apiKey: String) {
    var date by remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    val columnWeight15 = .15f // 30%
    val columnWeight25 = .25f // 70%
    var isEditing by mutableStateOf(false)

    var tasksForDate by remember { mutableStateOf(emptyList<Task>()) }

    LaunchedEffect(date) {
        tasksForDate = getTasksForDate(timeTable, date)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp).focusable(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        DatePickerFieldToModal(
            modifier = Modifier.fillMaxWidth(),
            pickedDate = { pickedDate ->
                pickedDate?.let { pickedDate ->
                    date = pickedDate
                }
            }
        )

        Row {
            Button(
                onClick = {
                    isEditing = !isEditing
                    tasksForDate = getTasksForDate(timeTable, date)
                }
            ) {
                Text("Editieren")
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (isEditing) {
                Button(
                    onClick = {timeTable.updateListOfTasks()}
                ) {
                    Text("Aktualisieren")
                }
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            Modifier
                .height(200.dp)
                .border(1.dp, Color.Black)
        ) {
            // Here is the header
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableCell(text = "Datum", weight = columnWeight15)
                    TableCell(text = "Vorgangs-ID", weight = columnWeight15)
                    TableCell(text = "Startzeit", weight = columnWeight15)
                    TableCell(text = "Endzeitpunkt", weight = columnWeight15)
                    TableCell(text = "Dauer", weight = columnWeight15)
                    TableCell(text = "Beschreibung", weight = columnWeight25)
                }
            }
            // Here are all the lines of your table.
            items(tasksForDate, key = { it.uid }) { task ->
                Row(Modifier.height(56.dp).fillMaxWidth()) {
                    if (isEditing) {
                        EditableTableCell(
                            value = task.date.toString(),
                            modifier = Modifier.weight(columnWeight15),
                            onValueChange = { newValue -> parseDate(newValue).let { t ->
                                if (t != null) {
                                    task.date = t
                                }
                            } }
                        )
                        EditableTableCell(
                            value = task.id,
                            modifier = Modifier.weight(columnWeight15),
                            onValueChange = { newValue -> task.id = newValue }
                        )
                        EditableTableCell(
                            value = task.startTime.toString(),
                            modifier = Modifier.weight(columnWeight15),
                            onValueChange = { newValue -> parseTime(newValue).let { t ->
                                if (t != null) {
                                    task.startTime = t
                                }
                            } }
                        )
                        EditableTableCell(
                            value = task.endTime.toString(),
                            modifier = Modifier.weight(columnWeight15),
                            onValueChange = { newValue -> parseTime(newValue).let { t ->
                                if (t != null) {
                                    task.endTime = t
                                }
                            } }
                        )
                        EditableTableCell(
                            value = durationToHHmm(task.duration),
                            modifier = Modifier.weight(columnWeight15),
                            onValueChange = { newValue -> parseDuration(newValue).let { t ->
                                if (t != null) {
                                    task.duration = t
                                }
                            } }
                        )
                        EditableTableCell(
                            value = task.desc,
                            modifier = Modifier.weight(columnWeight25),
                            onValueChange = { newValue -> task.desc = newValue }
                        )
                    } else {
                        TableCell(text = task.date.toString(), weight = columnWeight15)
                        TableCell(text = task.id, weight = columnWeight15)
                        TableCell(text = task.startTime.toString(), weight = columnWeight15)
                        TableCell(text = task.endTime.toString(), weight = columnWeight15)
                        TableCell(text = durationToHHmm(task.duration), weight = columnWeight15)
                        TableCell(text = task.desc, weight = columnWeight25)
                    }


                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp), // Einheitliche Höhe für alle Felder
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Abstand
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    val list = timeTable.getTasksByDate(targetDate = date)
                    val day = BuisnessDay.workDay(list)
                    val excelWriter = ExcelWriter(pathExcel)
                    excelWriter.writeDay(day)
                }
            ) {
                Text("Den Tag in Excel eintragen")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    val tasks = timeTable.getTasksByDate(targetDate = date)
                    val jiraTasks = Jira.upload(tasks, userName = userName, apiKey = apiKey)
                    timeTable.updateListOfTasks(oldTasks = tasks, newTasks = jiraTasks)
                }) {
                Text("Heutige Aufgaben in Jira hochladen")
            }
        }
    }
}

@Composable
fun EditableTableCell(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black)
            .padding(horizontal = 1.dp)
            .height(56.dp),
    )
}


private fun getTasksForDate(timeTable: TimeTable, date: LocalDate?): List<Task> {
    val localDate = try {
        LocalDate.from(date)
    } catch (e: Exception) {

        LocalDate.now()
    }
    return timeTable.getTasksByDate(localDate)
}

private fun parseDate(dateString: String): LocalDate? {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    return try {
        LocalDate.parse(dateString, formatter)
    } catch (e: DateTimeParseException) {
        null
    }
}

private fun parseTime(timeString: String): LocalTime? {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    return try {
        LocalTime.parse(timeString, formatter)
    } catch (e: DateTimeParseException) {
        null
    }
}

private fun parseDuration(durationString: String): Duration? {
    return try {
        Duration.parse(durationString)
    } catch (e: Exception) {
        null
    }
}

fun durationToHHmm(duration: Duration): String {
    val hours = duration.toHours()
    val minutes = duration.minusHours(hours).toMinutes()
    return String.format("%02d:%02d", hours, minutes)
}


fun hhmmToDuration(time: String): Duration {
    val parts = time.split(":")
    require(parts.size == 2) { "Ungültiges Format, erwartet HH:mm" }

    val hours = parts[0].toLongOrNull()
        ?: throw IllegalArgumentException("Ungültige Stunden: ${parts[0]}")
    val minutes = parts[1].toLongOrNull()
        ?: throw IllegalArgumentException("Ungültige Minuten: ${parts[1]}")

    require(hours >= 0) { "Stunden müssen >= 0 sein" }
    require(minutes in 0..59) { "Minuten müssen zwischen 0 und 59 liegen" }

    return Duration.ofHours(hours).plusMinutes(minutes)
}