package org.exxjofr.timetracker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.exxjofr.timetracker.*
import java.time.LocalDate


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
    )
}

@Composable
fun TableScreen(timeTable: TimeTable,) {
    var date by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    val columnWeight15 = .15f // 30%
    val columnWeight25 = .25f // 70%

    var tasksForDate = getTasksForDate(timeTable, date)

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).focusable(), verticalArrangement = Arrangement.spacedBy(12.dp)) {

        DatePickerFieldToModal(
            modifier = Modifier.fillMaxWidth(),
            pickedDate = { pickedDate ->
                date = pickedDate
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            Modifier
                .height(200.dp)
                .border(1.dp, Color.Black)
        ) {
            // Here is the header
            item {
                Row(Modifier.height(40.dp).background(Color.Gray)) {
                    TableCell(text = "Datum", weight = columnWeight15)
                    TableCell(text = "Vorgangs-ID", weight = columnWeight15)
                    TableCell(text = "Startzeit", weight = columnWeight15)
                    TableCell(text = "Endzeitpunkt", weight = columnWeight15)
                    TableCell(text = "Dauer", weight = columnWeight15)
                    TableCell(text = "Beschreibung", weight = columnWeight25)
                }
            }
            // Here are all the lines of your table.
            items(tasksForDate) { task ->
                Row(Modifier.height(40.dp).fillMaxWidth()) {
                    TableCell(text = task.date.toString(), weight = columnWeight15)
                    TableCell(text = task.id, weight = columnWeight15)
                    TableCell(text = task.startTime.toString(), weight = columnWeight15)
                    TableCell(text = task.endTime.toString(), weight = columnWeight15)
                    TableCell(text = task.duration.toString(), weight = columnWeight15)
                    TableCell(text = task.desc, weight = columnWeight25)

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
                    val list = timeTable.getTasksByDate(targetDate = LocalDate.now())
                    val day = BuisnessDay.workDay(list)
                    val excelWriter = ExcelWriter("C:\\Users\\jofr\\Downloads\\Arbeitszeit.xlsx")
                    excelWriter.writeDay(day)
                }
            ) {
                Text("Den Tag in Excel eintragen")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    val tasks = timeTable.getTasksByDate(targetDate = LocalDate.now())
                    Jira.upload(tasks)
                }) {
                Text("Heutige Aufgaben in Jira hochladen")
            }
        }
    }
}



private fun getTasksForDate(timeTable: TimeTable, date: LocalDate?): List<Task> {
    val localDate = try {
        LocalDate.from(date)
    } catch (e: Exception) {
        LocalDate.now()
    }
    return timeTable.getTasksByDate(localDate)
}