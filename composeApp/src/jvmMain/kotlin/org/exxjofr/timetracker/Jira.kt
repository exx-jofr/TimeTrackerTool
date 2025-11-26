package org.exxjofr.timetracker

import java.time.LocalDate

class Jira {
    companion object {
        fun upload(tasks: MutableList<Task>) {
            var workLogEntry = WorkLogEntry()
            tasks.forEach { task ->
                workLogEntry.postWorklog(task)
            }
        }
    }
}