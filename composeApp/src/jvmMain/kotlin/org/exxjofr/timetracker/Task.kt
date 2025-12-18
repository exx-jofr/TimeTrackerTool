package org.exxjofr.timetracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID


@Suppress("IDENTITY_SENSITIVE_OPERATIONS_WITH_VALUE_TYPE")
data class Task(
    val uuid: String = UUID.randomUUID().toString(),
    val initialDate: LocalDate,
    val initialId: String,
    val initialDesc: String,
    val initialDuration: Duration = Duration.ZERO,
    val initialInJira: Boolean = false
) {

    var date by mutableStateOf(initialDate)
    var id by mutableStateOf(initialId)
    var desc by mutableStateOf(initialDesc)
    var duration by mutableStateOf(initialDuration)
    var inJira by mutableStateOf(initialInJira)

    var startTime by mutableStateOf<LocalTime?>(null)
    var endTime by mutableStateOf<LocalTime?>(null)

    init {
        require(id.isNotEmpty()) { "give me a valid id" }
        require(!id.contains(";")) { "id must not contain semicolon" }
        require(id.isNotBlank()) { "id must be have a value " }
        require(desc.isNotEmpty()) { "give me a valid description" }
        require(!desc.contains(";")) { "description must not contain semicolon" }
        require(desc.isNotBlank()) { "description must be have a value " }
        requireNotNull(date) { "Give me a valid date" }
        require(date.year == LocalDate.now().year) { "give me the current year" }
        require(date.monthValue in 1..12) { "Month must be between 1 to 12" }
        require(date.dayOfMonth in 1..31) { "Day must be between 1 to 31" }
        require(duration >= Duration.ZERO) { "Duration must be positive" }
    }

    fun addTime(start: LocalTime, end: LocalTime) {
        this.startTime = start
        this.endTime = end
        this.duration = calculateDuration(start, end)
    }

    fun addTime(start: String, end: String) {
        val timeRegex = Regex("^([01]\\d|2[0-3]):([0-5]\\d)$")
        require(start.isNotBlank()) { "Start time must not be blank" }
        require(end.isNotBlank()) { "End time must not be blank" }
        require(timeRegex.matches(start)) { "Start time must be in HH:mm format" }
        require(timeRegex.matches(end)) { "End time must be in HH:mm format" }
        this.startTime = LocalTime.parse(start)
        this.endTime = LocalTime.parse(end)
        this.duration = calculateDuration(this.startTime!!, this.endTime!!)
    }

    fun setIsInJira(inJira: Boolean) {
        this.inJira = inJira
    }

    private fun calculateDuration(start: LocalTime, end: LocalTime): Duration {
        return Duration.between(start, end)
    }


    fun addDuration(time: Duration) {
        this.duration = this.duration.plus(time)
    }

    fun addDesc(desc: String) {
        this.desc = "${this.desc} \n $desc"
    }

    fun mergeTask(other: Task) {
        if (this.date.isEqual(other.date)) {
            if (this.id == other.id) {
                this.addDuration(other.duration)
                this.addDesc(other.desc)
            }
        }
        return
    }

    fun isAfter(other: Task): Boolean {
        return when {
            // Wenn Datum später ist
            this.date.isAfter(other.date) -> true

            // Wenn Datum gleich, dann Startzeit vergleichen
            this.date.isEqual(other.date) && this.startTime != null && other.startTime != null -> this.startTime!!.isAfter(
                other.startTime!!
            )

            else -> false
        }
    }

    override fun toString(): String {
        return "${this.uuid};${this.date};${this.id};${this.startTime};${this.endTime};${this.duration};${this.desc};" +
                "${this.inJira}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (uuid != other.uuid) return false
        if (inJira != other.inJira) return false
        if (date != other.date) return false
        if (id != other.id) return false
        if (desc != other.desc) return false
        if (duration != other.duration) return false
        if (startTime != other.startTime) return false
        if (endTime != other.endTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inJira.hashCode()
        result = 31 * result + uuid.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + desc.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + (startTime?.hashCode() ?: 0)
        result = 31 * result + (endTime?.hashCode() ?: 0)
        return result
    }

    fun copy(): Task {
        val newTask = Task(
            uuid = this.uuid,
            initialDate = this.date,
            initialId = this.id,
            initialDesc = this.desc,
            initialDuration = this.duration,
            initialInJira = this.inJira
        )
        newTask.startTime = this.startTime
        newTask.endTime = this.endTime
        return newTask
    }

    companion object {
        fun createTaskFromString(taskString: String): Task {
            val parts = taskString.split(";")
            require(parts.size >= 8) { "Invalid task string format" }

            val uuid = parts[0]
            val date = LocalDate.parse(parts[1])
            val id = parts[2]
            val startTime = if (parts[3] != "null") LocalTime.parse(parts[3]) else null
            val endTime = if (parts[4] != "null") LocalTime.parse(parts[4]) else null
            val duration = Duration.parse(parts[5])
            val desc = parts[6]
            val inJira = parts[7].toBoolean()

            val task = Task(
                uuid = uuid,
                initialDate = date,
                initialId = id,
                initialDesc = desc,
                initialDuration = duration,
                initialInJira = inJira
            )
            if (startTime != null && endTime != null) {
                task.startTime = startTime
                task.endTime = endTime
            }
            return task
        }
    }
}