package org.exxjofr.timetracker

import org.apache.logging.log4j.LogManager
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

private val logger = LogManager.getLogger("App")

// Berechne Zeitzonenoffset (Europe/Berlin)
val localZoneId = ZoneId.of("Europe/Berlin")
val nowBerlin = ZonedDateTime.now(localZoneId)
var gTimeOffset = nowBerlin.offset.totalSeconds / 3600

// Jira API-Konfiguration
const val jiraUser = "jonas.franke@exxeta.com"   // Login-Email
const val jiraApiKey = "ATATT3xFfGF0dB8SxpMCiIXSbHskinVSAjboMMkOksvs7tPy6RuYmW9J8cnNZ0foj42jqQeHP9kJgjusqZQ23ZMmnXdvMdz9Cwmzk4jFwl0UJ0bk8n5fdhP-CzKLodT53hH80WYKGHcN51Op0DtjZ9UHwIezDqGTBED6BKoYWGLlGSWuQwvlYQI=0727048C"            // Dein API-Token
const val jiraBaseUrl = "https://exxeta.atlassian.net/rest/api/3/issue"

// WorklogEntry-Klasse
class WorkLogEntry() {
    // POST-Request an Jira senden
    fun postWorklog(task: Task) {
        val url = URI.create("$jiraBaseUrl/${task.id}/worklog").toURL()
        val connection = (url.openConnection() as HttpURLConnection)
            .apply {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-Type", "application/json")

            // BasicAuth mit API-Key
            val authString = "$jiraUser:$jiraApiKey"
            val basicAuth = "Basic " + Base64.getEncoder().encodeToString(authString.toByteArray())
            setRequestProperty("Authorization", basicAuth)
        }

        OutputStreamWriter(connection.outputStream).use { writer ->
            writer.write(this.toJson(task))
        }

        val responseCode = connection.responseCode
        logger.info("POST Worklog → Response: $responseCode")
        if (responseCode in 200..<300) {
            logger.info("Worklog sent succesfully to Jira. Task ID: ${task.id}: ${task.desc}")
        } else {
            logger.error("Failed to send worklog to Jira. Response Code: $responseCode, Task ID: ${task.id}: ${task.desc}")
        }

        connection.disconnect()
    }

    private fun getHour(time: String): String {
        val hour = time.split(":")[0].toInt() - gTimeOffset
        val adjHour = if (hour < 0) 9 else hour
        return adjHour.toString().padStart(2, '0')
    }

    private fun getMinutes(time: String): String {
        val minutes = time.split(":")[1].toInt()
        val adjMinutes = if (minutes < 0) 15 else minutes
        return adjMinutes.toString().padStart(2, '0')
    }

    private fun getStartDate(date: String, time: String): String {
        return "${date}T${getHour(time)}:${getMinutes(time)}:00.000+0000"
    }

    private fun getDurationInSeconds(duration: Long): Int {
        return (duration.toFloat() * 3600).toInt()
    }

    private fun getDurationInSeconds(duration: Duration): Int {
        return duration.toSeconds().toInt()
    }

    private fun getMessage(msg: String): String {
        var message = msg
        while (message.contains("  ")) {
            message = message.replace("  ", " ")
        }
        return message // JSON-safe für Kotlin-Strings
    }

    private fun toJson(task: Task): String {
        return """
        {
          "comment": {
            "content": [
              {
                "content": [
                  {
                    "text": "${getMessage(task.desc)}",
                    "type": "text"
                  }
                ],
                "type": "paragraph"
              }
            ],
            "type": "doc",
            "version": 1
          },
          "started": "${getStartDate(task.date.toString(), task.startTime.toString())}",
          "timeSpentSeconds": ${getDurationInSeconds(task.duration)}
        }
        """.trimIndent()
    }
}

// Hauptfunktion
/*fun main() {
    /*if (args.size < 2) {
        println("Usage: program <file>")
        exitProcess(1)
    }*/

    if (gTimeOffset.toLong() != 1L && gTimeOffset.toLong() != 2L) {
        gTimeOffset = 2
    }

    /*File(args[1]).forEachLine { line ->
        val trimmed = line.trim()
        if (trimmed.isEmpty()) return@forEachLine

        val tokens = trimmed.split("|").map { it.trim() }
        val workLogEntry = WorkLogEntry(
            date = tokens[0],
            time = tokens[1],
            duration = tokens[2],
            issue = tokens[3],
            msg = tokens[4]
        )*/
    val workLogEntry = WorkLogEntry(
        date = "2025-11-17",
        time = "09:00",
        duration = "4",
        issue = "EEXDEV-1565",
        msg = "Kotlin"
    )

    println("⏩ Sende Worklog für ${workLogEntry.issue} ...")
    workLogEntry.postWorklog()

}*/