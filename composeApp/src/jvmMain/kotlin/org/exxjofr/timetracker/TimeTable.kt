package org.exxjofr.timetracker

import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TimeTable(path: String) {
    private val file: File = File(path)
    private var linesCsv = file.readLines().filter { !it.startsWith("UUID;") }.toMutableList()

    fun reload() {
        linesCsv = file.readLines().filter { !it.startsWith("UUID;") }.toMutableList()
        LOGGER.info("TimeTable reloaded from file: ${file.absolutePath}")
    }

    fun addTask(task: Task) {
        val existingTasks = getAllTasks()

        val insertIndex = existingTasks.indexOfFirst { task.isAfter(it) }
        if (insertIndex != -1 && existingTasks.isNotEmpty()) {
            LOGGER.info("Appending task at the end of the file.")
            file.appendText("\n${task}")
            linesCsv.add(task.toString())
        } else {
            insertLineAt(insertIndex, task.toString())
            LOGGER.info("Inserted task at line ${insertIndex}.")
        }

    }

    fun updateTasks(newTasks: List<Task>) {
        val taskMap = newTasks.associateBy { it.uuid }

        linesCsv = linesCsv.map { line ->
            val uuid = line.substringBefore(";")
            taskMap[uuid]?.toString() ?: line
        }.toMutableList()

        file.writeText(CSV_HEADER + sortAfterTime(linesCsv).joinToString("\n"))
        LOGGER.info("Tasks updated successfully.")
    }

    fun sortAfterTime(lines: List<String>): List<String> {
        val tasks = lines
            .filter { !it.startsWith("UUID;") }
            .map { Task.createTaskFromString(it) }
        val sortedTasks = tasks.sortedWith(
            compareBy<Task> { it.date }
                .thenBy { it.startTime }
        )
        LOGGER.info("File sorted successfully.")
        return sortedTasks.map { it.toString() }
    }

    fun getTasksByDate(targetDate: LocalDate): List<Task> {
        val matchingTasks = linesCsv
            .filter {
                LocalDate.parse(it.split(";")[1], DATE_FORMATTER).isEqual(targetDate)
            }
            .map { Task.createTaskFromString(it) }

        if (matchingTasks.isEmpty()) {
            LOGGER.error("No tasks found")
        }

        return matchingTasks
    }

    fun loadingIDs(): List<String> {
        val uniqueIds = mutableSetOf<String>()
        var currentDate = LocalDate.now()

        repeat(5) {
            this.getTasksByDate(currentDate)
                .forEach { uniqueIds.add(it.id) }
            currentDate = currentDate.minusDays(1)
        }
        return uniqueIds.toList()
    }

    private fun getAllTasks(): MutableList<Task> {
        return linesCsv.map { Task.createTaskFromString(it) }.toMutableList()
    }

    private fun insertLineAt(lineNumber: Int, newContent: String) {
        linesCsv.add(lineNumber, newContent) // Zeile einfügen
        file.writeText(CSV_HEADER + linesCsv.joinToString("\n"))
    }

    companion object {
        private val LOGGER = LogManager.getLogger("TimeTable")
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        private const val CSV_HEADER = "UUID;Date;Vorgangs-ID;Startzeit;Endzeit;Dauer;Beschreibung;InJira\n"

        fun createFile(path: String) {
            try {
                val year = LocalDate.now().year
                val file = File("$path/timetracker_$year.csv")

                // Ensure parent directories exist
                file.parentFile?.mkdirs()

                // Create and write content (creates file if missing)
                file.writeText(CSV_HEADER)
                LOGGER.info("File created at: ${file.absolutePath}")

            } catch (e: IOException) {
                LOGGER.error(e.toString())
            }
        }
    }
}
