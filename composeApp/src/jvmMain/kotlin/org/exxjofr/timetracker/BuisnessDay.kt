package org.exxjofr.timetracker

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class BuisnessDay(
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val breakDuration: Duration
) {
    override fun toString(): String {
        return "Buisness Day: $date, Start Time: $startTime, End Time: $endTime, Break Duration: $breakDuration"
    }


    companion object {
        fun workDay(tasksForADay: List<Task>): BuisnessDay {
            val sortedTasks =
                tasksForADay.sortedWith(compareBy<Task> { it.date }.thenBy { it.startTime })
            if (!tasksForADay.first().date.isEqual(tasksForADay.last().date)) {
                println("Tasks are not from the same day!")
                return BuisnessDay(LocalDate.now(), LocalTime.of(0, 0), LocalTime.of(0, 0), Duration.ZERO)
            }
            var firstTask = sortedTasks.first()
            var endtime = firstTask.endTime
            var breakDuration = Duration.ZERO
            tasksForADay.drop(1).forEach { task ->
                if (task.endTime?.isAfter(endtime) == true) {
                    endtime = task.endTime
                }
                if (firstTask.endTime?.isBefore(task.startTime) == true) {
                    breakDuration = breakDuration.plus(Duration.between(firstTask.endTime, task.startTime))
                }
                firstTask = task

            }
            return BuisnessDay(
                date = tasksForADay.first().date,
                startTime = tasksForADay.first().startTime!!,
                endTime = endtime!!,
                breakDuration = breakDuration
            )
        }
    }
}