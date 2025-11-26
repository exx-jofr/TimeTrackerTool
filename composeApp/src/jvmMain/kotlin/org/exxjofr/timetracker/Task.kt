package org.exxjofr.timetracker

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime


@Suppress("IDENTITY_SENSITIVE_OPERATIONS_WITH_VALUE_TYPE")
data class Task(
    var date: LocalDate, var id: String, var desc: String, var duration: Duration = Duration.ZERO
) {
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

    var startTime: LocalTime? = null
    var endTime: LocalTime? = null

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
        return "${this.date};${this.id};${this.startTime};${this.endTime};${this.duration};${this.desc}"
    }


}