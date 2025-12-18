package org.exxjofr.timetracker

class Jira {
    companion object {
        fun upload(tasks: List<Task>, apiKey:String, userName:String): List<Task> {
            val jiraTasks = mutableListOf<Task>()
            val workLogEntry = WorkLogEntry(userName = userName, apiKey=apiKey)
            tasks.forEach { task ->
                val copy = task.copy()
                if (copy.inJira) {
                    jiraTasks.add(copy)
                    return@forEach
                }
                val inJira = workLogEntry.postWorklog(copy)
                copy.setIsInJira(inJira)
                jiraTasks.add(copy)
            }
            return jiraTasks
        }
    }
}