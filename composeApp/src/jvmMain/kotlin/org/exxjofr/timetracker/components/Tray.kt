package org.exxjofr.timetracker.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.apache.logging.log4j.LogManager
import org.exxjofr.timetracker.Task
import org.exxjofr.timetracker.TimeTable
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.ActionListener
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO
import kotlin.system.exitProcess


private var trayIconInstance: TrayIcon? = null
private val logger = LogManager.getLogger("TrayPopupWindow")

fun setupSystemTray(onTrayClick: () -> Unit) {
    val icon =
        "C:\\_Projekte\\Kotlin\\TimeTracker\\TimeTrackerNewNew\\TimeTracker\\composeApp\\src\\jvmMain\\kotlin\\org\\exxjofr\\timetracker\\components\\icon.png"
    try {
        if (!SystemTray.isSupported()) {
            println("SystemTray wird nicht unterstützt")
            logger.error("SystemTray is not supported on this platform")
            return
        }
        val tray = SystemTray.getSystemTray()

        if (trayIconInstance != null && tray.trayIcons.contains(trayIconInstance)) {
            logger.info("SystemTray exists already.")
            return
        }

        val iconFile = File(icon)
        if (!iconFile.exists()) {
            logger.error("Sytsemtray: Icon file not found: ${iconFile.absolutePath}")
            return
        }

        val image = ImageIO.read(iconFile)
        val popup = PopupMenu()

        val exitItem = MenuItem("Exit").apply {
            addActionListener {
                tray.remove(trayIconInstance)
                logger.info("SystemTray-instance: $trayIconInstance exited successfully")
                exitProcess(0)
            }
        }
        popup.add(exitItem)
        val trayIcon = TrayIcon(image, "TimeTracker", popup)
        trayIcon.isImageAutoSize = true
        trayIcon.addActionListener(ActionListener {
            onTrayClick()
        })
        tray.add(trayIcon)
        trayIconInstance = trayIcon
    } catch (e: Exception) {
        logger.error("SystemTray-Error: {}",e.toString())
    }
}

@Composable
fun TrayPopupWindow(onClose: () -> Unit) {
    MaterialTheme {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        var id by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var timeStart by remember { mutableStateOf("") }
        var timeEnd by remember { mutableStateOf("") }
        val timetable by remember { mutableStateOf(TimeTable("C:\\Users\\jofr\\Downloads\\timetracker_2025.csv")) }

        Column(
            modifier = Modifier.padding(16.dp).width(250.dp)
        ) {
            OutlinedTextField(value = id, onValueChange = { id = it }, label = { Text("ID") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") })
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    timeStart =  roundDownTime(LocalTime.now()).format(timeFormatter)
                    logger.info("Task $id started at $timeStart")
                }) {
                    Text("Start")
                }
                Button(onClick = {
                    timeEnd = roundUpTime(LocalTime.now()).format(timeFormatter)
                    logger.info("Task $id paused at $timeEnd")
                    val task = Task(
                        date = LocalDate.now(),
                        id = id,
                        desc = description
                    )
                    task.addTime(timeStart, timeEnd)
                    logger.info("Task $id added at $timeStart to $timeEnd")
                    timetable.addTask(task)
                }) {
                    Text("Pause")
                }
                Button(onClick = { onClose() }) {
                    Text("Close")
                }
            }
        }
    }
}

private fun roundDownTime(time: LocalTime): LocalTime {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val minutes = time.minute
    val roundedMinutes = (minutes / 15) * 15 // Abrunden auf vorherige Viertelstunde
    return LocalTime.of(time.hour, roundedMinutes)
}

private fun roundUpTime(time: LocalTime): LocalTime {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val minutes = time.minute
    var roundedMinutes = ((minutes + 14) / 15) * 15 // +14 bewirkt Aufrunden

    var roundedHour = time.hour
    if (roundedMinutes == 60) { // Überlauf in nächste Stunde
        roundedMinutes = 0
        roundedHour = (roundedHour + 1) % 24 // Falls Mitternacht
    }

    return LocalTime.of(roundedHour, roundedMinutes)
}
