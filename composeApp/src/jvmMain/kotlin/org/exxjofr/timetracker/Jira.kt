package org.exxjofr.timetracker

class Jira {
    companion object {
        fun upload(tasks: MutableList<Task>, apiKey:String, userName:String) {
            val workLogEntry = WorkLogEntry(userName = userName, apiKey=apiKey)
            tasks.forEach { task ->
                workLogEntry.postWorklog(task)
            }
        }
    }
}