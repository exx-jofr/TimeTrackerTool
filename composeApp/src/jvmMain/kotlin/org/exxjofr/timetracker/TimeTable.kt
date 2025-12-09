package org.exxjofr.timetracker

import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.IOException
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TimeTable(path: String) {
    val logger = LogManager.getLogger("TimeTable")
    val file: File = File(path)

    fun addTask(task: Task) {
        val existingTasks = getTasks()

        val insertIndex = existingTasks.indexOfFirst { task.isAfter(it) }
        if (insertIndex == 0) {
            logger.info("Appending task at the end of the file.")
            file.appendText("\n${task}")
        } else {
            insertLineAt(insertIndex + 1, task.toString())
            logger.info("Inserted task at line ${insertIndex + 1}.")
        }

    }

    fun updateTask(oldTask: Task, newTask: Task) {
        val lines = file.readLines().toMutableList()
        val oldTaskString = oldTask.toString()
        val newTaskString = newTask.toString()

        val lineIndex = lines.indexOfFirst { it == oldTaskString }
        if (lineIndex != -1) {
            lines[lineIndex] = newTaskString
            file.writeText(lines.joinToString("\n"))
            logger.info("Task updated successfully.")
        } else {
            logger.error("Old task not found in the file.")
        }
    }

    fun updateListOfTasks(oldTasks: List<Task>, newTasks: List<Task>) {
        val lines = file.readLines().toMutableList()
        oldTasks.forEachIndexed { index, task ->
                val oldTaskString = task.toString()
                val newTaskString = newTasks[index].toString()

                val lineIndex = lines.indexOfFirst { it == oldTaskString }
                if (lineIndex != -1) {
                    lines[lineIndex] = newTaskString
                } else {
                    logger.error("Old task not found in the file: $oldTaskString")
                }
            file.writeText(lines.joinToString("\n"))
            logger.info("Task updated successfully.")
        }
    }

    //Date;Vorgangs-ID;Startzeit;Endzeit;Dauer;Beschreibung\
    fun showTaks() {
        try {
            var list = mutableListOf<Task>()
            var tasks = getTasks()
            tasks.forEach { currentTask ->
                val sameTasks = list.filter { t ->
                    t.id == currentTask.id && t.date.isEqual(currentTask.date)
                }

                if (sameTasks.isNotEmpty()) {
                    sameTasks.forEach { existingTask ->
                        currentTask.mergeTask(existingTask)
                        list.remove(existingTask)
                    }
                }
                list.add(currentTask)
            }
            print(list)
        } catch (e: Exception) {
            println("Error: $e")
        }
    }

    fun getTasksByDate(targetDate: LocalDate): MutableList<Task> {
        val list = mutableListOf<Task>()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        //"$date;$id;$start;$end;$dauer;$desc;&inJira
        val matchingLines = this.file.readLines().drop(1)
            .filter { line ->
                val date = LocalDate.parse(line.split(";")[1], formatter)
                date.isEqual(targetDate)
            }

        if (matchingLines.isEmpty()) {
            logger.error("No tasks found")
            return list
        }

        matchingLines.forEach { line ->
            val p = line.split(";")
            val currentTask = Task(
                uid = p[0],
                initialDate = LocalDate.parse(p[1], formatter),
                initialId = p[2],
                initialDesc = p[6],
                initialDuration = Duration.parse(p[5]),
                initialInJira = p[7].toBooleanStrict()
            )
            currentTask.addTime(p[3], p[4])
            list.add(currentTask)
        }

        return list
    }

    fun loadingIDs(): List<String> {
        var currentDate = LocalDate.now()
        val uniqueIds = mutableListOf<String>()

        for (i in 1..5) {
            val tasks = this.getTasksByDate(currentDate)
            if (tasks.isEmpty()) {
                currentDate = currentDate.minusDays(1)
                continue
            }
            tasks.forEach { task ->
                if (!uniqueIds.contains(task.id)) {
                    uniqueIds.add(task.id)
                }
            }
            currentDate = currentDate.minusDays(1)
        }

        return uniqueIds
    }

    private fun getTasks(): MutableList<Task> {
        val list = mutableListOf<Task>()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        //"$date;$id;$start;$end;$dauer;$desc
        this.file.readLines().drop(1)
            .forEach { line ->
                val p = line.split(";")
                val currentTask = Task(
                    uid = p[0],
                    initialDate = LocalDate.parse(p[1], formatter),
                    initialId = p[2],
                    initialDesc = p[6],
                    initialDuration = Duration.parse(p[5]),
                    initialInJira = p[7].toBoolean()
                )
                currentTask.addTime(p[3], p[4])
                list.add(currentTask)
            }
        return list
    }

    private fun insertLineAt(lineNumber: Int, newContent: String) {
        val lines = file.readLines().toMutableList()
        lines.add(lineNumber, newContent) // Zeile einfügen
        file.writeText(lines.joinToString("\n"))
    }

    companion object {
        private val logger = LogManager.getLogger("TimeTable")

        fun createFile(path: String) {
            try {
                val year = LocalDate.now().year
                val file = File("$path/timetracker_$year.csv")

                // Ensure parent directories exist
                file.parentFile?.mkdirs()

                // Create and write content (creates file if missing)
                file.writeText("Date;Vorgangs-ID;Startzeit;Endzeit;Dauer;Beschreibung;InJira\n")
                logger.info("File created at: ${file.absolutePath}")

            } catch (e: IOException) {
                logger.error(e.toString())
            }
        }
    }
}
